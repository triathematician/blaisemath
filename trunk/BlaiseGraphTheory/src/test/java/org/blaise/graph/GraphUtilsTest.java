/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class GraphUtilsTest {

    /** Tests to see if all elements of one collection are contained in the other, and vice versa */
    static void assertCollectionContentsSame(Collection expected, Collection found) {
        assertEquals(expected.size(), found.size());
        assertTrue(expected.containsAll(found));
        assertTrue(found.containsAll(expected));
    }

    static Integer[] VV;
    static Integer[][] EE;
    static Graph<Integer> UNDIRECTED_INSTANCE, DIRECTED_INSTANCE;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- GraphsTest --");
        VV = new Integer[] { 1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21 };
        EE = new Integer[][] {
            {1,2}, {2,1}, {2,3}, {2,4}, {2,5}, {1,6}, {6,6}, {6,10}, {10,11}, {11,1}, {15, 15}, {20, 21}
        };
        UNDIRECTED_INSTANCE = new SparseGraph(false, Arrays.asList(VV), Arrays.asList(EE));
        DIRECTED_INSTANCE = new SparseGraph(true, Arrays.asList(VV), Arrays.asList(EE));
    }

    @Test
    public void testPrintGraph() {
        System.out.println("printGraph");
        Graph<Integer> test1 = GraphUtils.subgraph(UNDIRECTED_INSTANCE, new HashSet(Arrays.asList(1,2,3,6,10)));
        Graph<Integer> test2 = GraphUtils.subgraph(DIRECTED_INSTANCE, new HashSet(Arrays.asList(1,2,3,6,10)));
        assertEquals("NODES: [1, 2, 3, 6, 10]  EDGES: 1: [2, 6] 2: [1, 3] 3: [2] 6: [1, 6, 10] 10: [6]", GraphUtils.printGraph(test1));
        assertEquals("NODES: [1, 2, 3, 6, 10]", GraphUtils.printGraph(test1, true, false));
        assertEquals("EDGES: 1: [2, 6] 2: [1, 3] 3: [2] 6: [1, 6, 10] 10: [6]", GraphUtils.printGraph(test1, false, true));
        assertEquals("GRAPH", GraphUtils.printGraph(test1, false, false));
        assertEquals("NODES: [1, 2, 3, 6, 10]  EDGES: 1: [2, 6] 2: [1, 3] 3: [] 6: [6, 10] 10: []", GraphUtils.printGraph(test2));
        assertEquals("NODES: [1, 2, 3, 6, 10]", GraphUtils.printGraph(test2, true, false));
        assertEquals("EDGES: 1: [2, 6] 2: [1, 3] 3: [] 6: [6, 10] 10: []", GraphUtils.printGraph(test2, false, true));
        assertEquals("GRAPH", GraphUtils.printGraph(test2, false, false));
    }

    @Test
    public void testCopyGraph() {
        System.out.println("copyGraph");
        Graph<Integer> copy1 = GraphUtils.copy(UNDIRECTED_INSTANCE);
        Graph<Integer> copy2 = GraphUtils.copy(DIRECTED_INSTANCE);
        assertEquals(UNDIRECTED_INSTANCE.nodeCount(), copy1.nodeCount());
        assertEquals(DIRECTED_INSTANCE.nodeCount(), copy2.nodeCount());
        assertEquals(UNDIRECTED_INSTANCE.edgeCount(), copy1.edgeCount());
        assertEquals(DIRECTED_INSTANCE.edgeCount(), copy2.edgeCount());
        for (Integer[] e : EE) {
            assertTrue(copy1.adjacent(e[0], e[1]));
            assertTrue(copy2.adjacent(e[0], e[1]));
        }
    }

    @Test
    public void testAdjacencyMatrix() {
        System.out.println("adjacencyMatrix");
        boolean[][] result1 = GraphUtils.adjacencyMatrix(UNDIRECTED_INSTANCE, Arrays.asList(VV));
        boolean[][] result2 = GraphUtils.adjacencyMatrix(DIRECTED_INSTANCE, Arrays.asList(VV));
        assertEquals("[[false, true, false, false, false, true, false, true, false, false, false], [true, false, true, true, true, false, false, false, false, false, false], [false, true, false, false, false, false, false, false, false, false, false], [false, true, false, false, false, false, false, false, false, false, false], [false, true, false, false, false, false, false, false, false, false, false], [true, false, false, false, false, true, true, false, false, false, false], [false, false, false, false, false, true, false, true, false, false, false], [true, false, false, false, false, false, true, false, false, false, false], [false, false, false, false, false, false, false, false, true, false, false], [false, false, false, false, false, false, false, false, false, false, true], [false, false, false, false, false, false, false, false, false, true, false]]", Arrays.deepToString(result1));
        assertEquals("[[false, true, false, false, false, true, false, false, false, false, false], [true, false, true, true, true, false, false, false, false, false, false], [false, false, false, false, false, false, false, false, false, false, false], [false, false, false, false, false, false, false, false, false, false, false], [false, false, false, false, false, false, false, false, false, false, false], [false, false, false, false, false, true, true, false, false, false, false], [false, false, false, false, false, false, false, true, false, false, false], [true, false, false, false, false, false, false, false, false, false, false], [false, false, false, false, false, false, false, false, true, false, false], [false, false, false, false, false, false, false, false, false, false, true], [false, false, false, false, false, false, false, false, false, false, false]]", Arrays.deepToString(result2));

        List<String> vv = Arrays.asList("A","B","C","D","E");
        String[][] edges = {{"A","B"},{"B","C"},{"C","D"},{"D","A"},{"E","E"}};
        Graph<String> result3 = new SparseGraph(false, vv, Arrays.asList(edges));
        Graph<String> result4 = new SparseGraph(true, vv, Arrays.asList(edges));
        assertEquals("[[false, true, false, true, false], [true, false, true, false, false], [false, true, false, true, false], [true, false, true, false, false], [false, false, false, false, true]]",
                Arrays.deepToString(GraphUtils.adjacencyMatrix(result3, vv)));
        assertEquals("[[false, true, false, false, false], [false, false, true, false, false], [false, false, false, true, false], [true, false, false, false, false], [false, false, false, false, true]]",
                Arrays.deepToString(GraphUtils.adjacencyMatrix(result4, vv)));
    }

    @Test
    public void testAdjacencyMatrixPowers() {
        System.out.println("adjacencyMatrixPowers");
        List<String> vv = Arrays.asList("A","B","C","D","E");
        String[][] edges = {{"A","B"},{"B","C"},{"C","D"},{"D","A"},{"E","E"}};
        Graph<String> result1 = new SparseGraph(false, vv, Arrays.asList(edges));
        Graph<String> result2 = new SparseGraph(true, vv, Arrays.asList(edges));
        assertEquals("[[0, 1, 0, 1, 0], [1, 0, 1, 0, 0], [0, 1, 0, 1, 0], [1, 0, 1, 0, 0], [0, 0, 0, 0, 1]]",
                Arrays.deepToString(GraphUtils.adjacencyMatrixPowers(result1,vv,12)[0]));
        assertEquals("[[2, 0, 2, 0, 0], [0, 2, 0, 2, 0], [2, 0, 2, 0, 0], [0, 2, 0, 2, 0], [0, 0, 0, 0, 1]]",
                Arrays.deepToString(GraphUtils.adjacencyMatrixPowers(result1,vv,12)[1]));
        assertEquals("[[0, 0, 1, 0, 0], [0, 0, 0, 1, 0], [1, 0, 0, 0, 0], [0, 1, 0, 0, 0], [0, 0, 0, 0, 1]]",
                Arrays.deepToString(GraphUtils.adjacencyMatrixPowers(result2,vv,12)[1]));
        assertEquals("[[0, 0, 0, 1, 0], [1, 0, 0, 0, 0], [0, 1, 0, 0, 0], [0, 0, 1, 0, 0], [0, 0, 0, 0, 1]]",
                Arrays.deepToString(GraphUtils.adjacencyMatrixPowers(result2,vv,12)[2]));
    }

    @Test
    public void testDegreeDistribution() {
        System.out.println("degreeDistribution");
        int[] expected1 = new int[]{0, 5, 3, 1, 2};
        int[] expected2 = new int[]{0, 5, 3, 0, 2, 1};
        int[] result1 = GraphUtils.degreeDistribution(UNDIRECTED_INSTANCE);
        int[] result2 = GraphUtils.degreeDistribution(DIRECTED_INSTANCE);
        assertArrayEquals(expected1, result1);
        assertArrayEquals(expected2, result2);
    }

    @Test
    public void testGeodesicTree() {
        System.out.println("geodesicTree");
        Map<Integer, Integer> gd1_2 = GraphUtils.geodesicTree(UNDIRECTED_INSTANCE, 2);
        assertEquals(8, gd1_2.size());
        Map<Integer, Integer> gd1_11 = GraphUtils.geodesicTree(UNDIRECTED_INSTANCE, 11);
        assertEquals(8, gd1_11.size());
        Map<Integer, Integer> gd1_15 = GraphUtils.geodesicTree(UNDIRECTED_INSTANCE, 15);
        assertEquals(1, gd1_15.size());
        Map<Integer, Integer> gd1_20 = GraphUtils.geodesicTree(UNDIRECTED_INSTANCE, 20);
        assertEquals(2, gd1_20.size());
        Map<Integer, Integer> gd2_2 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 2);
        assertEquals(8, gd2_2.size());
        Map<Integer, Integer> gd2_3 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 3);
        assertEquals(8, gd2_3.size());
        Map<Integer, Integer> gd2_11 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 11);
        assertEquals(8, gd2_11.size());
        Map<Integer, Integer> gd2_15 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 15);
        assertEquals(1, gd2_15.size());
        Map<Integer, Integer> gd2_20 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 20);
        assertEquals(2, gd2_20.size());
        Map<Integer, Integer> gd2_21 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 21);
        assertEquals(2, gd2_21.size());

        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 6; j++) {
                Integer uDist = GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, i, j);
                assertEquals(uDist == -1 ? null : uDist, (Integer) GraphUtils.geodesicTree(UNDIRECTED_INSTANCE, i).get(j));
                Integer dDist = GraphUtils.geodesicDistance(DIRECTED_INSTANCE, i, j);
                assertEquals(dDist == -1 ? null : dDist, (Integer) GraphUtils.geodesicTree(DIRECTED_INSTANCE, i).get(j));
            }
        }
    }

    @Test
    public void testGeodesicDistance() {
        System.out.println("geodesicDistance");
        assertEquals(0, GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, 1, 1));
        assertEquals(1, GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, 1, 6));
        assertEquals(2, GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, 2, 11));
        assertEquals(1, GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, 3, 2));
        assertEquals(-1, GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, 1, 15));
        assertEquals(0, GraphUtils.geodesicDistance(DIRECTED_INSTANCE, 1, 1));
        assertEquals(1, GraphUtils.geodesicDistance(DIRECTED_INSTANCE, 1, 6));
        assertEquals(2, GraphUtils.geodesicDistance(DIRECTED_INSTANCE, 2, 11));
        assertEquals(1, GraphUtils.geodesicDistance(DIRECTED_INSTANCE, 3, 2));
        assertEquals(-1, GraphUtils.geodesicDistance(DIRECTED_INSTANCE, 1, 15));
    }

    @Test
    public void testNeighborhood() {
        System.out.println("neighborhood");
        assertCollectionContentsSame(Arrays.asList(2), GraphUtils.neighborhood(UNDIRECTED_INSTANCE, 2, 0));
        assertCollectionContentsSame(Arrays.asList(1,2,3,4,5), GraphUtils.neighborhood(UNDIRECTED_INSTANCE, 2, 1));
        assertCollectionContentsSame(Arrays.asList(1,2,3,4,5,6,11), GraphUtils.neighborhood(UNDIRECTED_INSTANCE, 2, 2));
        assertCollectionContentsSame(Arrays.asList(1,2,3,4,5,6,10,11), GraphUtils.neighborhood(UNDIRECTED_INSTANCE, 2, 3));
        assertCollectionContentsSame(Arrays.asList(1,2,3,4,5,6,10,11), GraphUtils.neighborhood(UNDIRECTED_INSTANCE, 2, 20));
        assertCollectionContentsSame(Arrays.asList(1,2,3,4,5), GraphUtils.neighborhood(DIRECTED_INSTANCE, 2, 1));
        assertCollectionContentsSame(Arrays.asList(1,2,3,4,5,6,11), GraphUtils.neighborhood(DIRECTED_INSTANCE, 2, 2));
        assertCollectionContentsSame(Arrays.asList(1,2,3,4,5,6,10,11), GraphUtils.neighborhood(DIRECTED_INSTANCE, 2, 3));
        assertCollectionContentsSame(Arrays.asList(1,2,3,4,5,6,10,11), GraphUtils.neighborhood(DIRECTED_INSTANCE, 2, 20));
        assertCollectionContentsSame(Arrays.asList(1,2,3,4,5,6,10,11), GraphUtils.neighborhood(DIRECTED_INSTANCE, 5, 20));
    }

    @Test
    public void testComponents() {
        System.out.println("components");
        List<Set<Integer>> result1 = new ArrayList<Set<Integer>>(GraphUtils.components(UNDIRECTED_INSTANCE));
        assertCollectionContentsSame(Arrays.asList(1,2,3,4,5,6,10,11), result1.get(0));
        assertCollectionContentsSame(Arrays.asList(15), result1.get(1));
        assertCollectionContentsSame(Arrays.asList(20,21), result1.get(2));
//        try { GraphUtils.components(DIRECTED_INSTANCE); assertFalse(true); } catch (IllegalArgumentException ex) { assertTrue(true); }
    }

    @Test
    public void testComponentGraphs() {
        System.out.println("componentGraphs");
        Set<Graph<Integer>> result1 = new TreeSet<Graph<Integer>>(GraphUtils.GRAPH_SIZE_DESCENDING);
        result1.addAll(GraphUtils.getComponentGraphs(UNDIRECTED_INSTANCE));
        Graph[] graphs = result1.toArray(new Graph[0]);
        assertCollectionContentsSame(Arrays.asList(1, 2, 3, 4, 5, 6, 10, 11), graphs[0].nodes());
        assertEquals(9, graphs[0].edgeCount());
        assertCollectionContentsSame(Arrays.asList(20, 21), graphs[1].nodes());
        assertEquals(1, graphs[1].edgeCount());
        assertCollectionContentsSame(Arrays.asList(15), graphs[2].nodes());
        assertEquals(1, graphs[2].edgeCount());
//        try { GraphUtils.getComponentGraphs(DIRECTED_INSTANCE); assertFalse(true); } catch (IllegalArgumentException ex) { assertTrue(true); }
    }

}