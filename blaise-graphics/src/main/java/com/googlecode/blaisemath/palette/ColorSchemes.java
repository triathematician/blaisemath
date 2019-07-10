package com.googlecode.blaisemath.palette;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import static com.googlecode.blaisemath.palette.ColorScheme.*;
import java.awt.Color;
import static java.awt.Color.*;

/**
 * Utilities for working with color schemes.
 * @author petereb1
 */
public class ColorSchemes {
    
    public static final Color[] SCHEME_BASIC = new Color[] {
        BLUE, RED, DARKISH_GREEN, BRIGHT_ORANGE, PINK, MAGENTA, GRAY, CYAN, ORANGE,
        LIGHT_BLUE, LIGHT_RED, DARK_GREEN, BROWN, DARK_BROWN, HOT_PINK, PURPLE
    };
    
    public static final Color[] SCHEME_GREEN_ARMITAGE = new Color[] {
        GA_GREEN, GA_BLUE, GA_ORPIMENT, GA_RED, GA_IRON, GA_CARAMEL, GA_TURQUOISE,
        GA_QUAGMIRE, GA_DAMSON, GA_ZINNIA, GA_VIOLET, GA_MALLOW, GA_WINE, GA_LIME,
        GA_YELLOW, GA_SKY, GA_PINK, GA_HONEYDEW, GA_AMETHYST, GA_URANIUM, GA_XANTHIN,
        GA_JADE, GA_NAVY, GA_KHAKI, GA_FOREST
    };
    
    public static final Color[] SCHEME_GLASBEY = new Color[] {
        BLUE, RED, GREEN, new Color(0, 0, 51), new Color(255, 0, 182), new Color(0, 83, 0), 
        new Color(255, 211, 0), new Color(0, 159, 255), new Color(154, 77, 66),
        new Color(0, 255, 190), new Color(120, 63, 193), new Color(31, 150, 152), 
        new Color(255, 172, 253), new Color(177, 204, 113), new Color(241, 8, 92), 
        new Color(254, 143, 66), new Color(221, 0, 255), new Color(32, 26, 1),
        new Color(114, 0, 85), new Color(118, 108, 149), new Color(2, 173, 36),
        new Color(200, 255, 0), new Color(136, 108, 0), new Color(255, 183, 159), 
        new Color(133, 133, 103), new Color(161, 3, 0), new Color(20, 249, 255), 
        new Color(0, 71, 158), new Color(220, 94, 147), new Color(147, 212, 255),
        new Color(0, 76, 255)
    };
    
    public static final Color[] SCHEME_CATEGORY10 = PaletteIo.colors("1f77b4ff7f0e2ca02cd627289467bd8c564be377c27f7f7fbcbd2217becf");
    public static final Color[] SCHEME_CATEGORY20 = PaletteIo.colors("1f77b4aec7e8ff7f0effbb782ca02c98df8ad62728ff98969467bdc5b0d58c564bc49c94e377c2f7b6d27f7f7fc7c7c7bcbd22dbdb8d17becf9edae5");
    public static final Color[] SCHEME_CATEGORY20B = PaletteIo.colors("393b795254a36b6ecf9c9ede6379398ca252b5cf6bcedb9c8c6d31bd9e39e7ba52e7cb94843c39ad494ad6616be7969c7b4173a55194ce6dbdde9ed6");
    public static final Color[] SCHEME_CATEGORY20C = PaletteIo.colors("3182bd6baed69ecae1c6dbefe6550dfd8d3cfdae6bfdd0a231a35474c476a1d99bc7e9c0756bb19e9ac8bcbddcdadaeb636363969696bdbdbdd9d9d9");

    public static final Color[] SCHEME_BLUES = { Color.BLUE, LIGHT_BLUE };
    
}
