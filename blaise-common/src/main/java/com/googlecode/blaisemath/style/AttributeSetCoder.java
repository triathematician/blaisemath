package com.googlecode.blaisemath.style;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.util.Colors;
import com.googlecode.blaisemath.util.encode.StringDecoder;
import com.googlecode.blaisemath.util.encode.StringEncoder;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.Objects.requireNonNull;
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
 *   <li>Colors are converted to #RRGGBB or #AARRGGBB notation, using {@link Colors#stringConverter()}.</li>
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

    /** String used to represent null explicitly. */
    private static final String NULL_STRING = "none";
    
    private static final Joiner.MapJoiner KEYVAL_JOINER = Joiner.on("; ")
            .withKeyValueSeparator(":");
    private static final Splitter.MapSplitter KEYVAL_SPLITTER = Splitter.on(";")
            .omitEmptyStrings().trimResults()
            .withKeyValueSeparator(Splitter.on(":").trimResults());

    /** Used in deserialization for custom type mapping */
    private final @Nullable Map<String, Class<?>> types = null;

    @Override
    public String encode(AttributeSet style) {
        requireNonNull(style);
        Map<String,String> props = Maps.newTreeMap();
        style.getAttributes().forEach(s -> tryPut(props, style, s));
        return KEYVAL_JOINER.join(props);
    }

    @Override
    public AttributeSet decode(String str) {
        requireNonNull(str);
        AttributeSet res = new AttributeSet();
        for (Entry<String, String> en : KEYVAL_SPLITTER.split(str).entrySet()) {
            String key = en.getKey();
            String sval = en.getValue();
            Object val = NULL_STRING.equals(sval) ? null
                    : types.containsKey(key) ? decodeValue(sval, types.get(key))
                    : decodeValue(sval, Object.class);
            res.put(key, val);
        }
        return res;
    }
    
    //<editor-fold defaultstate="collapsed" desc="ENCODE UTILS">
    
    private static void tryPut(Map<String, String> props, AttributeSet style, String key) {
        try {
            props.put(key, encodeValue(style.get(key)));
        } catch (UnsupportedOperationException x) {
            LOG.log(Level.WARNING, "Cannot convert value "+style.get(key)+" to string.", x);
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="VALUE ENCODING">
    
    /**
     * Converts values to strings. Supports the following types:
     * 
     * <ul>
     * <li>String</li>
     * <li>Double</li>
     * <li>Float</li>
     * <li>Integer</li>
     * <li>Color - in the form #RRGGBB or #AARRGGBB</li>
     * <li>Point - in the form !point[5,6.2]</li>
     * <li>Rectangle - in the form !rectangle[x=2,y=3,w=7,h=12]</li>
     * <li>Boolean - reverses as text</li>
     * <li>Font - reverses as text</li>
     * <li>Anchor - reverses as text</li>
     * </ul>
     * 
     * <p> When decoding from strings, the above lists describes how strings that
     * may match more than one type are handled. Types lower in the list are
     * always used prior to those higher up in the list.
     * 
     * @param x value to encode
     */
    private static String encodeValue(Object val) {
        try {
            if (val instanceof Color) {
                return Colors.encode((Color) val);
            } else if (val instanceof Marker) {
                return val.getClass().getSimpleName();
            } else if (val instanceof Rectangle) {
                return "!"+new RectangleAdapter().marshal((Rectangle)val);
            } else if (val instanceof Rectangle2D) {
                return "!"+new Rectangle2DAdapter().marshal((Rectangle2D)val);
            } else if (val instanceof Point) {
                return "!"+new PointAdapter().marshal((Point)val);
            } else if (val instanceof Point2D.Double) {
                return "!"+new Point2DAdapter().marshal((Point2D.Double)val);
            } else if (val instanceof Font) {
                return new FontAdapter().marshal((Font) val);
            } else {
                return val+"";
            }
        } catch (Exception x) {
            LOG.log(Level.WARNING, "Unable to convert "+val, x);
            return null;
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="VALUE DECODING">
    
    /**
     * Decodes a string value as a target type.
     * @param <X> decoded type
     * @param x string value
     * @param type decoded type
     * @return 
     */
    private static <X> X decodeValue(String x, Class<X> type) {
        Object val = VALUE_CONVERTER_INST.reverse().convert(x);
        return NULL_STRING.equals(val) ? null : val;
    }
        
    protected static Object doBackwardBestGuess(String sval) {
        try {
            if (sval.matches("#[0-9a-fA-f]{3}")
                    || sval.matches("#[0-9a-fA-f]{6}")
                    || sval.matches("#[0-9a-fA-f]{8}")) {
                return Colors.decode(sval);
            } else if (sval.matches("!point\\[(.*)\\]")) {
                return new Point2DAdapter().unmarshal(sval);
            } else if (sval.matches("!rectangle\\[(.*)\\]")) {
                return new RectangleAdapter().unmarshal(sval);
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
        } catch (Exception x) {
            LOG.log(Level.WARNING, "Unable to convert "+sval, x);
            return null;
        }
    }

    protected static Object doBackward(String sval, Class<?> prefType) {
        try {
            requireNonNull(prefType);
            if (prefType == Color.class) {
                return Colors.decode(sval);
            } else if (Point2D.class.isAssignableFrom(prefType)) {
                return new Point2DAdapter().unmarshal(sval);
            } else if (Rectangle2D.class.isAssignableFrom(prefType)) {
                return new RectangleAdapter().unmarshal(sval);
            } else if (prefType == Integer.class) {
                return Integer.valueOf(sval);
            } else if (prefType == Float.class) {
                return Float.valueOf(sval);
            } else if (prefType == Double.class) {
                return Double.valueOf(sval);
            } else if (prefType == String.class) {
                return sval;
            } else if (prefType == Boolean.class) {
                return Boolean.valueOf(sval);
            } else if (prefType == Anchor.class) {
                return Anchor.valueOf(sval);
            }
            return sval;
        } catch (Exception x) {
            LOG.log(Level.WARNING, "Unable to convert "+sval, x);
            return null;
        }
    }
    
    //</editor-fold>
    
}
