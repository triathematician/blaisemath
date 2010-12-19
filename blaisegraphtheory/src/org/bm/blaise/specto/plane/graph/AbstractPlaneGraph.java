/*
 * PlaneGraph.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.specto.plane.graph;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.WeightedGraph;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;
import primitive.GraphicString;
import primitive.style.AbstractPointStyle;
import primitive.style.ArrowStyle;
import primitive.style.PrimitiveStyle;
import primitive.style.StringStyle;
import util.ChangeBroadcaster;
import visometry.VMouseDragListener;
import visometry.VDraggablePrimitiveEntry;
import visometry.VPrimitiveEntry;
import visometry.plane.PlanePlotComponent;
import visometry.plottable.Plottable;

/**
 * <p>
 *    Displays a graph on a <code>PlanePlotComponent</code>, with draggable vertices.
 *    The graph is in the form of a <code>Graph</code>, which simply
 *    contains an iteration over a set of objects, and information about the adjacencies
 *    between those objects. If the graph is a <code>ValuedGraph</code>, then the
 *    values (or labels) may be displayed at the nodes.
 * </p>
 *
 * @param <P> the primitive data type that is used for the vertices; must be a point of some sort!
 *
 * @author Elisha Peterson
 * @see Graph
 * @see PlanePlotComponent
 */
public abstract class AbstractPlaneGraph<P extends Point2D.Double> extends Plottable<Point2D.Double>
        implements VMouseDragListener<Point2D.Double>, ChangeBroadcaster {

    /** Vertices; underlying primitive is an array of GraphicString's */
    VPrimitiveEntry vertexEntry;
    /** Edges; underlying primitive is an array of point-pairs */
    VPrimitiveEntry edgeEntry;
    /** Entry for edge weights */
    VPrimitiveEntry edgeWeightEntry;
    /** The underlying graph object. */
    Graph graph;
    /** The node position map (keeps order of vertices). */
    LinkedHashMap<Object, Point2D.Double> pos;

    /** The initial layout scheme */
    private static final StaticGraphLayout INITIAL_LAYOUT = StaticGraphLayout.CIRCLE;
    /** The layout scheme for adding vertices */
    private static final StaticGraphLayout ADDING_LAYOUT = StaticGraphLayout.ORIGIN;
    /** The initial layout parameters */
    private static final double[] LAYOUT_PARAMETERS = new double[] { 3 };

    public AbstractPlaneGraph(Graph graph, PrimitiveStyle vStyle) {
        ArrowStyle eStyle = new ArrowStyle(new Color(96, 192, 96));
        int n = graph.order(), ne = graph.edgeNumber();
        eStyle.setThickness(ne/n > 12 || ne > 500 ? .5f : ne/n > 6 || ne > 100 ? 1f : 2f);
        eStyle.setShapeSize((int) (eStyle.getThickness() * 6));
        addPrimitive(edgeEntry = new VPrimitiveEntry(null, eStyle));
        addPrimitive(vertexEntry = new VDraggablePrimitiveEntry(null, vStyle, this));
        StringStyle sStyle = new StringStyle(Color.DARK_GRAY, 10, primitive.style.Anchor.Center);
        addPrimitive(edgeWeightEntry = new VPrimitiveEntry(null, sStyle));
        setGraph(graph);
        edgeWeightEntry.visible = graph instanceof WeightedGraph && ne <= 100;
    }

    @Override
    public String toString() {
        return "Graph[" + graph.order() + " vertices]";
    }
    
    /** @return true if points are visible */
    public boolean isPointsVisible() { return vertexEntry.visible; }
    /** Sets visibility of points */
    public void setPointsVisible(boolean visible) { vertexEntry.visible = visible; firePlottableStyleChanged(); }

    /** @return current style for drawing edges of the graph */
    public ArrowStyle getEdgeStyle() { return (ArrowStyle) edgeEntry.style; }
    /** Sets current style for drawing edges of the graph */
    public void setEdgeStyle(ArrowStyle edgeStyle) { edgeEntry.style = edgeStyle; }

    /** @return true if edges are visible */
    public boolean isEdgesVisible() { return edgeEntry.visible; }
    /** Sets visibility of edges */
    public void setEdgesVisible(boolean visible) { edgeEntry.visible = visible; firePlottableStyleChanged(); }

    /** @return underlying graph */
    public Graph getGraph() { return graph; }
    /**
     * Sets the underlying graph. Resets graph layout if the number of nodes differs
     * from the current number; otherwise uses the same layout & the user should
     * reset the layout explicitly if desired.
     * @param graph the new graph
     */
    public void setGraph(Graph graph) {
//        System.out.println("setGraph");
        this.graph = graph;
        List nodes = graph.nodes();
        // first compute starting graph locations (keep locations for nodes that are the same since last graph)
        if (pos == null) {
            pos = new LinkedHashMap<Object, Point2D.Double>();
            Map<Object, Point2D.Double> initial = INITIAL_LAYOUT.layout(graph, LAYOUT_PARAMETERS);
            for (Object o : nodes)
                pos.put(o, initial.get(o));
        } else if (!pos.keySet().containsAll(nodes) || !nodes.containsAll(pos.keySet())){
            LinkedHashMap<Object, Point2D.Double> newPos = new LinkedHashMap<Object, Point2D.Double>();
            Map<Object, Point2D.Double> adding = null;
            for (Object o : nodes) {
                if (pos.containsKey(o))
                    newPos.put(o, pos.get(o));
                else {
                    if (adding == null) 
                        adding = ADDING_LAYOUT.layout(graph, LAYOUT_PARAMETERS);
                    newPos.put(o, adding.get(o));
                }
            }
            pos = newPos;
        }
        // now set up the vertex entry
        updateLabels();

        ArrowStyle eStyle = (ArrowStyle) edgeEntry.style;
        eStyle.setHeadShape(graph.isDirected() ? ArrowStyle.ArrowShape.TRIANGLE : ArrowStyle.ArrowShape.NONE);

        edgeWeightEntry.visible = graph instanceof WeightedGraph;

        firePlottableChanged();
    }

    /** Sets current list of vertex values */
    abstract public void setNodeValues(List values);

    /** @return entire position map */
    public Map<Object, Point2D.Double> getPositionMap() { return pos; }

    /** 
     * Sets position map. Only positions corresponding to nodes in the active graph
     * are used... the remainder are ignored. Okay if this map does not contain
     * all points in the graph.
     */
    public void setPositionMap(Map<?, Point2D.Double> map) {
//        System.out.println("setPositionMap");
        int i = 0;

        Point2D.Double[] arr = (Point2D.Double[]) vertexEntry.local;
        pos = new LinkedHashMap<Object, Point2D.Double>();
        for (Object v : graph.nodes()) {
            if (i >= arr.length || arr[i] == null) break;
            if (map.containsKey(v)) {
                arr[i].setLocation(map.get(v));
                pos.put(v, map.get(v));
            }
            i++;
        }
        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }

    /** @return window location of the i'th point */
    public Point2D.Double getPoint(int i) {
        Point2D.Double result = ((Point2D.Double[])vertexEntry.local)[i];
        return new Point2D.Double(result.x, result.y);
    }
    /** Sets location of the i'th point */
    public void setPoint(int i, Point2D.Double newValue) {
//        System.out.println("setPoint i");
        Point2D.Double[] arr = (Point2D.Double[]) vertexEntry.local;
        arr[i].setLocation(newValue);
        pos.put(graph.nodes().get(i), newValue);
        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }
    /** @return list of all points where vertices are placed, or null if there is no graph */
    public Point2D.Double[] getPoint() {
        if (pos == null)
            return null;
        Point2D.Double[] result = new Point2D.Double[pos.size()];
        int i = 0;
        for (Point2D.Double p : pos.values())
            result[i++] = p;
        return result;
    }
    /** Sets location of all vertices at once. Number of points must match the order of the graph. */
    public void setPoint(Point2D.Double[] loc) {
//        System.out.println("setPoint all");
        Point2D.Double[] arr = (Point2D.Double[]) vertexEntry.local;
        for (int i = 0; i < Math.min(arr.length, loc.length); i++) {
            arr[i].setLocation(loc[i]);
            pos.put(graph.nodes().get(i), loc[i]);
        }
        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }

    abstract public void setLabel(int i, String newLabel);

    /** Updates all the labels in the graph */
    abstract public void updateLabels();

    /** Updates colors to highlight selected nodes only */
    abstract public void highlightNodes(Collection subset);

    /** Requests that this graph be "recomputed", in effect refreshing its set of visual edges. */
    public void requestComputation() {
        firePlottableChanged();
    }


    @Override
    protected void recompute() {
//        System.out.println("recompute");
        WeightedGraph wg = null;
        if (graph instanceof WeightedGraph)
            wg = (WeightedGraph) graph;

        Point2D.Double[] arr = (Point2D.Double[]) vertexEntry.local;
        ArrayList<Point2D.Double[]> edges = new ArrayList<Point2D.Double[]>();
        ArrayList<GraphicString<Point2D.Double>> weights = new ArrayList<GraphicString<Point2D.Double>>();

        List nodes = graph.nodes();
        int size = nodes.size();
        for (int i1 = 0; i1 < size; i1++)
            for (int i2 = (graph.isDirected() ? 0 : i1+1); i2 < size; i2++)
                if (graph.adjacent(nodes.get(i1), nodes.get(i2))) {
                    edges.add(new Point2D.Double[]{arr[i1], arr[i2]});
                    if (wg != null) {
                        Object wt = wg.getWeight(nodes.get(i1), nodes.get(i2));
                        Point2D.Double midPt = new Point2D.Double(.5*(arr[i1].x+arr[i2].x), .5*(arr[i1].y+arr[i2].y));
                        weights.add(new GraphicString<Point2D.Double>(midPt, wt.toString()));
                    }
                }
        edgeEntry.local = edges.toArray(new Point2D.Double[][]{});
        edgeEntry.needsConversion = true;

        if (wg != null) {
            edgeWeightEntry.local = weights.toArray(new GraphicString[]{});
            edgeWeightEntry.needsConversion = true;
        }

        needsComputation = false;
    }

    //
    // MOUSE METHODS
    //

    private transient int dragIndex = -1;
    private transient Point2D.Double start = null, startDrag = null;

    public void mouseEntered(Object source, Point2D.Double start) {}
    public void mouseExited(Object source, Point2D.Double start) {}
    public void mouseMoved(Object source, Point2D.Double start) {}
    public void mouseDragInitiated(Object source, Point2D.Double start) {
        dragIndex = vertexEntry.getActiveIndex();
        if (dragIndex != -1) {
            this.start = getPoint(dragIndex);
            this.startDrag = start;
        }
    }
    public void mouseDragged(Object source, Point2D.Double current) {
        if (dragIndex != -1) {
            Point2D.Double relative = new Point2D.Double(
                    start.x + current.x - startDrag.x, start.y + current.y - startDrag.y );
            setPoint(dragIndex, relative);
            fireStateChanged();
        }
    }
    public void mouseDragCompleted(Object source, Point2D.Double end) {
        mouseDragged(source, end);
        dragIndex = -1;
    }
    
    //
    // EVENT HANDLING
    //

    protected ChangeEvent changeEvent = new ChangeEvent(this);
    protected EventListenerList listenerList = new EventListenerList();

    public void addChangeListener(ChangeListener l) { listenerList.add(ChangeListener.class, l); }
    public void removeChangeListener(ChangeListener l) { listenerList.remove(ChangeListener.class, l); }

    /** Notify interested listeners of an (unspecified) change in the plottable. */
    protected void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null)
                    return;
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
    }
}
