/*
 * GraphLocationUpdater.java
 * Created Aug 2012
 */
package org.blaise.graph.view;

import java.awt.geom.Point2D;
import java.util.Map;
import org.blaise.graph.Graph;

/** 
 * Object that listens for updates to a graph, either the entire graph or
 * just the locations of objects in the graph.
 * 
 * @author Elisha Peterson
 */
public interface GraphLocationUpdater {

    /**
     * Called when the entire graph is updated.
     * @param graph the new graph
     * @param points locations of nodes in the graph
     */
    public void graphUpdated(Graph graph, Map<?,Point2D.Double> points);

    /**
     * Called when locations only are updated.
     * @param points locations of nodes in the graph
     */
    public void locationsUpdated(Map<?,Point2D.Double> points);
    
    
    
    /**
     * Adapter class that can be sub-classed for custom functionality
     */
    public static class GraphLocationUpdaterAdapter implements GraphLocationUpdater {
        public void graphUpdated(Graph graph, Map<?,Point2D.Double> points) {}
        public void locationsUpdated(Map<?,Point2D.Double> points) {}
    }
    
}
