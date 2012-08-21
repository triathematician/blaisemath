/*
 * GraphFilterBar.java
 * Created Dec 20, 2010
 */

package graphexplorer.views;

import graphexplorer.controller.GraphControllerListener;
import graphexplorer.controller.GraphController;
import graphexplorer.controller.GraphControllerMaster;
import graphexplorer.controller.GraphFilterController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Provides access to the filtering mechanism for currently viewed graph.
 * 
 * @author elisha
 */
public class GraphFilterBar extends JPanel
        implements GraphControllerListener, ActionListener, ChangeListener {
    
    /** The filter controller */
    GraphFilterController gfc = null;

    /** The check box */
    JCheckBox enabledCB;
    /** The threshold */
    JSpinner threshSp;

    /** Sets up the panel without a controller */
    public GraphFilterBar() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(enabledCB = new JCheckBox("Filter by edge weight:"));
        add(threshSp = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000.0, 1.0)));
        setPreferredSize(new Dimension(250, getPreferredSize().height));
        setMaximumSize(new Dimension(300, 30));
        enabledCB.addActionListener(this);
        threshSp.addChangeListener(this);
        updateValues();
    }
    
    /** Sets up the panel with a controller */
    public GraphFilterBar(GraphController gc) { this(); setController(gc); }

    public void setController(GraphController gc) {
        if (gfc != gc.getFilterController()) {
            if (gfc != null)
                gfc.removePropertyChangeListener(this);
            gfc = gc.getFilterController();
            if (gfc != null)
                gfc.addPropertyChangeListener(this);
            updateValues();
        }
    }

    /** Called to update components with values from the controller */
    void updateValues() {
        if (gfc == null || !gfc.isApplicable()) {
            enabledCB.setSelected(false);
            enabledCB.setEnabled(false);
            threshSp.setEnabled(false);
        } else {
            boolean enabled = gfc.isEnabled();
            enabledCB.setSelected(enabled);
            enabledCB.setEnabled(true);
            if (enabled)
                threshSp.setValue(gfc.getFilterThreshold());
            threshSp.setEnabled(enabled);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        updateValues();
    }

    public void actionPerformed(ActionEvent e) {
        gfc.setEnabled(enabledCB.isSelected());
    }

    public void stateChanged(ChangeEvent e) {
        gfc.setFilterThreshold((Double) threshSp.getValue());
    }



}
