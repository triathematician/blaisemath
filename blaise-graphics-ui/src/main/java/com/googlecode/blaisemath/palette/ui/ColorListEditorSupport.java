package com.googlecode.blaisemath.palette.ui;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2019 Elisha Peterson
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

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Used to edit a list of colors. Provides custom hooks to  
 * 
 * @author Elisha Peterson
 */
public abstract class ColorListEditorSupport extends JPanel {
        
    protected final ColorList list = new ColorList();
    
    public ColorListEditorSupport() {
        setLayout(new BorderLayout());
        add(new JScrollPane(list), BorderLayout.CENTER);
        list.addPropertyChangeListener(evt -> updateModelStyles(list.getColorListModel().getColors()));
        list.setEditConstraints(new ColorListEditConstraints().keysEditable(false));
    }
    
    /**
     * Update the model content based on user edits in the UI.
     * @param styles list of styles from the UI
     */
    protected abstract void updateModelStyles(List<KeyColorBean> styles);
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    
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
