package com.googlecode.blaisemath.style
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

import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.encode.*
import com.googlecode.blaisemath.util.kotlin.javaTrim
import com.googlecode.blaisemath.util.kotlin.warning
import java.awt.*
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import kotlin.reflect.KClass

/**
 * Encode/decodes [AttributeSet]s as strings. The key-value pairs in the attribute set are encoded using colons and
 * semicolons as separators, e.g. "a:2; b:3".
 *
 * The string is intended to be compatible with html/css, but some features of the [AttributeSet] cannot be coded this way,
 * so the operation is not invertible. Conversion *to* a string uses the following rules:
 *
 * - The attribute name is not used for conversion.
 * - Only values of type Number, String, Color, Marker, and Anchor are supported.
 * - Number, String, and Anchor values are converted in the obvious way.
 * - Colors are converted to #RRGGBB or #AARRGGBB notation, using [Colors.encode].
 * - Marker values are persisted using their class name.
 * - Null values are converted to the string "none".
 *
 * Conversion *from* a string uses the following rules:
 *
 *  - The attribute name is not used for conversion.
 *  - If the value matches #RGB, #RRGGBB, or #AARRGGBB it is converted to a color.
 *  - A string value "none" is converted to a null value.
 *  - If a value can be parsed as an integer or double, it is converted to that type.
 *  - Otherwise, values are left as strings.
 *
 * Values of type [Marker] and [Anchor] are deserialized as strings rather than their previous type. Blaise supports
 * having string values for these attributes wherever they are used.
 *
 * @author Elisha Peterson
 */
class AttributeSetCoder(
        /** Optional fixed types for attributes by name. */
        val types: Map<String, Class<*>> = mapOf(),
) : StringEncoder<AttributeSet>, StringDecoder<AttributeSet> {

    override fun encode(style: AttributeSet) = mutableMapOf<String, String?>().apply {
        style.attributes.forEach {
            putOrLog(it, style[it])
        }
    }.joinToAttributeString()

    override fun decode(str: String) = AttributeSet().apply {
        str.splitToKeyValues().forEach { (key, value) ->
            put(key, when {
                value == NULL_STRING -> null
                types.containsKey(key) -> decodeValue(value, types[key]!!)
                else -> decodeValue(value)
            })
        }
    }

    companion object {

        //region STRING CODING UTILS

        /** String used to represent null explicitly. */
        const val NULL_STRING = "none"

        /** Converts key-value list to an attribute string. */
        private fun Map<String, String?>.joinToAttributeString() = map { "${it.key}:${it.value ?: NULL_STRING}" }.joinToString("; ")
        /** Splits string using semicolon separator. */
        private fun String.splitToAttributes() = split(";").map { it.javaTrim() }.filter { !it.isBlank() }
        /** Splits entry using colon separator, with error-handling. */
        private fun String.splitEntry(): Pair<String, String> = split(":").map { it.javaTrim() }.let {
            when (it.size) {
                0 -> throw IllegalStateException("Not possible")
                1 -> it[0] to NULL_STRING
                2 -> it[0] to it[1]
                else -> {
                    warning<AttributeSetCoder>("Invalid attribute string with too many values $it")
                    it[0] to it[1]
                }
            }
        }
        /** Splits an attribute string to key-value pairs, the inverse of [joinToAttributeString]. */
        private fun String.splitToKeyValues() = splitToAttributes().map { it.splitEntry() }

        /** Attempt to convert given value to a string and add to target map  */
        private fun MutableMap<String, String?>.putOrLog(key: String, value: Any?) {
            try {
                this[key] = encodeValue(value)
            } catch (x: UnsupportedOperationException) {
                warning<AttributeSetCoder>("Cannot convert value $value to string.", x)
            }
        }

        //endregion

        //region GENERIC VALUE ENCODE/DECODE

        /** Convert an arbitrary value to a string. */
        fun encodeValue(value: Any?): String? {
            return try {
                when {
                    value == null -> NULL_STRING
                    CODERS.containsKey(value.javaClass) -> CODERS[value.javaClass]!!.invoke(value)
                    else -> {
                        CODERS.forEach { (type, coder) ->
                            if (type.isAssignableFrom(value.javaClass)) return coder.invoke(value)
                        }
                        value.toString() + ""
                    }
                }
            } catch (x: Exception) {
                warning<AttributeSetCoder>("Unable to convert $value", x)
                null
            }
        }

        /** Decodes a value with unspecified type. */
        fun decodeValue(value: String) = decodeValue(value, Any::class.java)

        /** Decodes a string value as a target type, or null if unable to decode. */
        fun <X> decodeValue(value: String, type: Class<X>): X? {
            val trim = value.javaTrim()
            try {
                when {
                    NULL_STRING == value -> return null
                    DECODERS.containsKey(type) -> return trim.decodeAs(type)
                    trim.matches("#[0-9a-fA-f]{3}".toRegex()) || trim.matches("#[0-9a-fA-f]{6}".toRegex())
                            || trim.matches("#[0-9a-fA-f]{8}".toRegex()) -> return trim.decodeAs(Color::class) as X?
                    trim.matches("\\((.*),(.*)\\)".toRegex()) && trim.contains(".") -> return trim.decodeAs(Point2D::class) as X?
                    trim.matches("\\((.*),(.*)\\)".toRegex()) -> return trim.decodeAs(Point::class) as X?
                    trim.matches("rectangle\\((.*)\\)".toRegex()) -> return trim.decodeAs(Rectangle::class) as X?
                    trim.matches("rectangle2d\\((.*)\\)".toRegex()) -> return trim.decodeAs(Rectangle2D::class) as X?
                    else -> {
                        trim.toIntOrNull()?.let { if (type.isInstance(it)) return it as X }
                        trim.toDoubleOrNull()?.let { if (type.isInstance(it)) return it as X }
                        if (type.isInstance(value)) return value as X
                    }
                }
            } catch (x: Exception) {
                warning<AttributeSetCoder>("Unable to decode $value as $type", x)
                return null
            }
            warning<AttributeSetCoder>("Unable to decode $value as $type")
            return null
        }

        //endregion

        //region TYPE CODING UTILS

        /** Encoders for specific types. Listed in order of type checks for encoding.  */
        private val CODERS = listOf(
            coder(ColorCoder), coder(FontCoder), coder(InsetsCoder),
            coder(PointCoder), coder(Point2DCoder), coder(RectangleCoder), coder(Rectangle2DCoder),
            coder(MarkerCoder)
        ).map { it.type to it }.toMap()

        /** Decoders for specific types. Listed in order of type checks for decoding.  */
        private val DECODERS = listOf(
            decoder { it.toInt() }, decoder { it.toFloat() }, decoder { it.toDouble() }, decoder { it.toBoolean() },
            decoder { Anchor.valueOf(it) }, decoder { it },
            decoder(ColorCoder), decoder(FontCoder), decoder(InsetsCoder),
            decoder(PointCoder), decoder(Point2DCoder), decoder(RectangleCoder), decoder(Rectangle2DCoder)
        ).map { it.type to it }.toMap()

        /** Decodes as given type. Assumes an entry in [DECODERS]. */
        private fun <V: Any> String.decodeAs(type: KClass<V>) = decodeAs(type.java)
        /** Decodes as given type. Assumes an entry in [DECODERS]. */
        private fun <V> String.decodeAs(type: Class<V>) = DECODERS[type]!!.invoke(this) as V?
        /** Decodes as given type. Assumes an entry in [DECODERS]. */
        private inline fun <reified V> decodeAs(str: String) = DECODERS[V::class.java]!!.invoke(str) as V

        private inline fun <reified V> coder(noinline coder: (V) -> String) = CoderBox(V::class.java, coder)
        private inline fun <reified V> coder(coder: StringCoder<V>) = CoderBox(V::class.java) { coder.encode(it) }
        private inline fun <reified V> decoder(noinline decoder: (String) -> V) = DecoderBox(V::class.java, decoder)
        private inline fun <reified V> decoder(decoder: StringDecoder<V>) = DecoderBox(V::class.java) { decoder.decode(it) }

        /** Stores type and a coder. */
        private class CoderBox<V>(val type: Class<out V>, val coder: (V) -> String): (Any) -> String {
            override fun invoke(p1: Any) = coder(p1 as V)
        }
        /** Stores type and a decoder. */
        private class DecoderBox<V>(val type: Class<out V>, val decoder: (String) -> V): (String) -> V {
            override fun invoke(p1: String) = decoder(p1)
        }

        //endregion
    }

}