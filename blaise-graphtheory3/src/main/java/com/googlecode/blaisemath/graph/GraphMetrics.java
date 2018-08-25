/**
 * GraphMetrics.java
 * Created Mar 26, 2015
 */
package com.googlecode.blaisemath.graph;

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


import com.google.common.base.Function;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for working with {@link GraphMetric}, {@link GraphNodeMetric},
 * and {@link GraphSubsetMetric} instances.
 * @author elisha
 */
public class GraphMetrics {
    
    // utility class
    private GraphMetrics() {
    }

    //
    // METHODS FOR COMPILING INFORMATION ABOUT METRICS IN A GRAPH (ALL NODES)
    //
    
    /**
     * Returns function computing metric value for each node in the given graph.
     * @param <N> node type
     * @param <T> value type
     * @param graph the graph
     * @param metric metric used to generate values
     * @return function computing values
     */
    public static <N,T> Function<N,T> asFunction(final Graph<N> graph, final GraphNodeMetric<T> metric) {
        return new Function<N,T>(){
            @Override
            public T apply(N node) {
                return metric.apply(graph, node);
            }
        };
    }
    
    /**
     * Returns metric values associated with ndoes in the graph.
     * @param <N> node type
     * @param <T> value type
     * @param graph the graph
     * @param metric metric used to generate values
     * @return distribution of values
     */
    public static <N,T> Map<N,T> computeValues(Graph<N> graph, GraphNodeMetric<T> metric) {
        return Maps.asMap(graph.nodes(), asFunction(graph, metric));
    }

    /**
     * Applies a metric that operates on connected graphs only to graphs with multiple
     * components, by weighting the result based on the component size.
     * @param <V> graph vertex type
     * @param graph graph
     * @param connectedGraphMetric a function that computes values for connected graphs
     * @return result
     */
    public static <V> Map<V, Double> applyToComponents(Graph<V> graph, Function<Graph<V>, Map<V, Double>> connectedGraphMetric) {
        if (graph.nodeCount() == 0) {
            return Collections.emptyMap();
        } else if (graph.nodeCount() == 1) {
            return Collections.singletonMap((V) graph.nodes().toArray()[0], 0.0);
        }

        int n = graph.nodeCount();
        Set<Graph<V>> components = GraphUtils.componentGraphs(graph);
        Map<V, Double> values = new HashMap<V, Double>();
        for (Graph<V> compt : components) {
            if (compt.nodeCount() == 1) {
                values.put(compt.nodes().iterator().next(), 0.0);
            } else {
                values.putAll(connectedGraphMetric.apply(compt));
            }
        }
        for (Graph<V> compt : components) {
            double multiplier = compt.nodeCount() / (double) n;
            for (V v : compt.nodes()) {
                values.put(v, multiplier * values.get(v));
            }
        }
        return values;
    }

    /**
     * Returns distribution of the values of a particular metric
     * @param <N> node type
     * @param <T> metric result type
     * @param graph the graph
     * @param metric metric used to generate values
     * @return distribution of values
     */
    public static <N,T> Multiset<T> computeDistribution(Graph<N> graph, GraphNodeMetric<T> metric) {
        return HashMultiset.create(Iterables.transform(graph.nodes(), asFunction(graph, metric)));
    }
    
    //
    // FACTORY METHODS
    //
    
    /**
     * Crete an additive subset metric based on the given node metric. The resulting metric
     * adds the computed values for each node in a subset together.
     * @param <N> node type
     * @param baseMetric base metric
     * @return additive metric
     */
    public static <N extends Number> GraphSubsetMetric<N> additiveSubsetMetric(GraphNodeMetric<N> baseMetric) {
        return new AdditiveSubsetMetric<N>(baseMetric);
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
        return new ContractiveSubsetMetric<N>(baseMetric);
    }
    
    //<editor-fold defaultstate="collapsed" desc="INNER CLASSES">
    
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
    
    //</editor-fold>
}
