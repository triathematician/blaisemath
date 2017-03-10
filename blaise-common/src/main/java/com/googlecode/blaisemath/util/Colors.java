/*
 * Colors.java
 * Created Aug 24, 2013
 */
package com.googlecode.blaisemath.util;

/*
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2017 Elisha Peterson
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
import static com.google.common.base.Preconditions.checkArgument;
import java.awt.Color;

/**
 * Provides a number of utilities for working with colors, e.g. creating lighter/darker colors,
 * adjusting the alpha of a color, converting to/from hex strings.
 * 
 * @author Elisha
 */
public class Colors {

    private static final ColorStringConverter CONVERTER_INST = new ColorStringConverter();
    
    // utility class
    private Colors() {
    }

    /**
     * Transform the alpha component of a color
     * @param col the color
     * @param a new alpha value
     * @return transformed color
     */
    public static Color alpha(Color col, int a) {
        checkArgument(a >= 0 && a <= 255);
        return new Color(col.getRed(), col.getGreen(), col.getBlue(), a);
    }

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
    
    public static Converter<Color, String> stringConverter() {
        return CONVERTER_INST;
    }
    
    //<editor-fold defaultstate="collapsed" desc="INNER">
    
    /**
     * Converts colors to/from hex strings. Supports {@code #AARRGGBB} and
     * {@code #RRGGBB} formats.
     *
     * @see Color#decode(java.lang.String)
     *
     * @author Elisha Peterson
     */
    private static final class ColorStringConverter extends Converter<Color, String> {

        @Override
        protected Color doBackward(String v) {
            if (v == null) {
                return null;
            }
            String col = v.startsWith("#") ? v : ("#"+v);
            try {
                if (col.length() == 7) {
                    // #RRGGBB
                    return Color.decode(col);
                } else if (col.length() == 9) {
                    // #AARRGGBB
                    int alpha = Integer.decode(col.substring(0,3));
                    int rgb = Integer.decode("#"+col.substring(3));
                    return new Color((alpha << 24) + rgb, true);
                } else if (col.length() == 4) {
                    // #RGB
                    int red = Integer.valueOf(col.substring(1,2), 16);
                    int green = Integer.valueOf(col.substring(2,3), 16);
                    int blue = Integer.valueOf(col.substring(3,4), 16);
                    return new Color(17*red, 17*green, 17*blue);
                } else {
                    throw new IllegalArgumentException("Not a color: "+col);
                }
            } catch (NumberFormatException x) {
                throw new IllegalArgumentException("Not a color: "+col, x);
            }
        }

        @Override
        protected String doForward(Color c) {
            if (c == null) {
                return null;
            } else if (c.getAlpha() == 255) {
                return String.format("#%02x%02x%02x", c.getRed(),c.getGreen(),c.getBlue());
            } else {
                return String.format("#%02x%02x%02x%02x", c.getAlpha(), c.getRed(),c.getGreen(),c.getBlue());
            }
        }
    }
    
    //</editor-fold>
    
}
