/*
 * ComponentCircleLayout.java
 * Created on Nov 28, 2011
 */
package com.googlecode.blaisemath.graph.modules.layout;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import com.googlecode.blaisemath.util.SetSelectionModel;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This layout places components of a graph in a spiral pattern, with the
 * largest component in the center. The nodes in each component are placed
 * around a circle. Provides a good starting location (when graphs have many
 * components) for iterative layout algorithms that ensures things don't get too
 * far apart, and nodes in the same component start nearby.
 *
 * @author petereb1
 */
public class ComponentCircleLayout implements StaticGraphLayout {

    public static final int DIST_SCALE = 50;
    
    public static final Comparator<Graph> GRAPH_SIZE_DESCENDING = new Comparator<Graph>() {
        @Override
        public int compare(Graph o1, Graph o2) {
            return -(o1.nodeCount() == o2.nodeCount() && o1.edgeCount() == o2.edgeCount() ? o1.nodes().toString().compareTo(o2.nodes().toString())
                    : o1.nodeCount() == o2.nodeCount() ? o1.edgeCount() - o2.edgeCount()
                    : o1.nodeCount() - o2.nodeCount());
        }
    };
    
    @Override
    public String toString() {
        return "ComponentCircleLayout";
    }
    
    private final SetSelectionModel pinned = new SetSelectionModel();
    
    public SetSelectionModel getPinnedNodes() {
        return pinned;
    }

    public Map<Object, Point2D.Double> layout(Graph graph, double... doubles) {
        if (graph.isDirected()) {
            graph = GraphUtils.copyAsUndirectedSparseGraph(graph);
        }
        Set<Graph> components = new TreeSet<Graph>(GRAPH_SIZE_DESCENDING);
        components.addAll(GraphUtils.componentGraphs(graph));

        Map<Object, Point2D.Double> result = new HashMap<Object, Point2D.Double>();

        List<Rectangle2D.Double> priors = new ArrayList<Rectangle2D.Double>();
        List<Integer> layers = new ArrayList<Integer>();
        for (Graph compt : components) {
            result.putAll(layoutNext(compt, priors, layers));
        }

        return result;
    }

    private static Map<Object, Point2D.Double> layoutNext(Graph graph, List<Rectangle2D.Double> priors, List<Integer> layers) {
        Set nodes = graph.nodes();
        int n = nodes.size();

        Map<Object, Point2D.Double> result = new HashMap<Object, Point2D.Double>();

        Rectangle2D.Double nxt = nextBounds(n, priors, layers);
        double cx = nxt.getCenterX(), cy = nxt.getCenterY();
        double rad = Math.min(nxt.getWidth(), nxt.getHeight());

        if (n == 3 && graph.edgeCount() == 2) {
            Object[] nn = nodes.toArray();
            if (graph.degree(nn[0]) == 2) {
                Object nt = nn[1];
                nn[1] = nn[0];
                nn[0] = nt;
            }
            if (graph.degree(nn[2]) == 2) {
                Object nt = nn[1];
                nn[1] = nn[2];
                nn[2] = nt;
            }
            assert graph.degree(nn[1]) == 2;
            result.put(nn[0], new Point2D.Double(cx - rad, 0));
            result.put(nn[1], new Point2D.Double(0, 0));
            result.put(nn[2], new Point2D.Double(cx + rad, 0));
        } else {
            int rots = (int) (Math.sqrt(n) / 2);
            int i = 0;
            for (Object o : nodes) {
                double pct = i++ / (double) n;
                double theta = rots * 2 * Math.PI * pct;
                result.put(o, new Point2D.Double(cx + pct * rad * Math.cos(theta), cy + pct * rad * Math.sin(theta)));
            }
        }
        return result;
    }

    /**
     * Generates position in a sequence of circles.
     */
    private static Rectangle2D.Double nextBounds(int sz, List<Rectangle2D.Double> priors, List<Integer> layers) {
        double nueRad = DIST_SCALE * Math.sqrt(sz) / Math.PI;
        if (priors.isEmpty()) {
            Rectangle2D.Double r = new Rectangle2D.Double(-nueRad, -nueRad, 2 * nueRad, 2 * nueRad);
            priors.add(r);
            layers.add(0);
            return r;
        }

        Rectangle2D.Double ctr = priors.get(0);
        double cx = ctr.getCenterX(), cy = ctr.getCenterY();
        double cRad = Math.min(ctr.getWidth(), ctr.getHeight());

        if (priors.size() == 1) {
            Rectangle2D.Double r = new Rectangle2D.Double(cx + cRad, cy - nueRad, 2 * nueRad, 2 * nueRad);
            priors.add(r);
            layers.add(1);
            return r;
        } else {
            int lSz = layers.size();
            int layer = layers.get(lSz - 1);
            int startLayer = 0;
            while (layers.get(startLayer) < layer) {
                startLayer++;
            }
            Rectangle2D.Double lProto = priors.get(startLayer);
            double lRad1 = Math.min(lProto.getWidth(), lProto.getHeight());
            double lRad2 = Math.abs(cx - lProto.getCenterX());
            double availableOnLayer = 2 * Math.PI * lRad2; // how much space in this radial layer for layout
            int alreadyOnLayer = lSz - startLayer + 1;
            if (alreadyOnLayer * 2 * lRad1 > availableOnLayer - 2 * lRad1) {
                // start a new layer
                layer++;
                Rectangle2D.Double r = new Rectangle2D.Double(cx + lRad2 + lRad1, cy - nueRad, 2 * nueRad, 2 * nueRad);
                priors.add(r);
                layers.add(layer);
                return r;
            } else {
                // continue existing layer
                double theta = 2 * lRad1 / lRad2 * alreadyOnLayer;
                Rectangle2D.Double r = new Rectangle2D.Double(cx + lRad2 * Math.cos(theta) - nueRad, cy + lRad2 * Math.sin(theta) - nueRad, 2 * nueRad, 2 * nueRad);
                priors.add(r);
                layers.add(layer);
                return r;
            }
        }
    }
}
