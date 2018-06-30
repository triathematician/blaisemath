package com.googlecode.blaisemath.util;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2018 Elisha Peterson
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
import static java.util.Objects.requireNonNull;

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
    
    //<editor-fold defaultstate="collapsed" desc="DERIVED COLOR UTILS">
    
    /**
     * Convert color to string. Results in #RRGGBB or #RRGGBBAA, depending on
     * whether or not the color has an alpha channel.
     * @param c color
     * @return string
     * @throws NullPointerException if c is null
     */
    public static String encode(Color c) {
        requireNonNull(c);
        if (c.getAlpha() == 255) {
            return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
        } else {
            return String.format("#%02x%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        }
    }
    
    /**
     * Flexible color decoder. Uses {@link javafx.scene.paint.Color#web(java.lang.String)} to decode.
     * @param v color string
     * @return color
     * @throws NullPointerException if v is null
     * @throws IllegalArgumentException if v is an invalid string
     */
    public static Color decode(String v) {
        requireNonNull(v);
        javafx.scene.paint.Color fx = javafx.scene.paint.Color.web(v);
        return new Color((float) fx.getRed(), (float) fx.getGreen(),
                (float) fx.getBlue(), (float) fx.getOpacity());
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
     * Produces a color that is lighter than the specified color.
     * @param c source color
     * @return new color
     */
    public static Color lighterThan(Color c) {
        return new Color(lighten(c.getRed()), lighten(c.getGreen()), lighten(c.getBlue()), c.getAlpha());
    }

    private static int lighten(int i) {
        return i + Math.min(64, (255-i)/2);
    }

    /**
     * Produces a color that is "blander" than the specified color (reducing saturation by 50%).
     * @param c source color
     * @return new color
     */
    public static Color blanderThan(Color c) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
        Color c2 = Color.getHSBColor(hsb[0], .5f*hsb[1], hsb[2]);
        return new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), c.getAlpha());
    }
    
    //</editor-fold>
    
    /**
     * Get static instance of converter.
     * @return converter
     */
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
            return v == null ? null : Colors.decode(v);
        }

        @Override
        protected String doForward(Color c) {
            return c == null ? null : Colors.encode(c);
        }
    }
    
    //</editor-fold>
    
}
