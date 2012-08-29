/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.blaise.graph.modules.EdgeProbabilityBuilder;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

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
    static SparseGraph<Integer> UNDIR, DIR;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- SparseGraphTest --");
        VV = new Integer[] { 1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21 };
        EE = new Integer[][] {
            {1,2}, {2,1}, {2,3}, {2,4}, {2,5}, {1,6}, {6,6}, {6,10}, {10,11}, {11,1}, {15, 15}, {20, 21}
        };
        UNDIR = new SparseGraph(false, Arrays.asList(VV), Arrays.asList(EE));
        DIR = new SparseGraph(true, Arrays.asList(VV), Arrays.asList(EE));
    }

    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        for (Integer[] e : EE) {
            assertTrue(UNDIR.adjacent(e[0], e[1]));
            assertTrue(UNDIR.adjacent(e[1], e[0]));
            assertTrue(DIR.adjacent(e[0], e[1]));
        }
    }

    @Test
    public void testNodeCount() {
        System.out.println("order");
        assertEquals(11, UNDIR.nodeCount());
        assertEquals(11, DIR.nodeCount());
    }

    @Test
    public void testNodes() {
        System.out.println("nodes");
        List<Integer> expected = Arrays.asList(VV);
        assertCollectionContentsSame(expected, UNDIR.nodes());
        assertCollectionContentsSame(expected, DIR.nodes());
    }

    @Test
    public void testContains() {
        System.out.println("contains");
        for (Integer i : VV) {
            assertTrue(UNDIR.contains(i));
            assertTrue(DIR.contains(i));
        }
        assertFalse(UNDIR.contains(0));
        assertFalse(DIR.contains(0));
    }

    @Test
    public void testIsDirected() {
        System.out.println("isDirected");
        assertFalse(UNDIR.isDirected());
        assertTrue(DIR.isDirected());
    }

    @Test
    public void testAdjacent() {
        System.out.println("adjacent");
        for (Integer[] e : EE) {
            assertTrue(UNDIR.adjacent(e[0], e[1]));
            assertTrue(UNDIR.adjacent(e[1], e[0]));
            assertTrue(DIR.adjacent(e[0], e[1]));
        }
        assertFalse(UNDIR.adjacent((Integer)0, 2));
        assertFalse(DIR.adjacent((Integer)0, 2));
        assertTrue(DIR.adjacent((Integer)3, 2));
    }

    @Test
    public void testDegree() {
        System.out.print("degree ");
        int[] expectedDegrees1 = {3, 4, 1, 1, 1, 4, 2, 2, 2, 1, 1};
        int[] expectedDegrees2 = {4, 5, 1, 1, 1, 4, 2, 2, 2, 1, 1};
        int[] expectedIndeg2 =   {2, 1, 1, 1, 1, 2, 1, 1, 1, 0, 1};
        int[] expectedOutdeg2 =  {2, 4, 0, 0, 0, 2, 1, 1, 1, 1, 0};
        for (int i = 0; i < VV.length; i++) {
            System.out.print(VV[i]);
            assertEquals(expectedDegrees1[i], UNDIR.degree(VV[i]));
            assertEquals(expectedDegrees2[i], DIR.degree(VV[i]));
            assertEquals(expectedIndeg2[i], DIR.inDegree(VV[i]));
            assertEquals(expectedOutdeg2[i], DIR.outDegree(VV[i]));
        }
        System.out.println("");
    }

    @Test
    public void testNeighbors() {
        System.out.println("neighbors");
        assertEquals(0, UNDIR.neighbors(0).size());
        assertEquals(0, DIR.neighbors(0).size());
        Object[][] nbrs1 = new Object[][] { {2,6,11}, {1,3,4,5}, {2}, {2}, {2}, {1,6,10}, {6,11}, {10,1}, {15}, {21}, {20}};
        for (int i = 0; i < VV.length; i++) {
            assertCollectionContentsSame(Arrays.asList(nbrs1[i]), UNDIR.neighbors(VV[i]));
            assertCollectionContentsSame(Arrays.asList(nbrs1[i]), DIR.neighbors(VV[i]));
        }
        
        Graph<Integer> graph = new EdgeProbabilityBuilder(false, 200, .1f).createGraph();
        for (Integer v : graph.nodes()) {
            if (graph.neighbors(v).contains(null)) {
                fail("Neighbors of " + v + " contains null value");
            }
        }
    }

    @Test
    public void testEdgeNumber() {
        System.out.println("edgeNumber");
        assertEquals(11, UNDIR.edgeCount());
        assertEquals(12, DIR.edgeCount());
    }

}