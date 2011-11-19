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
public class ContractedGraphTest {

    /** Tests to see if all elements of one collection are contained in the other, and vice versa */
    static void assertCollectionContentsSame(Collection expected, Collection found) {
        assertEquals(expected.size(), found.size());
        assertTrue(expected.containsAll(found));
        assertTrue(found.containsAll(expected));
    }

    static Integer CVX;
    static Integer[] VV;
    static Integer[][] EE;
    static List<Integer> SUB;
    static ContractedGraph<Integer> UNDIRECTED_INSTANCE, DIRECTED_INSTANCE;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- ContractedGraphTest --");
        CVX = 0;
        VV = new Integer[] { 1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21 };
        SUB = Arrays.asList(1, 2, 5, 6, 15);
        EE = new Integer[][] {
            {1,2}, {2,1}, {2,3}, {2,4}, {2,5}, {1,6}, {6,6}, {6,10}, {10,11}, {11,1}, {15, 15}, {20, 21}
        };
        UNDIRECTED_INSTANCE = new ContractedGraph(GraphFactory.getGraph(false, Arrays.asList(VV), Arrays.asList(EE)), SUB, CVX);
        DIRECTED_INSTANCE = new ContractedGraph(GraphFactory.getGraph(true, Arrays.asList(VV), Arrays.asList(EE)), SUB, CVX);
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals("Nodes: [0, 3, 4, 10, 11, 20, 21]; Edges: [0, 0], [0, 3], [0, 4], [0, 10], [0, 11], [10, 11], [20, 21]", UNDIRECTED_INSTANCE.toString());
        assertEquals("Nodes: [0, 3, 4, 10, 11, 20, 21]; Edges: [0, 0], [0, 3], [0, 4], [0, 10], [10, 11], [11, 0], [20, 21]", DIRECTED_INSTANCE.toString());
    }

    @Test
    public void testOrder() {
        System.out.println("order");
        assertEquals(7, UNDIRECTED_INSTANCE.order());
        assertEquals(7, DIRECTED_INSTANCE.order());
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
        assertFalse(DIRECTED_INSTANCE.adjacent(4, 0));
        assertTrue(DIRECTED_INSTANCE.adjacent(0, 10));
        assertTrue(DIRECTED_INSTANCE.adjacent(10, 11));
        assertTrue(DIRECTED_INSTANCE.adjacent(11, 0));
        assertFalse(DIRECTED_INSTANCE.adjacent(0, 11));
        assertTrue(DIRECTED_INSTANCE.adjacent(20, 21));
    }

    @Test
    public void testDegree() {
        System.out.println("degree");
        int[] expectedDegrees1 = {6, 1, 1, 2, 2, 1, 1};
        int[] expectedDegrees2 = {4, 0, 0, 1, 1, 1, 0};
        List<Integer> nodes = UNDIRECTED_INSTANCE.nodes();
        for (int i = 0; i < nodes.size(); i++) {
            assertEquals(expectedDegrees1[i], UNDIRECTED_INSTANCE.degree(nodes.get(i)));
            assertEquals(expectedDegrees2[i], DIRECTED_INSTANCE.degree(nodes.get(i)));
        }
    }

    @Test
    public void testNeighbors() {
        System.out.println("neighbors");
        assertEquals(0, UNDIRECTED_INSTANCE.neighbors(1).size());
        assertEquals(0, DIRECTED_INSTANCE.neighbors(1).size());
        Object[][] nbrs1 = new Object[][] { {0,3,4,10,11}, {0}, {0}, {0,11}, {0,10}, {21}, {20} };
        Object[][] nbrs2 = new Object[][] { {0,3,4,10}, {}, {}, {11}, {0}, {21}, {} };
        List<Integer> nodes = UNDIRECTED_INSTANCE.nodes();
        for (int i = 0; i < SUB.size(); i++) {
            assertCollectionContentsSame(Arrays.asList(nbrs1[i]), UNDIRECTED_INSTANCE.neighbors(nodes.get(i)));
            assertCollectionContentsSame(Arrays.asList(nbrs2[i]), DIRECTED_INSTANCE.neighbors(nodes.get(i)));
        }
    }

    @Test
    public void testEdgeNumber() {
        System.out.println("edgeNumber");
        assertEquals(7, UNDIRECTED_INSTANCE.edgeCount());
        assertEquals(7, DIRECTED_INSTANCE.edgeCount());
    }

}