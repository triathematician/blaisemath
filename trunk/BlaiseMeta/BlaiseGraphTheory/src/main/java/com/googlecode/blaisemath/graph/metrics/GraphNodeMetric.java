/*
 * GraphNodeMetric.java
 * Created on Oct 26, 2009
 */

package com.googlecode.blaisemath.graph.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

/**
 * Returns a value for a single node in a graph.
 * @param <N> the type of value returned (usually a number)
 * 
 * @author Elisha Peterson
 */
public interface GraphNodeMetric<N> {

    /**
     * Computes the value of the metric for the given graph and node.
     * @param graph the graph
     * @param node a node in the graph
     * @return value of the metric
     * @throws IllegalArgumentException if the value cannot be computed for
     *      specified graph (e.g. graph is null, or graph is directed, but the
     *      metric only applies to undirected graphs)
     */
    public <V> N value(Graph<V> graph, V node);

}
