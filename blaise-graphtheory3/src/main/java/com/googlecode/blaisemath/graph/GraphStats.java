package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import com.google.common.collect.Maps;
import com.google.common.graph.Graph;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import java.util.Map;

/**
 * Caches computations of metrics on a graph. Both {@link GraphNodeMetric}s and {@link GraphMetric}s are captured here.
 * In the case of {@code GraphNodeMetric}s, the node values are computed along with a {@link SummaryStatistics} object
 * that describes their values.
 *
 * @author Elisha Peterson
 */
public class GraphStats {

    /** The base graph. */
    private final Graph graph;
    /** The node metrics that have been computed. */
    private final Map<GraphNodeMetric, GraphNodeStats> nodeStats = Maps.newHashMap();
    /** The global metrics that have been computed. */
    private final Map<GraphMetric, Object> globalStats = Maps.newHashMap();

    /**
     * Construct graph stats object.
     * @param graph graph for computations
     */
    public GraphStats(Graph graph) {
        this.graph = graph;
    }

    /**
     * The graph object.
     * @return graph
     */
    public Graph graph() {
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
     * Retrieve stats associated with a node metric. If there are none, the stats will be computed (which may take a
     * while) and the results cached.
     *
     * @param metric the metric
     * @return associated stats
     */
    public GraphNodeStats nodeStatsOf(GraphNodeMetric metric) {
        if (!nodeStats.containsKey(metric)) {
            nodeStats.put(metric, new GraphNodeStats(graph, metric));
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
     * Retrieve stats associated with a node metric. If there are none, the stats will be computed (which may take a
     * while) and the results cached.
     *
     * @param metric the metric
     * @return associated stats
     */
    public Object globalStatsOf(GraphMetric<?> metric) {
        if (!globalStats.containsKey(metric)) {
            globalStats.put(metric, metric.apply(graph));
        }
        return globalStats.get(metric);
    }

    //region INNER CLASSES

    //endregion

}
