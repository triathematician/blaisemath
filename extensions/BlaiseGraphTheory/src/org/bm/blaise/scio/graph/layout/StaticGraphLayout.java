/*
 * StaticGraphLayout.java
 * Created May 13, 2010
 */

package org.bm.blaise.scio.graph.layout;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import org.bm.blaise.scio.graph.Graph;

/**
 * This interface provides methods necessary to layout a graph.
 * @author Elisha Peterson
 */
public interface StaticGraphLayout {

    /**
     * @param g a graph written in terms of adjacencies
     * @param parameters parameters for the layout, e.g. radius
     * @return a mapping of points to vertices
     */
    public Map<Object,Point2D.Double> layout(Graph g, double... parameters);

    /** Lays out vertices all at the origin. */
    public static StaticGraphLayout ORIGIN = new StaticGraphLayout(){
        public Map<Object, Point2D.Double> layout(Graph g, double... parameters) {
            HashMap<Object, Point2D.Double> result = new HashMap<Object, Point2D.Double>();
            for (Object v : g.nodes())
                result.put(v, new Point2D.Double());
            return result;
        };
    };

    /** Lays out vertices uniformly around a circle (radius corresponds to first parameter). */
    public static StaticGraphLayout CIRCLE = new StaticGraphLayout(){
        public Map<Object, Point2D.Double> layout(Graph g, double... parameters) {
            HashMap<Object, Point2D.Double> result = new HashMap<Object, Point2D.Double>();
            int size = g.order();
            double radius = parameters.length > 0 ? parameters[0] : 1;
            int i = 0;
            for (Object v : g.nodes()) {
                result.put(v, new Point2D.Double(radius*Math.cos(2*Math.PI*i/size), radius*Math.sin(2*Math.PI*i/size)));
                i++;
            }
            return result;
        }
    };

    /** Lays out vertices at random positions within a square (size corresponds to first parameter). */
    public static StaticGraphLayout RANDOM = new StaticGraphLayout(){
        public Map<Object, Point2D.Double> layout(Graph g, double... parameters) {
            HashMap<Object, Point2D.Double> result = new HashMap<Object, Point2D.Double>();
            double multiplier = parameters.length > 0 ? parameters[0] : 1;
            for (Object v : g.nodes()) {
                result.put(v, new Point2D.Double(multiplier*(2*Math.random()-1), multiplier*(2*Math.random()-1)));
            }
            return result;
        }
    };
}
