/*
 * CliqueCount.java
 * Created Nov 4, 2011
 */
package com.googlecode.blaisemath.graph.mod.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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

import com.googlecode.blaisemath.graph.GraphNodeMetric;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;

/**
 * Computes the clique count of a particular vertex,
 * i.e. the number of connections between edges in the neighborhood
 * of the vertex, not counting the edges adjacent to the vertex itself.
 * Current computation time is linear in the # of edges in the graph (vertex case),
 * and quadratic in the map case (linear in edges * linear in vertices).
 * 
 * @author elisha
 */
public class CliqueCount implements GraphNodeMetric<Integer> {
    
    @Override
    public String toString() {
        return "Clique count";
    }
    
    @Override
    public <V> Integer apply(Graph<V> graph, V node) { 
        return GraphUtils.copySubgraph(graph, graph.neighbors(node)).edgeCount();
    }
    
}
