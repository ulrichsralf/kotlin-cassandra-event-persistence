package de.mc.persistence

import de.mc.persistence.journal.core.JacksonSerializer
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

/**
 * @author Ralf Ulrich
 * 28.02.17
 */
class JacksonSerializerTest {

    data class TestClass(val testValue: LocalDateTime = LocalDateTime.now())

    val serializer = JacksonSerializer()

    @Test
    fun testSerialize() {
        val tester = TestClass()
        val bytes = serializer.toBinary(tester)
        println(String(bytes))
        val fromBinary = serializer.fromBinary(bytes, TestClass::class.java.name)
        assertEquals(tester, fromBinary)
    }


}