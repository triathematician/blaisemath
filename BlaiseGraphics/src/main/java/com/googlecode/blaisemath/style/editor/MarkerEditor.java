/**
 * MarkerEditor.java
 * Created Oct 2014
 */
package com.googlecode.blaisemath.style.editor;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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
import com.googlecode.blaisemath.graphics.swing.ShapeRenderer;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Marker;
import com.googlecode.blaisemath.style.Markers;
import com.googlecode.blaisemath.style.Styles;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 * Provides combo box for selection of a preset marker shape.
 * 
 * @author Elisha Peterson
 */
public class MarkerEditor extends MPanelEditorSupport {
    
    /** Static array of classes displayed as options. */
    private static final Class<? extends Marker>[] MARKER_OPTIONS = new Class[]{
        Markers.NoShape.class, Markers.CircleShape.class,
        Markers.SquareShape.class, Markers.DiamondShape.class, Markers.TriangleShape.class,
        Markers.StarShape.class, Markers.Star7Shape.class, Markers.Star11Shape.class,
        Markers.PlusShape.class, Markers.CrossShape.class, Markers.CrosshairsShape.class,
        Markers.HappyFaceShape.class, Markers.HouseShape.class, Markers.SimpleArrowShape.class,
        Markers.TrianglePointerShape.class, Markers.TriangleFlagShape.class, Markers.TeardropShape.class,
        Markers.CarShape.class
    };
    
    /** Box used to select marker. */
    private final JComboBox combo;
    
    /** Initialize the editor. */
    public MarkerEditor() {
        combo = new JComboBox();
        combo.setModel(new DefaultComboBoxModel(MARKER_OPTIONS));
        combo.setRenderer(new MarkerCellRenderer());
    }

    @Override
    protected void initCustomizer() {
        combo.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Class c = (Class) combo.getSelectedItem();
                try {
                    setNewValue(c.newInstance());
                } catch (InstantiationException ex) {
                    Logger.getLogger(MarkerEditor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(MarkerEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        combo.setSelectedItem(getNewValue().getClass());
        panel = new JPanel(new BorderLayout());
        panel.add(combo);
    }

    @Override
    protected void initEditorValue() {
        combo.setSelectedItem(getNewValue());
    }
    
    //<editor-fold defaultstate="collapsed" desc="INNER CLASSES">
    
    /**
     * Renders markers as icons in a list.
     */
    public static class MarkerCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Class<? extends Marker> m = (Class<? extends Marker>) value;
            setText(m.getSimpleName());
            try {
                setIcon(new MarkerIcon(m.newInstance(), 16));
            } catch (InstantiationException ex) {
                Logger.getLogger(MarkerEditor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(MarkerEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            Shape sh = marker.create(new Point(size/2, size/2), 0, size/2-1);
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
    
    //</editor-fold>
    
    
}
