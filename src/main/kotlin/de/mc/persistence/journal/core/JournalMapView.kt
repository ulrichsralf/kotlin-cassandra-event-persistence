package de.mc.persistence.journal.core

/**
 * ralfulrich on 11.03.17.
 */
open class JournalMapView<U, V>(private val idExtractor: (V) -> U) : JournalView<V> {

    protected open val map = mutableMapOf<U, V>()

    override fun onCreate(entry: V) {
        map.put(idExtractor.invoke(entry), entry)
        afterUpdate(entry)
    }

    override fun onUpdate(entry: V) {
        onCreate(entry)
    }

    override fun onDelete(entry: V) {
        map.remove(idExtractor.invoke(entry))
        afterUpdate(entry)
    }

    override fun clear() {
        map.clear()
    }

    open fun afterUpdate(entry: V) {}

    open fun getById(id: U): V? = map[id]

    open fun query(predicate: (V) -> Boolean, limit: Int): List<V> = map.values.filter(predicate).take(limit)

    open fun query(predicate: (V) -> Boolean): List<V> = map.values.filter(predicate).toList()

    override fun all() = map.values.toList()

    open fun withMap(m: MutableMap<U, V>.() -> Unit) {
        m.invoke(map)
    }
}