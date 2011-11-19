/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class SubgraphTest {

    /** Tests to see if all elements of one collection are contained in the other, and vice versa */
    static void assertCollectionContentsSame(Collection expected, Collection found) {
        assertEquals(expected.size(), found.size());
        assertTrue(expected.containsAll(found));
        assertTrue(found.containsAll(expected));
    }

    public SubgraphTest() {
    }

    static Integer[] VV;
    static Integer[][] EE;
    static List<Integer> SUB;
    static Subgraph<Integer> UNDIRECTED_INSTANCE, DIRECTED_INSTANCE;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- SubgraphTest --");
        VV = new Integer[] { 1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21 };
        SUB = Arrays.asList(1, 2, 5, 6, 15);
        EE = new Integer[][] {
            {1,2}, {2,1}, {2,3}, {2,4}, {2,5}, {1,6}, {6,6}, {6,10}, {10,11}, {11,1}, {15, 15}, {20, 21}
        };
        UNDIRECTED_INSTANCE = new Subgraph(GraphFactory.getGraph(false, Arrays.asList(VV), Arrays.asList(EE)), SUB);
        DIRECTED_INSTANCE = new Subgraph(GraphFactory.getGraph(true, Arrays.asList(VV), Arrays.asList(EE)), SUB);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals("[1, 2], [1, 6], [2, 5], [6, 6], [15, 15]", UNDIRECTED_INSTANCE.toString());
        assertEquals("[1, 2], [1, 6], [2, 1], [2, 5], [6, 6], [15, 15]", DIRECTED_INSTANCE.toString());
    }

    @Test
    public void testOrder() {
        System.out.println("order");
        assertEquals(5, UNDIRECTED_INSTANCE.order());
        assertEquals(5, DIRECTED_INSTANCE.order());
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
        assertFalse(DIRECTED_INSTANCE.adjacent(5, 2));
        assertTrue(DIRECTED_INSTANCE.adjacent(6, 6));
        assertFalse(UNDIRECTED_INSTANCE.adjacent(6, 10));
        assertTrue(DIRECTED_INSTANCE.adjacent(15, 15));
    }

    @Test
    public void testDegree() {
        System.out.println("degree");
        int[] expectedDegrees1 = {2, 2, 1, 3, 2};
        int[] expectedDegrees2 = {2, 2, 0, 1, 1};
        for (int i = 0; i < SUB.size(); i++) {
            assertEquals(expectedDegrees1[i], UNDIRECTED_INSTANCE.degree(SUB.get(i)));
            assertEquals(expectedDegrees2[i], DIRECTED_INSTANCE.degree(SUB.get(i)));
        }
    }

    @Test
    public void testNeighbors() {
        System.out.println("neighbors");
        assertEquals(0, UNDIRECTED_INSTANCE.neighbors(0).size());
        assertEquals(0, UNDIRECTED_INSTANCE.neighbors(3).size());
        assertEquals(0, DIRECTED_INSTANCE.neighbors(0).size());
        assertEquals(0, DIRECTED_INSTANCE.neighbors(3).size());
        Object[][] nbrs1 = new Object[][] { {2,6}, {1,5}, {2}, {1,6}, {15} };
        Object[][] nbrs2 = new Object[][] { {2,6}, {1,5}, {}, {6}, {15} };
        for (int i = 0; i < SUB.size(); i++) {
            assertCollectionContentsSame(Arrays.asList(nbrs1[i]), UNDIRECTED_INSTANCE.neighbors(SUB.get(i)));
            assertCollectionContentsSame(Arrays.asList(nbrs2[i]), DIRECTED_INSTANCE.neighbors(SUB.get(i)));
        }
    }

    @Test
    public void testEdgeNumber() {
        System.out.println("edgeNumber");
        assertEquals(5, UNDIRECTED_INSTANCE.edgeCount());
        assertEquals(6, DIRECTED_INSTANCE.edgeCount());
    }

}