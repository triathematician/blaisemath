/*
 * ColorUtils.java
 * Created Aug 24, 2013
 */
package org.blaise.style;

import java.awt.Color;

/**
 * Provides a number of utilities for working with colors, e.g. creating lighter/darker colors,
 * adjusting the alpha of a color, converting to/from hex strings.
 * 
 * @author Elisha
 */
public class ColorUtils {
    
    // utility class
    private ColorUtils() {
    }
    
    //
    // GENERAL UTILITIES
    //

    /**
     * Produces a color that is lighter than the specified color
     * @param c source color
     * @return new color
     */
    public static Color lighterThan(Color c) {
        return new Color(lighten(c.getRed()), lighten(c.getGreen()), lighten(c.getBlue()), c.getAlpha());
    }

    /**
     * Produces a color that is "blander" than the specified color (reducing saturation)
     * @param c source color
     * @return new color
     */
    public static Color blanderThan(Color c) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
        Color c2 = Color.getHSBColor(hsb[0], .5f*hsb[1], hsb[2]);
        return new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), c.getAlpha());
    }

    private static int lighten(int i) {
        return i + Math.min(64, (255-i)/2);
    }
    
    //
    // BLAISE-SPECIFIC UTILITIES
    //

    /**
     * Changes specified color based on visibility hints
     * @param color color to change
     * @param hints hints to apply
     * @return modified color
     */
    public static Color applyHints(Color color, VisibilityHintSet hints) {
        if (hints == null) {
            return color;
        } else if (hints.contains(VisibilityHint.HIGHLIGHT) || hints.contains(VisibilityHint.SELECTED)) {
            return lighterThan(color);
        } else if (hints.contains(VisibilityHint.FADED)) {
            return blanderThan(color);
        } else {
            return color;
        }
    }
}
