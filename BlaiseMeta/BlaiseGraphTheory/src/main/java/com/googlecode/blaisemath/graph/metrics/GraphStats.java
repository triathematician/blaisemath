/*
 * GraphStats.java
 * Created Oct 29, 2011
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import com.googlecode.blaisemath.graph.Graph;

/**
 * <p>
 *   Caches computations of metrics on a graph.
 *   Both {@link GraphNodeMetric}s and {@link GraphMetric}s are captured here. In the
 *   case of {@code GraphNodeMetric}s, the node values are computed along with a
 *   {@link SummaryStatistics} object that describes their values.
 * </p>
 * 
 * @author Elisha Peterson
 */
public class GraphStats {
    
    /** The base graph */
    private final Graph graph;
    /** The node metrics that have been computed. */
    private final Map<GraphNodeMetric, NodeStats> nodeStats = new HashMap<GraphNodeMetric, NodeStats>();
    /** The global metrics that have been computed. */
    private final Map<GraphMetric, Object> globalStats = new HashMap<GraphMetric, Object>();
    
    /** 
     * Construct graph stats object 
     * @param graph graph for computations
     */
    public GraphStats(Graph graph) {
        this.graph = graph;
    }
    
    /**
     * The graph object.
     */
    public Graph getGraph() {
        return graph;
    }
    
    /**
     * Returns whether stats have been computed for specified metric.
     * @param metric the metric
     * @return true if these stats have been computed
     */
    public boolean containsNodeStats(GraphNodeMetric metric) {
        return nodeStats.containsKey(metric);
    }
    
    /**
     * Retrieve stats associated with a ndoe metric. If there are none, the stats
     * will be computed (which may take a while) and the results cached.
     * 
     * @param metric the metric
     * @return associated stats
     */
    public NodeStats getNodeStats(GraphNodeMetric metric) {
        if (!nodeStats.containsKey(metric)) {
            nodeStats.put(metric, new NodeStats(graph, metric));
        }
        return nodeStats.get(metric);
    }
    /**
     * Returns whether stats have been computed for specified metric.
     * @param metric the metric
     * @return true if these stats have been computed
     */
    public boolean containsGlobalStats(GraphMetric metric) {
        return globalStats.containsKey(metric);
    }
    
    /**
     * Retrieve stats associated with a ndoe metric. If there are none, the stats
     * will be computed (which may take a while) and the results cached.
     * 
     * @param metric the metric
     * @return associated stats
     */
    public Object getGlobalStats(GraphMetric metric) {
        if (!globalStats.containsKey(metric)) {
            globalStats.put(metric, metric.value(graph));
        }
        return globalStats.get(metric);
    }
    
    
    
    
    //
    // INNER CLASSES
    //
    
    /** 
     * Collects values of a metric on nodes together with summary statistics.
     */
    public static class NodeStats {
        /** The values on nodes. */
        private final Collection values;
        /** Summary statistics of the values. */
        private final SummaryStatistics stats;

        /** 
         * Construct the node stats object.
         * @param values the node values
         */
        public NodeStats(Collection<? extends Number> values) {
            this.values = values;
            this.stats = new SummaryStatistics();
            for (Object v : values) {
                if (v instanceof Number) {
                    stats.addValue(((Number)v).doubleValue());
                }
            }
        }
        
        /** 
         * Construct the node stats object given a graph and a node metric.
         * @param graph the graph
         * @param metric a node metric
         */
        public NodeStats(Graph graph, GraphNodeMetric<? extends Number> metric) {
            this(GraphMetrics.computeValues(graph, metric));
        }
        
        /** Values on the nodes */
        public Collection getValues() {
            return values;
        }
        
        /** Summary statistics */
        public SummaryStatistics getStatistics() {
            return stats;
        }
    } // INNER CLASS NodeStats
    
}
