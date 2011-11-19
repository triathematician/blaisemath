/*
 * PlaneGraphAdapter.java
 * Created Jan 31, 2011
 */
package org.bm.blaise.specto.plane.graph;

import java.awt.geom.Point2D.Double;
import org.bm.blaise.scio.graph.ValuedGraph;
import org.bm.blaise.style.PointStyle;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphUtils;
import org.bm.blaise.specto.graphics.VCustomGraph;
import org.bm.blaise.specto.plane.PlanePlotComponent;
import org.bm.blaise.style.ObjectStyler;
import org.bm.blaise.style.PathStyle;
import org.bm.util.Delegator;

/**
 * <p>
 *  Combines a {@link GraphManager} with a {@link VPointGraph} to manage the positions of a displayed graph;
 *  also manages the visual appearance of the graph. The provided {@link VPointGraph} may then be added
 *  to an arbitrary {@link PlanePlotComponent}.
 * </p>
 * 
 * @author Elisha Peterson
 */
public class PlaneGraphAdapter<Src> implements PropertyChangeListener {

    /** Manages graph and node locations */
    private final GraphManager manager;
    /** Stores the visible graph */
    private VCustomGraph<Point2D.Double, Object> vGraph;

    
    //<editor-fold defaultstate="collapsed" desc="CONSTRUCTORS">
    //
    // CONSTRUCTORS
    //
    
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
    public PlaneGraphAdapter(GraphManager manager) {
        this.manager = manager;

        initGraph(manager.getGraph());
        
        updateNodeLabels();
        updateNodeTips();

        manager.addPropertyChangeListener(this);
    }
    
    final void initGraph(Graph g) {
        int n = g.nodes().size();
        Point2D.Double[] pos = new Point2D.Double[n];
        Arrays.fill(pos, new Point2D.Double());
        vGraph = new VCustomGraph<Point2D.Double, Object>(pos) {
            // TODO - overriding here to make sure mouse drags are passed along to the layout manager
            // -- there is probably a better solution (i.e. VCustomGraph generates events)
            @Override 
            public synchronized void setPoint(int i, Point2D.Double point) {
                super.setPoint(i, point);
                manager.requestPositionArray(getObjects(), getPoint());
            }
        };
        vGraph.setEdgeStyler(new GraphEdgeStyler());
        vGraph.setObjects(g.nodes());
        ((GraphEdgeStyler)vGraph.getEdgeStyler()).setGraph(g);
    }

    //</editor-fold>
    
    
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

    /**
     * Return the VPointGraph for display
     * @return the VPointGraph
     */
    public VCustomGraph<Point2D.Double, ?> getViewGraph() {
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
    public ObjectStyler<Object[], PathStyle> getEdgeStyler() {
        return vGraph.getEdgeStyler();
    }
    
    /**
     * Sets edge styler
     * @param styler edge styler
     */
    public void setEdgeStyler(ObjectStyler<Object[], PathStyle> styler) {
        vGraph.setEdgeStyler(styler);
    }

    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //
    
    /**
     * Call to update the appearance.
     */
    public final void updateAppearance() {
        vGraph.getWindowEntry().fireAppearanceChanged();
    }
    
    /** 
     * Call to refresh the underlying node labels.
     * TODO - consider converting behavior to a property change event
     */
    public final void updateNodeLabels() {
        vGraph.getStyler().setLabelDelegate(new Delegator<Object, String>(){
            public String of(Object src) {
                ValuedGraph vg = manager.getGraph() instanceof ValuedGraph ? ((ValuedGraph) manager.getGraph()) : null;
                return vg == null ? src.toString() : vg.getValue(src).toString();
            }
        });
    }
    
    private void updateNodeTips() {
        vGraph.getStyler().setTipDelegate(new Delegator<Object, String>(){
            public String of(Object src) {
                ValuedGraph vg = manager.getGraph() instanceof ValuedGraph ? ((ValuedGraph) manager.getGraph()) : null;
                return vg == null ? src.toString() : (src.toString() + ": " + vg.getValue(src).toString());
            }
        });
    }

    public synchronized void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        if (prop.equals("layoutAnimating") || prop.equals("layoutAlgorithm")) {
            // don't care
        } else if (prop.equals("locationArray")) {
            vGraph.setEdges(GraphUtils.getEdgeMap(manager.getGraph()));
            vGraph.setPoint((Point2D.Double[]) evt.getNewValue());
            ((GraphEdgeStyler)vGraph.getEdgeStyler()).setGraph(manager.getGraph());
            updateNodeTips();
            updateNodeLabels();
        } else {
            System.err.println("Unsupported Property: " + evt);
        }
    }
    // </editor-fold>

}
