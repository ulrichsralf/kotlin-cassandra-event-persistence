package de.mc.persistence.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Ralf Ulrich
 * 03.03.17
 */


fun Any.getLogger(): Logger {
    return LoggerFactory.getLogger(javaClass)
}
