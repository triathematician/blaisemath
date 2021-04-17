package com.googlecode.blaisemath.util.kotlin

import java.util.logging.Level
import java.util.logging.Logger

/** Prints fine log. */
inline fun <reified X> fine(message: String, exception: Throwable? = null) = logger<X>().log(Level.FINE, message, exception)
/** Prints info log. */
inline fun <reified X> info(message: String, exception: Throwable? = null) = logger<X>().log(Level.INFO, message, exception)
/** Prints warning log. */
inline fun <reified X> warning(message: String, exception: Throwable? = null) = logger<X>().log(Level.WARNING, message, exception)
/** Prints severe log. */
inline fun <reified X> severe(message: String, exception: Throwable? = null) = logger<X>().log(Level.SEVERE, message, exception)

/** Gets logger for class. */
inline fun <reified X> logger() = Logger.getLogger(X::class.java.name)