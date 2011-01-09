/*
 * StyleUtils.java
 * Created Jan 8, 2011
 */

package primitive.style;

import java.util.Map;
import java.util.TreeMap;

/**
 * Provides useful utilities for style classes.
 * @author elisha
 */
class StyleUtils { private StyleUtils(){}

    public static final String $KEY_ANCHOR_SHAPE = "anchor shape";
    public static final String $KEY_CENTERED = "centered";
    public static final String $KEY_FILL_COLOR = "fill color";
    public static final String $KEY_HEAD_SHAPE = "head shape";
    public static final String $KEY_MAX_LENGTH = "max length";
    public static final String $KEY_OPACITY = "opacity";
    public static final String $KEY_SHAPE = "shape";
    public static final String $KEY_SIZE = "radius";
    public static final String $KEY_STROKE = "stroke";
    public static final String $KEY_STROKE_COLOR = "stroke color";
    public static final String $KEY_THICKNESS = "thickness";

    /** Turns array of String,Class, ... into a style key map */
    static Map<String,Class> keyMap(Object... arr) {
        TreeMap<String,Class> result = new TreeMap<String,Class>();
        for (int i = 0; i < arr.length; i+=2)
            result.put((String) arr[i], (Class) arr[i+1]);
        return result;
    }
}
