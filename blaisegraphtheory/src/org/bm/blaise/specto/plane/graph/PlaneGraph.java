/*
 * PlaneGraph.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.specto.plane.graph;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
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
public class PlaneGraph extends Plottable<Point2D.Double>
        implements PointDragListener<Point2D.Double>, ChangeBroadcaster {

    /** Vertices; underlying primitive is an array of GraphicString's */
    VPrimitiveEntry vertexEntry;
    /** Edges; underlying primitive is an array of point-pairs */
    VPrimitiveEntry edgeEntry;
    /** The underlying graph object. */
    Graph graph;

    /** Values associated with the vertices (may be null) */
    double[] vertexValues;


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
        vStyle.setMaxRadius(2.0);
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
        List nodes = graph.nodes();
        int size = nodes.size();
        Point2D.Double[] curPts = getPoint();
        if (vertexEntry.local == null || curPts == null || curPts.length != size) {
            // if objects do not exist or sizes do not match, create them
            GraphicPointFancy[] gpfa = new GraphicPointFancy[size];
            vertexEntry.local = gpfa;
            // initialize the layout, but use old positions whenever available
            Point2D.Double[] newPts = curPts == null ? INITIAL_LAYOUT.layout(graph, LAYOUT_PARAMETERS) : ADDING_LAYOUT.layout(graph, LAYOUT_PARAMETERS);
            if (curPts != null)
                for (int i = 0; i < Math.min(curPts.length, newPts.length); i++)
                    newPts[i] = curPts[i];
            ValuedGraph nvg = graph instanceof ValuedGraph ? (ValuedGraph) graph : null;
            Object value;
            for (int i = 0; i < size; i++) {
                if (nvg != null && (value = nvg.getValue(nodes.get(i))) != null)
                    gpfa[i] = new GraphicPointFancy<Point2D.Double>(newPts[i], value.toString(), 5);
                else
                    gpfa[i] = new GraphicPointFancy<Point2D.Double>(newPts[i], nodes.get(i).toString(), 5);
            }
        } else {
            // here the objects exist, and the sizes match, so just need to update the labels
            GraphicPointFancy[] gpfa = (GraphicPointFancy[]) vertexEntry.local;
            if (graph instanceof ValuedGraph) {
                ValuedGraph nvg = (ValuedGraph) graph;
                for (int i = 0; i < size; i++)
                    gpfa[i].string = nvg.getValue(nodes.get(i)).toString();
            } else {
                for (int i = 0; i < size; i++)
                    gpfa[i].string = nodes.get(i).toString();
            }
        }

        ArrowStyle eStyle = (ArrowStyle) edgeEntry.style;
        eStyle.setHeadShape(graph.isDirected() ? ArrowStyle.ArrowShape.TRIANGLE : ArrowStyle.ArrowShape.NONE);
        eStyle.setShapeSize(8);

        this.graph = graph;
        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }

    /** @return current list of vertex values */
    public double[] getNodeValues() {
        return vertexValues;
    }

    /** Sets current list of vertex values */
    public void setNodeValues(double[] values) {
        if (values == null) {
            vertexValues = null;
            firePlottableChanged();
        } else if (vertexEntry.local != null && graph != null) {
            if (values.length != graph.nodes().size())
                throw new IllegalArgumentException("setVertexValues requires #nodes = #values");
            GraphicPointFancy[] gpfa = (GraphicPointFancy[]) vertexEntry.local;
            for (int i = 0; i < values.length; i++)
                gpfa[i].rad = values[i];
            needsRepaint = true;
            vertexEntry.needsConversion = true;
            firePlottableChanged();
        }
    }


    /** @return window location of the i'th point */
    public Point2D.Double getPoint(int i) {
        return (Point2D.Double) ((GraphicPointFancy[])vertexEntry.local)[i].anchor;
    }
    /** Sets location of the i'th point */
    public void setPoint(int i, Point2D.Double newValue) {
        GraphicPointFancy[] gsa = (GraphicPointFancy[]) vertexEntry.local;
        gsa[i].anchor = newValue;
        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }
    /** @return list of all points where vertices are placed, or null if there is no graph */
    public Point2D.Double[] getPoint() {
        if (vertexEntry.local == null)
            return null;
        GraphicPointFancy[] gsa = (GraphicPointFancy[]) vertexEntry.local;
        Point2D.Double[] result = new Point2D.Double[gsa.length];
        for (int i = 0; i < gsa.length; i++)
            result[i] = (Point2D.Double) gsa[i].anchor;
        return result;
    }
    /** Sets location of all vertices at once. Number of points must match the order of the graph. */
    public void setPoint(Point2D.Double[] loc) {
        GraphicPointFancy[] gsa = (GraphicPointFancy[]) vertexEntry.local;
        for (int i = 0; i < Math.min(gsa.length, loc.length); i++)
            gsa[i].anchor = loc[i];
        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }

    /** Sets label of i'th vertex */
    public void setLabel(int i, String newLabel) {
        // TODO - probably should change this to make use of the <code>NodeValueGraph</code> construction.
        GraphicPointFancy[] gsa = (GraphicPointFancy[]) vertexEntry.local;
        gsa[i].string = newLabel;
        vertexEntry.needsConversion = true;
        firePlottableStyleChanged();
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
