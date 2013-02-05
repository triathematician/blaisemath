/*
 * PlaneGraphAdapter.java
 * Created Jan 31, 2011
 */
package org.blaise.graph.view;

import com.google.common.base.Functions;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.blaise.graph.Graph;
import org.blaise.graph.layout.GraphLayoutManager;
import org.blaise.style.BasicPathStyle;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PathStyle;
import org.blaise.style.PointStyle;
import org.blaise.util.Edge;
import org.blaise.visometry.VCustomGraph;
import org.blaise.visometry.plane.PlanePlotComponent;


/**
 * <p>
 *  Combines a {@link GraphLayoutManager} with a {@link VCustomGraph} to manage the positions of a displayed graph;
 *  also manages the visual appearance of the graph. The provided {@link VCustomGraph} may then be added
 *  to an arbitrary {@link PlanePlotComponent}.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneGraphAdapter implements PropertyChangeListener {

    /** Manages graph and node locations */
    private GraphLayoutManager layoutManager;
    /** Stores the visible graph */
    private VCustomGraph viewGraph;

    /**
     * Initialize adapter with an empty graph.
     */
    public PlaneGraphAdapter() {
        this(new GraphLayoutManager());
    }

    /**
     * Construct adapter with the specified graph.
     * @param graph the graph to display
     */
    public PlaneGraphAdapter(Graph graph) {
        this(new GraphLayoutManager(graph));
    }

    /**
     * Construct adapter with the specified manager.
     * @param manager a GraphLayoutManager with the graph to display
     */
    public PlaneGraphAdapter(final GraphLayoutManager manager) {
        setGraphManager(manager);
    }

    /**
     * Initializes view graph for the graph in the graph manager. This includes
     * setting up any styling appropriate for the graph, as well as updating
     * the nodes and edges in the view graph.
     */
    protected final void initViewGraph() {
        synchronized(layoutManager) {
            if (viewGraph == null) {
                // initialize the view graph with the same coordinate manager as the layout manager
                //   this ensures that layout changes will propagate to the view
                viewGraph = new VCustomGraph<Point2D.Double,Object,Edge<Object>>(layoutManager.getCoordinateManager());
            } else {
                viewGraph.setCoordinateManager(layoutManager.getCoordinateManager());
            }
            viewGraph.setEdges(layoutManager.getGraph().edges());
        }
        if (viewGraph.getStyler().getLabelDelegate() == null) {
            viewGraph.getStyler().setLabelDelegate(Functions.toStringFunction());
        }
        if (viewGraph.getStyler().getTipDelegate() == null) {
            viewGraph.getStyler().setTipDelegate(Functions.toStringFunction());
        }
        if (viewGraph.getEdgeStyler().getStyleDelegate() == null) {
            viewGraph.getEdgeStyler().setStyleDelegate(Functions.constant(new BasicPathStyle(new Color(0, 128, 0, 128), .5f)));
        }
    }

    //
    // EVENT HANDLERS
    //

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == layoutManager) {
//            System.out.println("pga.pc/manager");
            if ("graph".equals(evt.getPropertyName())) {
                initViewGraph();
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /**
     * Return the GraphLayoutManager with position data
     * @return GraphLayoutManager
     */
    public GraphLayoutManager getGraphManager() {
        return layoutManager;
    }

    public final void setGraphManager(GraphLayoutManager manager) {
        if (this.layoutManager != manager) {
            if (this.layoutManager != null) {
                this.layoutManager.removePropertyChangeListener("graph", this);
            }
            this.layoutManager = manager;
            initViewGraph();
            this.layoutManager.addPropertyChangeListener("graph", this);
        }
    }

    /**
     * Return the VPointGraph for display
     * @return the VPointGraph
     */
    public VCustomGraph getViewGraph() {
        return viewGraph;
    }

    /**
     * Return node styler
     * @return styler
     */
    public ObjectStyler<Object, PointStyle> getNodeStyler() {
        return viewGraph.getStyler();
    }

    /**
     * Return node styler
     * @param styler
     */
    public void setNodeStyler(ObjectStyler<Object, PointStyle> styler) {
        viewGraph.setStyler(styler);
    }

    /**
     * Return edge styler
     * @return edge styler
     */
    public ObjectStyler<Edge<Object>, PathStyle> getEdgeStyler() {
        return viewGraph.getEdgeStyler();
    }

    /**
     * Sets edge styler
     * @param styler edge styler
     */
    public void setEdgeStyler(ObjectStyler<Edge<Object>, PathStyle> styler) {
        viewGraph.setEdgeStyler(styler);
    }

    //</editor-fold>

}
