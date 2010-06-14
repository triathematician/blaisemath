/*
 * PlaneGraph.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.specto.plane.graph;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.Graphs;
import org.bm.blaise.scio.graph.NodeValueGraph;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;
import primitive.GraphicString;
import primitive.style.PathStylePoints;
import primitive.style.PointLabeledStyle;
import primitive.style.PointStyle;
import primitive.style.StringStyle;
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
        implements PointDragListener<Point2D.Double> {

    /** Vertices; underlying primitive is an array of GraphicString's */
    VPrimitiveEntry vertexEntry;
    /** Edges; underlying primitive is an array of point-pairs */
    VPrimitiveEntry edgeEntry;
    /** The underlying graph object. */
    Graph graph;

    /** Constructs graph with a sample network */
    public PlaneGraph() {
        this(Graphs.getRandomInstance(10, 10, true));
    }

    public PlaneGraph(Graph graph) {
        addPrimitive(edgeEntry = new VPrimitiveEntry(null, new PathStylePoints(new Color(96, 192, 96))));
        PointLabeledStyle vStyle = new PointLabeledStyle(PointStyle.PointShape.SQUARE, 4, StringStyle.ANCHOR_N);
        vStyle.setLabelColor(new Color(128, 128, 128));
        addPrimitive(vertexEntry = new VDraggablePrimitiveEntry(null, vStyle, this));
        setGraph(graph);
    }

    @Override
    public String toString() {
        return "Graph[" + graph.order() + " vertices]";
    }

    /** @return current style of point for this plottable */
    public PointLabeledStyle getPointStyle() { return (PointLabeledStyle) vertexEntry.style; }
    /** Set current style of point for this plottable */
    public void setPointStyle(PointLabeledStyle newValue) { if (vertexEntry.style != newValue) { vertexEntry.style = newValue; firePlottableStyleChanged(); } }
    /** @return current style for drawing edges of the graph */
    public PathStylePoints getEdgeStyle() { return (PathStylePoints) edgeEntry.style; }
    /** Sets current style for drawing edges of the graph */
    public void setEdgeStyle(PathStylePoints edgeStyle) { edgeEntry.style = edgeStyle; }
    
    /** @return underlying graph */
    public Graph getGraph() { return graph; }
    /**
     * Sets the underlying graph; uses default vertex positions around a circle
     * @param graph the new graph
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
        List nodes = graph.nodes();
        int size = nodes.size();
        GraphicString[] gsa = new GraphicString[size];
        Point2D.Double[] pts = StaticGraphLayout.RANDOM.layout(graph, 4.5);
        if (graph instanceof NodeValueGraph) {
            NodeValueGraph nvg = (NodeValueGraph) graph;
            for (int i = 0; i < size; i++)
                gsa[i] = new GraphicString<Point2D.Double>(pts[i], nvg.getValue(nodes.get(i)).toString());
        } else {
            for (int i = 0; i < size; i++)
                gsa[i] = new GraphicString<Point2D.Double>(pts[i], nodes.get(i).toString());
        }
        vertexEntry.local = gsa;
        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }


    /** @return window location of the i'th point */
    public Point2D.Double getPoint(int i) {
        return (Point2D.Double) ((GraphicString[])vertexEntry.local)[i].anchor;
    }
    /** Sets location of the i'th point */
    public void setPoint(int i, Point2D.Double newValue) {
        GraphicString[] gsa = (GraphicString[]) vertexEntry.local;
        gsa[i].anchor = newValue;
        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }
    /** @return list of all points where vertices are placed */
    public Point2D.Double[] getPoint() {
        GraphicString[] gsa = (GraphicString[]) vertexEntry.local;
        Point2D.Double[] result = new Point2D.Double[gsa.length];
        for (int i = 0; i < gsa.length; i++)
            result[i] = (Point2D.Double) gsa[i].anchor;
        return result;
    }
    /** Sets location of all vertices at once */
    public void setPoint(Point2D.Double[] loc) {
        GraphicString[] gsa = (GraphicString[]) vertexEntry.local;
        for (int i = 0; i < gsa.length; i++)
            gsa[i].anchor = loc[i];
        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }

    /** Sets label of i'th vertex */
    public void setLabel(int i, String newLabel) {
        // TODO - probably should change this to make use of the <code>NodeValueGraph</code> construction.
        GraphicString[] gsa = (GraphicString[]) vertexEntry.local;
        gsa[i].string = newLabel;
        vertexEntry.needsConversion = true;
        firePlottableStyleChanged();
    }

    @Override
    protected void recompute() {
        ArrayList<Point2D.Double[]> edges = new ArrayList<Point2D.Double[]>();
        GraphicString[] gsa = (GraphicString[]) vertexEntry.local;
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
        if (index != -1)
            setPoint(index, current);
    }
    public void mouseDragCompleted(Object source, Point2D.Double end) {
        mouseDragged(source, end);
    }

    /** Applies the specified layout to the current graph */
    public void applyLayout(StaticGraphLayout layoutAlgorithm, double... parameters) {
        setPoint(layoutAlgorithm.layout(graph, parameters));
    }
}
