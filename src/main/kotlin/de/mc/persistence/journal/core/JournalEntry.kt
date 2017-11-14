package de.mc.persistence.journal.core

import org.springframework.cassandra.core.PrimaryKeyType
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.mapping.Table
import java.util.*

/**
 * @author Ralf Ulrich
 * 28.02.17
 */
@Table(value = "journal")
data class JournalEntry(
        @PrimaryKeyColumn(name = "persistenceId", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
        val persistenceId: String,
        @PrimaryKeyColumn(name = "partition", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
        val partition: Long,
        @PrimaryKeyColumn(name = "timestamp", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
        val timestamp: UUID,
        @PrimaryKeyColumn(name = "timebucket", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
        val timebucket: String,
        val serializedType: String,
        val serializerId: Int,
        val operation: String,
        val principal: String?,
        val value: String)