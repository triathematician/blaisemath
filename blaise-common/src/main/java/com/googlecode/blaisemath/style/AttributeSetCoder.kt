package com.googlecode.blaisemath.style

import com.google.common.base.Joiner
import com.google.common.base.Splitter
import com.google.common.collect.Maps
import com.google.common.primitives.Doubles
import com.google.common.primitives.Ints
import com.googlecode.blaisemath.encode.*
import com.googlecode.blaisemath.primitive.Anchor
import com.googlecode.blaisemath.primitive.Marker
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.*
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.logging.Level
import java.util.logging.Logger

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
*/ /**
 * Encode/decodes [AttributeSet]s as strings. The key-value pairs in the
 * attribute set are encoded using colons and semicolons as separators, e.g. "a:2; b:3".
 *
 *
 *  The string is intended to be
 * compatible with html/css, but some features of the `AttributeSet` cannot
 * be encoded this way, so the operation is not invertible. Conversion *to* a
 * string uses the following rules:
 *
 *
 *  * The attribute name is not used for conversion.
 *  * Only values of type Number, String, Color, Marker, and Anchor are supported.
 *  * Number, String, and Anchor values are converted in the obvious way.
 *  * Colors are converted to #RRGGBB or #AARRGGBB notation, using [Colors.encode].
 *  * Marker values are persisted using their class name.
 *  * Null values are converted to the string "none".
 *
 *
 *
 *  Conversion *from* a string uses the following rules:
 *
 *
 *  * The attribute name is not used for conversion.
 *  * If the value matches #RGB, #RRGGBB, or #AARRGGBB it is converted to a color.
 *  * A string value "none" is converted to a null value.
 *  * If a value can be parsed as an integer or double, it is converted to that type.
 *  * Otherwise, values are left as strings.
 *
 *
 *
 *  Note that values of type Marker and Anchor are deserialized as strings rather
 * than their previous type. Blaise supports having string values for these attributes
 * wherever they are used.
 *
 * @author Elisha Peterson
 */
class AttributeSetCoder @JvmOverloads constructor(types: MutableMap<String?, Class<*>?>? = null) : StringEncoder<AttributeSet?>, StringDecoder<AttributeSet?> {
    //endregion
    /** Used in deserialization for custom type mapping  */
    private val types: MutableMap<String?, Class<*>?>?
    override fun encode(style: AttributeSet?): String? {
        Objects.requireNonNull(style)
        val props: MutableMap<String?, String?>? = Maps.newTreeMap()
        style.getAttributes().forEach(Consumer { s: String? -> tryPut(props, s, style.get(s)) })
        return CODER_JOINER.join(props)
    }

    override fun decode(s: String?): AttributeSet? {
        Objects.requireNonNull(s)

        // perform two separate splits instead of using MapSplitter to allow for duplicate keys
        val res = AttributeSet()
        val pairs = DECODER_PAIR_SPLITTER.splitToList(s)
        for (p in pairs) {
            val kv = DECODER_KEY_SPLITTER.splitToList(p)
            if (kv.size != 2) {
                LOG.log(Level.WARNING, "Invalid attribute string: {0}", s)
                return res
            }
            val key = kv[0]
            val str = kv[1]
            val `val` = if (NULL_STRING == str) null else if (types.containsKey(key)) decodeValue(str, types.get(key)) else decodeValue<Any?>(str, Any::class.java)
            res.put(key, `val`)
        }
        return res
    }
    //endregion
    /** Utility type for storing coders  */
    private class CoderMap : LinkedHashMap<Class<*>?, Function<*, *>?>() {
        private fun <X> put(type: Class<X?>?, toStr: Function<X?, String?>?): CoderMap? {
            super.put(type, toStr)
            return this
        }
    }

    /** Utility type for storing decoders  */
    private class DecoderMap : LinkedHashMap<Class<*>?, Function<*, *>?>() {
        private fun <X> put(type: Class<X?>?, fromStr: Function<String?, X?>?): DecoderMap? {
            super.put(type, fromStr)
            return this
        }

        private fun <X> apply(type: Class<X?>?, key: String?): X? {
            return get(type).apply(key) as X
        }
    }

    companion object {
        private val LOG = Logger.getLogger(AttributeSetCoder::class.java.name)
        //region CONFIGS
        /** String used to represent null explicitly.  */
        private val NULL_STRING: String? = "none"

        /** Joins values into the result string  */
        private val CODER_JOINER = Joiner.on("; ")
                .withKeyValueSeparator(":").useForNull(NULL_STRING)

        /** Functions used to encode specific types. Listed in order of type checks for encoding.  */
        private val CODERS = CoderMap()
                .put<Color?>(Color::class.java, Function { obj: Color? -> Colors.encode() })
                .put<Font?>(Font::class.java, Function { c: Font? -> FontCoder().encode(c) })
                .put<Insets?>(Insets::class.java, Function { v: Insets? -> InsetsCoder().encode(v) })
                .put<Point?>(Point::class.java, Function { v: Point? -> PointCoder().encode(v) })
                .put<Point2D?>(Point2D::class.java, Function { v: Point2D? -> Point2DCoder().encode(v) })
                .put<Rectangle?>(Rectangle::class.java, Function { v: Rectangle? -> RectangleCoder().encode(v) })
                .put<Rectangle2D?>(Rectangle2D::class.java, Function { v: Rectangle2D? -> Rectangle2DCoder().encode(v) })
                .put<Marker?>(Marker::class.java, Function { v: Marker? -> v.javaClass.simpleName })

        /** Splits key-value pairs in a string to decode  */
        private val DECODER_PAIR_SPLITTER = Splitter.on(";")
                .omitEmptyStrings().trimResults()

        /** Splits key from value  */
        private val DECODER_KEY_SPLITTER = Splitter.on(":")
                .omitEmptyStrings().trimResults()

        /** Functions used to encode specific types. Listed in order of type checks for decoding.  */
        private val DECODERS = DecoderMap()
                .put<Int?>(Int::class.java, Function { s: String? -> Integer.valueOf(s) })
                .put<Float?>(Float::class.java, Function { s: String? -> java.lang.Float.valueOf(s) })
                .put<Double?>(Double::class.java, Function { s: String? -> java.lang.Double.valueOf(s) })
                .put<Boolean?>(Boolean::class.java, Function { s: String? -> java.lang.Boolean.valueOf(s) })
                .put<Anchor?>(Anchor::class.java, Function { name: String? -> Anchor.valueOf(name) })
                .put<String?>(String::class.java, Function { s: String? -> s })
                .put<Color?>(Color::class.java, Function { obj: String? -> Colors.decode() })
                .put<Font?>(Font::class.java, Function { str: String? -> Font.decode(str) })
                .put<Insets?>(Insets::class.java, Function { v: String? -> InsetsCoder().decode(v) })
                .put<Point?>(Point::class.java, Function { v: String? -> PointCoder().decode(v) })
                .put<Point2D?>(Point2D::class.java, Function { v: String? -> Point2DCoder().decode(v) })
                .put<Rectangle?>(Rectangle::class.java, Function { v: String? -> RectangleCoder().decode(v) })
                .put<Rectangle2D?>(Rectangle2D::class.java, Function { v: String? -> Rectangle2DCoder().decode(v) })
        //region VALUE CONVERSION UTILS
        /**
         * Converts value from one type to another, with optional default.
         * @param value value to convert
         * @param targetType target type
         * @param def default value
         * @return value of target type if possible, else default; may return null if def is null
         */
        fun <X> convertValue(value: Any?, targetType: Class<X?>?, def: X?): X? {
            return try {
                if ("null" == value) {
                    null
                } else TypeConverter.convert(value, targetType, def)
            } catch (x: IllegalArgumentException) {
                LOG.log(Level.WARNING, "Unable to convert $value to $targetType")
                null
            } catch (x: UnsupportedOperationException) {
                LOG.log(Level.WARNING, "Unable to convert $value to $targetType")
                null
            }
        }
        //endregion
        //region ENCODE UTILS
        /** Attempt to convert given value to a string and add to target map  */
        private fun tryPut(props: MutableMap<String?, String?>?, key: String?, value: Any?) {
            try {
                props[key] = encodeValue(value)
            } catch (x: UnsupportedOperationException) {
                LOG.log(Level.WARNING, "Cannot convert value $value to string.", x)
            }
        }

        /**
         * Converts values to strings.
         * @param val value to encode
         * @return encoded value, or null if unable to encode
         */
        fun encodeValue(`val`: Any?): String? {
            return try {
                if (`val` == null) {
                    return NULL_STRING
                } else if (CODERS.containsKey(`val`.javaClass)) {
                    return CODERS.get(`val`.javaClass).apply(`val`) as String
                } else {
                    for (c in CODERS.keys) {
                        if (c.isAssignableFrom(`val`.javaClass)) {
                            return CODERS.get(c).apply(`val`) as String
                        }
                    }
                }
                `val`.toString() + ""
            } catch (x: Exception) {
                LOG.log(Level.WARNING, "Unable to convert $`val`", x)
                null
            }
        }
        //endregion
        //region DECODE UTILS
        /**
         * Decodes a string value as a target type.
         * @param <X> decoded type
         * @param val string value
         * @param type decoded type
         * @return decoded value, or null if unable to decode
        </X> */
        fun <X> decodeValue(`val`: String?, type: Class<X?>?): X? {
            Objects.requireNonNull(`val`)
            val trim = `val`.trim { it <= ' ' }
            try {
                if (NULL_STRING == `val`) {
                    return null
                } else if (DECODERS.containsKey(type)) {
                    return DECODERS.apply(type, `val`)
                } else if (ColorCoder.Companion.decodable(trim)) {
                    return DECODERS.apply<Color?>(Color::class.java, trim) as X?
                } else if (trim.matches("\\((.*),(.*)\\)") && trim.contains(".")) {
                    return DECODERS.apply<Point2D?>(Point2D::class.java, trim) as X?
                } else if (trim.matches("\\((.*),(.*)\\)")) {
                    return DECODERS.apply<Point?>(Point::class.java, trim) as X?
                } else if (trim.matches("rectangle\\((.*)\\)")) {
                    return DECODERS.apply<Rectangle?>(Rectangle::class.java, trim) as X?
                } else if (trim.matches("rectangle2d\\((.*)\\)")) {
                    return DECODERS.apply<Rectangle2D?>(Rectangle2D::class.java, trim) as X?
                }
                val i = Ints.tryParse(trim)
                if (type.isInstance(i)) {
                    return i as X?
                }
                val d = Doubles.tryParse(trim)
                if (type.isInstance(d)) {
                    return d as X?
                }
                if (type.isInstance(`val`)) {
                    return `val` as X?
                }
            } catch (x: Exception) {
                LOG.log(Level.WARNING, "Unable to decode $`val` as $type", x)
                return null
            }
            LOG.log(Level.WARNING, "Unable to decode {0} as {1}", arrayOf<Any?>(`val`, type))
            return null
        }
    }
    /**
     * Get coder instance where particular keys are associated with particular types,
     * which allows decoding operations to generate the correct types in more cases.
     * @param types types associated with keys
     */
    /**
     * Get default coder instance.
     */
    init {
        this.types = types ?: emptyMap<String?, Class<*>?>()
    }
}