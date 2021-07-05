package com.googlecode.blaisemath.encode

import com.google.common.collect.ImmutableMap
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.internal.Reflection
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Color
import java.awt.Point
import java.awt.geom.Point2D
import java.lang.reflect.InvocationTargetException
import java.util.*
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
 * Converts values from one type to another.
 * @author Elisha Peterson
 */
object TypeConverter {
    private val LOG = Logger.getLogger(TypeConverter::class.java.name)

    /** Functions for decoding specific types  */
    private val TYPE_DECODERS: MutableMap<Class<*>?, Function<String?, *>?>? = ImmutableMap.builder<Class<*>?, Function<String?, *>?>()
            .put(Color::class.java, Function<String?, Any?> { obj: String? -> Colors.decode() })
            .put(Point::class.java, Function<String?, Any?> { v: String? -> PointCoder().decode(v) })
            .put(Point2D::class.java, Function<String?, Any?> { v: String? -> Point2DCoder().decode(v) })
            .build()

    /**
     * Convert value to target type, if possible. Returns a default value if the
     * input is null or cannot be converted to the target type.
     * @param <X> target type
     * @param value value to convert
     * @param targetType target type
     * @param def default value to return if value is null, or unable to convert
     * @return converted value
    </X> */
    fun <X> convert(value: Any?, targetType: Class<X?>?, def: X?): X? {
        try {
            if (value == null) {
                return def
            } else if (targetType.isInstance(value)) {
                return value as X?
            } else if (targetType == String::class.java) {
                return Objects.toString(value) as X
            } else if (value is String) {
                return convertFromString(value as String?, targetType, def)
            } else if (Number::class.java.isAssignableFrom(targetType)) {
                return convertToNumber<Number?>(value, targetType as Class<*>?, def as Number?) as X?
            }
            throw UnsupportedOperationException()
        } catch (x: UnsupportedOperationException) {
            LOG.log(Level.WARNING, "Unable to convert {0} to {1}", arrayOf(value, targetType))
        }
        return def
    }

    /**
     * Convert value from a string to a target type.
     * @param <X> target type
     * @param value value to convert
     * @param targetType target type
     * @param def default value to return if unable to convert
     * @return converted value
     * @throws UnsupportedOperationException if unable to convert
    </X> */
    fun <X> convertFromString(value: String?, targetType: Class<X?>?, def: X?): X? {
        if (value == null) {
            return def
        } else if (TYPE_DECODERS.containsKey(targetType)) {
            return TYPE_DECODERS.get(targetType).apply(value) as X
        }
        val decoder = Reflection.staticMethod(targetType, arrayOf<String?>("valueOf", "decode"), String::class.java)
        if (decoder.isPresent) {
            try {
                return decoder.get().invoke(null, value) as X
            } catch (ex: IllegalAccessException) {
                LOG.log(Level.WARNING, "Failed to invoke factory method " + decoder.get(), ex)
            } catch (ex: IllegalArgumentException) {
                LOG.log(Level.WARNING, "Failed to invoke factory method " + decoder.get(), ex)
            } catch (ex: InvocationTargetException) {
                LOG.log(Level.WARNING, "Failed to invoke factory method " + decoder.get(), ex)
            } catch (ex: ClassCastException) {
                LOG.log(Level.WARNING, "Failed to invoke factory method " + decoder.get(), ex)
            }
        }
        val con = Reflection.constructor(targetType, String::class.java)
        if (con.isPresent) {
            try {
                return con.get().newInstance(value) as X
            } catch (ex: InstantiationException) {
                LOG.log(Level.WARNING, "Failed to invoke constructor " + con.get(), ex)
            } catch (ex: IllegalAccessException) {
                LOG.log(Level.WARNING, "Failed to invoke constructor " + con.get(), ex)
            } catch (ex: IllegalArgumentException) {
                LOG.log(Level.WARNING, "Failed to invoke constructor " + con.get(), ex)
            } catch (ex: InvocationTargetException) {
                LOG.log(Level.WARNING, "Failed to invoke constructor " + con.get(), ex)
            }
        }
        throw UnsupportedOperationException("Cannot construct instance of $targetType from a string.")
    }
    //region NUMBERS
    /**
     * Convert value to target numeric type, if possible. Returns a default value if unable
     * to convert. If the input value is null, always returns null.
     * @param <X> target type
     * @param value value to convert
     * @param targetType target type
     * @param def default value to return if unable to convert
     * @return converted value
     * @throws UnsupportedOperationException if unable to convert
    </X> */
    fun <X : Number?> convertToNumber(value: Any?, targetType: Class<X?>?, def: X?): X? {
        if (value == null) {
            return def
        }
        return if (value is Number) {
            numericValue(value as Number?, targetType)
        } else {
            throw UnsupportedOperationException()
        }
    }

    /**
     * Convert number to a given target type.
     * @param <X> target type
     * @param n number
     * @param targetType target type
     * @return converted number
    </X> */
    fun <X : Number?> numericValue(n: Number?, targetType: Class<X?>?): X? {
        Objects.requireNonNull(n)
        return if (targetType == Byte::class.java) {
            n.toByte() as Byte as X
        } else if (targetType == Double::class.java) {
            n.toDouble() as Double as X
        } else if (targetType == Float::class.java) {
            n.toFloat() as Float as X
        } else if (targetType == Int::class.java) {
            n.toInt() as Int as X
        } else if (targetType == Long::class.java) {
            n.toLong() as Long as X
        } else if (targetType == Short::class.java) {
            n.toShort() as Short as X
        } else {
            throw UnsupportedOperationException("Unsupported number: $n")
        }
    } //endregion
}