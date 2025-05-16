package com.googlecode.blaisemath.palette.ui;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2019 - 2025 Elisha Peterson
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
import com.googlecode.blaisemath.palette.ColorScheme;
import com.googlecode.blaisemath.util.Colors;
import java.awt.Color;
import java.util.List;
import java.util.Map;

/**
 * Used to edit colors in a color scheme. If the scheme is a gradient scheme, the number of colors is fixed.
 * 
 * @author Elisha Peterson
 */
public class ColorSchemeEditor extends ColorListEditorSupport {
        
    private ColorScheme scheme = new ColorScheme();
    
    public ColorSchemeEditor() {
        super();
        
        list.setEditConstraints(new ColorListEditConstraints().keysEditable(false));
    }
    
    @Override
    protected void updateModelStyles(List<KeyColorBean> items) {        
        // update names since the user should not be updating them
        for (int i = 0; i < items.size(); i++) {
            KeyColorBean b = items.get(i);
            b.setName(name(i, b.getColor()));
        }
        
        // update the scheme
        Color[] colors = new Color[items.size()];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = items.get(i).getColor();
        }
        scheme.setColors(colors);
        firePropertyChange("scheme", null, scheme);
    }
    
    private void updateListColors() {
        Map<String, Color> colors = Maps.newLinkedHashMap();
        for (int i = 0; i < scheme.getColors().length; i++) {
            Color color = scheme.getColors()[i];
            colors.put(name(i, color), color);
        }
        list.getColorListModel().setColorMap(colors);
        list.setEditConstraints(new ColorListEditConstraints().keysEditable(false));
    }
    
    private String name(int i, Color c) {
        return scheme.isDiscrete() ? Colors.encode(c) : "Stop " + (i + 1);
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    
    public ColorScheme getScheme() {
        return scheme;
    }
    
    public void setScheme(ColorScheme scheme) {
        if (this.scheme != scheme) {
            Object old = this.scheme;
            this.scheme = scheme;
            updateListColors();
            firePropertyChange("scheme", old, scheme);
        }
    }

    //</editor-fold>

}
