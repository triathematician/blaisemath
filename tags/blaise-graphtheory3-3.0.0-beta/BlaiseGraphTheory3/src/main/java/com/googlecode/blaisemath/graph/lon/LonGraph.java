/*
 * LonGraph.java
 * Created Jul 5, 2010
 */

package com.googlecode.blaisemath.graph.lon;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import java.util.Collection;
import java.util.List;
import com.googlecode.blaisemath.graph.Graph;

/**
 * <p>
 * The interface contains methods describing a graph that evolves over time.
 * The primary method allows the user to extract a graph for a specified time
 * (called a "slice"),provided that the graph exists at that time.
 * The time format is assumed to be a double.
 * Implementations may choose how to store the underlying data.
 * </p>
 *
 * @param <V> the type of the nodes
 *
 * @author Elisha Peterson
 */
public interface LonGraph<V> {

    /**
     * Returns <i>all</i> nodes contained in the longitudinal graph, for any time.
     * @return view of all nodes
     */
    Collection<V> getAllNodes();

    /**
     * Get whether longituinal graph's edges are directed
     * @return true if directed
     */
    boolean isDirected();

    /**
     * Returns the time intervals corresponding to the given node.
     * @param v a node in the graph
     * @return time intervals of corresponding node, or null if the node is not in the graph
     */
    List<double[]> getNodeIntervals(V v);

    /**
     * Returns the time intervals corresponding to the given edge.
     * @param v1 first node of the edge
     * @param v2 second node of the edge
     * @return time intervals of the corresponding edge, or null if the edge is never in the graph
     */
    List<double[]> getEdgeIntervals(V v1, V v2);

    /**
     * Returns a view of the graph at the specified time. Depending upon the
     * implementation, a graph may or may not exist at a given time. In some cases,
     * the implementation's nodes and arcs will occur at <i>intervals</i>, so the
     * slice will pick out any node or arc that occurs within the interval. In other
     * cases, the implementation will have graphs that occur at specific times, and
     * the method will pick out the (discrete) graph instance for the specified time.
     *
     * The second parameter allows the user to request a slice nearby the specified
     * time, even if it does not exist exactly.
     *
     * @param time time of interest
     * @param exact whether should only return graph at the exact time (true),
     *   or whether to return the closest available graph (false)
     * @return a copy of the graph at the specified time, or null if no graph
     *   exists at that time
     */
    Graph<V> slice(double time, boolean exact);

    /**
     * Returns the minimum time encoded by elements in this graph.
     * @return minimum time
     */
    double getMinimumTime();

    /**
     * Returns the maximum time encoded by elements in this graph
     * @return maximum time
     */
    double getMaximumTime();

    /**
     * Returns a list of times encoded by elements in this graph.
     * An empty list might be interpreted as a continuous spectrum.
     * @return a list of all times in this graph
     */
    List<Double> getTimes();

}
