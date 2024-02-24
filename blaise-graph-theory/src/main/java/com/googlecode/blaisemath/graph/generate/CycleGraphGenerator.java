package com.googlecode.blaisemath.graph.generate;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2024 Elisha Peterson
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

import java.util.AbstractList;

/** 
 * Constructs cycle graph with n nodes.
 *
 * @author Elisha Peterson
 */
public final class CycleGraphGenerator extends AbstractGraphGenerator {
    
    public CycleGraphGenerator() {
        super("Cycle Graph");
    }
    
    @Override
    public Graph<Integer> apply(DefaultGeneratorParameters p) {
        final int nodeCount = p.getNodeCount();
        return GraphGenerators.createGraphWithEdges(p,
            new AbstractList<Integer[]>() {
                @Override
                public Integer[] get(int index) {
                    return new Integer[]{index, (index + 1) % nodeCount};
                }
                @Override
                public int size() {
                    return nodeCount;
                }
            });
    }
    
}
