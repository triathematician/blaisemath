/*
 * PlaneGraph.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.specto.plane.graph;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.RandomGraph;
import org.bm.blaise.scio.graph.ValuedGraph;
import primitive.GraphicPointFancy;
import primitive.style.Anchor;
import primitive.style.PointFancyStyle;
import primitive.style.PointStyle;
import visometry.plane.PlanePlotComponent;

/**
 * <p>
 *    Displays a graph on a <code>PlanePlotComponent</code>, with draggable vertices.
 *    The graph is in the form of a <code>Graph</code>, which simply
 *    contains an iteration over a set of objects, and information about the adjacencies
 *    between those objects. If the graph is a <code>ValuedGraph</code>, then the
 *    values (or labels) may be displayed at the nodes.
 * </p>
 * @author Elisha Peterson
 * @see Graph
 * @see PlanePlotComponent
 */
public final class PlaneGraph extends AbstractPlaneGraph<GraphicPointFancy<Point2D.Double>> {

    /** Constructs graph with a sample network */
    public PlaneGraph() { this(RandomGraph.getInstance(20, 25, false)); }

    /** Constructs with specified graph */
    public PlaneGraph(Graph graph) { 
        super(graph, new PointFancyStyle(PointStyle.PointShape.CIRCLE, Anchor.North));
        PointFancyStyle vStyle = ((PointFancyStyle) vertexEntry.style);
        vStyle.setLabelColor(new Color(128, 128, 128));
        vStyle.setMaxRadius(5.0);
    }

    @Override
    public String toString() {
        return "Graph[" + graph.order() + " vertices]";
    }
    
    /** @return current style of point for this plottable */
    public PointFancyStyle getPointStyle() { return (PointFancyStyle) vertexEntry.style; }
    /** Set current style of point for this plottable */
    public void setPointStyle(PointFancyStyle newValue) { if (vertexEntry.style != newValue) { vertexEntry.style = newValue; firePlottableStyleChanged(); } }

    /** Sets current list of vertex values */
    public void setNodeValues(List values) {
//        System.out.println("setNodeValues");
        GraphicPointFancy[] gpfa = (GraphicPointFancy[]) vertexEntry.local;
        if (values == null) {
            return;
//            for (int i = 0; i < gpfa.length; i++)
//                gpfa[i].rad = 1;
//            vertexEntry.needsConversion = true;
//            firePlottableChanged();
        } else if (vertexEntry.local != null && graph != null) {
            if (values.size() != graph.nodes().size())
                throw new IllegalArgumentException("setVertexValues requires #nodes = #values");
            for (int i = 0; i < values.size(); i++)
                gpfa[i].rad = Math.sqrt(((Number) values.get(i)).doubleValue());
            vertexEntry.needsConversion = true;
            firePlottableChanged();
        }
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
//        System.out.println("updateLabels");
        List nodes = graph.nodes();
        int size = nodes.size();
        if (vertexEntry.local == null || ((GraphicPointFancy[])vertexEntry.local).length != size)
            vertexEntry.local = new GraphicPointFancy[size];
        Point2D.Double[] arr = (Point2D.Double[]) vertexEntry.local;
        ValuedGraph vg = graph instanceof ValuedGraph ? (ValuedGraph) graph : null;
        String value;
        int i = 0;
        for (Object o : nodes) {
            value = vg == null ? o.toString() : vg.getValue(o).toString();
            if (arr[i] == null)
                arr[i] = new GraphicPointFancy<Point2D.Double>((Point2D.Double) pos.get(o), value, 1);
            else
                ((GraphicPointFancy)arr[i]).setString(value);
//            if (i%2==0) gpfa[i].setColor(Color.RED);
            i++;
        }
        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }

    /** Updates colors to highlight selected nodes only */
    public void highlightNodes(Collection subset) {
//        System.out.println("highlight nodes");
        List nodes = graph.nodes();
        GraphicPointFancy[] gpfa = (GraphicPointFancy[]) vertexEntry.local;
        int i = 0;
        for (Object o : nodes)
            gpfa[i++].setColor(subset == null || subset.contains(o) ? Color.RED : null);
        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }
}
