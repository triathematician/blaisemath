package com.googlecode.blaisemath.graph;

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
import com.google.common.graph.Graphs;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.googlecode.blaisemath.test.AssertUtils.assertCollectionContentsSame;
import static com.googlecode.blaisemath.test.AssertUtils.assertIllegalArgumentException;
import static org.junit.Assert.*;

@SuppressWarnings("UnstableApiUsage")
public class SubgraphTest {

    private static final Integer[] VV = new Integer[] { 1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21 };
    private static final Integer[][] EE = new Integer[][] { {1,2}, {2,1}, {2,3}, {2,4}, {2,5}, {1,6}, {6,6}, {6,10}, {10,11}, {11,1}, {15, 15}, {20, 21} };
    private static final Set<Integer> SUB = new HashSet<>(Arrays.asList(1, 2, 5, 6, 15));
    private static final Graph<Integer> UNDIRECTED_INSTANCE = Graphs.inducedSubgraph(GraphUtils.createFromArrayEdges(false, Arrays.asList(VV), Arrays.asList(EE)), SUB);
    private static final Graph<Integer> DIRECTED_INSTANCE = Graphs.inducedSubgraph(GraphUtils.createFromArrayEdges(true, Arrays.asList(VV), Arrays.asList(EE)), SUB);

    @Test
    public void testToString() {
        assertEquals("NODES: [1, 2, 5, 6, 15]  EDGES: 1: [2, 6] 2: [1, 5] 5: [2] 6: [1, 6] 15: [15]", GraphUtils.printGraph(UNDIRECTED_INSTANCE));
        assertEquals("NODES: [1, 2, 5, 6, 15]  EDGES: 1: [2, 6] 2: [1, 5] 5: [] 6: [6] 15: [15]", GraphUtils.printGraph(DIRECTED_INSTANCE));
    }

    @Test
    public void testNodes() {
        assertCollectionContentsSame(SUB, UNDIRECTED_INSTANCE.nodes());
        assertCollectionContentsSame(SUB, DIRECTED_INSTANCE.nodes());
    }

    @Test
    public void testContains() {
        for (Integer i : SUB) {
            assertTrue(UNDIRECTED_INSTANCE.nodes().contains(i));
            assertTrue(DIRECTED_INSTANCE.nodes().contains(i));
        }
        assertFalse(UNDIRECTED_INSTANCE.nodes().contains(0));
        assertFalse(DIRECTED_INSTANCE.nodes().contains(0));
        assertFalse(UNDIRECTED_INSTANCE.nodes().contains(3));
        assertFalse(DIRECTED_INSTANCE.nodes().contains(3));
    }

    @Test
    public void testIsDirected() {
        assertTrue(DIRECTED_INSTANCE.isDirected());
        assertFalse(UNDIRECTED_INSTANCE.isDirected());
    }

    @Test
    public void testHasEdgeConnecting() {
        assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(1, 2));
        assertFalse(UNDIRECTED_INSTANCE.hasEdgeConnecting(1, 5));
        assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(2, 5));
        assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(5, 2));
        assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(6, 6));
        assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(15, 15));
        assertFalse(UNDIRECTED_INSTANCE.hasEdgeConnecting(6, 10));
        assertFalse(UNDIRECTED_INSTANCE.hasEdgeConnecting(0, 2));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(1, 2));
        assertFalse(DIRECTED_INSTANCE.hasEdgeConnecting(1, 5));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(2, 5));
        assertFalse(DIRECTED_INSTANCE.hasEdgeConnecting(5, 2));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(6, 6));
        assertFalse(UNDIRECTED_INSTANCE.hasEdgeConnecting(6, 10));
        assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(15, 15));
    }

    @Test
    public void testDegree() {
        int[] nodes = {1, 2, 5, 6, 15};
        int[] expectedDegrees1 = {2, 2, 1, 3, 2};
        int[] expectedDegrees2 = {3, 3, 1, 3, 2};
        for (int i = 0; i < SUB.size(); i++) {
            assertEquals(expectedDegrees1[i], UNDIRECTED_INSTANCE.degree(nodes[i]));
            assertEquals(expectedDegrees2[i], DIRECTED_INSTANCE.degree(nodes[i]));
        }
    }

    @Test
    public void testAdjacentNodes() {
        assertIllegalArgumentException(() -> UNDIRECTED_INSTANCE.adjacentNodes(0).size());
        assertIllegalArgumentException(() -> DIRECTED_INSTANCE.adjacentNodes(0).size());
        int[] nodes = {1, 2, 5, 6, 15};
        Integer[][] result = new Integer[][] { {2,6}, {1,5}, {2}, {1,6}, {15} };
        for (int i = 0; i < SUB.size(); i++) {
            assertCollectionContentsSame(Arrays.asList(result[i]), UNDIRECTED_INSTANCE.adjacentNodes(nodes[i]));
            assertCollectionContentsSame(Arrays.asList(result[i]), DIRECTED_INSTANCE.adjacentNodes(nodes[i]));
        }
    }

    @Test
    public void testEdges() {
        assertEquals(5, UNDIRECTED_INSTANCE.edges().size());
        assertEquals(6, DIRECTED_INSTANCE.edges().size());
    }

}
