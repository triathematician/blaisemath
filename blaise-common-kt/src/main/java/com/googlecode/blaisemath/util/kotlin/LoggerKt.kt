/*-
 * #%L
 * blaise-common-kt
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
