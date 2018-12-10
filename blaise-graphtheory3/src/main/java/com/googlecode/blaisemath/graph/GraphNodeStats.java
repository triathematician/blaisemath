package com.googlecode.blaisemath.graph;

import com.google.common.graph.Graph;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

/**
 * Collects values of a metric on nodes together with summary statistics.
 * @author Elisha Peterson
 */
public class GraphNodeStats {

    /** The values on nodes. */
    private final Iterable values;
    /** Summary statistics of the values. */
    private final SummaryStatistics stats;

    /**
     * Construct the node stats object.
     * @param values the node values
     */
    public GraphNodeStats(Iterable<? extends Number> values) {
        this.values = values;
        this.stats = new SummaryStatistics();
        for (Object v : values) {
            stats.addValue(((Number) v).doubleValue());
        }
    }

    /**
     * Construct the node stats object given a graph and a node metric.
     * @param <N> graph node type
     * @param <V> metric value type
     * @param graph the graph
     * @param metric a node metric
     */
    public <N,V extends Number> GraphNodeStats(Graph<N> graph, GraphNodeMetric<V> metric) {
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
    public SummaryStatistics statistics() {
        return stats;
    }

}
