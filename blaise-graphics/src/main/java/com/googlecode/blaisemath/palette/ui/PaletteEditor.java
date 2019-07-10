package com.googlecode.blaisemath.palette.ui;

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


import com.google.common.collect.Lists;
import com.googlecode.blaisemath.palette.MutablePalette;
import com.googlecode.blaisemath.palette.Palette;
import static com.googlecode.blaisemath.palette.Palette.BACKGROUND;
import static com.googlecode.blaisemath.palette.Palette.FOREGROUND;
import com.googlecode.blaisemath.palette.Palettes;
import java.awt.BorderLayout;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Used to edit the keyed colors associated with a {@link Palette}. Disables removal of fg/bg colors.
 * @author petereb1
 */
public final class PaletteEditor extends JPanel {
    
    private MutablePalette palette = Palettes.defaultPalette().mutableCopy();
    private final ColorList list = new ColorList();
    
    public PaletteEditor() {
        setLayout(new BorderLayout());
        add(new JScrollPane(list), BorderLayout.CENTER);
        list.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                updatePaletteColors();
            }
        });
        
        // don't allow removal of fg/bg colors
        list.setEditConstraints(new ColorListEditConstraints(){
            @Override
            public boolean isRemovable(String item) {
                return !Arrays.asList(BACKGROUND, FOREGROUND).contains(item);
            }
            @Override
            public boolean isKeyEditable(String item) {
                return isRemovable(item);
            }
        });
    }
    
    /** Called when the list changes to update the palette. */
    private void updatePaletteColors() {
        Map<String, Color> colorMap = list.getColorListModel().getColorMap();
        for (Map.Entry<String, Color> en : colorMap.entrySet()) {
            palette.set(en.getKey(), en.getValue());
        }
        List<String> curKeys = Lists.newArrayList(palette.colors());
        for (String c : curKeys) {
            if (!colorMap.containsKey(c)) {
                palette.remove(c);
            }
        }
        firePropertyChange("palette", null, palette);
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    
    public MutablePalette getPalette() {
        return palette;
    }
    
    public void setPalette(MutablePalette palette) {
        if (this.palette != palette) {
            Object old = this.palette;
            this.palette = palette;
            list.getColorListModel().setColors(palette);
            firePropertyChange("palette", old, palette);
        }
    }
    
    public ColorListModel getColorListModel() {
        return list.getColorListModel();
    }
    
    //</editor-fold>
    
    public void addColorListPropertyChangeListener(PropertyChangeListener l) {
        list.addPropertyChangeListener(ColorList.COLORS, l);
    }
    
    public void removeColorListPropertyChangeListener(PropertyChangeListener l) {
        list.removePropertyChangeListener(ColorList.COLORS, l);
    }

}
