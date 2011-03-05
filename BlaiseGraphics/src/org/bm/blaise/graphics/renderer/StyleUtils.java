/*
 * StyleUtils.java
 * Created Jan 8, 2011
 */

package org.bm.blaise.graphics.renderer;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides standard style keys, and useful utilities for style classes.
 * @author elisha
 */
public class StyleUtils {
    private StyleUtils(){}

    public static final String $KEY_FILL_COLOR = "fill color";
    public static final String $KEY_OPACITY = "opacity";
    public static final String $KEY_STROKE_COLOR = "stroke color";
    public static final String $KEY_THICKNESS = "thickness";

    /** Default stroke of 1 unit width. */
    public static final BasicStroke DEFAULT_STROKE = new BasicStroke(1.0f);
    /** Default composite */
    public static Composite DEFAULT_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);

    /** Turns array of String,Class, ... into a style key map */
    public static Map<String,Class> keyMap(Object... arr) {
        TreeMap<String,Class> result = new TreeMap<String,Class>();
        for (int i = 0; i < arr.length; i+=2)
            result.put((String) arr[i], (Class) arr[i+1]);
        return result;
    }

    public static Color lighterThan(Color c) {
        return new Color(lighten(c.getRed()), lighten(c.getGreen()), lighten(c.getBlue()));
    }

    private static int lighten(int i) {
        return i + Math.min(64, (255-i)/2);
    }
}
