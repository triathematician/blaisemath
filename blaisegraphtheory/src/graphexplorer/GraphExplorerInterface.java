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

    /** Returns thisComponent-view for dialogs */
    Component thisComponent();
    /** @return the currently active panel containing the graph plot window */
    public Component activePanel();
    /** Returns graph currently active in explorer */
    Graph activeGraph();

    /** @return true if current active graph is a longitudinal one */
    public boolean isLongitudinal();

    /** Adds a message to the output window. */
    void output(String output);

    /** Loads a graph into the explorer, by creating a new tabbed window that displays it. */
    void loadGraph(Graph sg, String name);
    /** Loads a longitudinal graph into the explorer, by creating a new tabbed window that displays it. */
    void loadLongitudinalGraph(LongitudinalGraph lg, String name);
    /** Closes the active graph */
    void closeActiveGraph();

    /** Returns point locations of current nodes */
    Point2D.Double[] getActivePoints();
    /** Sets active point locations */
    void setActivePoints(Point2D.Double[] pos);

    /** Initializes grpah for static layout */
    void initLayout(StaticGraphLayout layout, double... parameters);
    /** Initializes graph for iterative layout */
    void initLayout(IterativeGraphLayout layout);
    /** Begins layout animation */
    void animateLayout();
    /** Adjusts an iterative layout scheme on the fly, using specified layout mechanism. */
    void adjustLayout(StaticGraphLayout layout, double... parameters);
    /** Iterates active layout */
    void iterateLayout();
    /** Stops active layout */
    void stopLayout();


}
