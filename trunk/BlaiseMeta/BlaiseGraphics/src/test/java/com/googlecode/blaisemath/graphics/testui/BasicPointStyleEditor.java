/*
 * BasicPointStyleEditor.java
 * Created Aug 28, 2011
 */
package com.googlecode.blaisemath.graphics.testui;

/*
 * #%L
 * BlaiseVisometry
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

import com.googlecode.blaisemath.editor.ColorEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.beans.Customizer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.googlecode.blaisemath.graphics.swing.PointRenderer;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Marker;
import com.googlecode.blaisemath.style.Markers;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * GUI form for editing a {@link PointRenderer}.
 *
 * @author elisha
 */
public class BasicPointStyleEditor extends JPanel implements Customizer,
        ActionListener, ChangeListener, PropertyChangeListener {

    /** The style being edited */
    private AttributeSet rend = Styles.defaultPointStyle();

    /** Spinner for radius */
    private JSpinner radiusSp = null;
    /** Spinner for stroke */
    private JSpinner strokeSp = null;
    /** Color editor for fill */
    private ColorEditor fillEd = null;
    /** Color editor for stroke */
    private ColorEditor strokeEd = null;
    /** Combo box for shapes */
    private JComboBox shapeCombo = null;

    /** Initialize with defaults */
    public BasicPointStyleEditor() {
        initComponents();
    }

    /** Initialize with defaults and a style */
    public BasicPointStyleEditor(AttributeSet style) {
        initComponents();
        setObject(style);
    }

    /** Sets up the panel */
    private void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.ipadx = 3; gbc.ipady = 1;
        add(new JLabel("Radius:"), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        SpinnerNumberModel m1 = new SpinnerNumberModel(5.0, 0.0, 1000.0, 1.0);
        add(radiusSp = new JSpinner(m1), gbc);
        radiusSp.setToolTipText("Radius of point");
        radiusSp.addChangeListener(this);

        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel(" Fill:"), gbc);
        add((fillEd = new ColorEditor()).getCustomEditor(), gbc);
        fillEd.addPropertyChangeListener(this);

        gbc.gridy = 1;
        add(new JLabel("Outline:"), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        SpinnerNumberModel m2 = new SpinnerNumberModel(1.0, 0.0, 50.0, 0.5);
        add(strokeSp = new JSpinner(m2), gbc);
        strokeSp.setToolTipText("Width of stroke");
        strokeSp.addChangeListener(this);

        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel(" Stroke:"), gbc);
        add((strokeEd = new ColorEditor()).getCustomEditor(), gbc);
        strokeEd.addPropertyChangeListener(this);

        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0; gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(shapeCombo = new JComboBox(Markers.getAvailableMarkers().toArray()), gbc);
        shapeCombo.setRenderer(new ShapeListCellRenderer());
        shapeCombo.addActionListener(this);

        setObject(rend);
        validate();
    }

    public AttributeSet getObject() {
        return rend;
    }

    public void setObject(Object bean) {
        if (!(bean instanceof AttributeSet)) {
            throw new IllegalArgumentException();
        }

        this.rend = (AttributeSet) bean;
        radiusSp.setValue(rend.getFloat(Styles.MARKER_RADIUS));
        strokeSp.setValue(rend.getFloat(Styles.STROKE_WIDTH));
        fillEd.setValue(rend.getColor(Styles.FILL));
        strokeEd.setValue(rend.getColor(Styles.STROKE));
        for (int i = 0; i < shapeCombo.getItemCount(); i++) {
            if (shapeCombo.getItemAt(i).getClass() == rend.get(Styles.MARKER).getClass()) {
                shapeCombo.setSelectedIndex(i);
            }
        }
    }


    //
    // EVENT HANDLING
    //

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == shapeCombo) {
            rend.put(Styles.MARKER, (Marker) shapeCombo.getSelectedItem());
            firePropertyChange("style", null, rend);
        }
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == radiusSp) {
            rend.put(Styles.MARKER_RADIUS, ((Number)radiusSp.getValue()).floatValue());
        } else if (e.getSource() == strokeSp) {
            rend.put(Styles.STROKE_WIDTH, ((Number)strokeSp.getValue()).floatValue());
        } else {
            return;
        }
        shapeCombo.repaint();
        firePropertyChange("style", null, rend);
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getSource() == fillEd) {
            rend.put(Styles.FILL, (Color) (fillEd.getNewValue() == null ? fillEd.getValue() : fillEd.getNewValue()));
        } else if (e.getSource() == strokeEd) {
            rend.put(Styles.STROKE, (Color) (strokeEd.getNewValue() == null ? strokeEd.getValue() : strokeEd.getNewValue()));
        } else {
            return;
        }
        shapeCombo.repaint();
        firePropertyChange("style", null, rend);
    }


    //
    // INNER CLASSES
    //

    /** Draws elements of the list using the settings elsewhere. */
    private class ShapeListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel result = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            result.setToolTipText(value.toString());
            result.setText(null);
            result.setIcon(new ShapeIcon((Marker)value));
            return result;
        }
    }

    /** Icon for drawing stylized point on a component */
    private class ShapeIcon implements Icon {
        private final Marker shape;
        private ShapeIcon(Marker shape) {
            this.shape = shape;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double xc = c.getWidth()/2.0, yc = c.getHeight()/2.0;
            Marker shape1 = (Marker) rend.get(Styles.MARKER);
            rend.put(Styles.MARKER, shape);
            PointRenderer.getInstance().render(new Point2D.Double(xc, yc), rend, (Graphics2D) g);
            rend.put(Styles.MARKER, shape1);
        }

        public int getIconWidth() { return 50; }
        public int getIconHeight() { return 50; }
    }

}
