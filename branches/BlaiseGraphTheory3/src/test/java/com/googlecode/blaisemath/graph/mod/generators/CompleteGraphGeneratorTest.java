/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.graph.mod.generators;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class CompleteGraphGeneratorTest {

    @Test
    public void testGetEmptyGraphInstance() {
        System.out.println("getCompleteGraphInstance");
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1, 2, 3] 1: [0, 2, 3] 2: [0, 1, 3] 3: [0, 1, 2]", 
                GraphUtils.printGraph(new CompleteGraphGenerator().generate(new DefaultGeneratorParameters(false,4))));
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1, 2, 3] 1: [0, 2, 3] 2: [0, 1, 3] 3: [0, 1, 2]",
                GraphUtils.printGraph(new CompleteGraphGenerator().generate(new DefaultGeneratorParameters(true,4))));
        Graph result = new CompleteGraphGenerator().generate(new DefaultGeneratorParameters(false,6));
        Graph result2 = new CompleteGraphGenerator().generate(new DefaultGeneratorParameters(true,6));
        assertEquals(6, result.nodeCount());
        assertEquals(6, result2.nodeCount());
        assertEquals(15, result.edgeCount());
        assertEquals(30, result2.edgeCount());
        for (int i = 0; i < 6; i++) {
            assertTrue(result.contains(i));
            assertTrue(result2.contains(i));
        }
    }

}
