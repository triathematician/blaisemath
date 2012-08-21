/*
 * GraphStats.java
 * Created Oct 29, 2011
 */
package org.bm.blaise.scio.graph.metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.bm.blaise.scio.graph.Graph;

/**
 * <p>
 *   Gathers computations of various sorts on a graph. Keeps track of computations
 *   that have already been done so they do not need to be recomputed a second time.
 *   Both {@link NodeMetric}s and {@link GlobalMetric}s are captured here. In the
 *   case of {@code NodeMetric}s, the node values are computed along with a
 *   {@link SummaryStatistics} object that describes their values.
 * </p>
 * 
 * @author Elisha Peterson
 */
public class GraphStats {
    
    /** The base graph */
    private final Graph graph;
    /** The node metrics that have been computed. */
    private final Map<NodeMetric, NodeStats> nodeStats = new HashMap<NodeMetric, NodeStats>();
    /** The global metrics that have been computed. */
    private final Map<GlobalMetric, Object> globalStats = new HashMap<GlobalMetric, Object>();
    
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
    public boolean containsNodeStats(NodeMetric metric) {
        return nodeStats.containsKey(metric);
    }
    
    /**
     * Retrieve stats associated with a ndoe metric. If there are none, the stats
     * will be computed (which may take a while) and the results cached.
     * 
     * @param metric the metric
     * @return associated stats
     */
    public NodeStats getNodeStats(NodeMetric metric) {
        if (!nodeStats.containsKey(metric))
            nodeStats.put(metric, new NodeStats(graph, metric));
        return nodeStats.get(metric);
    }
    /**
     * Returns whether stats have been computed for specified metric.
     * @param metric the metric
     * @return true if these stats have been computed
     */
    public boolean containsGlobalStats(GlobalMetric metric) {
        return globalStats.containsKey(metric);
    }
    
    /**
     * Retrieve stats associated with a ndoe metric. If there are none, the stats
     * will be computed (which may take a while) and the results cached.
     * 
     * @param metric the metric
     * @return associated stats
     */
    public Object getGlobalStats(GlobalMetric metric) {
        if (!globalStats.containsKey(metric))
            globalStats.put(metric, metric.value(graph));
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
        private final List values;
        /** Summary statistics of the values. */
        private final SummaryStatistics stats;

        /** 
         * Construct the node stats object.
         * @param values the node values
         */
        public NodeStats(List values) {
            this.values = values;
            this.stats = new SummaryStatistics();
            for (Object v : values)
                if (v instanceof Number)
                    stats.addValue(((Number)v).doubleValue());
        }
        
        /** 
         * Construct the node stats object given a graph and a node metric.
         * @param graph the graph
         * @param metric a node metric
         */
        public NodeStats(Graph graph, NodeMetric metric) {
            this(metric.allValues(graph));
        }
        
        /** Values on the nodes */
        public List getValues() {
            return values;
        }
        
        /** Summary statistics */
        public SummaryStatistics getStatistics() {
            return stats;
        }
    } // INNER CLASS NodeStats
    
}
