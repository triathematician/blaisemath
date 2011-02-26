/*
 * LongitudinalGraphController.java
 * Created Nov 18, 2010
 */

package graphexplorer.controller;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.TreeMap;
import org.bm.blaise.scio.graph.FilteredWeightedGraph;
import org.bm.blaise.scio.graph.LongitudinalGraph;
import org.bm.blaise.scio.graph.WeightedGraph;

/**
 * <p>
 *   Specialized controller for longitudinal graphs. In a longitudinal graph,
 *   the base graph is determined by a specific point in time.
 * </p>
 *
 * @author elisha
 */
public class LongitudinalGraphController extends GraphController {

    //
    // PROPERTY CHANGE NAMES
    //
    
    /** Change in longitudinal graph */
    public static final String $LONGITUDINAL = "longitudinal";
    /** Change in time slice */
    public static final String $TIME = "time";

    //
    // PROPERTIES
    //

    /** Longitudinal graph (should be null if not in a longitudinal state) */
    private LongitudinalGraph longGraph = null;
    /** Time of slice in a longitudinal graph (should be null if not in a longitudinal state) */
    private Double time = null;

    //
    // CONSTRUCTORS & FACTORY METHODS
    //
    
    /** Construct instance of a longitudinal controller */
    public LongitudinalGraphController() {
    }

    /** Construct with specified graph */
    public LongitudinalGraphController(LongitudinalGraph graph) {
        super();
        setLongitudinalGraph(graph);
    }

    /** Construct with specified graph and name */
    public LongitudinalGraphController(LongitudinalGraph graph, String name) {
        super();
        setName(name);
        setLongitudinalGraph(graph);
    }

    //
    // INITIALIZERS & UTILITIES
    //

    @Override
    public String toString() {
        return "LongitudinalGraphController";
    }

    //
    // PROPERTY PATTERNS
    //

    /** @return the longitudinal graph */
    public LongitudinalGraph getLongitudinalGraph() {
        return longGraph;
    }

    /** Sets the longitudinal graph */
    public void setLongitudinalGraph(LongitudinalGraph lGraph) {
        if (this.longGraph != lGraph) {
            LongitudinalGraph oldGraph = this.longGraph;
            this.longGraph = lGraph;
            setTime(longGraph.getMinimumTime());
            setBaseGraph(longGraph.slice(time, false));
            pcs.firePropertyChange($LONGITUDINAL, oldGraph, longGraph);
        }
    }

    /** @return time for l-graph */
    public double getTime() { 
        return time;
    }


    /**
     * Sets the time slice of the longitudinal graph. If the longitudinal graph
     * does not contain a slice at the specified time, returns a graph at the
     * closest available time; the time stored is then the exact time of the slice.
     * @param time the new time
     * @throws IllegalStateException if not in longitudinal state
     */
    public void setTime(double time) {
        if (this.time == null || this.time != time) {
            double oldTime = this.time;
            this.time = time;
            setBaseGraph(longGraph.slice(time, false));
            pcs.firePropertyChange($TIME, oldTime, time);
        }
    }

}
