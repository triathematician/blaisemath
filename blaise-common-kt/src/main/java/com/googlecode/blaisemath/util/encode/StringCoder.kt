package com.googlecode.blaisemath.util.encode

/*-
* #%L
* blaise-common
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

/** Converts an object to/from a string. */
interface StringCoder<X> : StringEncoder<X>, StringDecoder<X>

/**
 * Converts an object to a string, preferably in a way that can be also decoded.
 * @param <X> type of object to converter
 */
interface StringEncoder<X> {
    fun encode(obj: X): String
}

/**
 * Converts an object from a string if possible.
 * @param <X> type of object to converter
 */
interface StringDecoder<X> {
    fun decode(str: String): X?
}