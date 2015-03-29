/**
 * TimeGraphManager.java
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

import static com.google.common.base.Preconditions.checkNotNull;
import com.googlecode.blaisemath.annotation.InvokedFromThread;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.lon.LonGraph;
import com.googlecode.blaisemath.graph.lon.SimultaneousLayout;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.TimerTask;
import javax.annotation.concurrent.GuardedBy;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * <p>
 *   Manages visual display of a time graph (slices over time). Notifies listeners when the
 *   active slice has changed.
 *</p>
 * <p>
 *   Supports a "simultaneous layout" algorithm that positions multiple time slices
 *   at once. This layout operates in a background thread, and updates positions
 *   each time it completes.
 * </p>
 * 
 * @author elisha
 */
public final class LongitudinalGraphManager {
    
    /** Default time between layout iterations. */
    private static final int DEFAULT_DELAY = 10;
    /** Default # iterations per layout step */
    private static final int DEFAULT_ITER = 2;
    
    /** The time graph */
    private final LonGraph tGraph;
    /** The active time */
    private double time;
    /** The active slice */
    private Graph slice;
    
    /** Performs layout on multiple slices at once. */
    @GuardedBy("itself")
    private final SimultaneousLayout layout;
    /** Timer that performs iterative layout */
    private java.util.Timer layoutTimer;
    /** Timer task */
    private java.util.TimerTask layoutTask;

    /** Handles property change events. Dispatches events on EDT. */
    private final PropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

    
    /** 
     * Constructs manager for the specified graph
     * @param graph the graph
     */
    public LongitudinalGraphManager(LonGraph graph) {
        this.tGraph = checkNotNull(graph);
        this.layout = new SimultaneousLayout(tGraph);
        setTime(graph.getMinimumTime(), true);
    }
    

    // <editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /** 
     * Get the manager's graph (immutable)
     * @return graph
     */
    public LonGraph getTimeGraph() { 
        return tGraph;
    }

    /** 
     * Get time of the current slice
     * @return time
     */
    public double getTime() {
        return time;
    }

    /**
     * Sets the graph slice to the time closest to that specified.
     * @param time slice time
     */
    public void setTime(double time) {
        setTime(time, false);
    }

    /**
     * Sets the active time, which determines the currently active graph
     * @param time slice time
     * @param exact whether time must be exact
     */
    public void setTime(double time, boolean exact) {
        Object old = this.time;
        if (this.time != time) {
            Map pos;
            synchronized (layout) {
                this.time = time;
                slice = tGraph.slice(time, exact);
                pos = layout.getPositionsCopy(time);
            }
            pcs.firePropertyChange("time", old, time);
            pcs.firePropertyChange("nodePositions", null, pos);
        }
    }
    
    /** 
     * Return current graph slice
     * @return the current slice 
     */
    public Graph getSlice() { 
        if (slice == null) {
            slice = tGraph.slice(time, false);
        }
        return slice;
    }
    
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Layout Methods">

    /** 
     * Get current iterative layout algorithm 
     * @return layout algorithm
     */
    public SimultaneousLayout getLayoutAlgorithm() { 
        return layout; 
    }
    
    /** 
     * Return true if an iterative layout is active 
     * @return true if animating
     */
    public boolean isLayoutAnimating() {
        return layoutTask != null;
    }

    /** 
     * Sets layout to animate or stop animating
     * @param value true if animating
     */
    public void setLayoutAnimating(boolean value) {
        boolean old = layoutTask != null;
        if (value != old) {
            if (value) {
                startLayoutTask(DEFAULT_DELAY, DEFAULT_ITER);
            } else {
                stopLayoutTask();
            }
            fireAnimatingChanged();
        }
    }

    /** Iterates layout, if an iterative layout has been provided. */
    @InvokedFromThread("multiple")
    public void iterateLayout() {
        Map pos;
        synchronized (layout) {
            layout.iterate();
            pos = layout.getPositionsCopy(time);
        }
        pcs.firePropertyChange("nodePositions", null, pos);
    }

    /**
     * Activates the layout timer, if an iterative layout has been provided.
     * @param delay delay in ms between layout calls
     * @param iter # of iterations per task execution
     */
    public void startLayoutTask(int delay, final int iter) {
        if (layoutTimer == null) {
            layoutTimer = new java.util.Timer();
        }
        stopLayoutTask();
        layoutTask = new TimerTask() {
            @Override 
            public void run() {
                for (int i = 0; i < iter; i++) {
                    iterateLayout();
                }
            }
        };
        layoutTimer.schedule(layoutTask, delay, delay);
    }

    /** Stops the layout timer */
    public void stopLayoutTask() {
        if (layoutTask != null) {
            layoutTask.cancel();
            layoutTask = null;
        }
    }
    // </editor-fold>

    
    // <editor-fold defaultstate="collapsed" desc="Event Handling">
    
    /** The layout algorithm has started/stopped its timer */
    private void fireAnimatingChanged() {
        boolean val = isLayoutAnimating();
        pcs.firePropertyChange("layoutAnimating", !val, val);
    }
    
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) { pcs.addPropertyChangeListener(listener); }

    // </editor-fold>
}
