/*
 * SimplePalette.java
 * Created Sep 18, 2013
 */
package com.googlecode.blaisemath.dev.palette;

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


import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Provides access to a coordinated group of colors. Palettes may optionally
 * have {@link CategoricalPalette}s and {@link GradientPalette}s, which can be
 * used to color various information in a way that is coordinated with the palette's
 * color scheme.
 * 
 * @author Elisha
 */
public final class SimplePalette {
    
    //
    // STATIC KEYS FOR SPECIFIC COLORS
    //
    
    /** Unique key for foreground color */
    public static final String KEY_FOREGROUND = "FOREGROUND";
    /** Unique key for background color */
    public static final String KEY_BACKGROUND = "BACKGROUND";
    
    //
    // ATTRIBUTES
    //
    
    /** Contains colors associated with specific keys */
    private final Map<String,Color> keyColors = Maps.newLinkedHashMap();
    /** Contains a gradient palette */
    @Nullable
    private GradientPalette gradientPalette = null;
    /** Contains a categorical palette */
    @Nullable 
    private CategoricalPalette categoryPalette = null;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    public SimplePalette() {
        setForeground(Color.black);
        setBackground(Color.white);
    }
    
    
    //
    // KEY COLOR MUTATORS
    //
   
    /** Get available color keys */
    public List<String> getColorKeys() {
        return Lists.newArrayList(keyColors.keySet());
    }
    
    /** 
     * Get color associated with a given key, or null if there is none.
     * @param key the color key
     * @return associated color, null if there is none in this palette for the given key
     */
    @Nullable
    public Color getColor(String key) {
        checkNotNull(key);
        return keyColors.get(key);
    }
    
    /** 
     * Put color associated with given key.
     * @param key the color key
     * @param color the color to associate with the key
     */
    public void putColor(String key, Color color) {
        checkNotNull(key);
        checkNotNull(color);
        keyColors.put(key, color);
    }
    
    /** 
     * Remove color associated with given key.
     * @param key key whose color is to be removed
     * @return true if color was in palette and removed, false if it was not removed
     */
    public boolean removeColor(String key) {
        checkNotNull(key);
        return keyColors.remove(key) != null;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    public Color getBackground() {
        return getColor(KEY_BACKGROUND);
    }
    
    public void setBackground(Color c) {
        putColor(KEY_BACKGROUND, c);
    }
    
    public Color getForeground() {
        return getColor(KEY_FOREGROUND);
    }
    
    public void setForeground(Color c) {
        putColor(KEY_FOREGROUND, c);
    }

    /**
     * Get associated categorical color set, which contains a list of unique
     * colors that can be used for different categories.
     * @return categorical color set
     */
    @Nullable 
    public CategoricalPalette getCategoricalPalette() {
        return categoryPalette;
    }

    public void setCategoricalPalette(@Nullable CategoricalPalette cp) {
        if (this.categoryPalette != cp) {
            Object old = this.categoryPalette;
            this.categoryPalette = cp;
            pcs.firePropertyChange("categoricalPalette", old, cp);
        }
    }
    
    /**
     * Get associated gradient color set, which interpolates colors along a linear scale.
     * @return gradient color set
     */
    @Nullable 
    public GradientPalette getGradientPalette() {
        return gradientPalette;
    }

    public void setGradientPalette(@Nullable GradientPalette gp) {
        if (this.gradientPalette != gp) {
            Object old = this.gradientPalette;
            this.gradientPalette = gp;
            pcs.firePropertyChange("gradientPalette", old, gp);
        }
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY CHANGE LISTENING">
    //
    // PROPERTY CHANGE LISTENING
    //
    
    public void addPropertyChangeListener(PropertyChangeListener pl) {
        pcs.addPropertyChangeListener(pl);
    }
    
    public void addPropertyChangeListener(String string, PropertyChangeListener pl) {
        pcs.addPropertyChangeListener(string, pl);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener pl) {
        pcs.removePropertyChangeListener(pl);
    }
    
    public void removePropertyChangeListener(String string, PropertyChangeListener pl) {
        pcs.removePropertyChangeListener(string, pl);
    }
    //</editor-fold>

}
