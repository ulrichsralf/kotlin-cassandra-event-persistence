package de.mc.persistence.journal.core

/**
 * ralfulrich on 10.03.17.
 */
interface JournalView<V> {

    fun onCreate(entry: V)

    fun onUpdate(entry: V)

    fun onDelete(entry: V)

    fun clear()

    fun all(): List<V>
}