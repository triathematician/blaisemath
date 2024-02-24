package com.googlecode.blaisemath.palette.ui;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2019 - 2024 Elisha Peterson
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
import com.googlecode.blaisemath.palette.Palette;
import com.googlecode.blaisemath.palette.Palettes;
import com.googlecode.blaisemath.primitive.Marker;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Color;
import static java.util.Collections.list;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.DefaultListModel;

/**
 * List model for colors associated with strings.
 * @author Elisha Peterson
 */
public class ColorListModel extends DefaultListModel<KeyColorBean> {
    
    /**
     * Construct color list model from given palette.
     * @param p palette
     * @return list model
     */
    public static ColorListModel create(Palette p) {
        ColorListModel res = new ColorListModel();
        res.setColors(p);
        return res;
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    
    public List<KeyColorBean> getColors() {
        return list(elements());
    }
    
    public void setColors(List<KeyColorBean> colors) {
        clear();
        for (KeyColorBean c : colors) {
            addElement(c);
        }
    }
    
    public void setColors(Palette pal) {
        setColorMap(Palettes.colorMap(pal));
    }
    
    public Map<String, Color> getColorMap() {
        LinkedHashMap<String, Color> res = Maps.newLinkedHashMap();
        for (KeyColorBean en : list(elements())) {
            res.put(en.getName(), en.getColor());
        }
        return res;
    }
    
    public void setColorMap(Map<String, Color> cols) {
        clear();
        for (Entry<String, Color> en : cols.entrySet()) {
            addElement(KeyColorBean.create(en.getKey(), en.getValue()));
        }
    }

    //</editor-fold>
    
    public @Nullable String name(int index) {
        return index >= 0 && index < size() ? get(index).getName(): null;
    }
    
    public @Nullable Color color(int index) {
        return index >= 0 && index < size() ? get(index).getColor(): null;
    }
    
    public @Nullable Marker marker(int index) {
        return index >= 0 && index < size() ? get(index).getMarker(): null;
    }
    
}
