/*
 * ColorUtils.java
 * Created Aug 24, 2013
 */
package com.googlecode.blaisemath.util;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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
    
}
