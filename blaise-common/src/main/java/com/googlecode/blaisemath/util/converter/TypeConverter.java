package com.googlecode.blaisemath.util.converter;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Converts values from one type to another.
 * @author Elisha Peterson
 */
public class TypeConverter {

    private static final Logger LOG = Logger.getLogger(TypeConverter.class.getName());
    
    /**
     * Convert value to target type, if possible. Returns a default value if unable
     * to convert.
     * @param <X> target type
     * @param value value to convert
     * @param targetType target type
     * @param def default value to return if
     * @return converted value
     */
    public static <X> X convert(Object value, Class<X> targetType, @Nullable X def) {
        try {
            return targetType.cast(value);
        } catch (ClassCastException x) {
            LOG.log(Level.WARNING, "Cast from "+value+" to "+targetType+" failed.", x);
            throw x;
        }
    }
    
}
