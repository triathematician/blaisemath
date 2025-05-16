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

import com.google.common.collect.Sets;
import com.googlecode.blaisemath.palette.MutablePalette;
import com.googlecode.blaisemath.palette.Palette;
import static com.googlecode.blaisemath.palette.Palette.BACKGROUND;
import static com.googlecode.blaisemath.palette.Palette.FOREGROUND;
import com.googlecode.blaisemath.palette.Palettes;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Used to edit the keyed colors associated with a {@link Palette}. Disables removal of fg/bg colors.
 * @author Elisha Peterson
 */
public final class PaletteEditor extends ColorListEditorSupport {
    
    private MutablePalette palette = Palettes.defaultPalette().mutableCopy();
    
    public PaletteEditor() {
        super();
        
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
    
    @Override
    protected void updateModelStyles(List<KeyColorBean> styles) {
        Set<String> removeKeys = Sets.newHashSet(palette.colors());
        for (KeyColorBean b : styles) {
            palette.set(b.getName(), b.getColor());
            removeKeys.remove(b.getName());
        }
        for (String c : removeKeys) {
            palette.remove(c);
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
    
    //</editor-fold>

}
