package com.googlecode.blaisemath.graph;

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
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.googlecode.blaisemath.test.AssertUtils.assertCollectionContentsSame;
import static com.googlecode.blaisemath.test.AssertUtils.assertIllegalArgumentException;
import static org.junit.Assert.*;

@SuppressWarnings("UnstableApiUsage")
public class ContractedGraphTest {

    private static final Integer NODE = 0;
    private static final Integer[] NODES = new Integer[] { 1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21 };
    private static final Integer[][] EDGES = new Integer[][] { {1,2}, {2,1}, {2,3}, {2,4}, {2,5}, {1,6}, {6,6}, {6,10}, {10,11}, {11,1}, {15, 15}, {20, 21} };
    private static final List<Integer> NODES_TO_REPLACE = Arrays.asList(1, 2, 5, 6, 15);
    private static final Graph<Integer> UNDIRECTED_INSTANCE = GraphUtils.contractedGraph(GraphUtils.createFromArrayEdges(false, Arrays.asList(NODES), Arrays.asList(EDGES)), NODES_TO_REPLACE, NODE);
    private static final Graph<Integer> DIRECTED_INSTANCE = GraphUtils.contractedGraph(GraphUtils.createFromArrayEdges(true, Arrays.asList(NODES), Arrays.asList(EDGES)), NODES_TO_REPLACE, NODE);

    @Test
    public void testNodeCount() {
        assertEquals(7, UNDIRECTED_INSTANCE.nodes().size());
        assertEquals(7, DIRECTED_INSTANCE.nodes().size());
    }

    @Test
    public void testNodes() {
        List<Integer> expected = Arrays.asList(0, 3, 4, 10, 11, 20, 21);
        assertCollectionContentsSame(expected, UNDIRECTED_INSTANCE.nodes());
        assertCollectionContentsSame(expected, DIRECTED_INSTANCE.nodes());
    }

    @Test
    public void testContains() {
        List<Integer> expected = Arrays.asList(0, 3, 4, 10, 11, 20, 21);
        for (Integer i : expected) {
            assertTrue(UNDIRECTED_INSTANCE.nodes().contains(i));
            assertTrue(DIRECTED_INSTANCE.nodes().contains(i));
        }
        for (Integer i : NODES_TO_REPLACE) {
            assertFalse(UNDIRECTED_INSTANCE.nodes().contains(i));
            assertFalse(DIRECTED_INSTANCE.nodes().contains(i));
        }
    }

    @Test
    public void testIsDirected() {
        assertTrue(DIRECTED_INSTANCE.isDirected());
        assertFalse(UNDIRECTED_INSTANCE.isDirected());
    }

    @Test
    public void testHasEdgeConnecting() {
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
        assertFalse(DIRECTED_INSTANCE.hasEdgeConnecting(4, 0));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(0, 10));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(10, 11));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(11, 0));
        assertFalse(DIRECTED_INSTANCE.hasEdgeConnecting(0, 11));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(20, 21));
    }

    @Test
    public void testDegree() {
        int[] nodes = {0, 3, 4, 10, 11, 20, 21};
        int[] expectedDegrees1 = {6, 1, 1, 2, 2, 1, 1};
        int[] expectedDegrees2 = {6, 1, 1, 2, 2, 1, 1};
        int[] expectedOut      = {4, 0, 0, 1, 1, 1, 0};
        int[] expectedIn       = {2, 1, 1, 1, 1, 0, 1};
        for (int i = 0; i < NODES_TO_REPLACE.size(); i++) {
            assertEquals(expectedDegrees1[i], UNDIRECTED_INSTANCE.degree(nodes[i]));
            assertEquals(expectedDegrees2[i], DIRECTED_INSTANCE.degree(nodes[i]));
            assertEquals(expectedOut[i], DIRECTED_INSTANCE.outDegree(nodes[i]));
            assertEquals(expectedIn[i], DIRECTED_INSTANCE.inDegree(nodes[i]));
        }
    }

    @Test
    public void testNeighbors() {
        assertIllegalArgumentException(() -> UNDIRECTED_INSTANCE.degree(1));
        assertIllegalArgumentException(() -> DIRECTED_INSTANCE.degree(1));
        int[] nodes = {0, 3, 4, 10, 11, 20, 21};
        Integer[][] neighbors = new Integer[][] {{0, 3, 4, 10, 11}, {0}, {0}, {0, 11}, {0, 10}, {21}, {20}};
        for (int i = 0; i < NODES_TO_REPLACE.size(); i++) {
            assertCollectionContentsSame(Arrays.asList(neighbors[i]), UNDIRECTED_INSTANCE.adjacentNodes(nodes[i]));
            assertCollectionContentsSame(Arrays.asList(neighbors[i]), DIRECTED_INSTANCE.adjacentNodes(nodes[i]));
        }
    }

    @Test
    public void testEdgeCount() {
        assertEquals(7, UNDIRECTED_INSTANCE.edges().size());
        assertEquals(7, DIRECTED_INSTANCE.edges().size());
    }

}
