package com.googlecode.blaisemath.graph.mod.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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
import com.googlecode.blaisemath.graph.GraphUtils;

import java.util.Set;

/**
 * Utility class for working with {@link GraphSubsetMetric} instances.
 *
 * @author elisha
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
    private static class AdditiveSubsetMetric<N extends Number> implements GraphSubsetMetric<N> {

        GraphNodeMetric<N> baseMetric;

        /**
         * Constructs with provided base metric.
         * @param baseMetric the metric to use for computations on individual nodes.
         */
        private AdditiveSubsetMetric(GraphNodeMetric<N> baseMetric) { 
            this.baseMetric = baseMetric;
        }

        @Override
        public <V> N getValue(Graph<V> graph, Set<V> vertices) {
            Double result = 0.0;
            Number val = null;
            for (V v : vertices) {
                val = baseMetric.apply(graph, v);
                result += val.doubleValue();
            }
            if (val instanceof Integer) {
                return (N) (Integer) result.intValue();
            }
            else if (val instanceof Double) {
                return (N) result;
            }
            else if (val instanceof Float) {
                return (N) (Float) result.floatValue();
            }
            return null;
        }
    }

    @Beta
    private static class ContractiveSubsetMetric<N> implements GraphSubsetMetric<N> {

        GraphNodeMetric<N> baseMetric;

        /**
         * Constructs with provided base metric.
         * @param baseMetric the metric to use for computations of contracted node
         */
        private ContractiveSubsetMetric(GraphNodeMetric<N> baseMetric) { 
            this.baseMetric = baseMetric; 
        }

        @Override
        public <V> N getValue(Graph<V> graph, Set<V> nodes) {
            V starNode = null;
            for (V v : nodes) {
                starNode = v;
                break;
            }
            Graph<V> contracted = GraphUtils.contractedGraph(graph, nodes, starNode);
            return baseMetric.apply(contracted, starNode);
        }

    }
    
    //endregion
}
