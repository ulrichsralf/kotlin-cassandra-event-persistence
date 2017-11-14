package de.mc.persistence.journal.core

/**
 * @author Ralf Ulrich
 * 28.02.17
 */
interface Serializer {

    fun manifest(obj: Any): String

    fun fromBinary(data: ByteArray, type: String): Any

    fun toBinary(value: Any): ByteArray

    fun identifier(): Int
}