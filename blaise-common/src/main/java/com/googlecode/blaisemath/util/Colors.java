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

import static com.google.common.base.Preconditions.checkArgument;
import java.awt.Color;
import static java.util.Objects.requireNonNull;

/**
 * Provides a number of utilities for working with colors, e.g. creating lighter/darker colors,
 * adjusting the alpha of a color, converting to/from hex strings.
 * 
 * @author Elisha Peterson
 */
public final class Colors {
    
    // utility class
    private Colors() {
    }
    
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
     * Transform the alpha component of a color.
     * @param col the color
     * @param a new alpha value
     * @return transformed color
     */
    public static Color alpha(Color col, int a) {
        checkArgument(a >= 0 && a <= 255);
        return new Color(col.getRed(), col.getGreen(), col.getBlue(), a);
    }
    
    /**
     * Interpolates between two colors, e.g. r = r1*wt + r2*(1-wt).
     * @param c1 first color
     * @param wt weight of first color (between 0 and 1)
     * @param c2 second color
     * @return interpolated color
     */
    public static Color interpolate(Color c1, float wt, Color c2) {
        checkArgument(wt >= 0f && wt <= 1f);
        float awt = 1-wt;
        return new Color((c1.getRed()*wt + c2.getRed()*awt)/255,
                (c1.getGreen()*wt + c2.getGreen()*awt)/255,
                (c1.getBlue()*wt + c2.getBlue()*awt)/255,
                (c1.getAlpha()*wt + c2.getAlpha()*awt)/255);
    }
    
    /**
     * Produces a color that is lighter than the specified color.
     * @param c source color
     * @return new color
     */
    public static Color lighterThan(Color c) {
        return new Color(lighten(c.getRed()), lighten(c.getGreen()),
                lighten(c.getBlue()), c.getAlpha());
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
    
}
