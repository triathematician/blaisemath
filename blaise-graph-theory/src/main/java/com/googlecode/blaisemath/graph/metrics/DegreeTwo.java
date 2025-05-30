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

import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;

/**
 * Computes the second-order degree of a node in a graph, i.e. how many nodes are within two hops.
 * Does not include the node itself.
 *
 * @author Elisha Peterson
 */
public class DegreeTwo extends AbstractGraphNodeMetric<Integer> {
    
    public DegreeTwo() {
        super("Neighborhood size (radius 2)");
    }
    
    @Override
    public <N> Integer apply(Graph<N> graph, N node) {
        return GraphUtils.neighborhood(graph, node, 2).size() - 1;
    }
    
}
