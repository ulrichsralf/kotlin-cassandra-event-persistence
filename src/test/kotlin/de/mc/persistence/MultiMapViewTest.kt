package de.mc.persistence

import de.mc.persistence.journal.core.JournalMultiMapView
import junit.framework.Assert.assertEquals
import org.junit.Test

/**
 * Created by ralf on 11.06.17.
 */
open class MultiMapViewTest {


    @Test
    fun testMultiMapView() {

        val test = TestMapMapView()
        val entry1 = TestEntity("id1", "group1", "payload1")
        test.onCreate(entry1)
        val entry2 = TestEntity("id1", "group2", "payload1")
        test.onCreate(entry2)
        val entry3 = TestEntity("id1", "group2", "payload2")
        test.onCreate(entry3)
        val entry4 = TestEntity("id2", "group2", "payload1")
        test.onCreate(entry4)

        assertEquals(listOf(entry2, entry4, entry1), test.all())
        println(test.all())
        assertEquals(listOf(entry2, entry4), test.getById("group2"))
        println(test.getById("group2"))
        assertEquals(listOf(entry1), test.getById("group1"))
        println(test.getById("group1"))

    }


    data class TestEntity(val id: String, val group: String, val payload: String) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as TestEntity

            if (id != other.id) return false
            if (group != other.group) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id.hashCode()
            result = 31 * result + group.hashCode()
            return result
        }
    }

    class TestMapMapView : JournalMultiMapView<String, TestEntity>({ e: TestEntity -> e.group })
}