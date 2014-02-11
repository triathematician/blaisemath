/*
 * BasicPointStyleEditor.java
 * Created Aug 28, 2011
 */
package org.blaise.visometry;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.beans.Customizer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.blaise.firestarter.editor.ColorEditor;
import org.blaise.style.PointStyleBasic;
import org.blaise.style.Marker;
import org.blaise.style.MarkerLibrary;

/**
 * GUI form for editing a {@link PointStyleBasic}.
 *
 * @author elisha
 */
public class BasicPointStyleEditor extends JPanel implements Customizer,
        ActionListener, ChangeListener, PropertyChangeListener {

    /** The style being edited */
    private PointStyleBasic rend = new PointStyleBasic();

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
    public BasicPointStyleEditor(PointStyleBasic bean) {
        initComponents();
        setObject(bean);
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
        add(shapeCombo = new JComboBox(MarkerLibrary.getAvailableMarkers().toArray()), gbc);
        shapeCombo.setRenderer(new ShapeListCellRenderer());
        shapeCombo.addActionListener(this);

        setObject(rend);
        validate();
    }

    public PointStyleBasic getObject() {
        return rend;
    }

    public void setObject(Object bean) {
        if (!(bean instanceof PointStyleBasic)) {
            throw new IllegalArgumentException();
        }

        this.rend = (PointStyleBasic) bean;
        radiusSp.setValue(rend.getMarkerRadius());
        strokeSp.setValue(rend.getStrokeWidth());
        fillEd.setValue(rend.getFill());
        strokeEd.setValue(rend.getStroke());
        for (int i = 0; i < shapeCombo.getItemCount(); i++) {
            if (shapeCombo.getItemAt(i).getClass() == rend.getMarker().getClass()) {
                shapeCombo.setSelectedIndex(i);
            }
        }
    }


    //
    // EVENT HANDLING
    //

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == shapeCombo) {
            rend.setMarker((Marker) shapeCombo.getSelectedItem());
            firePropertyChange("style", null, rend);
        }
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == radiusSp) {
            rend.setMarkerRadius(((Number)radiusSp.getValue()).floatValue());
        } else if (e.getSource() == strokeSp) {
            rend.setStrokeWidth(((Number)strokeSp.getValue()).floatValue());
        } else {
            return;
        }
        shapeCombo.repaint();
        firePropertyChange("style", null, rend);
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getSource() == fillEd) {
            rend.setFill((Color) (fillEd.getNewValue() == null ? fillEd.getValue() : fillEd.getNewValue()));
        } else if (e.getSource() == strokeEd) {
            rend.setStroke((Color) (strokeEd.getNewValue() == null ? strokeEd.getValue() : strokeEd.getNewValue()));
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
            Marker shape1 = rend.getMarker();
            rend.setMarker(shape);
            rend.draw(new Point2D.Double(xc, yc), (Graphics2D) g, null);
            rend.setMarker(shape1);
        }

        public int getIconWidth() { return 50; }
        public int getIconHeight() { return 50; }
    }

}
