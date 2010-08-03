/*
 * PlaneGraph.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.specto.plane.graph;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.RandomGraph;
import org.bm.blaise.scio.graph.ValuedGraph;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;
import primitive.GraphicPointFancy;
import primitive.style.ArrowStyle;
import primitive.style.PointFancyStyle;
import primitive.style.PointStyle;
import primitive.style.StringStyle;
import util.ChangeBroadcaster;
import visometry.PointDragListener;
import visometry.VDraggablePrimitiveEntry;
import visometry.VPrimitiveEntry;
import visometry.plane.PlanePlotComponent;
import visometry.plottable.Plottable;
import visometry.plottable.VPointSet;

/**
 * <p>
 *    Displays a graph on a <code>PlanePlotComponent</code>, with draggable vertices.
 *    The graph is in the form of a <code>NeighborSetInterface</code>, which simply
 *    contains an iteration over a set of objects, and information about the adjacencies
 *    between those objects.
 *    If the graph implements <code>IndexedNeighborSetInterface</code>, then support
 *    is added for labeling the vertices as well.
 * </p>
 * @author Elisha Peterson
 * @see org.bm.blaise.scio.graph.Graph
 * @see PlanePlotComponent
 * @see VPointSet
 */
public final class PlaneGraph extends Plottable<Point2D.Double>
        implements PointDragListener<Point2D.Double>, ChangeBroadcaster {

    /** Vertices; underlying primitive is an array of GraphicString's */
    VPrimitiveEntry vertexEntry;
    /** Edges; underlying primitive is an array of point-pairs */
    VPrimitiveEntry edgeEntry;
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

    /** Constructs graph with a sample network */
    public PlaneGraph() {
        this(RandomGraph.getInstance(20, 25, false));
    }

    public PlaneGraph(Graph graph) {
        ArrowStyle eStyle = new ArrowStyle(new Color(96, 192, 96));
        eStyle.setThickness(2f);
        addPrimitive(edgeEntry = new VPrimitiveEntry(null, eStyle));

        PointFancyStyle vStyle = new PointFancyStyle(PointStyle.PointShape.CIRCLE, StringStyle.Anchor.N);
        vStyle.setLabelColor(new Color(128, 128, 128));
        vStyle.setMaxRadius(5.0);
        addPrimitive(vertexEntry = new VDraggablePrimitiveEntry(null, vStyle, this));
        setGraph(graph);
    }

    @Override
    public String toString() {
        return "Graph[" + graph.order() + " vertices]";
    }
    
    /** @return current style of point for this plottable */
    public PointFancyStyle getPointStyle() { return (PointFancyStyle) vertexEntry.style; }
    /** Set current style of point for this plottable */
    public void setPointStyle(PointFancyStyle newValue) { if (vertexEntry.style != newValue) { vertexEntry.style = newValue; firePlottableStyleChanged(); } }

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
        this.graph = graph;
        List nodes = graph.nodes();
        int size = nodes.size();
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
        vertexEntry.local = new GraphicPointFancy[size];
        updateLabels();

        ArrowStyle eStyle = (ArrowStyle) edgeEntry.style;
        eStyle.setHeadShape(graph.isDirected() ? ArrowStyle.ArrowShape.TRIANGLE : ArrowStyle.ArrowShape.NONE);
        eStyle.setShapeSize(8);

        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }

    /** Sets current list of vertex values */
    public void setNodeValues(List values) {
        GraphicPointFancy[] gpfa = (GraphicPointFancy[]) vertexEntry.local;
        if (values == null) {
            for (int i = 0; i < gpfa.length; i++)
                gpfa[i].rad = 1;
            vertexEntry.needsConversion = true;
            firePlottableChanged();
        } else if (vertexEntry.local != null && graph != null) {
            if (values.size() != graph.nodes().size())
                throw new IllegalArgumentException("setVertexValues requires #nodes = #values");
            for (int i = 0; i < values.size(); i++)
                gpfa[i].rad = Math.sqrt(((Number) values.get(i)).doubleValue());
            vertexEntry.needsConversion = true;
            firePlottableChanged();
        }
    }

    /** @return entire position map */
    public Map<Object, Point2D.Double> getPositionMap() { return pos; }

    /** 
     * Sets position map. Only positions corresponding to nodes in the active graph
     * are used... the remainder are ignored. Okay if this map does not contain
     * all points in the graph.
     */
    public void setPositionMap(Map<?, Point2D.Double> map) {
        int i = 0;
        GraphicPointFancy[] arr = (GraphicPointFancy[]) vertexEntry.local;
        pos = new LinkedHashMap<Object, Point2D.Double>();
        for (Object v : graph.nodes()) {
            if (i >= arr.length || arr[i] == null) break;
            if (map.containsKey(v)) {
                arr[i].anchor = map.get(v);
                pos.put(v, map.get(v));
            }
            i++;
        }
        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }

    /** @return window location of the i'th point */
    public Point2D.Double getPoint(int i) {
        return (Point2D.Double) ((GraphicPointFancy[])vertexEntry.local)[i].anchor;
    }
    /** Sets location of the i'th point */
    public void setPoint(int i, Point2D.Double newValue) {
        GraphicPointFancy[] gsa = (GraphicPointFancy[]) vertexEntry.local;
        gsa[i].anchor = newValue;
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
        GraphicPointFancy[] gsa = (GraphicPointFancy[]) vertexEntry.local;
        for (int i = 0; i < Math.min(gsa.length, loc.length); i++) {
            gsa[i].anchor = loc[i];
            pos.put(graph.nodes().get(i), loc[i]);
        }
        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }

    public void setLabel(int i, String newLabel) {
        // TODO - probably should change this to make use of the <code>NodeValueGraph</code> construction.
        GraphicPointFancy[] gsa = (GraphicPointFancy[]) vertexEntry.local;
        gsa[i].string = newLabel;
        vertexEntry.needsConversion = true;
        firePlottableStyleChanged();
    }

    /** Updates all the labels in the graph */
    public void updateLabels() {
        List nodes = graph.nodes();
        GraphicPointFancy[] gpfa = (GraphicPointFancy[]) vertexEntry.local;
        ValuedGraph vg = graph instanceof ValuedGraph ? (ValuedGraph) graph : null;
        String value;
        int i = 0;
        for (Object o : nodes) {
            value = vg == null ? o.toString() : vg.getValue(o).toString();
            if (gpfa[i] == null)
                gpfa[i] = new GraphicPointFancy<Point2D.Double>(pos.get(o), value, 1);
            else
                gpfa[i].setString(value);
            i++;
        }
        firePlottableChanged();
    }

    @Override
    protected void recompute() {
        ArrayList<Point2D.Double[]> edges = new ArrayList<Point2D.Double[]>();
        GraphicPointFancy[] gsa = (GraphicPointFancy[]) vertexEntry.local;
        List nodes = graph.nodes();
        int size = nodes.size();
        for (int i1 = 0; i1 < size; i1++)
            for (int i2 = 0; i2 < size; i2++)
                if (graph.adjacent(nodes.get(i1), nodes.get(i2)))
                    edges.add(new Point2D.Double[]{(Point2D.Double)gsa[i1].getAnchor(), (Point2D.Double)gsa[i2].getAnchor()});
        edgeEntry.local = edges.toArray(new Point2D.Double[][]{});
        edgeEntry.needsConversion = true;
    }

    public void mouseEntered(Object source, Point2D.Double start) {}
    public void mouseExited(Object source, Point2D.Double start) {}
    public void mouseMoved(Object source, Point2D.Double start) {}
    public void mouseDragInitiated(Object source, Point2D.Double start) {}
    public void mouseDragged(Object source, Point2D.Double current) {
        int index = vertexEntry.getActiveIndex();
        if (index != -1) {
            setPoint(index, current);
            fireStateChanged();
        }
    }
    public void mouseDragCompleted(Object source, Point2D.Double end) {
        mouseDragged(source, end);
    }
    
    //
    // EVENT HANDLING
    //

    protected ChangeEvent changeEvent = new ChangeEvent(this);
    protected EventListenerList listenerList = new EventListenerList();

    public void addChangeListener(ChangeListener l) { listenerList.add(ChangeListener.class, l); }
    public void removeChangeListener(ChangeListener l) { listenerList.remove(ChangeListener.class, l); }

    /** Notify interested listeners of an (unspecified) change in the plottable. */
    public void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null)
                    return;
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
    }
}
