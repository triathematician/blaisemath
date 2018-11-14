package com.googlecode.blaisemath.graph;

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

import java.util.Arrays;
import java.util.List;
import static com.googlecode.blaisemath.test.AssertUtils.assertCollectionContentsSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.graph.Graph;
import org.junit.BeforeClass;
import org.junit.Test;

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
        UNDIRECTED_INSTANCE = GraphUtils.contractedGraph(GraphUtils.createFromArrayEdges(false, Arrays.asList(VV), Arrays.asList(EE)), SUB, CVX);
        DIRECTED_INSTANCE = GraphUtils.contractedGraph(GraphUtils.createFromArrayEdges(true, Arrays.asList(VV), Arrays.asList(EE)), SUB, CVX);
    }

    @Test
    public void testNodeCount() {
        System.out.println("order");
        assertEquals(7, UNDIRECTED_INSTANCE.nodes().size());
        assertEquals(7, DIRECTED_INSTANCE.nodes().size());
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
            assertTrue(UNDIRECTED_INSTANCE.nodes().contains(i));
            assertTrue(DIRECTED_INSTANCE.nodes().contains(i));
        }
        for (Integer i : SUB) {
            assertFalse(UNDIRECTED_INSTANCE.nodes().contains(i));
            assertFalse(DIRECTED_INSTANCE.nodes().contains(i));
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
        assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(0, 0));
        assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(0, 3));
        assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(4, 0));
        assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(10, 0));
        assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(0, 11));
        assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(10, 11));
        assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(20, 21));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(0, 0));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(0, 3));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(0, 4));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(4, 0));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(0, 10));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(10, 11));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(11, 0));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(0, 11));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(20, 21));
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
        assertEquals(0, UNDIRECTED_INSTANCE.degree(1));
        assertEquals(0, DIRECTED_INSTANCE.degree(1));
        int[] nodes = {0, 3, 4, 10, 11, 20, 21};
        Object[][] nbrs1 = new Object[][] { {0,3,4,10,11}, {0}, {0}, {0,11}, {0,10}, {21}, {20} };
        for (int i = 0; i < SUB.size(); i++) {
            assertCollectionContentsSame(Arrays.asList(nbrs1[i]), UNDIRECTED_INSTANCE.adjacentNodes(nodes[i]));
            assertCollectionContentsSame(Arrays.asList(nbrs1[i]), DIRECTED_INSTANCE.adjacentNodes(nodes[i]));
        }
    }

    @Test
    public void testEdgeCount() {
        System.out.println("edgeCount");
        assertEquals(7, UNDIRECTED_INSTANCE.edges().size());
        assertEquals(7, DIRECTED_INSTANCE.edges().size());
    }

}
