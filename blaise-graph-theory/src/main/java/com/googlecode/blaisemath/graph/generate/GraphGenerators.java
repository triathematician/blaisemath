package com.googlecode.blaisemath.graph.generate;

/*
 * #%L
 * blaise-graphtheory3
 * --
 * Copyright (C) 2009 - 2022 Elisha Peterson
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

import java.util.AbstractList;
import java.util.List;

class GraphGenerators {

    /** Utility class */
    private GraphGenerators() {}

    /**
     * Generate graph with given set of edges.
     * @param parameters the parameters
     * @param edges edges
     * @return created graph
     */
    static Graph<Integer> createGraphWithEdges(DefaultGeneratorParameters parameters, Iterable<Integer[]> edges) {
        return GraphUtils.createFromArrayEdges(parameters.isDirected(), intList(0, parameters.getNodeCount()), edges);
    }

    /**
     * Returns abstract list of integers min, ..., max-1
     * @param min list min
     * @param max list max
     * @return list
     */
    @SuppressWarnings("SameParameterValue")
    static List<Integer> intList(final int min, final int max) {
        return new AbstractList<Integer>() {
            @Override
            public Integer get(int index) {
                return min + index;
            }
            @Override
            public int size() {
                return max - min;
            }
        };
    }

}
