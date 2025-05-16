package com.googlecode.blaisemath.graph.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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

import com.google.common.annotations.Beta;
import com.google.common.graph.Graph;
import com.google.common.graph.Graphs;
import com.googlecode.blaisemath.graph.GraphSubsetMetric;

import java.util.HashSet;
import java.util.Set;

/**
 * Measures difference in team performance with and without a node/subset of
 * nodes. Result contains several values: first is selfish contribution, second
 * is altruistic; third is entire team value, fourth is partial assessment
 * value, fifth is partial team value.
 *
 * @param <T> metric result type
 *
 * @author Elisha Peterson
 */
@SuppressWarnings("UnstableApiUsage")
@Beta
class CooperationMetric<T extends Number> implements GraphSubsetMetric<double[]> {

    private final GraphSubsetMetric<T> baseM;

    /**
     * Construct with base subset metric.
     *
     * @param baseM base metric
     */
    public CooperationMetric(GraphSubsetMetric<T> baseM) {
        this.baseM = baseM;
    }

    @Override
    public String toString() {
        return "CooperationMetric[" + baseM + "]";
    }

    @Override
    public <N> double[] getValue(Graph<N> graph, Set<N> nodes) {
        int n = graph.nodes().size(), m = nodes.size();
        Set<N> all = graph.nodes();
        Set<N> complement = new HashSet<>(all);
        complement.removeAll(nodes);
        double outcomeAll = n == 0 ? 0.0 : baseM.getValue(graph, all).doubleValue();
        double outcomeAll2 = m == n ? 0.0 : baseM.getValue(graph, complement).doubleValue();
        double outcomePart = m == n ? 0.0 : baseM.getValue(Graphs.inducedSubgraph(graph, complement), complement).doubleValue();
        return new double[]{
            // selfish contribution
            outcomeAll - outcomeAll2,
            // altruistic contribution
            outcomeAll2 - outcomePart,
            // value of the outcome for the whole team
            outcomeAll,
            // value of the outcome for the whole team, perceived by complement
            outcomeAll2,
            // value of the outcome for the complement
            outcomePart};
    }

}
