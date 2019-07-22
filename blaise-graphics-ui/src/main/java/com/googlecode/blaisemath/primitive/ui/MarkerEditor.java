package com.googlecode.blaisemath.primitive.ui;

/*
 * #%L
 * BlaiseGraphics
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

import com.googlecode.blaisemath.editor.MPanelEditorSupport;
import com.googlecode.blaisemath.graphics.swing.render.ShapeRenderer;
import com.googlecode.blaisemath.primitive.Marker;
import com.googlecode.blaisemath.primitive.Markers;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;

import javax.swing.*;
import java.awt.*;

/**
 * Provides combo box for selection of a preset marker shape.
 * 
 * @author Elisha Peterson
 */
public class MarkerEditor extends MPanelEditorSupport {
    
    /** Box used to select marker. */
    private final JComboBox<Marker> combo;
    
    /** Initialize the editor. */
    public MarkerEditor() {
        combo = new JComboBox<>();
        combo.setModel(new DefaultComboBoxModel(Markers.getAvailableMarkers().toArray()));
        combo.setRenderer(new MarkerCellRenderer(16));
    }

    @Override
    protected void initCustomizer() {
        panel = new JPanel(new BorderLayout());
        panel.add(combo);
        combo.setSelectedItem(getNewValue());
        combo.addActionListener(e -> setNewValue(combo.getSelectedItem()));
    }

    @Override
    protected void initEditorValue() {
        combo.setSelectedItem(getNewValue());
    }
    
    //region INNER CLASSES
    
    /**
     * Renders markers as icons in a list.
     */
    public static class MarkerCellRenderer extends DefaultListCellRenderer {

        private final int size;

        public MarkerCellRenderer(int sz) {
            this.size = sz;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.getClass().getSimpleName());
            setIcon(new MarkerIcon((Marker) value, size));
            return this;
        }
    }
    
    /**
     * Renders marker as an icon.
     */
    private static class MarkerIcon implements Icon {

        private final AttributeSet style = Styles.fillStroke(Color.white, Color.black, 1f);
        private final int size;
        private final Marker marker;

        public MarkerIcon(Marker m, int size) {
            this.marker = m;
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Shape sh = marker.create(new Point(size/2, size/2), 0, size/2f-1);
            ShapeRenderer.getInstance().render(sh, style, (Graphics2D) g);
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }        
    }
    
    //endregion
    
    
}
