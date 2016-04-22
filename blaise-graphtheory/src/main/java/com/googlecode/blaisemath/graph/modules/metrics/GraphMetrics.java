/*
 * GraphMetrics.java
 * Created May 12, 2010
 */

package com.googlecode.blaisemath.graph.modules.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.googlecode.blaisemath.graph.GraphNodeMetric;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.googlecode.blaisemath.graph.Graph;

/**
 * Utility library for compiling information about metrics in a graph.
 * 
 * @author Elisha Peterson
 */
public class GraphMetrics {

    //
    // METHODS FOR COMPILING INFORMATION ABOUT METRICS IN A GRAPH (ALL NODES)
    //
    
    public static <N> List<N> computeValues(Graph graph, GraphNodeMetric<N> metric) {
        List<N> result = new ArrayList<N>();
        for (Object node : graph.nodes()) {
            result.add((N) metric.apply(graph, node));
        }
        return result;
    }

    /**
     * Returns computeDistribution of the values of a particular metric
     * @param <N> metric result type
     * @param graph the graph
     * @param metric metric used to generate values
     * @return distribution of values
     */
    public static <N> Multiset<N> computeDistribution(Graph graph, GraphNodeMetric<N> metric) {
        return HashMultiset.create(computeValues(graph, metric));
    }
}
