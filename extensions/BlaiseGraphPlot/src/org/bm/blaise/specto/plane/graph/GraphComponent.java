/*
 * GraphComponent.java
 * Created Jan 31, 2011
 */

package org.bm.blaise.specto.plane.graph;

import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.specto.plane.PlanePlotComponent;

/**
 * Provides a view of a graph, using a {@link GraphManager} for positions/layout
 * and a {@link PlaneGraphAdapter} for appearance.
 * @author elisha
 */
public class GraphComponent extends PlanePlotComponent {

    /** Manages the visual elements of the underlying graph */
    private PlaneGraphAdapter pga;

    /** 
     * Construct without a graph 
     */
    public GraphComponent() { 
        this((GraphManager) null);
    }

    /** 
     * Construct with specified graph 
     * @param graph the graph to initialize with
     */
    public GraphComponent(Graph graph) { 
        this(new GraphManager(graph));
    }
    
    /** 
     * Construct with specified graph manager (contains graph and positions)
     * @param gm graph manager to initialize with
     */
    public GraphComponent(GraphManager gm) { 
        setDesiredRange(-5.0, -5.0, 5.0, 5.0); 
        setGraphManager(gm); 
    }

    /** 
     * Return the adapter that contains the graph manager and the graph, responsible for handling the visual appearance. 
     * @return the adapter
     */
    public synchronized PlaneGraphAdapter getAdapter() { 
        return pga; 
    }

    /** 
     * Return the graph manager underlying the component, responsible for handling the graph and node locations. 
     * @return the manager
     */
    public synchronized GraphManager getGraphManager() { 
        return pga.getGraphManager(); 
    }

    /** 
     * Changes the component's graph manager. Used to change the graph being displayed. 
     * @param gm new graph manager
     */
    public synchronized final void setGraphManager(GraphManager gm) {
        GraphManager thisGM = pga == null ? null : getGraphManager();
        if (thisGM != gm) {
            if (thisGM != null) {
                thisGM.stopLayoutTask();
                thisGM.removePropertyChangeListener(pga);
            }
            if (pga != null)
                getVisometryGraphicRoot().removeGraphic(pga.getViewGraph());
            pga = null;
            if (gm != null) {
                pga = new PlaneGraphAdapter(gm);
                getVisometryGraphicRoot().addGraphic(pga.getViewGraph());
            }
        }
    }

}
