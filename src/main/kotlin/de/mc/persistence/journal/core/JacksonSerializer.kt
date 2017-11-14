package de.mc.persistence.journal.core

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * @author Ralf Ulrich
 * 28.02.17
 */
@Component
@Scope("prototype")
open class JacksonSerializer : Serializer {
    private val IMPLEMENTATION_IDENTIFIER = 1
    private val objectMapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())

    override fun manifest(obj: Any): String {
        return obj.javaClass.name
    }

    override fun fromBinary(data: ByteArray, type: String): Any {
        return objectMapper.readValue(data, Class.forName(type))
    }


    override fun toBinary(value: Any): ByteArray {
        return objectMapper.writeValueAsBytes(value)
    }

    override fun identifier(): Int {
        return IMPLEMENTATION_IDENTIFIER
    }
}