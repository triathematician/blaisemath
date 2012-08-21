/*
 * GraphMetricBar.java
 * Created Dec 20, 2010
 */

package graphexplorer.views;

import graphexplorer.controller.GraphControllerListener;
import graphexplorer.actions.ExplorerStatActions;
import graphexplorer.controller.GraphController;
import graphexplorer.controller.GraphStatController;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Provides access to node metric computation algorithms.
 * 
 * @author elisha
 */
public class GraphMetricBar extends JPanel
        implements GraphControllerListener, ActionListener {
    
    /** The stat controller */
    GraphStatController gsc = null;

    /** The label */
    JLabel label;
    /** The threshold */
    JComboBox metricCB;

    /** Sets up the panel without a controller */
    public GraphMetricBar() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(label = new JLabel("Node metric:"));
        add(metricCB = new JComboBox(
                new DefaultComboBoxModel(ExplorerStatActions.StatEnum.values())
                ));
        setPreferredSize(new Dimension(250, getPreferredSize().height));
        setMaximumSize(new Dimension(300, 30));
        metricCB.addActionListener(this);
        setToolTipText("Compute and display the specified metric.");
        updateValues();
    }
    
    /** Sets up the panel with a controller */
    public GraphMetricBar(GraphController gc) { this(); setController(gc); }

    public void setController(GraphController gc) {
        if (gsc != gc.getStatController()) {
            if (gsc != null)
                gsc.removePropertyChangeListener(this);
            gsc = gc.getStatController();
            if (gsc != null)
                gsc.addPropertyChangeListener(this);
            updateValues();
        }
    }

    /** Called to update components with values from the controller */
    void updateValues() {
        if (gsc == null) {
            metricCB.setEnabled(false);
        } else {
            metricCB.setEnabled(true);
            metricCB.setSelectedItem(ExplorerStatActions.StatEnum.itemOf(gsc.getMetric()));
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(GraphStatController.$METRIC))
            updateValues();
    }

    public void actionPerformed(ActionEvent e) {
        gsc.setMetric(((ExplorerStatActions.StatEnum)metricCB.getSelectedItem()).getMetric());
    }

}
