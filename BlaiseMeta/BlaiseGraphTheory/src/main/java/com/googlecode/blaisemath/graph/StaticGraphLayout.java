/*
 * StaticGraphLayout.java
 * Created May 13, 2010
 */
package com.googlecode.blaisemath.graph;

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

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import com.googlecode.blaisemath.graph.Graph;

/**
 * This interface provides methods necessary to layout a graph.
 *
 * @author Elisha Peterson
 */
public interface StaticGraphLayout {

    /**
     * @param g a graph written in terms of adjacencies
     * @param parameters parameters for the layout, e.g. radius
     * @return a mapping of points to vertices
     * @param <C> graph node type
     * @throws InterruptedException if running on a background thread which is interrupted
     */
    public <C> Map<C, Point2D.Double> layout(Graph<C> g, double... parameters) throws InterruptedException;

    /**
     * Lays out vertices all at the origin.
     */
    public static StaticGraphLayout ORIGIN = new StaticGraphLayout() {
        public <C> Map<C, Point2D.Double> layout(Graph<C> g, double... parameters) {
            HashMap<C, Point2D.Double> result = new HashMap<C, Point2D.Double>();
            for (C v : g.nodes()) {
                result.put(v, new Point2D.Double());
            }
            return result;
        }
    ;
    };

    /** Lays out vertices uniformly around a circle (radius corresponds to first parameter). */
    public static StaticGraphLayout CIRCLE = new StaticGraphLayout() {
        public <C> Map<C, Point2D.Double> layout(Graph<C> g, double... parameters) {
            HashMap<C, Point2D.Double> result = new HashMap<C, Point2D.Double>();
            int size = g.nodeCount();
            double radius = parameters.length > 0 ? parameters[0] : 1;
            int i = 0;
            for (C v : g.nodes()) {
                result.put(v, new Point2D.Double(radius * Math.cos(2 * Math.PI * i / size), radius * Math.sin(2 * Math.PI * i / size)));
                i++;
            }
            return result;
        }
    };
    /**
     * Lays out vertices at random positions within a square (size corresponds
     * to first parameter).
     */
    public static StaticGraphLayout RANDOM = new StaticGraphLayout() {
        public <C> Map<C, Point2D.Double> layout(Graph<C> g, double... parameters) {
            HashMap<C, Point2D.Double> result = new HashMap<C, Point2D.Double>();
            double multiplier = parameters.length > 0 ? parameters[0] : 1;
            for (C v : g.nodes()) {
                result.put(v, new Point2D.Double(multiplier * (2 * Math.random() - 1), multiplier * (2 * Math.random() - 1)));
            }
            return result;
        }
    };
}
