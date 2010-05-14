/*
 * PlaneGraph.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.specto.plane.graph;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.bm.blaise.scio.graph.Edge;
import org.bm.blaise.scio.graph.NeighborSetInterface;
import org.bm.blaise.scio.graph.SimpleGraph;
import org.bm.blaise.scio.graph.creation.GraphCreation;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;
import primitive.GraphicString;
import primitive.style.PathStylePoints;
import primitive.style.PointLabeledStyle;
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
 * @see NeighborSetInterface
 * @see PlanePlotComponent
 * @see VPointSet
 */
public class PlaneGraph extends Plottable<Point2D.Double>
        implements PointDragListener<Point2D.Double> {

    /** Vertices; underlying primitive is an array of GraphicString's */
    VPrimitiveEntry vertexEntry;
    /** Edges; underlying primitive is an array of point-pairs */
    VPrimitiveEntry edgeEntry;
    /** Captures connections. */
    NeighborSetInterface nsi;

    /** Constructs graph with a sample network */
    public PlaneGraph() {
        this(GraphCreation.generateSparseRandomGraph(10, 10, false));
    }

    public PlaneGraph(NeighborSetInterface nsi) {
        addPrimitive(edgeEntry = new VPrimitiveEntry(null, new PathStylePoints(new Color(96, 192, 96))));
        addPrimitive(vertexEntry = new VDraggablePrimitiveEntry(null, new PointLabeledStyle(StringStyle.ANCHOR_W), this));
        setGraph(nsi);
    }

    @Override
    public String toString() {
        return "Graph[" + nsi.getSize() + " vertices]";
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
    public NeighborSetInterface getGraph() { return nsi; }
    /** Sets the underlying graph; uses default vertex positions around a circle */
    public void setGraph(NeighborSetInterface nsi) {
        this.nsi = nsi;
        int size = nsi.getSize();
        GraphicString[] gsa = new GraphicString[size];
        Point2D.Double[] pts = StaticGraphLayout.RANDOM.layout(nsi, 4.5);
        for (int i = 0; i < size; i++)
            gsa[i] = new GraphicString<Point2D.Double>(pts[i], nsi.getLabel(i));
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

    /** @return edge in local coordinates between specified indexed vertices */
    public Point2D.Double[] getEdge(int i1, int i2) {
        return new Point2D.Double[] { getPoint(i1), getPoint(i2) };
    }

    @Override
    protected void recompute() {
        ArrayList<Point2D.Double[]> edges = new ArrayList<Point2D.Double[]>();
        if (nsi instanceof SimpleGraph) {
            SimpleGraph sg = (SimpleGraph) nsi;
            for (Edge e : sg.getEdges())
                edges.add(getEdge(e.getSource(), e.getSink()));
        } else {
            for (int i1 = 0; i1 < nsi.getSize(); i1++)
                for (int i2 = 0; i2 < nsi.getSize(); i2++)
                    if (i1 != i2 && nsi.adjacent(i1, i2))
                        edges.add(getEdge(i1, i2));
        }
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
        setPoint(layoutAlgorithm.layout(nsi, parameters));
    }
}
