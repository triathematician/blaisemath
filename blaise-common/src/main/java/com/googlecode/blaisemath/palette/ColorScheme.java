package com.googlecode.blaisemath.palette;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2014 - 2022 Elisha Peterson
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

import java.awt.Color;

/**
 * Maintains a list of colors suitable for use as categories or color scales within charts.
 * Each scheme is defined by a list of colors, and has a flag that indicates whether its a
 * discrete or continuous (i.e. gradient) scheme.
 * 
 * @author Elisha Peterson
 */
public class ColorScheme {
    
    // <editor-fold defaultstate="collapsed" desc="STATIC COLORS">

    public static final Color BRIGHT_ORANGE = new Color(255, 150, 0);

    public static final Color LIGHT_BLUE = new Color(128, 128, 255);
    public static final Color DARK_BLUE = new Color(0, 0, 128);
    public static final Color LIGHT_RED = new Color(255, 100, 100);
    public static final Color DARK_GREEN = new Color(0, 164, 0);
    public static final Color DARKISH_GREEN = new Color(0, 216, 0);
    public static final Color BROWN = new Color(140, 80, 0);
    public static final Color DARK_BROWN = new Color(80, 40, 0);
    public static final Color HOT_PINK = new Color(200, 40, 140);
    public static final Color PURPLE = new Color(130, 10, 150);

    // Green-Armitage colors

    public static final Color GA_AMETHYST = new Color(240, 163, 255);
    public static final Color GA_BLUE = new Color(0, 117, 220);
    public static final Color GA_CARAMEL = new Color(153, 63, 0);
    public static final Color GA_DAMSON = new Color(76, 0, 92);
    public static final Color GA_EBONY = new Color(25, 25, 25);
    public static final Color GA_FOREST = new Color(0, 92, 49);
    public static final Color GA_GREEN = new Color(43, 206, 72);
    public static final Color GA_HONEYDEW = new Color(255, 204, 153);
    public static final Color GA_IRON = new Color(128, 128, 128);
    public static final Color GA_JADE = new Color(148, 255, 181);
    public static final Color GA_KHAKI = new Color(153, 124, 0);
    public static final Color GA_LIME = new Color(157, 204, 0);
    public static final Color GA_MALLOW = new Color(194, 0, 136);
    public static final Color GA_NAVY = new Color(0, 51, 128);
    public static final Color GA_ORPIMENT = new Color(255, 164, 5);
    public static final Color GA_PINK = new Color(255, 168, 187);
    public static final Color GA_QUAGMIRE = new Color(66, 102, 0);
    public static final Color GA_RED = new Color(255, 0, 16);
    public static final Color GA_SKY = new Color(94, 241, 242);
    public static final Color GA_TURQUOISE = new Color(0, 153, 143);
    public static final Color GA_URANIUM = new Color(224, 255, 102);
    public static final Color GA_VIOLET = new Color(116, 10, 255);
    public static final Color GA_WINE = new Color(153, 0, 0);
    public static final Color GA_XANTHIN = new Color(255, 255, 128);
    public static final Color GA_YELLOW = new Color(255, 225, 0);
    public static final Color GA_ZINNIA = new Color(255, 80, 5);

    // </editor-fold>
    
    private String name;
    private boolean discrete = true;
    private Color[] colors = new Color[0];
    
    /**
     * Construct discrete color scheme.
     * @param name scheme name
     * @param colors scheme colors
     * @return scheme
     */
    public static ColorScheme create(String name, Color... colors) {
        ColorScheme res = new ColorScheme();
        res.name = name;
        res.discrete = true;
        res.colors = colors;
        return res;
    }
    
    /**
     * Construct gradient color scheme.
     * @param name scheme name
     * @param colors colors to use for gradient
     * @return scheme
     */
    public static ColorScheme createGradient(String name, Color... colors) {
        ColorScheme res = new ColorScheme();
        res.name = name;
        res.discrete = false;
        res.colors = colors;
        return res;
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    
    /**
     * Get name of scheme.
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Return true if a discrete scheme.
     * @return discrete
     */
    public boolean isDiscrete() {
        return discrete;
    }

    public void setDiscrete(boolean discrete) {
        this.discrete = discrete;
    }
    
    /**
     * Get array of colors in scheme.
     * @return colors
     */
    public Color[] getColors() {
        return colors;
    }

    public void setColors(Color[] colors) {
        this.colors = colors;
    }
    
    //</editor-fold>
    

}
