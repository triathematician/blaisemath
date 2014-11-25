/**
 * TimeGraphManager.java
 * Created Feb 5, 2011
 */

package com.googlecode.blaisemath.graph.view;

/*
 * #%L
 * BlaiseGraphTheory
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

import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import com.googlecode.blaisemath.graph.longitudinal.LongitudinalGraph;
import com.googlecode.blaisemath.graph.longitudinal.SimultaneousLayout;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages visual display of a time graph (slices over time). Keeps track of slices
 * of the graph using {@link GraphManager}s. Notifies listeners when the
 * active slice has changed.
 *
 * @author elisha
 * 
 * @todo review thread safety
 */
public class LongitudinalGraphManager {

    // LAYOUT CONSTANTS
    
    /** Default time between layout iterations. */
    private static final int DEFAULT_DELAY = 10;
    /** Default # iterations per layout step */
    private static final int DEFAULT_ITER = 2;
    /** The initial layout scheme */
    private static final StaticGraphLayout INITIAL_LAYOUT = StaticGraphLayout.CIRCLE;
    /** The initial layout parameters */
    private static final double[] LAYOUT_PARAMETERS = { 3.0 };
    
    // STATE VARIABLES
    
    /** The time graph */
    private final LongitudinalGraph tGraph;
    /** The active time */
    private double time;
    /** The active slice */
    private Graph slice;
    
    /** Performs layout on multiple slices at once. */
    private SimultaneousLayout layout = null;
    /** Timer that performs iterative layout */
    private java.util.Timer layoutTimer;
    /** Timer task */
    private java.util.TimerTask layoutTask;

    /** Handles property change events */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    
    /** Constructs manager for the specified graph */
    public LongitudinalGraphManager(LongitudinalGraph graph) {
        if (graph == null)
            throw new IllegalArgumentException("Graph cannot be null.");
        this.tGraph = graph;
        setTime(graph.getMinimumTime(), true);
    }
    

    // <editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /** 
     * Get the manager's graph (immutable) 
     */
    public LongitudinalGraph getTimeGraph() { 
        return tGraph;
    }

    /** 
     * Get time of the current slice
     */
    public double getTime() {
        return time;
    }

    /**
     * Sets the graph slice to the time closest to that specified.
     */
    public void setTime(double time) {
        setTime(time, false);
    }

    /**
     * Sets the active time, which determines the currently active graph
     */
    public void setTime(double time, boolean exact) {
        if (this.time != time) {
            this.time = time;
            slice = tGraph.slice(time, exact);
            fireTimeChanged();
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
    
    /** Use to overwrite the default graph slice selection. Use with caution!! */
    public void setSlice(Graph slice) {
        this.slice = slice;
    }
    
    /**
     * Return time data, an array including the time and the node positions as a map
     * @return time data (time, node positions as a map)
     */
    public Object[] getTimeData() {
        return new Object[]{ time, layout == null ? null : layout.getPositionMap(time) };
    }
    
    /**
     * Return node positions
     * @return node positions
     */
    public Map<Object, Point2D.Double> getNodePositions() {
        return layout.getPositionMap(getTime());
    }
    
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Layout Methods">

    /** 
     * Get current iterative layout algorithm 
     */
    public SimultaneousLayout getLayoutAlgorithm() { 
        return layout; 
    }
    
    /** 
     * Initialize iterative layout algorithm 
     */
    public void initLayoutAlgorithm() {
        if (layout == null) {
            layout = new SimultaneousLayout(tGraph);
        }
    }

    /** 
     * Return true if an iterative layout is active 
     */
    public boolean isLayoutAnimating() {
        return layoutTask != null;
    }

    /** 
     * Sets layout to animate or stop animating
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
    public void iterateLayout() {
        initLayoutAlgorithm();
        long t0 = System.currentTimeMillis();
        try {
            layout.iterate();
        } catch (ConcurrentModificationException ex) {
            Logger.getLogger(LongitudinalGraphManager.class.getName()).log(Level.SEVERE, 
                    "Failed layout iteration", ex);
        }
        long t1 = System.currentTimeMillis();
        firePositionChanged();
        long t2 = System.currentTimeMillis();
        if ((t1-t0) > 100) {
            Logger.getLogger(LongitudinalGraphManager.class.getName()).log(Level.WARNING,
                    "Long Iterative Layout Step: {0}ms", (t1-t0));
        }
        if ((t2-t1) > 100) {
            Logger.getLogger(LongitudinalGraphManager.class.getName()).log(Level.WARNING, 
                    "Long Position Update: {0}ms", (t2-t1));
        }
    }

    /**
     * Activates the layout timer, if an iterative layout has been provided.
     * @param delay delay in ms between layout calls
     */
    public void startLayoutTask(int delay, final int iter) {
        initLayoutAlgorithm();
        if (layoutTimer == null) {
            layoutTimer = new java.util.Timer();
        }
        stopLayoutTask();
        layoutTask = new TimerTask() {
            @Override public void run() {
                for(int i=0;i<iter;i++) {
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
    
    /** The entire graph has changed: nodes, positions, edges */
    private void fireTimeChanged() {
        pcs.firePropertyChange("timeData", null, getTimeData());
    }
    /** The layout algorithm has started/stopped its timer */
    private void fireAnimatingChanged() {
        boolean val = isLayoutAnimating();
        pcs.firePropertyChange("layoutAnimating", !val, val);
    }
    /** The node positions have changed */
    private void firePositionChanged() {
        pcs.firePropertyChange("nodePositions", null, getNodePositions());
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
