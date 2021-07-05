package com.googlecode.blaisemath.graph;

/*
 * #%L
 * blaise-graphtheory3
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


import com.google.common.graph.Graph;
import java.util.DoubleSummaryStatistics;

/**
 * Collects values of a metric on nodes together with summary statistics.
 * @author Elisha Peterson
 */
public class GraphNodeStats {

    /** The values on nodes. */
    private final Iterable values;
    /** Summary statistics of the values. */
    private final DoubleSummaryStatistics stats;

    /**
     * Construct the node stats object.
     * @param values the node values
     */
    public GraphNodeStats(Iterable<? extends Number> values) {
        this.values = values;
        this.stats = new DoubleSummaryStatistics();
        for (Object v : values) {
            stats.accept(((Number) v).doubleValue());
        }
    }

    /**
     * Construct the node stats object given a graph and a node metric.
     * @param <N> graph node type
     * @param <T> metric value type
     * @param graph the graph
     * @param metric a node metric
     */
    public <N, T extends Number> GraphNodeStats(Graph<N> graph, GraphNodeMetric<T> metric) {
        this(GraphMetrics.distribution(graph, metric));
    }

    /**
     * Values on the nodes
     * @return values
     */
    public Iterable values() {
        return values;
    }

    /**
     * Summary statistics
     * @return stats for metric values
     */
    public DoubleSummaryStatistics statistics() {
        return stats;
    }

}
