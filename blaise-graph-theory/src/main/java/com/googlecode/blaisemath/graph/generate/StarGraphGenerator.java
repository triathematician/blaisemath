package com.googlecode.blaisemath.graph.generate;

/*
 * #%L
 * BlaiseGraphTheory
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

/**
 * Constructs star graph with n nodes; all nodes are connected to a central hub.
 * @author Elisha Peterson
 */
public final class StarGraphGenerator extends AbstractGraphGenerator {

    public StarGraphGenerator() {
        super("Star Graph");
    }

    @Override
    public Graph<Integer> apply(DefaultGeneratorParameters parameters) {
        final int nodes = parameters.getNodeCount();
        if (nodes == 0) {
            return GraphUtils.emptyGraph(parameters.isDirected());
        }
        return GraphGenerators.createGraphWithEdges(parameters, new AbstractList<Integer[]>() {
            @Override
            public Integer[] get(int index) {
                return new Integer[]{0, index + 1};
            }

            @Override
            public int size() {
                return nodes - 1;
            }
        });
    }
    
}
