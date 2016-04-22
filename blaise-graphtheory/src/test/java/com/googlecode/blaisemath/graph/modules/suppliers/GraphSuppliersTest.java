/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.graph.modules.suppliers;

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

import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.modules.suppliers.GraphSuppliers.CompleteGraphSupplier;
import com.googlecode.blaisemath.graph.modules.suppliers.GraphSuppliers.CycleGraphSupplier;
import com.googlecode.blaisemath.graph.modules.suppliers.GraphSuppliers.EmptyGraphSupplier;
import com.googlecode.blaisemath.graph.modules.suppliers.GraphSuppliers.StarGraphSupplier;
import com.googlecode.blaisemath.graph.modules.suppliers.GraphSuppliers.WheelGraphSupplier;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class GraphSuppliersTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- GraphBuildersTest --");
    }

    @Test
    public void testGetEmptyGraphInstance() {
        System.out.println("getEmptyGraphInstance");
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [] 1: [] 2: [] 3: []", GraphUtils.printGraph(new EmptyGraphSupplier(false,4).get()));
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [] 1: [] 2: [] 3: []", GraphUtils.printGraph(new EmptyGraphSupplier(true,4).get()));
        Graph result = new EmptyGraphSupplier(true,10).get();
        assertEquals(10, result.nodeCount());
        assertEquals(0, result.edgeCount());
        for (int i = 0; i < 10; i++)
            assertTrue(result.contains(i));
    }

    @Test
    public void testGetCompleteGraphInstance() {
        System.out.println("getCompleteGraphInstance");
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1, 2, 3] 1: [0, 2, 3] 2: [0, 1, 3] 3: [0, 1, 2]", GraphUtils.printGraph(new CompleteGraphSupplier(false,4).get()));
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1, 2, 3] 1: [0, 2, 3] 2: [0, 1, 3] 3: [0, 1, 2]", GraphUtils.printGraph(new CompleteGraphSupplier(true,4).get()));
        Graph result = new CompleteGraphSupplier(false,6).get();
        Graph result2 = new CompleteGraphSupplier(true,6).get();
        assertEquals(6, result.nodeCount());
        assertEquals(6, result2.nodeCount());
        assertEquals(15, result.edgeCount());
        assertEquals(30, result2.edgeCount());
        for (int i = 0; i < 6; i++) {
            assertTrue(result.contains(i));
            assertTrue(result2.contains(i));
        }
    }

    @Test
    public void testGetCycleGraphInstance() {
        System.out.println("getCycleGraphInstance");
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1, 3] 1: [0, 2] 2: [1, 3] 3: [0, 2]", GraphUtils.printGraph(new CycleGraphSupplier(false,4).get()));
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1] 1: [2] 2: [3] 3: [0]", GraphUtils.printGraph(new CycleGraphSupplier(true,4).get()));
    }

    @Test
    public void testGetStarGraphInstance() {
        System.out.println("getStarGraphInstance");
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1, 2, 3] 1: [0] 2: [0] 3: [0]", GraphUtils.printGraph(new StarGraphSupplier(false,4).get()));
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1, 2, 3] 1: [] 2: [] 3: []", GraphUtils.printGraph(new StarGraphSupplier(true,4).get()));
    }

    @Test
    public void testGetWheelGraphInstance() {
        System.out.println("getWheelGraphInstance");
        assertEquals("NODES: [0, 1, 2, 3, 4]  EDGES: 0: [1, 2, 3, 4] 1: [0, 2, 4] 2: [0, 1, 3] 3: [0, 2, 4] 4: [0, 1, 3]", GraphUtils.printGraph(new WheelGraphSupplier(false,5).get()));
        assertEquals("NODES: [0, 1, 2, 3, 4]  EDGES: 0: [1, 2, 3, 4] 1: [2, 4] 2: [1, 3] 3: [2, 4] 4: [1, 3]", GraphUtils.printGraph(new WheelGraphSupplier(true,5).get()));
    }
}
