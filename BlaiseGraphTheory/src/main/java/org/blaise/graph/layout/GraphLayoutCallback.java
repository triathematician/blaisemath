/*
 * GraphLayoutCallback.java
 * Created on Feb 13, 2012
 */
package org.blaise.graph.layout;

import java.awt.geom.Point2D;
import java.util.Map;
import org.blaise.graph.Graph;

/**
 * Contains an asynchronous callback method to be used in conjunction
 * with {@link GraphLayoutService}.
 *
 * @author petereb1
 */
public interface GraphLayoutCallback {

    /**
     * Can be used to return intermediate results of a layout algorithm. May or may
     * not be called by a {@link GraphLayoutService}.
     * @param graph the original graph
     * @param steps # of steps completed
     * @param energy energy level
     * @param positions positions returned in most recent layout iteration
     */
    public void intermediateLayout(Graph graph, int steps, float energy, Map<Object, Point2D.Double> positions);



    /**
     * Called when layout results are completed
     * @param graph the original graph
     * @param ic the initial conditions
     * @param result the resulting positions
     */
    public void layoutCompleted(Graph graph, Map<Object, Point2D.Double> ic, Map<Object, Point2D.Double> result);

}
