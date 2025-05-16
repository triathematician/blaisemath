package com.googlecode.blaisemath.graph.generate;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Constructs complete graph with n nodes.
 *
 * @author Elisha Peterson
 */
public final class CompleteGraphGenerator extends AbstractGraphGenerator {
    
    public CompleteGraphGenerator() {
        super("Complete Graph");
    }

    @Override
    public Graph<Integer> apply(DefaultGeneratorParameters p) {
        List<Integer[]> edges = new ArrayList<>();
        for (int i = 0; i < p.getNodeCount(); i++) {
            for (int j = i + 1; j < p.getNodeCount(); j++) {
                edges.add(new Integer[]{i, j});
                if (p.isDirected()) {
                    edges.add(new Integer[]{j, i});
                }
            }
        }
        return GraphGenerators.createGraphWithEdges(p, edges);
    }
    
}
