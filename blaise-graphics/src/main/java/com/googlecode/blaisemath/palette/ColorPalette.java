package com.googlecode.blaisemath.palette;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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
import com.google.common.collect.Maps;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * An ordered collection of named colors.
 * 
 * @author Elisha Peterson
 * @since 2.3.0
 */
public final class ColorPalette {
    
    /** Unique key for foreground color */
    public static final String KEY_FOREGROUND = "fg";
    /** Unique key for background color */
    public static final String KEY_BACKGROUND = "bg";
    
    /** Contains colors associated with specific keys */
    private final Map<String,Color> colors = Maps.newLinkedHashMap();

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    public ColorPalette() {
        setForeground(Color.black);
        setBackground(Color.white);
    }
    
    /** 
     * Put color associated with given key.
     * @param key the color key
     * @param color the color to associate with the key
     */
    public void putColor(String key, Color color) {
        checkNotNull(key);
        checkNotNull(color);
        if (!Objects.equals(color, color(key))) {
            colors.put(key, color);
            fireChangeEvent();
        }
    }
    
    /** 
     * Remove color associated with given key.
     * @param key key whose color is to be removed
     * @return true if color was in palette and removed, false if it was not removed
     */
    public boolean removeColor(String key) {
        checkNotNull(key);
        if (colors.remove(key) != null) {
            fireChangeEvent();
            return true;
        }
        return false;
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
   
    /** 
     * Get available color keys
     * @return keys
     */
    public Set<String> colorKeys() {
        return Collections.unmodifiableSet(colors.keySet());
    }
    
    /** 
     * Get color associated with a given key, or null if there is none.
     * @param key the color key
     * @return associated color, null if there is none in this palette for the given key
     */
    @Nullable
    public Color color(String key) {
        checkNotNull(key);
        return colors.get(key);
    }
    
    public Color getForeground() {
        return color(KEY_FOREGROUND);
    }
    
    public void setForeground(Color c) {
        putColor(KEY_FOREGROUND, c);
    }
    
    public Color getBackground() {
        return color(KEY_BACKGROUND);
    }
    
    public void setBackground(Color c) {
        putColor(KEY_BACKGROUND, c);
    }
    
    //</editor-fold>
    
    private void fireChangeEvent() {
        pcs.firePropertyChange("palette", null, this);
    }    
    
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
