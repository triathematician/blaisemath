/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package graphexplorer;

import java.awt.Component;
import java.awt.geom.Point2D;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.LongitudinalGraph;
import org.bm.blaise.scio.graph.layout.IterativeGraphLayout;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;

/**
 * Provides methods that actions classes can use.
 * 
 * @author elisha
 */
interface GraphExplorerInterface {

    /** Returns component-view for dialogs */
    Component component();

    /** Returns graph currently active in explorer */
    Graph activeGraph();

    /** Returns point locations of current nodes */
    Point2D.Double[] getActivePoints();
    /** Sets active point locations */
    void setActivePoints(Point2D.Double[] pos);

    /** Adds a message to the output window. */
    void output(String output);

    /** Loads a graph into the explorer, by creating a new tabbed window that displays it. */
    void loadGraph(Graph sg, String name);

    /** Loads a longitudinal graph into the explorer, by creating a new tabbed window that displays it. */
    void loadLongitudinalGraph(LongitudinalGraph lg, String name);

    /** Closes the active graph */
    void closeActiveGraph();

    /** Initializes grpah for static layout */
    void initLayout(StaticGraphLayout layout, double... parameters);
    /** Initializes graph for iterative layout */
    void initLayout(IterativeGraphLayout layout);
    /** Adjusts an iterative layout scheme on the fly, using specified layout mechanism. */
    void adjustLayout(StaticGraphLayout layout, double... parameters);
    /** Iterates active layout */
    void iterateLayout();
    /** Stops active layout */
    void stopLayout();

}
