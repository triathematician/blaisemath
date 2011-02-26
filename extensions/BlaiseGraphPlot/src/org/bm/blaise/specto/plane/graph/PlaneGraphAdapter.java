/*
 * PlaneGraphAdapter.java
 * Created Jan 31, 2011
 */

package org.bm.blaise.specto.plane.graph;

import java.util.HashSet;
import java.util.Set;
import graphics.renderer.BasicStrokeRenderer;
import graphics.renderer.PointRenderer;
import graphics.renderer.ShapeRenderer;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.ValuedGraph;
import org.bm.blaise.scio.graph.WeightedGraph;
import utils.IndexedGetter;
import utils.MapGetter;
import visometry.plottable.VPointGraph;
import static org.bm.blaise.specto.plane.graph.GraphManager.*;

/**
 * Uses a <code>GraphManager</code> to handle the location of nodes of a graph,
 * and provides a <code>VPointGraph</code> that can be added to a <code>PlanePlotComponent</code>
 * to display the graph.
 *
 * This class handles the visual elements of the graph, including styles, labels, tooltips, etc.
 * Each adapter instance can only handle a single graph. To change the graph; a new adapter instance
 * must be used.
 * 
 * @author Elisha Peterson
 */
public class PlaneGraphAdapter implements ChangeListener, PropertyChangeListener {

    /** Manages graph and node locations */
    private final GraphManager manager;
    /** Stores the visible graph */
    private final VPointGraph<Point2D.Double> vGraph;
    /** The last node set used */
    private List nodes = null;

    /** Construct adapter with the specified graph. */
    public PlaneGraphAdapter(Graph graph) {
        this(new GraphManager(graph));
    }
    /** Construct adapter with the specified manager. */
    public PlaneGraphAdapter(GraphManager manager) {
        this.manager = manager;
        Object[] graphData = manager.getGraphData();
        nodes = (List) graphData[0];
        Point2D.Double[] pos = (Point2D.Double[]) graphData[1];
        int[][] edges = (int[][]) graphData[2];
        vGraph = new VPointGraph<Point2D.Double>(pos, edges);
        if (manager.getGraph() instanceof WeightedGraph)
            updateWeightedEdges(vGraph, (WeightedGraph) manager.getGraph(), nodes, edges);
        useStandardTooltips(nodes);
        manager.addPropertyChangeListener(this);
        vGraph.addChangeListener(this);
    }

    public GraphManager getGraphManager() {
        return manager;
    }

    public VPointGraph<Double> getViewGraph() {
        return vGraph;
    }

    public void copyAppearanceOf(PlaneGraphAdapter adapter) {
        setNodeRenderer(adapter.getNodeRenderer());
        setNodeCustomizer(adapter.getNodeCustomizer());
        setNodeLabels(adapter.getNodeLabels());
        setNodeTooltips(adapter.getNodeTooltips());
        setEdgeRenderer(adapter.getEdgeRenderer());
        setEdgeCustomizer(adapter.getEdgeCustomizer());
        setEdgeLabels(adapter.getEdgeLabels());
        setEdgeTooltips(adapter.getEdgeTooltips());
    }

    // <editor-fold defaultstate="collapsed" desc="Event Handling">

    /** Call to notify that the appearance has changed (e.g. one of the customizers has changed internally) */
    public void appearanceChanged() {
        vGraph.firePlottableStyleChanged();
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == vGraph) {
            // called e.g. when the node locations change due to user dragging
            manager.requestPositionArray(nodes, vGraph.getElement());
        }
    }

    public synchronized void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        if (prop.equals($ANIMATING) || prop.equals($LAYOUT_ALGORITHM)) {
            // don't care
        } else if (prop.equals($POSITIONS)) {
            nodes = (List) ((Object[])evt.getNewValue())[0];
            Point2D.Double[] arr = (Point2D.Double[]) ((Object[])evt.getNewValue())[1];
            vGraph.setElement(arr);
        } else if (prop.equals($GRAPH)) {
            nodes = (List) ((Object[])evt.getNewValue())[0];
            Point2D.Double[] arr = (Point2D.Double[]) ((Object[])evt.getNewValue())[1];
            int[][] edges = (int[][]) ((Object[])evt.getNewValue())[2];
            {
                vGraph.setGraph(arr, edges);
                if (manager.getGraph() instanceof WeightedGraph)
                    updateWeightedEdges(vGraph, (WeightedGraph) manager.getGraph(), nodes, edges);
                else
                    vGraph.setEdgeCustomizer(null);
                useStandardTooltips(nodes);
            }
        } else if (prop.equals($EDGES)) {
            nodes = (List) ((Object[])evt.getNewValue())[0];
            int[][] edges = (int[][]) ((Object[])evt.getNewValue())[1];
            vGraph.setEdges(edges);
            if (manager.getGraph() instanceof WeightedGraph)
                updateWeightedEdges(vGraph, (WeightedGraph) manager.getGraph(), nodes, edges);
        } else
            System.err.println("Unsupported Property: " + evt);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Style and Customization Property Patterns">
    private MapGetter.IndexedAdapter<?,String> nLabel, eLabel;
    private MapGetter.IndexedAdapter<?,String> nTip, eTip;
    private MapGetter.IndexedAdapter<?,PointRenderer> nRend;

    /** @return object supplying tooltips (mapping from node objects to strings) */
    public MapGetter<String> getNodeLabels() { 
        return nLabel == null ? null : nLabel.getMap();
    }
    /** Sets tooltips */
    public void setNodeLabels(MapGetter<String> map) {
        vGraph.setLabels(nLabel = new MapGetter.IndexedAdapter<Object, String>(nodes, map));
    }

    /** @return object supplying tooltips (mapping from node objects to strings) */
    public MapGetter<String> getNodeTooltips() { 
        return nTip == null ? null : nTip.getMap();
    }
    /** Sets tooltips */
    public void setNodeTooltips(MapGetter<String> map) {
        vGraph.setTooltips(nTip = new MapGetter.IndexedAdapter<Object, String>(nodes, map));
    }

    /** @return default node renderer to use in absence of customization */
    public PointRenderer getNodeRenderer() { 
        return vGraph.getPointRenderer();
    }
    /** Set default node renderer to use in absence of customization */
    public void setNodeRenderer(PointRenderer rend) { 
        vGraph.setPointRenderer(rend);
    }

    /** @return node customization map */
    public MapGetter<PointRenderer> getNodeCustomizer() { 
        return nRend == null ? null : nRend.getMap();
    }
    /** Sets customization for nodes */
    public void setNodeCustomizer(MapGetter<PointRenderer> map) {
        if (map != null)
            vGraph.setIndexedPointRenderer(nRend = new MapGetter.IndexedAdapter<Object, PointRenderer>(nodes, map));
    }

    /** @return object supplying tooltips (mapping from node objects to strings) */
    public MapGetter<String> getEdgeLabels() { 
        return eLabel == null ? null : eLabel.getMap();
    }
    /** Sets tooltips */
    public void setEdgeLabels(MapGetter<String> map) {
    }

    /** @return object supplying tooltips (mapping from node objects to strings) */
    public MapGetter<String> getEdgeTooltips() { 
        return eTip == null ? null : eTip.getMap();
    }
    /** Sets tooltips */
    public void setEdgeTooltips(MapGetter<String> map) {
    }

    /** @return default edge renderer to use in absence of customization */
    public BasicStrokeRenderer getEdgeRenderer() {
        return vGraph.getEdgeRenderer();
    }
    /** Set default edge renderer to use in absence of customization */
    public void setEdgeRenderer(BasicStrokeRenderer rend) {
        vGraph.setEdgeRenderer(rend);
    }

    /** @return edge customization map */
    public MapGetter<ShapeRenderer> getEdgeCustomizer() { 
        return vGraph.getEdgeCustomizer();
    }
    /** Sets customization for edges */
    public void setEdgeCustomizer(MapGetter<ShapeRenderer> map) { 
        vGraph.setEdgeCustomizer(map);
    }
    // </editor-fold>


    private void useStandardTooltips(List nodes) {
        final List<String> tipper = new ArrayList<String>();
        ValuedGraph vg = manager.getGraph() instanceof ValuedGraph ? ((ValuedGraph)manager.getGraph()) : null;
        for (Object n : nodes)
            tipper.add( vg == null ? n.toString() : n.toString() + ": " + vg.getValue(n).toString() );
        vGraph.setTooltips(new IndexedGetter<String>(){
            public int getSize() { return tipper.size(); }
            public String getElement(int i) { return tipper.get(i); }
        });
    }
    
    // <editor-fold defaultstate="collapsed" desc="Special Sauce for Weighted Graphs">
    //
    // WEIGHTED GRAPH STUFF
    //

    /** Sets up variable edge thickness for a graph with weighted edges */
    private static void updateWeightedEdges(VPointGraph<Point2D.Double> vGraph, WeightedGraph wg, List nodes, int[][] edges) {
        Map<int[],ShapeRenderer> eMap = new HashMap<int[],ShapeRenderer>();
        float thick = vGraph.getEdgeRenderer().getThickness();
        float maxWeight = 0f;
        Collection<int[]> ee = edges == null ? eMap.keySet() : Arrays.asList(edges);
        final Set<ShapeRenderer> negs = new HashSet<ShapeRenderer>(); // records edges with negative weights
        for (int[] e : ee) {
            Object o = wg.getWeight(nodes.get(e[0]), nodes.get(e[1]));
            if (o instanceof Number) {
                float wt = ((Number)o).floatValue();
                boolean positive = wt >= 0;
                float awt = Math.abs(wt);
                maxWeight = Math.max(awt, maxWeight);
                BasicStrokeRenderer br = null;
                if (!eMap.containsKey(e))
                    eMap.put(e, br = new BasicStrokeRenderer(Color.blue, awt));
                else {
                    br = (BasicStrokeRenderer) eMap.get(e);
                    br.setThickness(awt);
                }
                if (!positive)
                    negs.add(br);
            } else if (o instanceof Color) {
                if (!eMap.containsKey(e))
                    eMap.put(e, new BasicStrokeRenderer((Color)o));
            } else
                // nothing interesting to do
                return;
        }
        for (ShapeRenderer r : eMap.values()) {
            BasicStrokeRenderer br = (BasicStrokeRenderer) r;
            double relativeWeight = br.getThickness()/maxWeight;
            if (negs.contains(r))
                // large weight (255,0,0,255); small weight (100,0,255,100)
                br.setStroke(new Color(
                        205+(int)(50*relativeWeight),
                        0,
                        100-(int)(50*relativeWeight),
                        100+(int)(155*relativeWeight))
                        );
            else
                // large weight (0,155,255,255); small weight (50,255,100,100)
                br.setStroke(new Color(
                        25-(int)(25*relativeWeight),
                        205+(int)(50*relativeWeight),
                        100-(int)(50*relativeWeight),
                        100+(int)(155*relativeWeight))
                        );
//                br.setStroke(new Color(
//                        (int)(50*ratio),
//                        255-(int)(255*ratio),
//                        (int)(255*ratio),
//                        100+(int)(155*ratio))
//                        );
            br.setThickness(1.5f*thick*br.getThickness()/maxWeight);
        }
        vGraph.setEdgeCustomizer(new MapGetter.MapInstance(eMap));
    }

    // </editor-fold>
}
