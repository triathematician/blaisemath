/**
 * TimeGraphSlider.java
 * Created Feb 5, 2011
 */

package org.blaise.graph.view;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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
public final class LongitudinalGraphSlider extends JComponent
        implements PropertyChangeListener, ChangeListener {

    /** The manager of the time graph */
    private LongitudinalGraphManager manager;
    /** The list slider */
    private final ListSlider slider;

    /** Create a slider for the graph without a manager */
    public LongitudinalGraphSlider() {
        this(null);
    }

    /** Create a slider for the graph of the specified manager */
    public LongitudinalGraphSlider(LongitudinalGraphManager manager) {
        setManager(manager);
        slider = new ListSlider();
        slider.addChangeListener(this);
        setLayout(new java.awt.BorderLayout());
        add(slider, BorderLayout.CENTER);
    }

    /** @return manager for the slider */
    public LongitudinalGraphManager getManager() { return manager; }
    /** Sets manager for the slider */
    public void setManager(LongitudinalGraphManager m) {
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
