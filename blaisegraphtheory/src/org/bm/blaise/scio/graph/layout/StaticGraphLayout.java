/*
 * StaticGraphLayout.java
 * Created May 13, 2010
 */

package org.bm.blaise.scio.graph.layout;

import java.awt.geom.Point2D;
import org.bm.blaise.scio.graph.Graph;

/**
 * This interface provides methods necessary to layout a graph.
 * @author Elisha Peterson
 */
public interface StaticGraphLayout {

    /**
     * @param g a graph written in terms of adjacencies
     * @param parameters parameters for the layout, e.g. radius
     * @return a set of points representing sequential positions of the vertices
     */
    public Point2D.Double[] layout(Graph g, double... parameters);

    /** Lays out vertices uniformly around a circle (radius corresponds to first parameter). */
    public static StaticGraphLayout CIRCLE = new StaticGraphLayout(){
        public Point2D.Double[] layout(Graph g, double... parameters) {
            int size = g.order();
            Point2D.Double[] result = new Point2D.Double[size];
            double radius = parameters.length > 0 ? parameters[0] : 1;
            for (int i = 0; i < result.length; i++)
                result[i] = new Point2D.Double(radius*Math.cos(2*Math.PI*i/size), radius*Math.sin(2*Math.PI*i/size));
            return result;
        }
    };

    /** Lays out vertices at random positions within a square (size corresponds to first parameter). */
    public static StaticGraphLayout RANDOM = new StaticGraphLayout(){
        public Point2D.Double[] layout(Graph g, double... parameters) {
            int size = g.order();
            Point2D.Double[] result = new Point2D.Double[size];
            double multiplier = parameters.length > 0 ? parameters[0] : 1;
            for (int i = 0; i < result.length; i++)
                result[i] = new Point2D.Double(multiplier*(2*Math.random()-1), multiplier*(2*Math.random()-1));
            return result;
        }
    };
}
