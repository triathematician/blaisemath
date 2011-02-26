/*
 * ImageGraph.java
 * Created on Aug 9, 2010
 */

package org.bm.blaise.specto.plane.graph;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.RandomGraph;
import org.bm.blaise.scio.graph.ValuedGraph;
import primitive.GraphicImage;
import primitive.style.ImageStyle;
import visometry.plane.PlanePlotComponent;

/**
 * <p>
 *    Displays a graph on a <code>PlanePlotComponent</code>, with draggable vertices
 *    in the form of images.
 * </p>
 * @author Elisha Peterson
 * @see Graph
 * @see GraphicImage
 * @see PlanePlotComponent
 */
public final class ImageGraph extends AbstractPlaneGraph<GraphicImage<Point2D.Double>> {
    
    /** Constructs graph with a sample network */
    public ImageGraph() {
        this(RandomGraph.getInstance(20, 25, false));
    }

    /** Constructs with specified graph */
    public ImageGraph(Graph graph) {
        super(graph, new ImageStyle());
        getImageStyle().setMaxHeight(80);
        getImageStyle().setMaxWidth(80);
    }

    @Override
    public String toString() {
        return "Graph[" + graph.order() + " vertices]";
    }
    
    /** @return current style of image for this plottable */
    public ImageStyle getImageStyle() { return (ImageStyle) vertexEntry.style; }
    /** Set current style of image for this plottable */
    public void setImageStyle(ImageStyle newValue) { if (vertexEntry.style != newValue) { vertexEntry.style = newValue; firePlottableStyleChanged(); } }

    /** Labels not supported by this view */
    public void updateLabels() {
        List nodes = graph.nodes();
        int size = nodes.size();
        if (vertexEntry.local == null) vertexEntry.local = new GraphicImage[size];
        Point2D.Double[] gpfa = (Point2D.Double[]) vertexEntry.local;
        ValuedGraph vg = graph instanceof ValuedGraph ? (ValuedGraph) graph : null;
        int i = 0;
        for (Object o : nodes) {
            Image image = null;
            Object v1 = vg.getValue(o);
            if (v1 != null && v1.getClass().equals(Object[].class)) {
                Object[] v2 = (Object[]) v1;
                if (v2.length >= 2 && v2[1] instanceof Image)
                    image = (Image) v2[1];
            }
            if (gpfa[i] == null)
                gpfa[i] = new GraphicImage<Point2D.Double>((Point2D.Double) pos.get(o), image);
            else
                ((GraphicImage)gpfa[i]).setImage(image);
            i++;
        }
        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }

    /** Highlights not supported by this view */
    public void highlightNodes(Collection subset) {
        List nodes = graph.nodes();
        GraphicImage[] gia = (GraphicImage[]) vertexEntry.local;
        int i = 0;
        for (Object o : nodes)
            gia[i++].highlight = subset.contains(o);
        vertexEntry.needsConversion = true;
        firePlottableChanged();
    }

    /** Node values not supported by this view */
    public void setNodeValues(List values) {}
    /** Labels not supported by this view */
    public void setLabel(int i, String newLabel) {}
}
