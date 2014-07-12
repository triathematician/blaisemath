/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

import java.util.Set;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;

/**
 * Provides a derived {@link GraphSubsetMetric} by contracting all the
 * nodes in a subset to a single node, and using a {@link GraphNodeMetric}
 * on that node.
 *
 * @author Elisha Peterson
 */
public class ContractiveSubsetMetric<N> implements GraphSubsetMetric<N> {

    GraphNodeMetric<N> baseMetric;

    /**
     * Constructs with provided base metric.
     * @param baseMetric the metric to use for computations of contracted node
     */
    public ContractiveSubsetMetric(GraphNodeMetric<N> baseMetric) { 
        this.baseMetric = baseMetric; 
    }

    public <V> N getValue(Graph<V> graph, Set<V> nodes) {
        V starNode = null;
        for (V v : nodes) { starNode = v; break; }
        Graph<V> contracted = GraphUtils.contractedGraph(graph, nodes, starNode);
        return baseMetric.value(contracted, starNode);
    }

}
