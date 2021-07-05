package com.googlecode.blaisemath.graph.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2021 Elisha Peterson
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
import com.google.common.collect.Iterables;
import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphNodeMetric;
import com.googlecode.blaisemath.graph.GraphSubsetMetric;
import com.googlecode.blaisemath.graph.GraphUtils;

import java.util.Set;

/**
 * Utility class for working with {@link GraphSubsetMetric} instances.
 *
 * @author Elisha Peterson
 */
@Beta
public class SubsetMetrics {

    // utility class
    private SubsetMetrics() {
    }
    
    /**
     * Crete an additive subset metric based on the given node metric. The resulting metric
     * adds the computed values for each node in a subset together.
     * @param <N> node type
     * @param baseMetric base metric
     * @return additive metric
     */
    public static <N extends Number> GraphSubsetMetric<N> additiveSubsetMetric(GraphNodeMetric<N> baseMetric) {
        return new AdditiveSubsetMetric<>(baseMetric);
    }
    
    /**
     * Create a contractive subset metric based on the given node metric. The resulting metric
     * computes a subset metric by first contracting the subset to a point, and then
     * using the base metric in that new graph.
     * @param <N> node type
     * @param baseMetric base metric
     * @return contractive metric
     */
    public static <N> GraphSubsetMetric<N> contractiveSubsetMetric(GraphNodeMetric<N> baseMetric) {
        return new ContractiveSubsetMetric<>(baseMetric);
    }
    
    //region INNER CLASSES

    @Beta
    private static class AdditiveSubsetMetric<T extends Number> implements GraphSubsetMetric<T> {

        final GraphNodeMetric<T> baseMetric;

        /**
         * Constructs with provided base metric.
         * @param baseMetric the metric to use for computations on individual nodes.
         */
        private AdditiveSubsetMetric(GraphNodeMetric<T> baseMetric) {
            this.baseMetric = baseMetric;
        }

        @Override
        public <N> T getValue(Graph<N> graph, Set<N> nodes) {
            Double result = 0.0;
            Number val = null;
            for (N n : nodes) {
                val = baseMetric.apply(graph, n);
                result += val.doubleValue();
            }
            if (val instanceof Integer) {
                return (T) (Integer) result.intValue();
            }
            else if (val instanceof Double) {
                return (T) result;
            }
            else if (val instanceof Float) {
                return (T) (Float) result.floatValue();
            }
            return null;
        }
    }

    @Beta
    private static class ContractiveSubsetMetric<T> implements GraphSubsetMetric<T> {

        final GraphNodeMetric<T> baseMetric;

        /**
         * Constructs with provided base metric.
         * @param baseMetric the metric to use for computations of contracted node
         */
        private ContractiveSubsetMetric(GraphNodeMetric<T> baseMetric) {
            this.baseMetric = baseMetric; 
        }

        @Override
        public <N> T getValue(Graph<N> graph, Set<N> nodes) {
            N starNode = Iterables.getFirst(nodes, null);
            Graph<N> contracted = GraphUtils.contractedGraph(graph, nodes, starNode);
            return baseMetric.apply(contracted, starNode);
        }

    }
    
    //endregion
}
