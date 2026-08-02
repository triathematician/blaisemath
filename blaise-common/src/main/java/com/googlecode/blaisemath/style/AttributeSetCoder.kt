package com.googlecode.blaisemath.style

import com.google.common.base.Joiner
import com.google.common.base.Splitter
import com.googlecode.blaisemath.encode.*
import com.googlecode.blaisemath.primitive.Anchor
import com.googlecode.blaisemath.primitive.Marker
import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.kotlin.warning
import java.awt.*
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.util.*

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

/**
 * Encode/decodes [AttributeSet]s as strings. The key-value pairs in the
 * attribute set are encoded using colons and semicolons as separators, e.g. "a:2; b:3".

 * The string is intended to be compatible with html/css, but some features of the `AttributeSet` cannot
 * be encoded this way, so the operation is not invertible. Conversion *to* a string uses the following rules:
 *  - The attribute name is not used for conversion.
 *  - Only values of type Number, String, Color, Marker, and Anchor are supported.
 *  - Number, String, and Anchor values are converted in the obvious way.
 *  - Colors are converted to #RRGGBB or #AARRGGBB notation, using [Colors.encode].
 *  - Marker values are persisted using their class name.
 *  - Null values are converted to the string "none".
 *
 *  Conversion *from* a string uses the following rules:
 *  - The attribute name is not used for conversion.
 *  - If the value matches #RGB, #RRGGBB, or #AARRGGBB it is converted to a color.
 *  - A string value "none" is converted to a null value.
 *  - If a value can be parsed as an integer or double, it is converted to that type.
 *  - Otherwise, values are left as strings.
 *
 * Note that values of type Marker and Anchor are deserialized as strings rather
 * than their previous type. Blaise supports having string values for these attributes
 * wherever they are used.
 */
class AttributeSetCoder @JvmOverloads constructor(_types: Map<String, Class<*>>? = null) : StringEncoder<AttributeSet>, StringDecoder<AttributeSet> {

    /** Used in deserialization for custom type mapping  */
    private val types: Map<String, Class<*>> = _types ?: mapOf()

    override fun encode(style: AttributeSet): String {
        val props = TreeMap<String, String?>()
        style.attributes.forEach { props.tryPut(it, style[it]) }
        return CODER_JOINER.join(props)
    }

    override fun decode(s: String): AttributeSet {
        // perform two separate splits instead of using MapSplitter to allow for duplicate keys
        val res = AttributeSet()
        val pairs = DECODER_PAIR_SPLITTER.splitToList(s)
        for (p in pairs) {
            val kv = DECODER_KEY_SPLITTER.splitToList(p)
            if (kv.size != 2) {
                warning<AttributeSetCoder>("Invalid attribute string: $s")
                return res
            }
            val key = kv[0]
            val str = kv[1]
            val value = when {
                NULL_STRING == str -> null
                types.containsKey(key) -> decodeValue(str, types[key]!!)
                else -> decodeValue(str, Any::class.java)
            }
            res.put(key, value)
        }
        return res
    }

    companion object {

        //region CONFIGS

        /** String used to represent null explicitly.  */
        private val NULL_STRING: String? = "none"

        /** Joins values into the result string  */
        private val CODER_JOINER = Joiner.on("; ")
                .withKeyValueSeparator(":").useForNull(NULL_STRING)

        /** Functions used to encode specific types. Listed in order of type checks for encoding.  */
        private val CODERS = CoderMap().apply {
            put(Color::class.java, Colors::encode)
            put(Font::class.java) { it: Font -> FontCoder.encode(it) }
            put(Insets::class.java) { it: Insets -> InsetsCoder.encode(it) }
            put(Point::class.java) { it: Point -> PointCoder.encode(it) }
            put(Point2D::class.java) { it: Point2D -> Point2DCoder.encode(it) }
            put(Rectangle::class.java) { it: Rectangle -> RectangleCoder.encode(it) }
            put(Rectangle2D::class.java) { it: Rectangle2D -> Rectangle2DCoder.encode(it) }
            put(Marker::class.java) { it: Marker -> it.javaClass.simpleName }
        }

        /** Splits key-value pairs in a string to decode  */
        private val DECODER_PAIR_SPLITTER = Splitter.on(";")
                .omitEmptyStrings().trimResults()

        /** Splits key from value  */
        private val DECODER_KEY_SPLITTER = Splitter.on(":")
                .omitEmptyStrings().trimResults()

        /** Functions used to encode specific types. Listed in order of type checks for decoding.  */
        private val DECODERS = DecoderMap().apply {
            put(Int::class.java) { it.toIntOrNull() }
            put(Float::class.java) { it.toFloatOrNull() }
            put(Double::class.java) { it.toDoubleOrNull() }
            put(Boolean::class.java) { it.toBoolean() }
            put(Anchor::class.java) { Anchor.valueOf(it) }
            put(String::class.java) { it }
            put(Color::class.java) { Colors.decode(it) }
            put(Font::class.java) { Font.decode(it) }
            put(Insets::class.java) { InsetsCoder.decode(it) }
            put(Point::class.java) { PointCoder.decode(it) }
            put(Point2D::class.java) { Point2DCoder.decode(it) }
            put(Rectangle::class.java) { RectangleCoder.decode(it) }
            put(Rectangle2D::class.java) { Rectangle2DCoder.decode(it) }
        }

        //region VALUE CONVERSION UTILS

        /** Converts value from one type to another, with optional default. */
        inline fun <reified X> convertValue(value: Any?, def: X?): X? = convertValue<X>(value, X::class.java, def)

        /**
         * Converts value from one type to another, with optional default.
         * @param value value to convert
         * @param targetType target type
         * @param def default value
         * @return value of target type if possible, else default; may return null if def is null
         */
        fun <X> convertValue(value: Any?, targetType: Class<X>, def: X?): X? {
            return try {
                if ("null" == value) {
                    null
                } else TypeConverter.convert(value, targetType) ?: def
            } catch (x: IllegalArgumentException) {
                warning<AttributeSetCoder>("Unable to convert $value to $targetType")
                null
            } catch (x: UnsupportedOperationException) {
                warning<AttributeSetCoder>("Unable to convert $value to $targetType")
                null
            }
        }

        //endregion

        //region ENCODE UTILS

        /** Attempt to convert given value to a string and add to target map  */
        private fun MutableMap<String, String?>.tryPut(key: String, value: Any?) {
            try {
                this[key] = encodeValue(value)
            } catch (x: UnsupportedOperationException) {
                warning<AttributeSetCoder>("Cannot convert value $value to string.", x)
            }
        }

        /**
         * Convert a value to a standardized string representation for use in [AttributeSet] strings.
         * Returns null if there is an error.
         */
        fun encodeValue(value: Any?): String? {
            return try {
                when {
                    value == null -> return NULL_STRING
                    CODERS.containsKey(value.javaClass) -> return CODERS[value.javaClass]!!.invoke(value)
                    else -> {
                        CODERS.keys.find { it.isAssignableFrom(value.javaClass) }
                                ?.let { return CODERS[it]!!.invoke(value) }
                    }
                }
                value.toString() + ""
            } catch (x: Exception) {
                warning<AttributeSetCoder>("Unable to convert $value", x)
                null
            }
        }

        //endregion

        //region DECODE UTILS

        /** Decodes a string value to the given type, if possible, returning null if unable to decode. */
        fun <X> decodeValue(value: String, type: Class<X>): X? {
            val trim = value.trim { it <= ' ' }
            try {
                when {
                    NULL_STRING == value -> return null
                    type.isInstance(value) -> return value as X?
                    DECODERS.containsKey(type) -> return DECODERS[type]!!.invoke(trim) as X?
                    ColorCoder.decodable(trim) -> return DECODERS[Color::class.java]!!.invoke(trim) as X?
                    trim.matches("\\((.*),(.*)\\)".toRegex()) && trim.contains(".") -> return DECODERS[Point2D::class.java]!!.invoke(trim) as X?
                    trim.matches("\\((.*),(.*)\\)".toRegex()) -> return DECODERS[Point::class.java]!!.invoke(trim) as X?
                    trim.matches("rectangle\\((.*)\\)".toRegex()) -> return DECODERS[Rectangle::class.java]!!.invoke(trim) as X?
                    trim.matches("rectangle2d\\((.*)\\)".toRegex()) -> return DECODERS[Rectangle2D::class.java]!!.invoke(trim) as X?
                    else -> {
                        trim.toIntOrNull()?.let { if (type.isInstance(it)) return it as X }
                        trim.toDoubleOrNull()?.let { if (type.isInstance(it)) return it as X }
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

        /** Utility type for storing coders  */
        internal class CoderMap : LinkedHashMap<Class<*>, (Any?) -> String>() {
            fun <X> put(type: Class<X>, toStr: (X) -> String) {
                super.put(type, toStr as (Any?) -> String)
            }
        }

        /** Utility type for storing decoders  */
        internal class DecoderMap : LinkedHashMap<Class<*>, (String) -> Any?>() {
            fun <X> put(type: Class<X>, fromStr: (String) -> X?) {
                super.put(type, fromStr)
            }

            fun <X> apply(type: Class<X>, key: String): X? {
                return get(type)?.invoke(key) as? X
            }
        }
    }
}