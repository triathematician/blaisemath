/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.graph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static org.blaise.graph.AssertUtils.assertCollectionContentsSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class SubgraphTest {

    public SubgraphTest() {
    }

    static Integer[] VV;
    static Integer[][] EE;
    static Set<Integer> SUB;
    static Graph<Integer> UNDIRECTED_INSTANCE, DIRECTED_INSTANCE;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- SubgraphTest --");
        VV = new Integer[] { 1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21 };
        SUB = new HashSet(Arrays.asList(1, 2, 5, 6, 15));
        EE = new Integer[][] {
            {1,2}, {2,1}, {2,3}, {2,4}, {2,5}, {1,6}, {6,6}, {6,10}, {10,11}, {11,1}, {15, 15}, {20, 21}
        };
        UNDIRECTED_INSTANCE = GraphUtils.subgraph(new SparseGraph(false, Arrays.asList(VV), Arrays.asList(EE)), SUB);
        DIRECTED_INSTANCE = GraphUtils.subgraph(new SparseGraph(true, Arrays.asList(VV), Arrays.asList(EE)), SUB);
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals("NODES: [1, 2, 5, 6, 15]  EDGES: 1: [2, 6] 2: [1, 5] 5: [2] 6: [1, 6] 15: [15]", GraphUtils.printGraph(UNDIRECTED_INSTANCE));
        assertEquals("NODES: [1, 2, 5, 6, 15]  EDGES: 1: [2, 6] 2: [1, 5] 5: [] 6: [6] 15: [15]", GraphUtils.printGraph(DIRECTED_INSTANCE));
    }

    @Test
    public void testOrder() {
        System.out.println("order");
        assertEquals(5, UNDIRECTED_INSTANCE.nodeCount());
        assertEquals(5, DIRECTED_INSTANCE.nodeCount());
    }

    @Test
    public void testNodes() {
        System.out.println("nodes");
        assertCollectionContentsSame(SUB, UNDIRECTED_INSTANCE.nodes());
        assertCollectionContentsSame(SUB, DIRECTED_INSTANCE.nodes());
    }

    @Test
    public void testContains() {
        System.out.println("contains");
        for (Integer i : SUB) {
            assertTrue(UNDIRECTED_INSTANCE.contains(i));
            assertTrue(DIRECTED_INSTANCE.contains(i));
        }
        assertFalse(UNDIRECTED_INSTANCE.contains(0));
        assertFalse(DIRECTED_INSTANCE.contains(0));
        assertFalse(UNDIRECTED_INSTANCE.contains(3));
        assertFalse(DIRECTED_INSTANCE.contains(3));
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
        assertTrue(UNDIRECTED_INSTANCE.adjacent(1, 2));
        assertFalse(UNDIRECTED_INSTANCE.adjacent(1, 5));
        assertTrue(UNDIRECTED_INSTANCE.adjacent(2, 5));
        assertTrue(UNDIRECTED_INSTANCE.adjacent(5, 2));
        assertTrue(UNDIRECTED_INSTANCE.adjacent(6, 6));
        assertTrue(UNDIRECTED_INSTANCE.adjacent(15, 15));
        assertFalse(UNDIRECTED_INSTANCE.adjacent(6, 10));
        assertFalse(UNDIRECTED_INSTANCE.adjacent(0, 2));
        assertTrue(DIRECTED_INSTANCE.adjacent(1, 2));
        assertFalse(DIRECTED_INSTANCE.adjacent(1, 5));
        assertTrue(DIRECTED_INSTANCE.adjacent(2, 5));
        assertTrue(DIRECTED_INSTANCE.adjacent(5, 2));
        assertTrue(DIRECTED_INSTANCE.adjacent(6, 6));
        assertFalse(UNDIRECTED_INSTANCE.adjacent(6, 10));
        assertTrue(DIRECTED_INSTANCE.adjacent(15, 15));
    }

    @Test
    public void testDegree() {
        System.out.println("degree");
        int[] nodes = {1, 2, 5, 6, 15};
        int[] expectedDegrees1 = {2, 2, 1, 3, 2};
        int[] expectedDegrees2 = {3, 3, 1, 3, 2};
        for (int i = 0; i < SUB.size(); i++) {
            assertEquals(expectedDegrees1[i], UNDIRECTED_INSTANCE.degree(nodes[i]));
            assertEquals(expectedDegrees2[i], DIRECTED_INSTANCE.degree(nodes[i]));
        }
    }

    @Test
    public void testNeighbors() {
        System.out.println("neighbors");
        assertEquals(0, UNDIRECTED_INSTANCE.neighbors(0).size());
        assertEquals(0, UNDIRECTED_INSTANCE.neighbors(3).size());
        assertEquals(0, DIRECTED_INSTANCE.neighbors(0).size());
        assertEquals(0, DIRECTED_INSTANCE.neighbors(3).size());
        int[] nodes = {1, 2, 5, 6, 15};
        Object[][] nbrs1 = new Object[][] { {2,6}, {1,5}, {2}, {1,6}, {15} };
        for (int i = 0; i < SUB.size(); i++) {
            assertCollectionContentsSame(Arrays.asList(nbrs1[i]), UNDIRECTED_INSTANCE.neighbors(nodes[i]));
            assertCollectionContentsSame(Arrays.asList(nbrs1[i]), DIRECTED_INSTANCE.neighbors(nodes[i]));
        }
    }

    @Test
    public void testEdgeNumber() {
        System.out.println("edgeNumber");
        assertEquals(5, UNDIRECTED_INSTANCE.edgeCount());
        assertEquals(6, DIRECTED_INSTANCE.edgeCount());
    }

}