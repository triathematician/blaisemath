/*
 * GraphComponent.java
 * Created Jan 31, 2011
 */

package org.blaise.graph.view;

import java.awt.geom.Point2D;
import org.blaise.graph.Graph;
import org.blaise.graph.layout.GraphLayoutManager;
import org.blaise.graphics.GraphicSelector;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PathStyle;
import org.blaise.style.PointStyle;
import org.blaise.util.Edge;
import org.blaise.visometry.VGraphicComponent;
import org.blaise.visometry.plane.PlanePlotMouseHandler;
import org.blaise.visometry.plane.PlaneVisometry;


/**
 * Provides a view of a graph, using a {@link GraphLayoutManager} for positions/layout
 * and a {@link PlaneGraphAdapter} for appearance.
 *
 * @author elisha
 */
public class GraphComponent extends VGraphicComponent<Point2D.Double> {

    /** Manages the visual elements of the underlying graph */
    protected final PlaneGraphAdapter adapter;


    //<editor-fold defaultstate="collapsed" desc="INITIALIZATION">
    //
    // INITIALIZATION
    //

    /**
     * Construct without a graph
     */
    public GraphComponent() {
        this(new GraphLayoutManager());
    }

    /**
     * Construct with specified graph
     * @param graph the graph to initialize with
     */
    public GraphComponent(Graph graph) {
        this(new GraphLayoutManager(graph));
    }

    /**
     * Construct with specified graph manager (contains graph and positions)
     * @param gm graph manager to initialize with
     */
    public GraphComponent(GraphLayoutManager gm) {
        super(new PlaneVisometry());
        adapter = new PlaneGraphAdapter(gm);
        getVisometryGraphicRoot().addGraphic(adapter.getViewGraph());
        ((PlaneVisometry) getVisometry()).setDesiredRange(-5.0, -5.0, 5.0, 5.0);
        setPreferredSize(new java.awt.Dimension(400, 400));
        // enable selection
        GraphicSelector gs = new GraphicSelector(this);
        // enable zoom and drag
        PlanePlotMouseHandler ppmh = new PlanePlotMouseHandler(((PlaneVisometry) getVisometry()), this);
        addMouseListener(ppmh);
        addMouseMotionListener(ppmh);
        addMouseWheelListener(ppmh);
        overlays.add(ppmh);
    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="DELEGATING PROPERTIES">
    //
    // DELEGATING PROPERTIES
    //

    /**
     * Return the adapter that contains the graph manager and the graph, responsible for handling the visual appearance.
     * @return the adapter
     */
    public PlaneGraphAdapter getAdapter() {
        return adapter;
    }

    public ObjectStyler<Edge<Object>, PathStyle> getEdgeStyler() {
        return adapter.getEdgeStyler();
    }

    public void setEdgeStyler(ObjectStyler<Edge<Object>, PathStyle> edgeStyler) {
        adapter.setEdgeStyler(edgeStyler);
    }

    public ObjectStyler<Object, PointStyle> getNodeStyler() {
        return adapter.getNodeStyler();
    }

    public void setNodeStyler(ObjectStyler<Object, PointStyle> nodeStyler) {
        adapter.setNodeStyler(nodeStyler);
    }

    /**
     * Return the graph manager underlying the component, responsible for handling the graph and node locations.
     * @return the manager
     */
    public GraphLayoutManager getGraphManager() {
        return adapter.getGraphManager();
    }

    public void setGraphManager(GraphLayoutManager gm) {
        adapter.setGraphManager(gm);
    }

    public synchronized Graph getGraph() {
        return adapter.getGraphManager().getGraph();
    }

    public synchronized void setGraph(Graph graph) {
        adapter.getGraphManager().setGraph(graph);
    }

    public boolean isLayoutAnimating() {
        return getGraphManager().isLayoutAnimating();
    }

    public void setLayoutAnimating(boolean val) {
        getGraphManager().setLayoutAnimating(val);
    }

    //</editor-fold>




}
