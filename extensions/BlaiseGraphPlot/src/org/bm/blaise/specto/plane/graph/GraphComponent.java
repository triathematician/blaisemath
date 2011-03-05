/*
 * GraphComponent.java
 * Created Jan 31, 2011
 */

package org.bm.blaise.specto.plane.graph;

import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.specto.plane.PlanePlotComponent;

/**
 * Provides a view of a graph, using a <code>GraphManager</code> for positions/layout
 * and a <code>PlaneGraphAdapter</code> for appearnce.
 * @author elisha
 */
public class GraphComponent extends PlanePlotComponent {

    /** Manages the visual elements of the underlying graph */
    public PlaneGraphAdapter pga;

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    /** Construct without a graph */
    public GraphComponent() { setDesiredRange(-5.0, -5.0, 5.0, 5.0); }

    /** Construct with specified graph */
    public GraphComponent(Graph graph) { setGraphManager(new GraphManager(graph)); }

    /** Construct with specified graph manager (contains graph and positions) */
    public GraphComponent(GraphManager gm) { setGraphManager(gm); }
    // </editor-fold>

    /** Return the adapter that contains the graph manager and the graph, responsible for handling the visual appearance. */
    public synchronized PlaneGraphAdapter getAdapter() { return pga; }

    /** Return the graph manager underlying the component, responsible for handling the graph and node locations. */
    public synchronized GraphManager getGraphManager() { return pga.getGraphManager(); }

    /** Changes the component's graph manager. Used to change the graph being displayed. */
    public synchronized final void setGraphManager(GraphManager gm) {
        GraphManager thisGM = pga == null ? null : getGraphManager();
        if (thisGM != gm) {
            if (thisGM != null) {
                thisGM.stopLayoutTask();
                thisGM.removePropertyChangeListener(pga);
            }
            if (pga != null)
                remove(pga.getViewGraph());
            pga = null;
            if (gm != null) {
                pga = new PlaneGraphAdapter(gm);
                add(pga.getViewGraph());
            }
        }
    }

}
