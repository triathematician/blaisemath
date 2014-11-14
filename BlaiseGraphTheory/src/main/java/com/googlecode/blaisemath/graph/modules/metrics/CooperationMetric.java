/*
 * CooperationMetric.java
 * Created Jul 14, 2010
 */

package com.googlecode.blaisemath.graph.modules.metrics;

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

import java.util.HashSet;
import java.util.Set;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;

/**
 * Measures difference in team performance with and without a node/subset of nodes.
 * Result contains several values: first is selfish contribution, second is altruistic;
 *  third is entire team value, fourth is partial assessment value, fifth is partial team value
 * 
 * @author Elisha Peterson
 */
public class CooperationMetric<N extends Number> implements GraphSubsetMetric<double[]> {

    GraphSubsetMetric<N> baseM;

    /** Construct with base subset metric. */
    public CooperationMetric(GraphSubsetMetric<N> baseM) {
        this.baseM = baseM;
    }

    @Override public String toString() {
        return "CooperationMetric[" + baseM + "]";
    }

    public <V> double[] getValue(Graph<V> graph, Set<V> nodes) {
        int n = graph.nodeCount(), m = nodes.size();
        Set<V> all = graph.nodes();
        Set<V> complement = new HashSet<V>(all);
        complement.removeAll(nodes);
        double outcomeAll = n == 0 ? 0.0 : baseM.getValue(graph, all).doubleValue();
        double outcomeAll2 = m == n ? 0.0 : baseM.getValue(graph, complement).doubleValue();
        double outcomePart = m == n ? 0.0 : baseM.getValue(GraphUtils.copySubgraph(graph, complement), complement).doubleValue();
        return new double[] { 
            outcomeAll - outcomeAll2,   // selfish contribution
            outcomeAll2 - outcomePart,  // altruistic contribution
            outcomeAll,                 // value of the outcome for the whole team
            outcomeAll2,                // value of the outcome for the whole team, perceived by complement
            outcomePart };              // value of the outcome for the complement
    }

}
