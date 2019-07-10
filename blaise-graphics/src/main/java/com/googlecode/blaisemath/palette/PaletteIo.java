package com.googlecode.blaisemath.palette;

/*
 * #%L
 * blaise-common
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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.googlecode.blaisemath.util.Colors;
import java.awt.Color;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles palette read/write operations.
 * @author petereb1
 */
public class PaletteIo {

    private static final Logger LOG = Logger.getLogger(PaletteIo.class.getName());
    
    /**
     * Loads palettes from properties file.
     * @param p properties file with key-value pairs defining individual palette colors
     * @return decoded palettes
     */
    static Map<String, Palette> loadPalettes(Properties p) {
        Table<String, String, Color> colors = HashBasedTable.create();
        for (String k : p.stringPropertyNames()) {
            if (k.startsWith("palette.")) {
                int dot = k.indexOf('.', 8);
                String name = k.substring(8, dot);
                String col = k.substring(dot+1);
                try {
                    colors.put(name, col, Colors.stringConverter().reverse().convert(p.getProperty(k)));
                } catch (IllegalArgumentException x) {
                    LOG.log(Level.WARNING, "Invalid color: {0}", p.getProperty(k));
                }
            }
        }
        
        Map<String, Palette> res = Maps.newLinkedHashMap();
        for (String k : colors.rowKeySet()) {
            res.put(k, new ImmutableMapPalette(colors.row(k)));
        }
        return res;
    }

    /** Decode array of colors from string. */
    static Color[] colors(String f) {
        Color[] res = new Color[f.length()/6];
        for (int i = 0; i < res.length; i++) {
            res[i] = Color.decode("#"+f.substring(6*i,6*(i+1)));
        }
        return res;
    }
}
