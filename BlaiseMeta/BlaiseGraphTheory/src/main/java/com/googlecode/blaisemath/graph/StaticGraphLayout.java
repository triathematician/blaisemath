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

import com.google.common.collect.Maps;
import com.googlecode.blaisemath.util.SetSelectionModel;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

/**
 * This interface provides methods necessary to layout a graph.
 *
 * @param <P> object describing layout parameters
 * 
 * @author Elisha Peterson
 */
public interface StaticGraphLayout<P> {
    
    /**
     * Get data model representing a set of nodes to pin in the layout.
     * @return pinnable model
     */
    SetSelectionModel getPinnedNodes();

    /**
     * Perform layout on given graph, and return result.
     * @param g a graph written in terms of adjacencies
     * @param ic initial conditions
     * @param parameters parameters for the layout, e.g. radius
     * @return a mapping of points to vertices
     * @param <C> graph node type
     */
    <C> Map<C, Point2D.Double> layout(Graph<C> g, Map<C, Point2D.Double> ic, P parameters);

    /**
     * Puts vertices all at the origin.
     */
    public static StaticGraphLayout ORIGIN = new StaticGraphLayout() {
        private final SetSelectionModel pinned = new SetSelectionModel();
        @Override
        public String toString() {
            return "Position nodes at origin";
        }
        public SetSelectionModel getPinnedNodes() {
            return pinned;
        }
        public Map layout(Graph g, Map ic, Object parameters) {
            HashMap result = Maps.newHashMap();
            for (Object v : g.nodes()) {
                result.put(v, new Point2D.Double());
            }
            return result;
        }
    };

    /** Lays out vertices uniformly around a circle (radius corresponds to first parameter). */
    public static StaticGraphLayout<Double> CIRCLE = new StaticGraphLayout<Double>() {
        private final SetSelectionModel pinned = new SetSelectionModel();
        @Override
        public String toString() {
            return "Position nodes in a circle";
        }
        public SetSelectionModel getPinnedNodes() {
            return pinned;
        }
        public Map layout(Graph g, Map ic, Double radius) {
            HashMap<Object, Point2D.Double> result = Maps.newHashMap();
            int size = g.nodeCount();
            int i = 0;
            for (Object v : g.nodes()) {
                result.put(v, new Point2D.Double(
                        radius * Math.cos(2 * Math.PI * i / size), 
                        radius * Math.sin(2 * Math.PI * i / size)));
                i++;
            }
            return result;
        }
    };
    /**
     * Lays out vertices at random positions within a square (size corresponds
     * to first parameter).
     */
    public static StaticGraphLayout<Double> RANDOM = new StaticGraphLayout<Double>() {
        private final SetSelectionModel pinned = new SetSelectionModel();
        @Override
        public String toString() {
            return "Position nodes randomly in a rectangle";
        }
        public SetSelectionModel getPinnedNodes() {
            return pinned;
        }
        public Map layout(Graph g, Map ic, Double boxSize) {
            HashMap<Object, Point2D.Double> result = Maps.newHashMap();
            for (Object v : g.nodes()) {
                result.put(v, new Point2D.Double(
                        boxSize * (2 * Math.random() - 1), 
                        boxSize * (2 * Math.random() - 1)));
            }
            return result;
        }
    };
}
