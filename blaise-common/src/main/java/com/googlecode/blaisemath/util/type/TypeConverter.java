package com.googlecode.blaisemath.util.type;

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

import com.google.common.collect.ImmutableMap;
import com.googlecode.blaisemath.util.Colors;
import com.googlecode.blaisemath.util.encode.Point2DCoder;
import com.googlecode.blaisemath.util.encode.PointCoder;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Converts values from one type to another.
 * @author Elisha Peterson
 */
public class TypeConverter {

    private static final Logger LOG = Logger.getLogger(TypeConverter.class.getName());

    /** Functions for decoding specific types */
    private static final Map<Class, Function<String, ?>> TYPE_DECODERS
            = ImmutableMap.<Class, Function<String, ?>>builder()
                    .put(Color.class, Colors::decode)
                    .put(Point.class, new PointCoder()::decode)
                    .put(Point2D.class, new Point2DCoder()::decode)
                    .build();
    
    /**
     * Convert value to target type, if possible. Returns a default value if the
     * input is null or cannot be converted to the target type.
     * @param <X> target type
     * @param value value to convert
     * @param targetType target type
     * @param def default value to return if value is null, or unable to convert
     * @return converted value
     * @throws UnsupportedOperationException if unable to convert
     */
    public static <X> @Nullable X convert(@Nullable Object value, Class<X> targetType,
            @Nullable X def) {
        try {
            if (value == null) {
                return def;
            } else if (targetType.isInstance(value)) {
                return (X) value;
            } else if (targetType == String.class) {
                return (X) Objects.toString(value);
            } else if (value instanceof String) {
                return convertFromString((String) value, targetType, def);
            } else if (Number.class.isAssignableFrom(targetType)) {
                return (X) convertToNumber(value, (Class) targetType, (Number) def);
            }
            throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException x) {
            LOG.log(Level.WARNING, "Unable to convert "+value+" to "+targetType, x);
        }
        return def;
    }
    
    /**
     * Convert value from a string to a target type.
     * @param <X> target type
     * @param value value to convert
     * @param targetType target type
     * @param def default value to return if unable to convert
     * @return converted value
     * @throws UnsupportedOperationException if unable to convert
     */
    public static <X> @Nullable X convertFromString(@Nullable String value, 
            Class<X> targetType, @Nullable X def) {
        if (value == null) {
            return def;
        } else if (TYPE_DECODERS.containsKey(targetType)) {
            return (X) TYPE_DECODERS.get(targetType).apply(value);
        }
        
        Optional<Method> decoder = ReflectionUtils.findStaticMethod(targetType, 
                new String[]{"valueOf", "decode"}, String.class);
        if (decoder.isPresent()) {
            try {
                return (X) decoder.get().invoke(null, value);
            } catch (IllegalAccessException | IllegalArgumentException 
                    | InvocationTargetException | ClassCastException ex) {
                LOG.log(Level.SEVERE, "Failed to invoke factory method "+decoder.get(), ex);
            }
        }
        Optional<Constructor> con = ReflectionUtils.findConstructor(targetType, String.class);
        if (con.isPresent()) {
            try {
                return (X) con.get().newInstance(value);
            } catch (InstantiationException | IllegalAccessException 
                    | IllegalArgumentException | InvocationTargetException ex) {
                LOG.log(Level.SEVERE, "Failed to invoke constructor "+con.get(), ex);
            }
        }
        throw new UnsupportedOperationException("Cannot construct instance of "
                + targetType + " from a string.");
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
     */
    @Nullable
    public static <X extends Number> X convertToNumber(@Nullable Object value, 
            Class<X> targetType, @Nullable X def) {
        if (value == null) {
            return def;
        }
        if (value instanceof Number) {
            return numericValue((Number) value, targetType);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    /**
     * Convert number to a given target type.
     * @param <X> target type
     * @param n number
     * @param targetType target type
     * @return converted number
     */
    public static <X extends Number> X numericValue(Number n, Class<X> targetType) {
        requireNonNull(n);
        if (targetType == Byte.class) {
            return (X) (Byte) n.byteValue();
        } else if (targetType == Double.class) {
            return (X) (Double) n.doubleValue();
        } else if (targetType == Float.class) {
            return (X) (Float) n.floatValue();
        } else if (targetType == Integer.class) {
            return (X) (Integer) n.intValue();
        } else if (targetType == Long.class) {
            return (X) (Long) n.longValue();
        } else if (targetType == Short.class) {
            return (X) (Short) n.shortValue();
        } else {
            throw new UnsupportedOperationException("Unsupported number: "+n);
        }
    }
    
    //endregion
    
}
