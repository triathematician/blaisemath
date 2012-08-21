/*
 * ComponentCircleLayout.java
 * Created on Nov 28, 2011
 */
package org.bm.blaise.scio.graph.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphUtils;

/**
 * This layout places components of a graph in a spiral pattern, with the largest
 * component in the center. The nodes in each component are placed around a circle.
 * Provides a good starting location (when graphs have many components) for
 * iterative layout algorithms that ensures things don't get too far apart, and
 * nodes in the same component start nearby.
 *
 * @author petereb1
 */
public class ComponentCircleLayout implements StaticGraphLayout {

    public static final Comparator<Graph> GRAPH_SIZE_DESCENDING = new Comparator<Graph>(){
        @Override
        public int compare(Graph o1, Graph o2) {
            return -(o1.order() == o2.order() && o1.edgeCount() == o2.edgeCount() ? o1.nodes().toString().compareTo(o2.nodes().toString())
                    : o1.order() == o2.order() ? o1.edgeCount() - o2.edgeCount()
                    : o1.order() - o2.order());
        }
    };

    public Map<Object, Point2D.Double> layout(Graph graph, double... doubles) {
        if (graph.isDirected())
            graph = GraphUtils.undirectedCopy(graph);
        List<Graph> components = GraphUtils.getComponentGraphs(graph);
        Collections.sort(components, GRAPH_SIZE_DESCENDING);

        Map<Object,Point2D.Double> result = new HashMap<Object,Point2D.Double>();

        List<Rectangle2D.Double> priors = new ArrayList<Rectangle2D.Double>();
        List<Integer> layers = new ArrayList<Integer>();
        for (Graph compt : components)
            result.putAll(layoutNext(compt, priors, layers));

        return result;
    }

    private static Map<Object,Point2D.Double> layoutNext(Graph graph, List<Rectangle2D.Double> priors, List<Integer> layers) {
        List nodes = graph.nodes();
        int n = nodes.size();

        Map<Object,Point2D.Double> result = new HashMap<Object,Point2D.Double>();

        Rectangle2D.Double nxt = nextBounds(n, priors, layers);
        double cx = nxt.getCenterX(), cy = nxt.getCenterY();
        double rad = Math.min(nxt.getWidth(), nxt.getHeight());

        if (n == 3 && graph.edgeCount() == 2) {
            Object n1 = nodes.get(0);
            Object n2 = nodes.get(1);
            Object n3 = nodes.get(2);
            if (graph.degree(n1) == 2) { Object nt = n2; n2 = n1; n1 = nt; }
            if (graph.degree(n3) == 2) { Object nt = n2; n2 = n3; n3 = nt; }
            assert graph.degree(n2) == 2;
            result.put(n1, new Point2D.Double(cx-rad,0));
            result.put(n2, new Point2D.Double(0,0));
            result.put(n3, new Point2D.Double(cx+rad,0));
        } else {
            int rots = (int) (Math.sqrt(n)/2);
            for (int i = 0; i < n; i++) {
                double pct = i/(double) n;
                double theta = rots*2*Math.PI*pct;
                result.put(nodes.get(i), new Point2D.Double(cx+pct*rad*Math.cos(theta), cy+pct*rad*Math.sin(theta)));
            }
        }
        return result;
    }

    /** Generates position in a sequence of circles. */
    private static Rectangle2D.Double nextBounds(int sz, List<Rectangle2D.Double> priors, List<Integer> layers) {
        double nueRad = Math.sqrt(sz)/Math.PI;
        if (priors.isEmpty()) {
            Rectangle2D.Double r = new Rectangle2D.Double(-nueRad,-nueRad,2*nueRad,2*nueRad);
            priors.add(r);
            layers.add(0);
            return r;
        }

        Rectangle2D.Double ctr = priors.get(0);
        double cx = ctr.getCenterX(), cy = ctr.getCenterY();
        double cRad = Math.min(ctr.getWidth(), ctr.getHeight());

        if (priors.size() == 1) {
            Rectangle2D.Double r = new Rectangle2D.Double(cx+cRad, cy-nueRad, 2*nueRad, 2*nueRad);
            priors.add(r);
            layers.add(1);
            return r;
        } else {
            int lSz = layers.size();
            int layer = layers.get(lSz-1);
            int startLayer = 0;
            while (layers.get(startLayer) < layer)
                startLayer++;
            Rectangle2D.Double lProto = priors.get(startLayer);
            double lRad1 = Math.min(lProto.getWidth(), lProto.getHeight());
            double lRad2 = Math.abs(cx-lProto.getCenterX());
            double availableOnLayer = 2*Math.PI*lRad2; // how much space in this radial layer for layout
            int alreadyOnLayer = lSz-startLayer+1;
            if (alreadyOnLayer*2*lRad1 > availableOnLayer-2*lRad1) {
                // start a new layer
                layer++;
                Rectangle2D.Double r = new Rectangle2D.Double(cx+lRad2+lRad1, cy-nueRad, 2*nueRad, 2*nueRad);
                priors.add(r);
                layers.add(layer);
                return r;
            } else {
                // continue existing layer
                double theta = 2*lRad1/lRad2 * alreadyOnLayer;
                Rectangle2D.Double r = new Rectangle2D.Double(cx+lRad2*Math.cos(theta)-nueRad, cy+lRad2*Math.sin(theta)-nueRad, 2*nueRad, 2*nueRad);
                priors.add(r);
                layers.add(layer);
                return r;
            }
        }
    }

}
