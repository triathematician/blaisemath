package com.googlecode.blaisemath.palette;

/*
 * #%L
 * blaise-common
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
import com.googlecode.blaisemath.util.Colors;
import java.awt.Color;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles palette read/write operations.
 * @author Elisha Peterson
 */
public class PaletteIo {

    private static final Logger LOG = Logger.getLogger(PaletteIo.class.getName());
    
    /**
     * Loads palettes from properties file.
     * @param p properties file with key-value pairs defining individual palette colors
     * @return decoded palettes
     */
    static Map<String, Palette> loadPalettes(Properties p) {
        Map<String, Map<String, Color>> palettes = Maps.newTreeMap();
        for (String k : p.stringPropertyNames()) {
            if (k.startsWith("palette.")) {
                int dot = k.indexOf('.', 8);
                String palette = k.substring(8, dot);
                palettes.putIfAbsent(palette, Maps.newLinkedHashMap());
                String color = k.substring(dot + 1);
                try {
                    palettes.get(palette).put(color, Colors.decode(p.getProperty(k)));
                } catch (IllegalArgumentException x) {
                    LOG.log(Level.WARNING, "Invalid color: {0}", p.getProperty(k));
                }
            }
        }
        
        Map<String, Palette> res = Maps.newLinkedHashMap();
        for (String k : palettes.keySet()) {
            res.put(k, new ImmutableMapPalette(palettes.get(k)));
        }
        return res;
    }
    
    /**
     * Loads color schemes from properties file.
     * @param p properties file with schemes encoded as contiguous color strings
     * @return decoded schemes
     */
    static Map<String, ColorScheme> loadSchemes(Properties p) {
        Map<String, ColorScheme> res = Maps.newTreeMap();
        for (String k : p.stringPropertyNames()) {
            if (k.startsWith("scheme.")) {
                String scheme = k.substring(7);
                ColorScheme cs = ColorScheme.create(scheme, colors(p.getProperty(k)));
                res.put(scheme, cs);
            } else if (k.startsWith("scheme-gradient.")) {
                String scheme = k.substring(16);
                ColorScheme cs = ColorScheme.createGradient(scheme, colors(p.getProperty(k)));
                res.put(scheme, cs);
            }
        }
        return res;
    }

    /** Decode array of colors from string. */
    static Color[] colors(String f) {
        Color[] res = new Color[f.length() / 6];
        for (int i = 0; i < res.length; i++) {
            res[i] = Color.decode("#" + f.substring(6 * i, 6 * (i + 1)));
        }
        return res;
    }
    
    /** Encode array of colors as a string. */
    static String colorString(Color[] colors) {
        StringBuilder res = new StringBuilder();
        for (Color c : colors) {
            res.append(String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
        }
        return res.toString();
    }
    
}
