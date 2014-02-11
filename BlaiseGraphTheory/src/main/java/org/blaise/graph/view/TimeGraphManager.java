/**
 * TimeGraphManager.java
 * Created Feb 5, 2011
 */

package org.blaise.graph.view;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.TimerTask;
import org.blaise.graph.Graph;
import org.blaise.graph.dynamic.SimultaneousLayout;
import org.blaise.graph.dynamic.TimeGraph;
import org.blaise.graph.layout.StaticGraphLayout;

/**
 * Manages visual display of a time graph (slices over time). Keeps track of slices
 * of the graph using <code>GraphManager</code>s. Notifies listeners when the
 * active slice has changed.
 *
 * @author elisha
 */
public class TimeGraphManager {
    
    /** The time graph */
    private final TimeGraph tGraph;
    /** The active time */
    private double time;
    /** The active slice */
    private Graph slice;

    
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /** Constructs manager for the specified graph */
    public TimeGraphManager(TimeGraph graph) {
        if (graph == null)
            throw new IllegalArgumentException("Graph cannot be null.");
        this.tGraph = graph;
        setTime(graph.getMinimumTime(), true);
    }
    // </editor-fold>
    

    // <editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /** @return the manager's graph (immutable) */
    public TimeGraph getTimeGraph() { return tGraph; }

    /** @return time of the current slice */
    public double getTime() { return time; }
    /** Sets the graph slice to the time closest to that specified. */
    public void setTime(double time) { setTime(time, false); }
    /** Sets the active time, which determines the currently active graph */
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
    public synchronized Object[] getTimeData() {
        return new Object[]{ time, layout == null ? null : layout.getPositionMap(time) };
    }
    
    /**
     * Return node positions
     * @return node positions
     */
    public synchronized Map<Object, Point2D.Double> getNodePositions() {
        return layout.getPositionMap(getTime());
    }
    
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Layout Methods">

    /** Default time between layout iterations. */
    private static final int DEFAULT_DELAY = 10;
    /** Default # iterations per layout step */
    private static final int DEFAULT_ITER = 2;
    /** The initial layout scheme */
    private static final StaticGraphLayout INITIAL_LAYOUT = StaticGraphLayout.CIRCLE;
    /** The initial layout parameters */
    private static final double[] LAYOUT_PARAMETERS = new double[] { 3 };

    /** Synchronized layout of a time graph */
    SimultaneousLayout layout = null;

    /** Timer that performs iterative layout */
    private transient java.util.Timer layoutTimer;
    /** Timer task */
    private transient java.util.TimerTask layoutTask;

    /** @return current iterative layout algorithm */
    public SimultaneousLayout getLayoutAlgorithm() { return layout; }
    /** @return current iterative layout algorithm */
    public void initLayoutAlgorithm() {
        if (layout == null)
            layout = new SimultaneousLayout(tGraph);
    }

    /** @return true if an iterative layout is active */
    public boolean isLayoutAnimating() {
        return layoutTask != null;
    }

    /** Sets layout to animate */
    public void setLayoutAnimating(boolean value) {
        boolean old = layoutTask != null;
        if (value != old) {
            if (value)
                startLayoutTask(DEFAULT_DELAY, DEFAULT_ITER);
            else
                stopLayoutTask();
            fireAnimatingChanged();
        }
    }

    private static int _i = 0;
    private static int _p = 0;

    /** Iterates layout, if an iterative layout has been provided. */
    public synchronized void iterateLayout() {
//        System.out.println("Starting layout");
        if (layout == null)
            initLayoutAlgorithm();
        if (layout != null) {
            long t0 = System.currentTimeMillis();
            try {
                layout.iterate();
            } catch (ConcurrentModificationException ex) {
                System.err.println("Failed Layout Iteration: ConcurrentModificationException");
            }
            long t1 = System.currentTimeMillis();
            firePositionChanged();
            long t2 = System.currentTimeMillis();
            if ((t1-t0) > 100)
                System.err.println("Long Iterative Layout Step " + (++_i) + ": " + (t1-t0) + "ms");
            if ((t2-t1) > 100)
                System.err.println("Long Position Update " + (++_p) + ": " + (t2-t1) + "ms");
        }
//        System.out.println("... layout complete");
    }

    /**
     * Activates the layout timer, if an iterative layout has been provided.
     * @param delay delay in ms between layout calls
     */
    public void startLayoutTask(int delay, final int iter) {
        if (layout == null)
            initLayoutAlgorithm();
        if (layoutTimer == null)
            layoutTimer = new java.util.Timer();
        stopLayoutTask();
        layoutTask = new TimerTask() {
            @Override public void run() {
                for(int i=0;i<iter;i++)
                    iterateLayout();
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
    private synchronized void fireTimeChanged() {
        pcs.firePropertyChange("timeData", null, getTimeData());
    }
    /** The layout algorithm has started/stopped its timer */
    private synchronized void fireAnimatingChanged() {
        boolean val = isLayoutAnimating();
        pcs.firePropertyChange("layoutAnimating", !val, val);
    }
    /** The node positions have changed */
    private synchronized void firePositionChanged() {
        pcs.firePropertyChange("nodePositions", null, getNodePositions());
    }

    /** Handles property change events */
    PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) { pcs.removePropertyChangeListener(propertyName, listener); }
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) { pcs.removePropertyChangeListener(listener); }
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) { pcs.addPropertyChangeListener(propertyName, listener); }
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) { pcs.addPropertyChangeListener(listener); }

    // </editor-fold>
}
