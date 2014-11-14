/**
 * MarkerEditor.java
 * Created Oct 2014
 */
package com.googlecode.blaisemath.style.editor;

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
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
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
    
    private final Class<? extends Marker>[] MARKER_OPTIONS = new Class[]{
        Markers.NoShape.class, Markers.CircleShape.class,
        Markers.SquareShape.class, Markers.DiamondShape.class, Markers.TriangleShape.class,
        Markers.StarShape.class, Markers.Star7Shape.class, Markers.Star11Shape.class,
        Markers.PlusShape.class, Markers.CrossShape.class, Markers.CrosshairsShape.class,
        Markers.HappyFaceShape.class, Markers.HouseShape.class, Markers.SimpleArrowShape.class,
        Markers.TrianglePointerShape.class, Markers.TriangleFlagShape.class, Markers.TeardropShape.class,
        Markers.CarShape.class
    };
    private final JComboBox combo;
    
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
    
    private static class MarkerCellRenderer extends DefaultListCellRenderer {
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
    
    private static class MarkerIcon implements Icon {
        private final AttributeSet style = Styles.fillStroke(Color.white, Color.black);
        private final int size;
        private final Marker marker;

        public MarkerIcon(Marker m, int size) {
            this.marker = m;
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Shape sh = marker.create(new Point2D.Double(size/2, size/2), 0, size/2-1);
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
    
    
}
