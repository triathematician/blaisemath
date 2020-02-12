package com.googlecode.blaisemath.palette;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2014 - 2020 Elisha Peterson
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

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilities for working with color schemes.
 * @author Elisha Peterson
 */
public class ColorSchemes {

    private static final Logger LOG = Logger.getLogger(ColorSchemes.class.getName());
    
    public static final String BASIC = "BASIC";
    public static final String GREEN_ARMITAGE = "GREEN_ARMITAGE";
    public static final String GLASBEY = "GLASBEY";
    
    public static final String CATEGORY10 = "CATEGORY10";
    public static final String CATEGORY20 = "CATEGORY20";
    public static final String CATEGORY20B = "CATEGORY20B";
    public static final String CATEGORY20C = "CATEGORY20C";
    public static final String BLUES = "BLUES";
    
    private static final Map<String, ColorScheme> SCHEMES = Maps.newLinkedHashMap();
    
    /**
     * Get set of static schemes by name.
     * @return schemes
     */
    public static Set<String> schemes() {
        loadSchemes();
        return Collections.unmodifiableSet(SCHEMES.keySet());
    }
    /**
     * Load scheme by id, returning missing value if not present
     * @param schemeId id of scheme
     * @return scheme
     */
    public static Optional<ColorScheme> scheme(String schemeId) {
        loadSchemes();
        return Optional.ofNullable(SCHEMES.get(schemeId));
    }
    
    private static void loadSchemes() {
        if (!SCHEMES.isEmpty()) {
            return;
        }
        try {
            Properties p = new Properties();
            p.load(Palettes.class.getResource("resources/ColorSchemes.properties").openStream());
            SCHEMES.putAll(PaletteIo.loadSchemes(p));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
    
//    public static void main(String[] args) {
//        System.out.println(PaletteIo.colorString(SCHEME_BASIC));
//        System.out.println(PaletteIo.colorString(SCHEME_GREEN_ARMITAGE));
//        System.out.println(PaletteIo.colorString(SCHEME_GLASBEY));
//        System.out.println(PaletteIo.colorString(SCHEME_BLUES));
//    }
//    
//    public static final Color[] SCHEME_BASIC = new Color[] {
//        BLUE, RED, DARKISH_GREEN, BRIGHT_ORANGE, PINK, MAGENTA, GRAY, CYAN, ORANGE,
//        LIGHT_BLUE, LIGHT_RED, DARK_GREEN, BROWN, DARK_BROWN, HOT_PINK, PURPLE
//    };
//    
//    public static final Color[] SCHEME_GREEN_ARMITAGE = new Color[] {
//        GA_GREEN, GA_BLUE, GA_ORPIMENT, GA_RED, GA_IRON, GA_CARAMEL, GA_TURQUOISE,
//        GA_QUAGMIRE, GA_DAMSON, GA_ZINNIA, GA_VIOLET, GA_MALLOW, GA_WINE, GA_LIME,
//        GA_YELLOW, GA_SKY, GA_PINK, GA_HONEYDEW, GA_AMETHYST, GA_URANIUM, GA_XANTHIN,
//        GA_JADE, GA_NAVY, GA_KHAKI, GA_FOREST
//    };
//    
//    public static final Color[] SCHEME_GLASBEY = new Color[] {
//        BLUE, RED, GREEN, new Color(0, 0, 51), new Color(255, 0, 182), new Color(0, 83, 0), 
//        new Color(255, 211, 0), new Color(0, 159, 255), new Color(154, 77, 66),
//        new Color(0, 255, 190), new Color(120, 63, 193), new Color(31, 150, 152), 
//        new Color(255, 172, 253), new Color(177, 204, 113), new Color(241, 8, 92), 
//        new Color(254, 143, 66), new Color(221, 0, 255), new Color(32, 26, 1),
//        new Color(114, 0, 85), new Color(118, 108, 149), new Color(2, 173, 36),
//        new Color(200, 255, 0), new Color(136, 108, 0), new Color(255, 183, 159), 
//        new Color(133, 133, 103), new Color(161, 3, 0), new Color(20, 249, 255), 
//        new Color(0, 71, 158), new Color(220, 94, 147), new Color(147, 212, 255),
//        new Color(0, 76, 255)
//    };
//    
//    public static final Color[] SCHEME_CATEGORY10 = PaletteIo.colors("1f77b4ff7f0e2ca02cd627289467bd8c564be377c27f7f7fbcbd2217becf");
//    public static final Color[] SCHEME_CATEGORY20 = PaletteIo.colors("1f77b4aec7e8ff7f0effbb782ca02c98df8ad62728ff98969467bdc5b0d58c564bc49c94e377c2f7b6d27f7f7fc7c7c7bcbd22dbdb8d17becf9edae5");
//    public static final Color[] SCHEME_CATEGORY20B = PaletteIo.colors("393b795254a36b6ecf9c9ede6379398ca252b5cf6bcedb9c8c6d31bd9e39e7ba52e7cb94843c39ad494ad6616be7969c7b4173a55194ce6dbdde9ed6");
//    public static final Color[] SCHEME_CATEGORY20C = PaletteIo.colors("3182bd6baed69ecae1c6dbefe6550dfd8d3cfdae6bfdd0a231a35474c476a1d99bc7e9c0756bb19e9ac8bcbddcdadaeb636363969696bdbdbdd9d9d9");
//
//    public static final Color[] SCHEME_BLUES = { Color.BLUE, LIGHT_BLUE };
    
}
