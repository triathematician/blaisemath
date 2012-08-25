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
import java.util.Arrays;
import org.blaise.graph.Graph;
import org.blaise.graph.GraphUtils;
import org.blaise.util.Edge;
import org.blaise.style.BasicPathStyle;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PathStyle;
import org.blaise.style.PointStyle;
import org.blaise.util.Delegator;
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

    /** 
     * Handles updates to the graph or locations. Called after the graph changes
     * or the positions of the graph change.
     */
    protected GraphUpdater updater = new GraphUpdaterAdapter();


    //<editor-fold defaultstate="collapsed" desc="INITIALIZATION">
    //
    // INITIALIZATION
    //

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

    final void initGraph(Graph g) {
        int n = g.nodes().size();
        Point2D.Double[] pos = new Point2D.Double[n];
        Arrays.fill(pos, new Point2D.Double());
        vGraph = new VCustomGraph<Point2D.Double,Object,Edge<Object>>(pos);
        // XXX - removed set by index updating through graph component
        vGraph.setObjects(g.nodes());
        vGraph.getStyler().setLabelDelegate(DEFAULT_LABEL_DELEGATE);
        vGraph.getStyler().setTipDelegate(DEFAULT_LABEL_DELEGATE);

        ObjectStyler<Edge<Object>, PathStyle> ges = new ObjectStyler<Edge<Object>, PathStyle>();
        ges.setStyleDelegate(new NonDelegator<Edge<Object>,PathStyle>(new BasicPathStyle(new Color(0, 128, 0, 128), .5f)));
        vGraph.setEdgeStyler(ges);
    }

    //</editor-fold>


    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == manager) {
            if (evt.getPropertyName().equals("graph")) {
                Point2D.Double[] points = (Point2D.Double[]) evt.getNewValue();
                Graph graph = manager.getGraph();
                vGraph.setEdges(graph.edges());
                vGraph.setPoint(points);
                if (updater != null)
                    updater.graphUpdated(graph, points);
            } else if (evt.getPropertyName().equals("locationArray")) {
                Point2D.Double[] points = (Point2D.Double[]) evt.getNewValue();
                vGraph.setPoint(points);
                if (updater != null)
                    updater.locationsUpdated(points);
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
                this.manager.removePropertyChangeListener(this);
            }
            this.manager = manager;
            this.manager = manager;
            initGraph(manager.getGraph());
            this.manager.addPropertyChangeListener(this);
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

    public GraphUpdater getUpdater() {
        return updater;
    }

    public void setGraphUpdater(GraphUpdater updater) {
        this.updater = updater;
    }

    // </editor-fold>



    /** Class providing functionality to update a graph */
    public static interface GraphUpdater {
        /**
         * Called when the graph is updated.
         * @param graph the new graph
         * @param points locations of nodes in the graph
         */
        public void graphUpdated(Graph graph, Point2D.Double[] points);
        /**
         * Called when locations only are updated.
         * @param points locations of nodes in the graph
         */
        public void locationsUpdated(Point2D.Double[] points);
    }
        
    /** 
     * Adapter class that can be subclassed for custom functionality
     */
    public static class GraphUpdaterAdapter implements GraphUpdater {
        public void graphUpdated(Graph graph, Double[] points) {}
        public void locationsUpdated(Double[] points) {}
    }

}
