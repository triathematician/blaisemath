package com.googlecode.blaisemath.graph.generate;

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

import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("UnstableApiUsage")
public class EdgeCountGeneratorTest {

    @Test
    public void testEdgeCountBuilder() {
        Graph<Integer> result1 = new EdgeCountGenerator().apply(new ExtendedGeneratorParameters(false, 10, 0));
        assertEquals(10, result1.nodes().size()); assertEquals(0, result1.edges().size());
        result1 = new EdgeCountGenerator().apply(new ExtendedGeneratorParameters(false, 10, 30));
        assertEquals(10, result1.nodes().size()); assertEquals(30, result1.edges().size());
        System.out.println("  UNDIRECTED: " + GraphUtils.printGraph(result1));
        result1 = new EdgeCountGenerator().apply(new ExtendedGeneratorParameters(true, 10, 30));
        assertEquals(10, result1.nodes().size()); assertEquals(30, result1.edges().size());
        System.out.println("  DIRECTED: " + GraphUtils.printGraph(result1));
    }

}
