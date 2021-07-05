package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2021 Elisha Peterson
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

import com.google.common.collect.*;
import com.google.common.graph.Graph;
import com.google.common.graph.Graphs;
import org.junit.Test;

import java.util.*;

import static com.googlecode.blaisemath.test.AssertUtils.assertCollectionContentsSame;
import static com.googlecode.blaisemath.test.AssertUtils.assertSets;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("UnstableApiUsage")
public class GraphUtilsTest {

    private static final Integer[] VV = new Integer[] { 1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21 };
    private static final Integer[][] EE = new Integer[][] { {1,2}, {2,1}, {2,3}, {2,4}, {2,5}, {1,6}, {6,6}, {6,10}, {10,11}, {11,1}, {15, 15}, {20, 21} };
    private static final Graph<Integer> UNDIRECTED_INSTANCE = GraphUtils.createFromArrayEdges(false, asList(VV), asList(EE));
    private static final Graph<Integer> DIRECTED_INSTANCE = GraphUtils.createFromArrayEdges(true, asList(VV), asList(EE));

    @Test
    public void testPrintGraph() {
        Graph<Integer> test1 = Graphs.inducedSubgraph(UNDIRECTED_INSTANCE, new HashSet<>(asList(1,2,3,6,10)));
        Graph<Integer> test2 = Graphs.inducedSubgraph(DIRECTED_INSTANCE, new HashSet<>(asList(1,2,3,6,10)));
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
        Graph<Integer> copy1 = Graphs.copyOf(UNDIRECTED_INSTANCE);
        Graph<Integer> copy2 = Graphs.copyOf(DIRECTED_INSTANCE);
        assertEquals(UNDIRECTED_INSTANCE.nodes().size(), copy1.nodes().size());
        assertEquals(DIRECTED_INSTANCE.nodes().size(), copy2.nodes().size());
        assertEquals(UNDIRECTED_INSTANCE.edges().size(), copy1.edges().size());
        assertEquals(DIRECTED_INSTANCE.edges().size(), copy2.edges().size());
        for (Integer[] e : EE) {
            assertTrue(copy1.hasEdgeConnecting(e[0], e[1]));
            assertTrue(copy2.hasEdgeConnecting(e[0], e[1]));
        }
    }

    @Test
    public void testAdjacencyMatrix() {
        boolean[][] result1 = GraphUtils.adjacencyMatrix(UNDIRECTED_INSTANCE, asList(VV));
        boolean[][] result2 = GraphUtils.adjacencyMatrix(DIRECTED_INSTANCE, asList(VV));
        assertEquals("[[false, true, false, false, false, true, false, true, false, false, false], [true, false, true, true, true, false, false, false, false, false, false], [false, true, false, false, false, false, false, false, false, false, false], [false, true, false, false, false, false, false, false, false, false, false], [false, true, false, false, false, false, false, false, false, false, false], [true, false, false, false, false, true, true, false, false, false, false], [false, false, false, false, false, true, false, true, false, false, false], [true, false, false, false, false, false, true, false, false, false, false], [false, false, false, false, false, false, false, false, true, false, false], [false, false, false, false, false, false, false, false, false, false, true], [false, false, false, false, false, false, false, false, false, true, false]]", Arrays.deepToString(result1));
        assertEquals("[[false, true, false, false, false, true, false, false, false, false, false], [true, false, true, true, true, false, false, false, false, false, false], [false, false, false, false, false, false, false, false, false, false, false], [false, false, false, false, false, false, false, false, false, false, false], [false, false, false, false, false, false, false, false, false, false, false], [false, false, false, false, false, true, true, false, false, false, false], [false, false, false, false, false, false, false, true, false, false, false], [true, false, false, false, false, false, false, false, false, false, false], [false, false, false, false, false, false, false, false, true, false, false], [false, false, false, false, false, false, false, false, false, false, true], [false, false, false, false, false, false, false, false, false, false, false]]", Arrays.deepToString(result2));

        List<String> vv = asList("A","B","C","D","E");
        String[][] edges = {{"A","B"},{"B","C"},{"C","D"},{"D","A"},{"E","E"}};
        Graph<String> result3 = GraphUtils.createFromArrayEdges(false, vv, asList(edges));
        Graph<String> result4 = GraphUtils.createFromArrayEdges(true, vv, asList(edges));
        assertEquals("[[false, true, false, true, false], [true, false, true, false, false], [false, true, false, true, false], [true, false, true, false, false], [false, false, false, false, true]]",
                Arrays.deepToString(GraphUtils.adjacencyMatrix(result3, vv)));
        assertEquals("[[false, true, false, false, false], [false, false, true, false, false], [false, false, false, true, false], [true, false, false, false, false], [false, false, false, false, true]]",
                Arrays.deepToString(GraphUtils.adjacencyMatrix(result4, vv)));
    }

    @Test
    public void testAdjacencyMatrixPowers() {
        List<String> vv = asList("A","B","C","D","E");
        String[][] edges = {{"A","B"},{"B","C"},{"C","D"},{"D","A"},{"E","E"}};
        Graph<String> result1 = GraphUtils.createFromArrayEdges(false, vv, asList(edges));
        Graph<String> result2 = GraphUtils.createFromArrayEdges(true, vv, asList(edges));
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
        Multiset<Integer> result1 = GraphUtils.degreeDistribution(UNDIRECTED_INSTANCE);
        Multiset<Integer> result2 = GraphUtils.degreeDistribution(DIRECTED_INSTANCE);
        assertEquals("[1 x 5, 2 x 3, 3, 4 x 2]", result1.toString());
        assertEquals("[1 x 5, 2 x 3, 4 x 2, 5]", result2.toString());
    }

    @Test
    public void testGeodesicTree() {
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
        assertEquals(1, gd2_3.size());
        Map<Integer, Integer> gd2_11 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 11);
        assertEquals(8, gd2_11.size());
        Map<Integer, Integer> gd2_15 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 15);
        assertEquals(1, gd2_15.size());
        Map<Integer, Integer> gd2_20 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 20);
        assertEquals(2, gd2_20.size());
        Map<Integer, Integer> gd2_21 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 21);
        assertEquals(1, gd2_21.size());

        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 6; j++) {
                int uDist = GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, i, j);
                assertEquals(uDist == -1 ? null : uDist, GraphUtils.geodesicTree(UNDIRECTED_INSTANCE, i).get(j));
                int dDist = GraphUtils.geodesicDistance(DIRECTED_INSTANCE, i, j);
                assertEquals(dDist == -1 ? null : dDist, GraphUtils.geodesicTree(DIRECTED_INSTANCE, i).get(j));
            }
        }
    }

    @Test
    public void testGeodesicDistance() {
        assertEquals(0, GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, 1, 1));
        assertEquals(1, GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, 1, 6));
        assertEquals(2, GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, 2, 11));
        assertEquals(1, GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, 3, 2));
        assertEquals(-1, GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, 1, 15));
        assertEquals(0, GraphUtils.geodesicDistance(DIRECTED_INSTANCE, 1, 1));
        assertEquals(1, GraphUtils.geodesicDistance(DIRECTED_INSTANCE, 1, 6));
        assertEquals(4, GraphUtils.geodesicDistance(DIRECTED_INSTANCE, 2, 11));
        assertEquals(-1, GraphUtils.geodesicDistance(DIRECTED_INSTANCE, 3, 2));
        assertEquals(-1, GraphUtils.geodesicDistance(DIRECTED_INSTANCE, 1, 15));
    }

    @Test
    public void testNeighborhood() {
        assertCollectionContentsSame(Collections.singletonList(2), GraphUtils.neighborhood(UNDIRECTED_INSTANCE, 2, 0));
        assertCollectionContentsSame(asList(1,2,3,4,5), GraphUtils.neighborhood(UNDIRECTED_INSTANCE, 2, 1));
        assertCollectionContentsSame(asList(1,2,3,4,5,6,11), GraphUtils.neighborhood(UNDIRECTED_INSTANCE, 2, 2));
        assertCollectionContentsSame(asList(1,2,3,4,5,6,10,11), GraphUtils.neighborhood(UNDIRECTED_INSTANCE, 2, 3));
        assertCollectionContentsSame(asList(1,2,3,4,5,6,10,11), GraphUtils.neighborhood(UNDIRECTED_INSTANCE, 2, 20));
        assertCollectionContentsSame(asList(1,2,3,4,5), GraphUtils.neighborhood(DIRECTED_INSTANCE, 2, 1));
        assertCollectionContentsSame(asList(1,2,3,4,5,6), GraphUtils.neighborhood(DIRECTED_INSTANCE, 2, 2));
        assertCollectionContentsSame(asList(1,2,3,4,5,6,10), GraphUtils.neighborhood(DIRECTED_INSTANCE, 2, 3));
        assertCollectionContentsSame(asList(1,2,3,4,5,6,10,11), GraphUtils.neighborhood(DIRECTED_INSTANCE, 2, 20));
        assertCollectionContentsSame(Collections.singletonList(5), GraphUtils.neighborhood(DIRECTED_INSTANCE, 5, 20));
    }

    @Test
    public void testComponents() {
        for (Graph<Integer> g : asList(UNDIRECTED_INSTANCE, DIRECTED_INSTANCE)) {
            List<Set<Integer>> result1 = new ArrayList<>(GraphUtils.components(g));
            result1.sort(new Ordering<Collection>() {
                @Override
                public int compare(Collection left, Collection right) {
                    return left.size() - right.size();
                }
            }.reverse());
            assertCollectionContentsSame(asList(1,2,3,4,5,6,10,11), result1.get(0));
            assertCollectionContentsSame(asList(20,21), result1.get(1));
            assertCollectionContentsSame(Collections.singletonList(15), result1.get(2));
        }
    }
    
    @Test
    public void testComponents_Multimap() {
        assertSets(GraphUtils.components(HashMultimap.create()));
        assertSets(GraphUtils.components(multimap(0, 0)), ImmutableSet.of(0));
        assertSets(GraphUtils.components(multimap(0, 1, 1, 2)), ImmutableSet.of(0, 1, 2));
        assertSets(GraphUtils.components(multimap(0, 1, 2, 3)), ImmutableSet.of(0, 1), ImmutableSet.of(2, 3));
    }

    @Test
    public void testComponentGraphs() {
        for (Graph<Integer> g : asList(UNDIRECTED_INSTANCE, DIRECTED_INSTANCE)) {
            Set<Graph<Integer>> result1 = new TreeSet<>(GraphUtils.GRAPH_SIZE_DESCENDING);
            result1.addAll(GraphUtils.componentGraphs(g));
            Graph[] graphs = result1.toArray(new Graph[0]);
            assertCollectionContentsSame(asList(1, 2, 3, 4, 5, 6, 10, 11), graphs[0].nodes());
            assertEquals(g.isDirected() ? 10 : 9, graphs[0].edges().size());
            assertCollectionContentsSame(asList(20, 21), graphs[1].nodes());
            assertEquals(1, graphs[1].edges().size());
            assertCollectionContentsSame(Collections.singletonList(15), graphs[2].nodes());
            assertEquals(1, graphs[2].edges().size());
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static <X> Multimap<X,X> multimap(X k, X v) {
        Multimap<X,X> res = HashMultimap.create();
        res.put(k, v);
        return res;
    }

    @SuppressWarnings("SameParameterValue")
    private static <X> Multimap<X,X> multimap(X k1, X v1, X k2, X v2) {
        Multimap<X,X> res = HashMultimap.create();
        res.put(k1, v1);
        res.put(k2, v2);
        return res;
    }

}
