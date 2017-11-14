package de.mc.persistence.journal.core

import com.datastax.driver.core.utils.UUIDs
import de.mc.persistence.util.getLogger
import org.springframework.beans.factory.SmartInitializingSingleton
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.cassandra.core.CassandraOperations
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong


/**
 * @author Ralf Ulrich
 * 28.02.17
 */
abstract class JournalRepository<T>(val persistenceId: String, val clazz: Class<in T>) : SmartInitializingSingleton {

    val log = getLogger()

    init {
        val existing = REGISTRY.put(persistenceId, javaClass.name)
        if (existing != null) {
            throw IllegalStateException("Repository ${javaClass.name} can't be registered with persistenceId " +
                    "$persistenceId , already registered: $existing")
        }
    }

    @Autowired
    lateinit var views: List<JournalView<T>>


    @Autowired
    lateinit var serializer: Serializer
    @Autowired
    lateinit var cassandraOps: CassandraOperations

    private val timeBucketFormat = SimpleDateFormat("yyyyMMdd")
    private val partition = Partition()
    private var initialized = false


    override fun afterSingletonsInstantiated() {
        beforeReplay()
        readJournal()
        initialized = true
        loadDataFiles()
        replayFinished()
    }

    private fun loadDataFiles() {
        try {
            val data = javaClass.classLoader.getResourceAsStream("data/${javaClass.simpleName}.data")
            if (data != null) {
                val scanner = Scanner(data)
                while (scanner.hasNextLine()) {
                    val line = scanner.nextLine()
                    val className = line.substringBefore(';')
                    val dataJson = line.substringAfter(';')
                    @Suppress("UNCHECKED_CAST")
                    val entity = serializer.fromBinary(dataJson.toByteArray(), className) as T
                    log.info("create entity $entity")
                    create(entity)
                }
            }
        } catch (e: Exception) {
            log.warn("could not load data for repository ${javaClass.simpleName} ${e.message}")
        }
    }

    fun create(entity: T) {
        persist(entity, Operation.CREATE)
    }

    fun update(entity: T) {
        persist(entity, Operation.UPDATE)
    }


    fun delete(entity: T) {
        persist(entity, Operation.DELETE)
    }

    private fun persist(entity: T, operation: Operation) {
        if (!initialized) throw IllegalStateException("Repository is not initialized yet")
        val timeBasedUUID = UUIDs.timeBased()
        val timeBucket = timeBucketFormat.format(Date(UUIDs.unixTimestamp(timeBasedUUID)))
        cassandraOps.insert(JournalEntry(persistenceId,
                partition.currentPartition(),
                timeBasedUUID,
                timeBucket,
                serializer.manifest(entity as Any),
                serializer.identifier(),
                operation.name,
                null,//SecurityContextHolder.getContext()?.authentication?.name,
                String(serializer.toBinary(entity))))
        updateState(entity, operation)
    }

    fun readJournal() {
        var currentPartition: Long
        do {
            currentPartition = partition.currentPartition()
            getAllEntriesOfPartition(currentPartition)
                    .forEach {
                        val operation = it.operation
                        val entity = serializer.fromBinary(it.value.toByteArray(), it.serializedType)
                        updateState(entity as T, Operation.valueOf(operation))
                    }
        } while (currentPartition != partition.currentPartition())
    }

    fun getAllEntriesOfPartition(partition: Long): Iterator<JournalEntry> {
        return cassandraOps.stream("SELECT * FROM ES.JOURNAL WHERE PERSISTENCEID = '$persistenceId' AND PARTITION = $partition", JournalEntry::class.java)
    }


    fun getCount(): Long {
        return partition.count.get()
    }

    fun getEntityClass(): Class<in T> {
        return clazz
    }

    open fun beforeReplay() {
    }

    open fun replayFinished() {

    }

    private fun updateState(entity: T, operation: Operation) {
        partition.inc()
        when (operation) {
            Operation.CREATE -> views.forEach { it.onCreate(entity) }
            Operation.UPDATE -> views.forEach { it.onUpdate(entity) }
            Operation.DELETE -> views.forEach { it.onDelete(entity) }

        }
    }

    companion object {
        /**
         *  the partition size can't be changed dynamically with this implementation once there is existing data
         */
        private val PARTITION_SIZE = 10000

        val REGISTRY = ConcurrentHashMap<String, String>()
    }

    private enum class Operation {
        CREATE, UPDATE, DELETE
    }

    private class Partition(var count: AtomicLong = AtomicLong(0)) {
        fun inc(): Partition {
            count.incrementAndGet()
            return this
        }

        fun currentPartition(): Long {
            return count.get() / PARTITION_SIZE
        }

        fun reset() {
            count.set(0)
        }
    }

    fun reload() {
        log.info("reloading repo for ${this.clazz} start")
        views.forEach { it.clear() }
        partition.reset()
        readJournal()
        log.info("reloading repo for ${this.clazz} finished")
    }
}
