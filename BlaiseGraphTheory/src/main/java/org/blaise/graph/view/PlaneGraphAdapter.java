/*
 * PlaneGraphAdapter.java
 * Created Jan 31, 2011
 */
package org.blaise.graph.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.blaise.graph.Graph;
import org.blaise.style.BasicPathStyle;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PathStyle;
import org.blaise.style.PointStyle;
import org.blaise.util.Delegator;
import org.blaise.util.Edge;
import org.blaise.util.NonDelegator;
import org.blaise.visometry.VCustomGraph;
import org.blaise.visometry.plane.PlanePlotComponent;

/**
 * <p>
 *  Combines a {@link GraphManager} with a {@link VCustomGraph} to manage the positions of a displayed graph;
 *  also manages the visual appearance of the graph. The provided {@link VCustomGraph} may then be added
 *  to an arbitrary {@link PlanePlotComponent}.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneGraphAdapter implements PropertyChangeListener {

    /** Manages graph and node locations */
    private GraphManager manager;
    /** Stores the visible graph */
    private VCustomGraph<Point2D.Double,Object,Edge<Object>> vGraph;
    /** Handles updates to the graph or locations. Called after the graph changes or the positions of the graph change. */
    protected GraphLocationUpdater updater = null;

    /** Used to label objects in the graph */
    private final Delegator<Object, String> DEFAULT_LABEL_DELEGATE = new Delegator<Object, String>(){
        public String of(Object src) { return ""+src; }
    };

    /**
     * Initialize adapter with an empty graph.
     */
    public PlaneGraphAdapter() {
        this(new GraphManager());
    }

    /**
     * Construct adapter with the specified graph.
     * @param graph the graph to display
     */
    public PlaneGraphAdapter(Graph graph) {
        this(new GraphManager(graph));
    }

    /**
     * Construct adapter with the specified manager.
     * @param manager a GraphManager with the graph to display
     */
    public PlaneGraphAdapter(final GraphManager manager) {
        setGraphManager(manager);
    }

    /**
     * Initializes view graph for the graph in the graph manager.
     */
    protected final void initViewGraph() {
        synchronized(manager) {
            Map<Object,Point2D.Double> loc = manager.getLocations();
            vGraph = new VCustomGraph<Point2D.Double,Object,Edge<Object>>(loc);
            vGraph.setEdges(manager.getGraph().edges());
        }
        vGraph.getStyler().setLabelDelegate(DEFAULT_LABEL_DELEGATE);
        vGraph.getStyler().setTipDelegate(DEFAULT_LABEL_DELEGATE);

        // default edge style
        ObjectStyler<Edge<Object>, PathStyle> ges = new ObjectStyler<Edge<Object>, PathStyle>();
        ges.setStyleDelegate(new NonDelegator<Edge<Object>,PathStyle>(new BasicPathStyle(new Color(0, 128, 0, 128), .5f)));
        vGraph.setEdgeStyler(ges);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == manager) {
            if ("graph".equals(evt.getPropertyName())) {
                initViewGraph();
            }
        } else if (evt.getSource() == manager.getPointManager()) {
            if ("add".equals(evt.getPropertyName())) {
                vGraph.addObjects((Map<Object,Point2D.Double>) evt.getNewValue());
            } else if ("remove".equals(evt.getPropertyName())) {
                vGraph.removeObjects((Set) evt.getNewValue());
            }
        }
    }


    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /**
     * Return the GraphManager with position data
     * @return GraphManager
     */
    public GraphManager getGraphManager() {
        return manager;
    }

    public final void setGraphManager(GraphManager manager) {
        if (this.manager != manager) {
            if (this.manager != null) {
                this.manager.removePropertyChangeListener("graph", this);
                this.manager.getPointManager().removePropertyChangeListener(this);
            }
            this.manager = manager;
            initViewGraph();
            this.manager.addPropertyChangeListener("graph", this);
            this.manager.getPointManager().addPropertyChangeListener(this);
        }
    }

    /**
     * Return the VPointGraph for display
     * @return the VPointGraph
     */
    public VCustomGraph<Point2D.Double,Object,Edge<Object>> getViewGraph() {
        return vGraph;
    }

    /**
     * Return node styler
     * @return styler
     */
    public ObjectStyler<Object, PointStyle> getNodeStyler() {
        return vGraph.getStyler();
    }

    /**
     * Return node styler
     * @param styler
     */
    public void setNodeStyler(ObjectStyler<Object, PointStyle> styler) {
        vGraph.setStyler(styler);
    }

    /**
     * Return edge styler
     * @return edge styler
     */
    public ObjectStyler<Edge<Object>, PathStyle> getEdgeStyler() {
        return vGraph.getEdgeStyler();
    }

    /**
     * Sets edge styler
     * @param styler edge styler
     */
    public void setEdgeStyler(ObjectStyler<Edge<Object>, PathStyle> styler) {
        vGraph.setEdgeStyler(styler);
    }

    public GraphLocationUpdater getUpdater() {
        return updater;
    }

    public void setGraphUpdater(GraphLocationUpdater updater) {
        this.updater = updater;
    }

    // </editor-fold>


}
