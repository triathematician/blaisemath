/**
 * AttributeSets.java
 * Created Summer 2014
 */
package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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


import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.util.Colors;
import java.awt.Color;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 *   Utility library for {@link AttributeSet}.
 * </p>
 * <p>
 *   Provides a way to convert {@link AttributeSet} to/from a string. The string is intended to be
 *   compatible with html/css, but some features of the {@code AttributeSet} cannot
 *   be encoded this way, so the operation is not invertible. Conversion <i>to</i> a
 *   string uses the following rules:
 * </p>
 * <ul>
 *   <li>The attribute name is not used for conversion.</li>
 *   <li>Only values of type Number, String, Color, Marker, and Anchor are supported.</li>
 *   <li>Number, String, and Anchor values are converted in the obvious way.</li>
 *   <li>Colors are converted to #RRGGBB or #AARRGGBB notation, using {@link Colors#stringConverter()}.</li>
 *   <li>Marker values are persisted using their class name.</li>
 *   <li>Null values are converted to the string "none".</li>
 * </ul>
 * <p>
 *   Conversion <i>from</i> a string uses the following rules:
 * </p>
 * <ul>
 *   <li>The attribute name is not used for conversion.</li>
 *   <li>If the value matches #RRGGBB or #AARRGGBB it is converted to a color.</li>
 *   <li>A string value "none" is converted to a null value.</li>
 *   <li>If a value can be parsed as an integer or double, it is converted to that type.</li>
 *   <li>Otherwise, values are left as strings.</li>
 * </ul>
 * <p>
 *   Note that values of type Marker and Anchor are deserialized as strings rather
 *   than their previous type. Blaise supports having string values for these attributes
 *   wherever they are used.
 * </p>
 * 
 * @author Elisha
 */
public final class AttributeSets {
    
    /** String used to represent null explicitly. */
    private static final String NULL_STRING = "none";
    
    
    private static final AttributeSetConverter CONVERTER_INST = new AttributeSetConverter();
    private static final AttributeValueConverter VALUE_CONVERTER_INST = new AttributeValueConverter();

    private static final MapSplitter KEYVAL_SPLITTER = Splitter.on(";")
            .omitEmptyStrings().trimResults()
            .withKeyValueSeparator(Splitter.on(":").trimResults());
    private static final MapJoiner KEYVAL_JOINER = Joiner.on("; ")
            .withKeyValueSeparator(":");
    
    // non-instantiable utility class
    private AttributeSets() {
    }
    
    
    /**
     * Return object that can be used to convert an {@link AttributeSet} to/from a string.
     * The resulting style uses ";" to separate entries, and ":" to separate a key and value.
     * @return converter instance
     */
    public static Converter<AttributeSet,String> stringConverter() {
        return CONVERTER_INST;
    }
    
    /**
     * Return object that can be used to convert string values to/from Java object values.
     * Supports numeric and color types.
     * @return converter instance
     */
    public static Converter<Object,String> valueConverter() {
        return VALUE_CONVERTER_INST;
    }
    
    public static Object valueFromString(String x) {
        Object val = VALUE_CONVERTER_INST.reverse().convert(x);
        return NULL_STRING.equals(val) ? null : val;
    }
    
    /** Converts {@link AttributeSet} to/from a string. */
    private static class AttributeSetConverter extends Converter<AttributeSet,String> {
        @Override
        protected AttributeSet doBackward(String str) {
            if (str == null) {
                return null;
            }
            if (str.isEmpty()) {
                return new AttributeSet();
            }
            AttributeSet res = new AttributeSet();
            Map<String, String> vals = KEYVAL_SPLITTER.split(str);
            for (String key : vals.keySet()) {
                String sval = vals.get(key);
                Object val = VALUE_CONVERTER_INST.reverse().convert(sval);
                if (NULL_STRING.equals(val)) {
                    val = null;
                }
                res.put(key, val);
            }
            return res;
        }

        @Override
        protected String doForward(AttributeSet style) {
            if (style == null) {
                return null;
            }
            Map<String,String> props = Maps.newTreeMap();
            for (String s : style.getAttributes()) {
                Object styleValue = style.get(s);
                try {
                    String value = VALUE_CONVERTER_INST.convert(styleValue);
                    if (value == null) {
                        value = NULL_STRING;
                    }
                    props.put(s, value);
                } catch (UnsupportedOperationException x) {
                    // keep trying to convert, but log a warning
                    Logger.getLogger(AttributeSets.class.getName()).log(Level.WARNING,
                            "Cannot convert value "+styleValue+" to string.", x);
                }
            }
            return KEYVAL_JOINER.join(props);
        }
    }
    
    /** Converts {@link AttributeSet} values to/from strings. */
    private static class AttributeValueConverter extends Converter<Object, String> {
        @Override
        protected Object doBackward(String sval) {
            if (sval.matches("#[0-9a-fA-f]{6}")
                    || sval.matches("#[0-9a-fA-f]{8}")) {
                return Colors.stringConverter().reverse().convert(sval);
            }
            try {
                return Integer.valueOf(sval);
            } catch (NumberFormatException x) {
                // wasn't an integer, try the next thing
            }
            try {
                return Double.valueOf(sval);
            } catch (NumberFormatException x) {
                // wasn't a double, try the next thing
            }
            return sval;
        }

        @Override
        protected String doForward(Object val) {
            if (val instanceof Color) {
                return Colors.stringConverter().convert((Color) val);
            } else if (val instanceof Number) {
                return val.toString();
            } else if (val instanceof String) {
                return (String) val;
            } else if (val instanceof Marker) {
                return val.getClass().getSimpleName();
            } else if (val instanceof Anchor) {
                return ((Anchor)val).toString();
            } else {
                throw new IllegalArgumentException(val + " cannot be converted to string.");
            }
        }
    }
    
}
