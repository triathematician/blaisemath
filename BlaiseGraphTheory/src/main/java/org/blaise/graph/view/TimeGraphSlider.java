/**
 * TimeGraphSlider.java
 * Created Feb 5, 2011
 */

package org.blaise.graph.view;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.blaise.util.gui.ListSlider;

/**
 * A slider component that manages a longitudinal graph.
 * @author elisha
 */
public final class TimeGraphSlider extends JComponent
        implements PropertyChangeListener, ChangeListener {

    /** The manager of the time graph */
    private TimeGraphManager manager;
    /** The list slider */
    private final ListSlider slider;

    /** Create a slider for the graph without a manager */
    public TimeGraphSlider() {
        this(null);
    }

    /** Create a slider for the graph of the specified manager */
    public TimeGraphSlider(TimeGraphManager manager) {
        setManager(manager);
        slider = new ListSlider();
        slider.addChangeListener(this);
        setLayout(new java.awt.BorderLayout());
        add(slider, BorderLayout.CENTER);
    }

    /** @return manager for the slider */
    public TimeGraphManager getManager() { return manager; }
    /** Sets manager for the slider */
    public void setManager(TimeGraphManager m) {
        if (this.manager != m) {
            if (this.manager != null)
                this.manager.removePropertyChangeListener(this);
            this.manager = m;
            List<Double> times = manager.getTimeGraph().getTimes();
            slider.setList(times);
            slider.setValue(0);
            if (m != null)
                m.addPropertyChangeListener(this);
        }
    }

    /** Flag indicating that the manager is being updated from here; prevents looped property changes */
    boolean updating = false;

    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        if (prop.equals("timeData")) {
            if (updating)
                return;
            Object[] nue = (Object[]) evt.getNewValue();
            double time = (Double) nue[0];
            List<Double> times = manager.getTimeGraph().getTimes();
            slider.setList(times);
            for (int i = 0; i < times.size(); i++) {
                if (time == times.get(i)) {
                    slider.setValue(i);
                    break;
                }
            }
        }
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == slider) {
            double time = slider.getListValue();
            updating = true;
            manager.setTime(time);
            updating = false;
        }
    }

}
