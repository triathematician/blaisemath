package com.googlecode.blaisemath.graph.generate;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EdgeLikelihoodGeneratorTest {

    @Test
    public void testEdgeLikelihoodGenerator() {
        Graph<Integer> result1 = new EdgeLikelihoodGenerator().apply(new EdgeLikelihoodGenerator.EdgeLikelihoodParameters(false, 10, 0f));
        assertEquals(10, result1.nodes().size()); assertEquals(0, result1.edges().size());
        result1 = new EdgeLikelihoodGenerator().apply(new EdgeLikelihoodGenerator.EdgeLikelihoodParameters(true, 10, 1f));
        assertEquals(10, result1.nodes().size()); assertEquals(100, result1.edges().size());
        result1 = new EdgeLikelihoodGenerator().apply(new EdgeLikelihoodGenerator.EdgeLikelihoodParameters(false, 10, 1f));
        assertEquals(10, result1.nodes().size()); assertEquals(45, result1.edges().size());
        result1 = new EdgeLikelihoodGenerator().apply(new EdgeLikelihoodGenerator.EdgeLikelihoodParameters(false, 10, .25f));
        System.out.println("  UNDIRECTED (.25 probability): " + result1.edges().size() + " edges, " + GraphUtils.printGraph(result1));
        result1 = new EdgeLikelihoodGenerator().apply(new EdgeLikelihoodGenerator.EdgeLikelihoodParameters(true, 10, .25f));
        System.out.println("  DIRECTED (.25 probability): " + result1.edges().size() + " edges, " + GraphUtils.printGraph(result1));
    }

}
