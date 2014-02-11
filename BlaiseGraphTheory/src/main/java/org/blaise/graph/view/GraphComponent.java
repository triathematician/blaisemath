/*
 * GraphComponent.java
 * Created Jan 31, 2011
 */

package org.blaise.graph.view;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import org.blaise.graph.Graph;
import org.blaise.graph.layout.GraphLayoutManager;
import org.blaise.util.ContextMenuInitializer;
import org.blaise.graphics.DelegatingNodeLinkGraphic;
import org.blaise.graphics.PanAndZoomHandler;
import org.blaise.graphics.GraphicComponent;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PathStyle;
import org.blaise.style.PointStyle;
import org.blaise.util.Edge;


/**
 * Provides a view of a graph, using a {@link GraphLayoutManager} for positions/layout
 * and a {@link PlaneGraphAdapter} for appearance.
 *
 * @author elisha
 */
public class GraphComponent extends GraphicComponent {

    /** Manages the visual elements of the underlying graph */
    protected final PlaneGraphAdapter adapter;
    /** Pan and zoom control */
    protected final PanAndZoomHandler zoomControl;


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
        adapter = new PlaneGraphAdapter(gm);
        addGraphic(adapter.getViewGraph());
        setPreferredSize(new java.awt.Dimension(400, 400));
        // TODO - graph bounding box
        // enable selection
        setSelectionEnabled(true);
        // enable zoom and drag
        zoomControl = new PanAndZoomHandler(this);
        // trun off animation if component hierarchy changes
        addHierarchyListener(new HierarchyListener(){
            public void hierarchyChanged(HierarchyEvent e) {
                if (e.getChangeFlags() == HierarchyEvent.PARENT_CHANGED) {
                    setLayoutAnimating(false);
                }
            }
        });
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


    /**
     * Adds context menu element to specified object
     * @param key either "graph", "node", or "link"
     * @param init used to initialize the context menu
     */
    public void addContextMenuInitializer(String key, ContextMenuInitializer init) {
        DelegatingNodeLinkGraphic win = (DelegatingNodeLinkGraphic) adapter.getViewGraph();
        if ("graph".equalsIgnoreCase(key)) {
            getGraphicRoot().addContextMenuInitializer(init);
        } else if ("node".equalsIgnoreCase(key)) {
            win.getPointGraphic().addContextMenuInitializer(init);
        } else if ("link".equalsIgnoreCase(key)) {
            win.getEdgeGraphic().addContextMenuInitializer(init);
        }
    }

    /**
     * Removes context menu element from specified object
     * @param key either "graph", "node", or "link"
     * @param init used to initialize the context menu
     */
    public void removeContextMenuInitializer(String key, ContextMenuInitializer init) {
        DelegatingNodeLinkGraphic win = (DelegatingNodeLinkGraphic) adapter.getViewGraph();
        if ("graph".equals(key)) {
            getGraphicRoot().removeContextMenuInitializer(init);
        } else if ("node".equals(key)) {
            win.getPointGraphic().removeContextMenuInitializer(init);
        } else if ("link".equals(key)) {
            win.getEdgeGraphic().removeContextMenuInitializer(init);
        }
    }




}
