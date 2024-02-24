package com.googlecode.blaisemath.palette;

/*
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2024 Elisha Peterson
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


import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import static com.googlecode.blaisemath.palette.Palette.*;
import com.googlecode.blaisemath.util.Colors;
import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;

/**
 * Provides a few basic static palettes, as well as some static color definitions.
 *
 * @author Elisha Peterson
 */
@Beta
public final class Palettes {

    private static final Logger LOG = Logger.getLogger(Palettes.class.getName());
    
    private static final String SYSLAF_PALETTE = "System";
    private static final String DEFAULT_PALETTE = "Simple";
    
    private static final Map<String, Palette> PALETTES = Maps.newLinkedHashMap();
    
    static final Map<String, Color> DEF_PALETTE = ImmutableMap.<String, Color>builder()
            .put(FOREGROUND, Color.black)
            .put(BACKGROUND, Color.white)
            .put(SUBTLE_FOREGROUND, new Color(225, 230, 220))
            .put(BRIGHT_FOREGROUND, new Color(16, 16, 16))
            .put(SELECTION, new Color(128, 0, 0, 128))
            .put(ANNOTATION, new Color(0, 0, 15, 192))
            .build();

    /**
     * Get a statically-defined default palette.
     * @return palette
     */
    public static Palette defaultPalette() {
        return new ImmutableMapPalette(DEF_PALETTE);
    }

    /**
     * Construct and return a palette based on the current look-and-feel.
     * @return palette
     */
    public static Palette lafPalette() {
        Color fg = UIManager.getColor("Label.foreground");
        Color bg = UIManager.getColor("Label.background");
        return new ImmutableMapPalette(ImmutableMap.of(
                FOREGROUND, fg, 
                BACKGROUND, bg,
                BRIGHT_FOREGROUND, Colors.interpolate(fg, 1.2f, bg, -0.2f), 
                SUBTLE_FOREGROUND, Colors.interpolate(fg, 0.4f, bg, 0.6f)
        ));
    }
    
    /**
     * Get set of static palettes by name.
     * @return palettes
     */
    public static Set<String> palettes() {
        loadPalettes();
        Set<String> res = Sets.newLinkedHashSet(Arrays.asList(SYSLAF_PALETTE, DEFAULT_PALETTE));
        res.addAll(PALETTES.keySet());
        return Collections.unmodifiableSet(res);
    }
    
    /**
     * Load palette by id, returning palette constructed from default LAF if not present.
     * @param paletteId id of palette
     * @return palette
     */
    public static Palette paletteOrDefault(String paletteId) {
        loadPalettes();
        if (SYSLAF_PALETTE.equals(paletteId)) {
            return lafPalette();
        }
        return PALETTES.getOrDefault(paletteId, lafPalette());
    }
    
    /** 
     * Return palette colors as key-value map.
     * @param palette palette
     * @return map
     */
    public static Map<String, Color> colorMap(Palette palette) {
        LinkedHashMap<String, Color> res = Maps.newLinkedHashMap();
        if (palette != null) {
            for (String k : palette.colors()) {
                res.put(k, palette.color(k));
            }
        }
        return res;
    }
    
    //<editor-fold defaultstate="collapsed" desc="PRIVATE UTILITIES">
    
    private static void loadPalettes() {
        if (!PALETTES.isEmpty()) {
            return;
        }
        PALETTES.put(DEFAULT_PALETTE, defaultPalette());
        try {
            Properties p = new Properties();
            p.load(Palettes.class.getResource("resources/Palettes.properties").openStream());
            PALETTES.putAll(PaletteIo.loadPalettes(p));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
    
    //</editor-fold>

}
