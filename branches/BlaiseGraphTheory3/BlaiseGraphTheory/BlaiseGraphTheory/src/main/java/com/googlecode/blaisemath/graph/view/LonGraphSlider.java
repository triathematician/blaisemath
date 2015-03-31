/**
 * LongitudinalGraphSlider.java
 * Created Feb 5, 2011
 */

package com.googlecode.blaisemath.graph.view;

/*
 * #%L
 * BlaiseGraphTheory
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

import com.googlecode.blaisemath.util.swing.ListSlider;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A slider component that manages a longitudinal graph.
 * @author elisha
 */
public final class LonGraphSlider extends JComponent {

    /** The manager of the time graph */
    private LonGraphManager manager;
    /** The list slider */
    private final ListSlider slider;
    
    /** Listens for changes to longitudinal graph */
    private final PropertyChangeListener managerListener;

    /** Create a slider for the graph without a manager */
    public LonGraphSlider() {
        this(null);
    }

    /** 
     * Create a slider for the graph of the specified manager.
     * @param manager graph manager
     */
    public LonGraphSlider(LonGraphManager manager) {
        managerListener = new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String prop = evt.getPropertyName();
                if (prop.equals("time")) {
                    Double time = (Double) evt.getNewValue();
                    List<Double> times = LonGraphSlider.this.manager.getTimeGraph().getTimes();
                    slider.setList(times);
                    slider.setListValue(time);
                }
            }
        };
        setManager(manager);
        slider = new ListSlider();
        slider.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                double time = slider.getListValue();
                LonGraphSlider.this.manager.setTime(time);
            }
        });
        setLayout(new java.awt.BorderLayout());
        add(slider, BorderLayout.CENTER);
    }

    /** @return manager for the slider */
    public LonGraphManager getManager() {
        return manager;
    }

    /**
     * Sets manager for the slider
     * @param m graph manager
     */
    public void setManager(LonGraphManager m) {
        if (this.manager != m) {
            if (this.manager != null) {
                this.manager.removePropertyChangeListener(managerListener);
            }
            this.manager = m;
            List<Double> times = manager.getTimeGraph().getTimes();
            slider.setList(times);
            slider.setValue(0);
            if (m != null) {
                m.addPropertyChangeListener(managerListener);
            }
        }
    }

}
