package de.mc.persistence.journal.core

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder

/**
 * View with multiple values per key
 * ralfulrich on 11.03.17.
 */
open class JournalMultiMapView<U, V>(private val idExtractor: (V) -> U) : JournalView<V> {

    protected open val map = MultimapBuilder.SetMultimapBuilder.hashKeys().hashSetValues().build<U, V>()

    override fun onCreate(entry: V) {
        map.put(idExtractor.invoke(entry), entry)
        afterUpdate(entry)
    }

    override fun onUpdate(entry: V) {
        onCreate(entry)
    }

    override fun onDelete(entry: V) {
        map[idExtractor.invoke(entry)]?.remove(entry)
        afterUpdate(entry)
    }

    override fun clear() {
        map.clear()
    }

    fun afterUpdate(entry: V) {}

    open fun getById(id: U): List<V> = map[id].toList()

    override fun all() = map.values().toList()

    open fun withMap(m: Multimap<U, V>.() -> Unit) {
        m.invoke(map)
    }
}