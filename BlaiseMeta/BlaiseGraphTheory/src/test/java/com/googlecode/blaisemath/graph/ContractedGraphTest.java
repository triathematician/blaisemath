/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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
import com.googlecode.blaisemath.graph.SparseGraph;
import com.googlecode.blaisemath.graph.Graph;
import java.util.Arrays;
import java.util.List;
import static com.googlecode.blaisemath.graph.AssertUtils.assertCollectionContentsSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class ContractedGraphTest {

    static Integer CVX;
    static Integer[] VV;
    static Integer[][] EE;
    static List<Integer> SUB;
    static Graph<Integer> UNDIRECTED_INSTANCE, DIRECTED_INSTANCE;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- ContractedGraphTest --");
        CVX = 0;
        VV = new Integer[] { 1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21 };
        SUB = Arrays.asList(1, 2, 5, 6, 15);
        EE = new Integer[][] {
            {1,2}, {2,1}, {2,3}, {2,4}, {2,5}, {1,6}, {6,6}, {6,10}, {10,11}, {11,1}, {15, 15}, {20, 21}
        };
        UNDIRECTED_INSTANCE = GraphUtils.contractedGraph(new SparseGraph(false, Arrays.asList(VV), Arrays.asList(EE)), SUB, CVX);
        DIRECTED_INSTANCE = GraphUtils.contractedGraph(new SparseGraph(true, Arrays.asList(VV), Arrays.asList(EE)), SUB, CVX);
    }

    @Test
    public void testNodeCount() {
        System.out.println("order");
        assertEquals(7, UNDIRECTED_INSTANCE.nodeCount());
        assertEquals(7, DIRECTED_INSTANCE.nodeCount());
    }

    @Test
    public void testNodes() {
        System.out.println("nodes");
        List expected = Arrays.asList(0, 3, 4, 10, 11, 20, 21);
        assertCollectionContentsSame(expected, UNDIRECTED_INSTANCE.nodes());
        assertCollectionContentsSame(expected, DIRECTED_INSTANCE.nodes());
    }

    @Test
    public void testContains() {
        System.out.println("contains");
        List<Integer> expected = Arrays.asList(0, 3, 4, 10, 11, 20, 21);
        for (Integer i : expected) {
            assertTrue(UNDIRECTED_INSTANCE.contains(i));
            assertTrue(DIRECTED_INSTANCE.contains(i));
        }
        for (Integer i : SUB) {
            assertFalse(UNDIRECTED_INSTANCE.contains(i));
            assertFalse(DIRECTED_INSTANCE.contains(i));
        }
    }

    @Test
    public void testIsDirected() {
        System.out.println("isDirected");
        assertTrue(DIRECTED_INSTANCE.isDirected());
        assertFalse(UNDIRECTED_INSTANCE.isDirected());
    }

    @Test
    public void testAdjacent() {
        System.out.println("adjacent");
        assertTrue(UNDIRECTED_INSTANCE.adjacent(0, 0));
        assertTrue(UNDIRECTED_INSTANCE.adjacent(0, 3));
        assertTrue(UNDIRECTED_INSTANCE.adjacent(4, 0));
        assertTrue(UNDIRECTED_INSTANCE.adjacent(10, 0));
        assertTrue(UNDIRECTED_INSTANCE.adjacent(0, 11));
        assertTrue(UNDIRECTED_INSTANCE.adjacent(10, 11));
        assertTrue(UNDIRECTED_INSTANCE.adjacent(20, 21));
        assertTrue(DIRECTED_INSTANCE.adjacent(0, 0));
        assertTrue(DIRECTED_INSTANCE.adjacent(0, 3));
        assertTrue(DIRECTED_INSTANCE.adjacent(0, 4));
        assertTrue(DIRECTED_INSTANCE.adjacent(4, 0));
        assertTrue(DIRECTED_INSTANCE.adjacent(0, 10));
        assertTrue(DIRECTED_INSTANCE.adjacent(10, 11));
        assertTrue(DIRECTED_INSTANCE.adjacent(11, 0));
        assertTrue(DIRECTED_INSTANCE.adjacent(0, 11));
        assertTrue(DIRECTED_INSTANCE.adjacent(20, 21));
    }

    @Test
    public void testDegree() {
        System.out.println("degree");
        int[] nodes = {0, 3, 4, 10, 11, 20, 21};
        int[] expectedDegrees1 = {6, 1, 1, 2, 2, 1, 1};
        int[] expectedDegrees2 = {6, 1, 1, 2, 2, 1, 1};
        int[] expectedOut      = {4, 0, 0, 1, 1, 1, 0};
        int[] expectedIn       = {2, 1, 1, 1, 1, 0, 1};
        for (int i = 0; i < SUB.size(); i++) {
            assertEquals(expectedDegrees1[i], UNDIRECTED_INSTANCE.degree(nodes[i]));
            assertEquals(expectedDegrees2[i], DIRECTED_INSTANCE.degree(nodes[i]));
            assertEquals(expectedOut[i], DIRECTED_INSTANCE.outDegree(nodes[i]));
            assertEquals(expectedIn[i], DIRECTED_INSTANCE.inDegree(nodes[i]));
        }
    }

    @Test
    public void testNeighbors() {
        System.out.println("neighbors");
        assertEquals(0, UNDIRECTED_INSTANCE.neighbors(1).size());
        assertEquals(0, DIRECTED_INSTANCE.neighbors(1).size());
        int[] nodes = {0, 3, 4, 10, 11, 20, 21};
        Object[][] nbrs1 = new Object[][] { {0,3,4,10,11}, {0}, {0}, {0,11}, {0,10}, {21}, {20} };
        for (int i = 0; i < SUB.size(); i++) {
            assertCollectionContentsSame(Arrays.asList(nbrs1[i]), UNDIRECTED_INSTANCE.neighbors(nodes[i]));
            assertCollectionContentsSame(Arrays.asList(nbrs1[i]), DIRECTED_INSTANCE.neighbors(nodes[i]));
        }
    }

    @Test
    public void testEdgeCount() {
        System.out.println("edgeCount");
        assertEquals(7, UNDIRECTED_INSTANCE.edgeCount());
        assertEquals(7, DIRECTED_INSTANCE.edgeCount());
    }

}