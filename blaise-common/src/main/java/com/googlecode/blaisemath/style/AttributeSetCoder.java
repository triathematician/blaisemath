package com.googlecode.blaisemath.style;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
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

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.googlecode.blaisemath.util.Colors;
import com.googlecode.blaisemath.util.encode.FontCoder;
import com.googlecode.blaisemath.util.encode.InsetsCoder;
import com.googlecode.blaisemath.util.encode.Point2DCoder;
import com.googlecode.blaisemath.util.encode.PointCoder;
import com.googlecode.blaisemath.util.encode.Rectangle2DCoder;
import com.googlecode.blaisemath.util.encode.RectangleCoder;
import com.googlecode.blaisemath.util.encode.StringDecoder;
import com.googlecode.blaisemath.util.encode.StringEncoder;
import com.googlecode.blaisemath.util.type.TypeConverter;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import static java.util.Collections.emptyMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Encode/decodes {@link AttributeSet}s as strings. The key-value pairs in the
 * attribute set are encoded using colons and semicolons as separators, e.g. "a:2; b:3".
 * 
 * <p> The string is intended to be
 * compatible with html/css, but some features of the {@code AttributeSet} cannot
 * be encoded this way, so the operation is not invertible. Conversion <i>to</i> a
 * string uses the following rules:
 * 
 * <ul>
 *   <li>The attribute name is not used for conversion.</li>
 *   <li>Only values of type Number, String, Color, Marker, and Anchor are supported.</li>
 *   <li>Number, String, and Anchor values are converted in the obvious way.</li>
 *   <li>Colors are converted to #RRGGBB or #AARRGGBB notation, using {@link Colors#encode(Color)}.</li>
 *   <li>Marker values are persisted using their class name.</li>
 *   <li>Null values are converted to the string "none".</li>
 * </ul>
 * 
 * <p> Conversion <i>from</i> a string uses the following rules:
 * 
 * <ul>
 *   <li>The attribute name is not used for conversion.</li>
 *   <li>If the value matches #RGB, #RRGGBB, or #AARRGGBB it is converted to a color.</li>
 *   <li>A string value "none" is converted to a null value.</li>
 *   <li>If a value can be parsed as an integer or double, it is converted to that type.</li>
 *   <li>Otherwise, values are left as strings.</li>
 * </ul>
 * 
 * <p> Note that values of type Marker and Anchor are deserialized as strings rather
 * than their previous type. Blaise supports having string values for these attributes
 * wherever they are used.
 * 
 * @author Elisha Peterson
 */
public class AttributeSetCoder implements StringEncoder<AttributeSet>, StringDecoder<AttributeSet> {

    private static final Logger LOG = Logger.getLogger(AttributeSetCoder.class.getName());

    //region CONFIGS

    /** String used to represent null explicitly. */
    private static final String NULL_STRING = "none";
    
    /** Joins values into the result string */
    private static final Joiner.MapJoiner CODER_JOINER = Joiner.on("; ")
            .withKeyValueSeparator(":");
    /** Functions used to encode specific types. Listed in order of type checks for encoding. */
    private static final CoderMap CODERS = new CoderMap()
                    .put(Color.class, Colors::encode)
                    .put(Font.class, new FontCoder()::encode)
                    .put(Insets.class, new InsetsCoder()::encode)
                    .put(Point.class, new PointCoder()::encode)
                    .put(Point2D.class, new Point2DCoder()::encode)
                    .put(Rectangle.class, new RectangleCoder()::encode)
                    .put(Rectangle2D.class, new Rectangle2DCoder()::encode)
                    .put(Marker.class, v -> v.getClass().getSimpleName());
    
    /** Splits key-value pairs in a string to decode */
    private static final Splitter DECODER_PAIR_SPLITTER = Splitter.on(";")
            .omitEmptyStrings().trimResults();
    /** Splits key from value */
    private static final Splitter DECODER_KEY_SPLITTER = Splitter.on(":")
            .omitEmptyStrings().trimResults();
    /** Functions used to encode specific types. Listed in order of type checks for decoding. */
    private static final DecoderMap DECODERS = new DecoderMap()
                    .put(Integer.class, Integer::valueOf)
                    .put(Float.class, Float::valueOf)
                    .put(Double.class, Double::valueOf)
                    .put(Boolean.class, Boolean::valueOf)
                    .put(Anchor.class, Anchor::valueOf)
                    .put(String.class, s -> s)
                    .put(Color.class, Colors::decode)
                    .put(Font.class, Font::decode)
                    .put(Insets.class, new InsetsCoder()::decode)
                    .put(Point.class, new PointCoder()::decode)
                    .put(Point2D.class, new Point2DCoder()::decode)
                    .put(Rectangle.class, new RectangleCoder()::decode)
                    .put(Rectangle2D.class, new Rectangle2DCoder()::decode);

    //endregion

    /** Used in deserialization for custom type mapping */
    private final Map<String, Class<?>> types;

    /**
     * Get default coder instance.
     */
    public AttributeSetCoder() {
        this(null);
    }
    
    /**
     * Get coder instance where particular keys are associated with particular types,
     * which allows decoding operations to generate the correct types in more cases.
     * @param types types associated with keys
     */
    public AttributeSetCoder(Map<String, Class<?>> types) {
        this.types = types == null ? emptyMap() : types;
    }

    @Override
    public String encode(AttributeSet style) {
        requireNonNull(style);
        Map<String,String> props = Maps.newTreeMap();
        style.getAttributes().forEach(s -> tryPut(props, s, style.get(s)));
        return CODER_JOINER.join(props);
    }

    @Override
    public AttributeSet decode(String str) {
        requireNonNull(str);
        
        // perform two separate splits instead of using MapSplitter to allow for duplicate keys
        AttributeSet res = new AttributeSet();
        List<String> pairs = DECODER_PAIR_SPLITTER.splitToList(str);
        for (String p : pairs) {
            List<String> kv = DECODER_KEY_SPLITTER.splitToList(p);
            if (kv.size() != 2) {
                LOG.log(Level.WARNING, "Invalid attribute string: {0}", str);
                return res;
            }
            String key = kv.get(0);
            String sval = kv.get(1);
            Object val = NULL_STRING.equals(sval) ? null
                    : types.containsKey(key) ? decodeValue(sval, types.get(key))
                    : decodeValue(sval, Object.class);
            res.put(key, val);
        }
        return res;
    }
    
    //region VALUE CONVERSION UTILS
    
    /**
     * Converts value from one type to another, with optional default.
     * @param value value to convert
     * @param targetType target type
     * @param def default value
     * @return value of target type if possible, else default; may return null if def is null
     */
    static <X> @Nullable X convertValue(@Nullable Object value, Class<X> targetType, @Nullable X def) {
        return TypeConverter.convert(value, targetType, def);
    }
    
    //endregion
    
    //region ENCODE UTILS
    
    /** Attempt to convert given value to a string and add to target map */
    private static void tryPut(Map<String, String> props, String key, Object value) {
        try {
            props.put(key, encodeValue(value));
        } catch (UnsupportedOperationException x) {
            LOG.log(Level.WARNING, "Cannot convert value " + value + " to string.", x);
        }
    }
    
    /**
     * Converts values to strings. 
     * @param val value to encode
     * @return encoded value, or null if unable to encode
     */
    static @Nullable String encodeValue(Object val) {
        try {
            if (val == null) {
                return NULL_STRING;
            } else if (CODERS.containsKey(val.getClass())) {
                return (String) CODERS.get(val.getClass()).apply(val);
            } else {
                for (Class c : CODERS.keySet()) {
                    if (c.isAssignableFrom(val.getClass())) {
                        return (String) CODERS.get(c).apply(val);
                    }
                }
            }
            return val+"";
        } catch (Exception x) {
            LOG.log(Level.WARNING, "Unable to convert "+val, x);
            return null;
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
     */
    @SuppressWarnings("unchecked")
    static <X> @Nullable X decodeValue(String val, Class<X> type) {
        requireNonNull(val);
        String trim = val.trim();
        try {
            if (NULL_STRING.equals(val)) {
                return null;
            } else if (DECODERS.containsKey(type)) {
                return DECODERS.apply(type, val);
            } else if (trim.matches("#[0-9a-fA-f]{3}")
                    || trim.matches("#[0-9a-fA-f]{6}")
                    || trim.matches("#[0-9a-fA-f]{8}")) {
                return (X) DECODERS.apply(Color.class, trim);
            } else if (trim.matches("\\((.*),(.*)\\)") && trim.contains(".")) {
                return (X) DECODERS.apply(Point2D.class, trim);
            } else if (trim.matches("\\((.*),(.*)\\)")) {
                return (X) DECODERS.apply(Point.class, trim);
            } else if (trim.matches("rectangle\\((.*)\\)")) {
                return (X) DECODERS.apply(Rectangle.class, trim);
            } else if (trim.matches("rectangle2d\\((.*)\\)")) {
                return (X) DECODERS.apply(Rectangle2D.class, trim);
            }
            Integer i = Ints.tryParse(trim);
            if (type.isInstance(i)) {
                return (X) i;
            }
            Double d = Doubles.tryParse(trim);
            if (type.isInstance(d)) {
                return (X) d;
            }
            if (type.isInstance(val)) {
                return (X) val;
            }
        } catch (Exception x) {
            LOG.log(Level.WARNING, "Unable to decode "+val+" as "+type, x);
            return null;
        }
        LOG.log(Level.WARNING, "Unable to decode {0} as {1}", new Object[]{val, type});
        return null;
    }
    
    //endregion
    
    /** Utility type for storing coders */
    private static class CoderMap extends LinkedHashMap<Class, Function> {
        private <X> CoderMap put(Class<X> type, Function<X, String> toStr) {
            super.put(type, toStr);
            return this;
        }
    }
    
    /** Utility type for storing decoders */ 
    private static class DecoderMap extends LinkedHashMap<Class, Function> {
        private <X> DecoderMap put(Class<X> type, Function<String, X> fromStr) {
            super.put(type, fromStr);
            return this;
        }

        private <X> X apply(Class<X> type, String key) {
            return (X) get(type).apply(key);
        }
    }
    
}
