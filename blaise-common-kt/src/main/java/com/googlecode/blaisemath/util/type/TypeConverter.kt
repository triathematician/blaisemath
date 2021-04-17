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
package com.googlecode.blaisemath.util.type

import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.encode.Point2DCoder
import com.googlecode.blaisemath.util.encode.PointCoder
import com.googlecode.blaisemath.util.kotlin.warning
import com.googlecode.blaisemath.util.kotlin.severe
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass

/**
 * Converts values from one type to another.
 */
object TypeConverter {

    /** Functions for decoding specific types  */
    private val TYPE_DECODERS: Map<Class<out Any>, (String) -> Any?> = mapOf(
            java.awt.Color::class.java to { Colors.decode(it) },
            java.awt.Point::class.java to { PointCoder.decode(it) },
            java.awt.geom.Point2D::class.java to { Point2DCoder.decode(it) })

    /**
     * Convert value to target type, if possible. Returns a default value if the input is null or cannot be converted to the target type.
     * @param <X> target type
     * @throws UnsupportedOperationException if unable to convert
     */
    inline fun <reified X> convert(value: Any?) = convert(value, X::class.java)
    /** Convert a general number to a specified numeric type. */
    inline fun <reified X : Number> numericValue(n: Number): X = numericValue(n, X::class.java)

    /**
     * Convert value to target type, if possible. Return null if input is null or cannot be converted to the target type.
     * @param <X> target type
     * @throws UnsupportedOperationException if unable to convert
     */
    fun <X> convert(value: Any?, targetType: Class<X>): X? {
        try {
            return when {
                value == null -> null
                targetType.isInstance(value) -> value as X
                targetType == String::class.java -> value.toString() as X
                value is Number && Number::class.java.isAssignableFrom(targetType) -> numericValue(value, targetType as Class<out Number>) as X
                value is String -> convertFromString(value, targetType)
                else -> throw UnsupportedOperationException()
            }
        } catch (x: UnsupportedOperationException) {
            warning<TypeConverter>("Unable to convert $value to $targetType", x)
        }
        return null
    }

    /**
     * Convert value from a string to a target type, returning a default value if unable to convert.
     * @throws UnsupportedOperationException if unable to convert
     */
    fun <X> convertFromString(value: String?, targetType: Class<X>): X? {
        if (value == null) {
            return null
        } else if (TYPE_DECODERS.containsKey(targetType)) {
            return TYPE_DECODERS[targetType]!!.invoke(value) as X
        }

        // attempt to convert from a static method with string argument
        ReflectionUtils.findStaticMethod(targetType, arrayOf("valueOf", "decode"), String::class.java)?.let {
            try {
                return it.invoke(null, value) as X
            } catch (ex: IllegalAccessException) {
                severe<TypeConverter>("Failed to invoke factory method $it", ex)
            } catch (ex: IllegalArgumentException) {
                severe<TypeConverter>("Failed to invoke factory method $it", ex)
            } catch (ex: InvocationTargetException) {
                severe<TypeConverter>("Failed to invoke factory method $it", ex)
            } catch (ex: ClassCastException) {
                severe<TypeConverter>("Failed to invoke factory method $it", ex)
            }
        }

        // attempt to convert from a constructor with a string argument
        ReflectionUtils.findConstructor(targetType, String::class.java)?.let {
            try {
                return it.newInstance(value) as X
            } catch (ex: InstantiationException) {
                severe<TypeConverter>("Failed to invoke constructor $it", ex)
            } catch (ex: IllegalAccessException) {
                severe<TypeConverter>("Failed to invoke constructor $it", ex)
            } catch (ex: IllegalArgumentException) {
                severe<TypeConverter>("Failed to invoke constructor $it", ex)
            } catch (ex: InvocationTargetException) {
                severe<TypeConverter>("Failed to invoke constructor $it", ex)
            }
        }

        throw UnsupportedOperationException("Cannot construct instance of $targetType from a string.")
    }

    //region NUMBERS

    /** Convert a general number to a specified numeric type. */
    fun <X: Number> numericValue(n: Number, target: Class<X>): X = when (target) {
        Byte::class.java -> n.toByte() as X
        Short::class.java -> n.toShort() as X
        Int::class.java -> n.toInt() as X
        Long::class.java -> n.toLong() as X
        Float::class.java -> n.toFloat() as X
        Double::class.java -> n.toDouble() as X
        java.lang.Byte::class.java -> n.toByte() as X
        java.lang.Short::class.java -> n.toShort() as X
        java.lang.Integer::class.java -> n.toInt() as X
        java.lang.Long::class.java -> n.toLong() as X
        java.lang.Float::class.java -> n.toFloat() as X
        java.lang.Double::class.java -> n.toDouble() as X
        else -> throw UnsupportedOperationException("Unsupported number: $n cannot be converted to type $target")
    }

    //endregion
}