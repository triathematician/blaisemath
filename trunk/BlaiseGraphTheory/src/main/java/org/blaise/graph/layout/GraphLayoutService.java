/*
 * GraphLayoutService.java
 * Created on Jul 3, 2012
 */
package org.blaise.graph.layout;

import java.awt.geom.Point2D;
import java.util.Map;
import org.blaise.graph.Graph;

/**
 * Performs a graph layout service, starting with a graph, a set of initial positions,
 * and parameters, and returning a mapping of nodes to locations. Intermediate and final
 * positions are reported via the {@link GraphLayoutCallback} parameter provided to
 * the layout method.
 *
 * @author Elisha Peterson
 */
public interface GraphLayoutService {

    /**
     * Returns the layout algorithm being used by the service.
     * @param g the graph to layout
     * @return layout algorithm
     */
    public IterativeGraphLayout getLayoutAlgorithm(Graph g);

    /**
     * Generates layout for a graph. When the layout is completed, the callback provides a mechanism
     * for returning the results asynchronously.
     * @param graph the graph
     * @param ic initial conditions (positions on some nodes), can be null if there are none
     * @param callback function to be executed when results are returned
     */
    public void layout(Graph graph, Map<Object, Point2D.Double> ic, GraphLayoutCallback callback);


    //
    // IMPLEMENTATIONS
    //

    /** Implementation for a static layout */
    public static class StaticLayoutService implements GraphLayoutService {
        private final StaticGraphLayout layout;
        public StaticLayoutService(StaticGraphLayout layout) {
            this.layout = layout;
        }
        public void layout(Graph graph, Map<Object, Point2D.Double> ic, GraphLayoutCallback callback) {
            callback.layoutCompleted(graph, ic, layout.layout(graph));
        }
        public IterativeGraphLayout getLayoutAlgorithm(Graph g) {
            return null;
        }
    }

    
}
