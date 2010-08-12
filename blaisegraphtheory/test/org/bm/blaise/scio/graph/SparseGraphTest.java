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
public class SparseGraphTest {

    /** Tests to see if all elements of one collection are contained in the other, and vice versa */
    static void assertCollectionContentsSame(Collection expected, Collection found) {
        assertEquals(expected.size(), found.size());
        assertTrue(expected.containsAll(found));
        assertTrue(found.containsAll(expected));
    }

    static Integer[] VV;
    static Integer[][] EE;
    static SparseGraph<Integer> UNDIRECTED_INSTANCE, DIRECTED_INSTANCE;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- SparseGraphTest --");
        VV = new Integer[] { 1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21 };
        EE = new Integer[][] {
            {1,2}, {2,1}, {2,3}, {2,4}, {2,5}, {1,6}, {6,6}, {6,10}, {10,11}, {11,1}, {15, 15}, {20, 21}
        };
        UNDIRECTED_INSTANCE = SparseGraph.getInstance(false, Arrays.asList(VV), Arrays.asList(EE));
        DIRECTED_INSTANCE = SparseGraph.getInstance(true, Arrays.asList(VV), Arrays.asList(EE));
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        String expResult = "{1=[2, 6, 11], 2=[1, 3, 4, 5], 3=[2], 4=[2], 5=[2], 6=[1, 6, 10], 10=[6, 11], 11=[1, 10], 15=[15], 20=[21], 21=[20]}";
        assertEquals(expResult, UNDIRECTED_INSTANCE.toString());
        String expResult2 = "{1=[2, 6], 2=[1, 3, 4, 5], 3=[], 4=[], 5=[], 6=[6, 10], 10=[11], 11=[1], 15=[15], 20=[21], 21=[]}";
        assertEquals(expResult2, DIRECTED_INSTANCE.toString());
    }

    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        for (Integer[] e : EE) {
            assertTrue(UNDIRECTED_INSTANCE.adjacent(e[0], e[1]));
            assertTrue(UNDIRECTED_INSTANCE.adjacent(e[1], e[0]));
            assertTrue(DIRECTED_INSTANCE.adjacent(e[0], e[1]));
        }
    }

    @Test
    public void testOrder() {
        System.out.println("order");
        assertEquals(11, UNDIRECTED_INSTANCE.order());
        assertEquals(11, DIRECTED_INSTANCE.order());
    }

    @Test
    public void testNodes() {
        System.out.println("nodes");
        List<Integer> expected = Arrays.asList(VV);
        assertCollectionContentsSame(expected, UNDIRECTED_INSTANCE.nodes());
        assertCollectionContentsSame(expected, DIRECTED_INSTANCE.nodes());
    }

    @Test
    public void testContains() {
        System.out.println("contains");
        for (Integer i : VV) {
            assertTrue(UNDIRECTED_INSTANCE.contains(i));
            assertTrue(DIRECTED_INSTANCE.contains(i));
        }
        assertFalse(UNDIRECTED_INSTANCE.contains(0));
        assertFalse(DIRECTED_INSTANCE.contains(0));
    }

    @Test
    public void testIsDirected() {
        System.out.println("isDirected");
        assertFalse(UNDIRECTED_INSTANCE.isDirected());
        assertTrue(DIRECTED_INSTANCE.isDirected());
    }

    @Test
    public void testAdjacent() {
        System.out.println("adjacent");
        for (Integer[] e : EE) {
            assertTrue(UNDIRECTED_INSTANCE.adjacent(e[0], e[1]));
            assertTrue(UNDIRECTED_INSTANCE.adjacent(e[1], e[0]));
            assertTrue(DIRECTED_INSTANCE.adjacent(e[0], e[1]));
        }
        assertFalse(UNDIRECTED_INSTANCE.adjacent((Integer)0, 2));
        assertFalse(DIRECTED_INSTANCE.adjacent((Integer)0, 2));
        assertFalse(DIRECTED_INSTANCE.adjacent((Integer)3, 2));
    }

    @Test
    public void testDegree() {
        System.out.println("degree");
        int[] expectedDegrees1 = {3, 4, 1, 1, 1, 4, 2, 2, 2, 1, 1};
        int[] expectedDegrees2 = {2, 4, 0, 0, 0, 2, 1, 1, 1, 1, 0};
        for (int i = 0; i < VV.length; i++) {
            assertEquals(expectedDegrees1[i], UNDIRECTED_INSTANCE.degree(VV[i]));
            assertEquals(expectedDegrees2[i], DIRECTED_INSTANCE.degree(VV[i]));
        }
    }

    @Test
    public void testNeighbors() {
        System.out.println("neighbors");
        assertEquals(0, UNDIRECTED_INSTANCE.neighbors(0).size());
        assertEquals(0, DIRECTED_INSTANCE.neighbors(0).size());
        Object[][] nbrs1 = new Object[][] { {2,6,11}, {1,3,4,5}, {2}, {2}, {2}, {1,6,10}, {6,11}, {10,1}, {15}, {21}, {20}};
        Object[][] nbrs2 = new Object[][] { {2,6}, {1,3,4,5}, {}, {}, {}, {6,10}, {11}, {1}, {15}, {21}, {}};
        for (int i = 0; i < VV.length; i++) {
            assertCollectionContentsSame(Arrays.asList(nbrs1[i]), UNDIRECTED_INSTANCE.neighbors(VV[i]));
            assertCollectionContentsSame(Arrays.asList(nbrs2[i]), DIRECTED_INSTANCE.neighbors(VV[i]));
        }
    }

    @Test
    public void testEdgeNumber() {
        System.out.println("edgeNumber");
        assertEquals(11, UNDIRECTED_INSTANCE.edgeNumber());
        assertEquals(12, DIRECTED_INSTANCE.edgeNumber());
    }

}