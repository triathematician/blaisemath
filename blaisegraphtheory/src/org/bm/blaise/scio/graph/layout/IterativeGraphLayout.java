/*
 * IterativeGraphLayout.java
 * Created Jul 9, 2010
 */

package org.bm.blaise.scio.graph.layout;

import java.awt.geom.Point2D;
import org.bm.blaise.scio.graph.Graph;

/**
 * Provides methods for a layout scheme that has several iterations. The layout
 * class may have internal parameters that are used to accomplish the layout, and
 * an internal state which changes over the course of the layout. The methods here
 * allow the user to reset the layout scheme and to iterate the layout scheme.
 * 
 * @author Elisha Peterson
 */
public interface IterativeGraphLayout {

    /**
     * Resets the layout algorithm's state, using the specified node locations
     * as starting locations. Implementations should check that there is one
     * location for each node in the graph.
     * @param g the graph
     * @param loc the locations for nodes in the graph
     */
    public void reset(Graph g, Point2D.Double[] loc);

    /** 
     * Iterate the energy layout algorithm, returning the locations of the nodes.
     * The list of nodes returned in order by the graphs <code>nodes()</code> method
     * corresponds to the order of the list of positions.
     * @param g the graph to layout
     * @return new positions for the nodes.
     */
    public Point2D.Double[] iterate(Graph g);

    /**
     * Returns the current list of point locations.
     * @return current list of point locations
     */
    public Point2D.Double[] getPoints();

}
