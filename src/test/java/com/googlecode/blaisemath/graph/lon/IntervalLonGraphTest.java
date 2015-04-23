package com.googlecode.blaisemath.graph.lon;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static com.googlecode.blaisemath.graph.AssertUtils.*;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class IntervalLonGraphTest {

    static IntervalLonGraph<Integer> UNDIR, DIR, UNDIR2;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- IntervalLongitudinalGraphTest --");
        TreeMap<Integer, double[]> nodeTimes = new TreeMap<Integer, double[]>();
        nodeTimes.put(1, new double[]{1,5});
        nodeTimes.put(2, new double[]{2,10});
        nodeTimes.put(3, new double[]{1,3});
        nodeTimes.put(4, new double[]{0,20});
        nodeTimes.put(5, new double[]{5,20});
        
        TreeMap<Integer, Map<Integer, double[]>> edgeTimes = new TreeMap<Integer, Map<Integer, double[]>>();
        putEdge(edgeTimes, 1, 2, new double[]{2,5});
        putEdge(edgeTimes, 1, 4, new double[]{1,3});
        putEdge(edgeTimes, 4, 1, new double[]{1,3});
        putEdge(edgeTimes, 5, 4, new double[]{10,20});

        UNDIR = IntervalLonGraph.getInstance(false, 100, nodeTimes, edgeTimes);
        DIR = IntervalLonGraph.getInstance(true, 100, nodeTimes, edgeTimes);

        TreeMap<Integer, List<double[]>> nodeTimes2 = new TreeMap<Integer, List<double[]>>();
        nodeTimes2.put(1, Arrays.asList(new double[]{1,5}));
        nodeTimes2.put(2, Arrays.asList(new double[]{2,10}));
        nodeTimes2.put(3, makeList(new double[]{1,3},new double[]{4,8}));
        nodeTimes2.put(4, Arrays.asList(new double[]{0,20}));
        nodeTimes2.put(5, Arrays.asList(new double[]{5,20}));
        
        TreeMap<Integer, Map<Integer, List<double[]>>> edgeTimes2 = new TreeMap<Integer, Map<Integer, List<double[]>>>();
        putEdge(edgeTimes2, 1, 2, makeList(new double[]{2,3}, new double[]{4,5}));
        putEdge(edgeTimes2, 1, 4, Arrays.asList(new double[]{1,3}));
        putEdge(edgeTimes2, 4, 1, Arrays.asList(new double[]{1,3}));
        putEdge(edgeTimes2, 5, 4, Arrays.asList(new double[]{10,20}));

        UNDIR2 = IntervalLonGraph.getInstance2(false, 5, nodeTimes2, edgeTimes2);
    }

    static List<double[]> makeList(double[] arr1, double[] arr2) {
        ArrayList<double[]> result = new ArrayList<double[]>();
        result.add(arr1); result.add(arr2);
        return result;
    }

    static void putEdge(Map<Integer, Map<Integer, double[]>> map, int i1, int i2, double[] times) {
        if (!map.containsKey(i1)) map.put(i1, new TreeMap<Integer,double[]>());
        map.get(i1).put(i2, times);
    }

    static void putEdge(Map<Integer, Map<Integer, List<double[]>>> map, int i1, int i2, List<double[]> times) {
        if (!map.containsKey(i1)) map.put(i1, new TreeMap<Integer,List<double[]>>());
        map.get(i1).put(i2, times);
    }

    @Test
    public void testToString() {
        System.out.println("toString: not tested");
    }

    @Test
    public void testIsDirected() {
        System.out.println("isDirected");
        assertTrue(DIR.isDirected());
        assertFalse(UNDIR.isDirected());
        assertFalse(UNDIR2.isDirected());
    }

    @Test
    public void testGetAllNodes() {
        System.out.println("getAllNodes");
        List<Integer> expected = Arrays.asList(1,2,3,4,5);
        assertCollectionContentsSame(expected, UNDIR.getAllNodes());
        assertCollectionContentsSame(expected, UNDIR2.getAllNodes());
        assertCollectionContentsSame(expected, DIR.getAllNodes());
    }

    @Test
    public void testGetNodeIntervals() {
        System.out.println("getNodeIntervals");

        double[][] expected = new double[][]{{1.0,5.0}};
        List<double[]> actual = UNDIR.getNodeIntervals(1);
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].length, actual.get(i).length);
            for (int j = 0; j < expected[i].length; j++)
                assertEquals(expected[i][j], actual.get(i)[j], 0.0);
        }

        expected = new double[][]{{1.0, 5.0}};
        actual = DIR.getNodeIntervals(1);
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].length, actual.get(i).length);
            for (int j = 0; j < expected[i].length; j++)
                assertEquals(expected[i][j], actual.get(i)[j], 0.0);
        }
        
        expected = new double[][]{{1.0, 3.0}, {4.0, 8.0}};
        actual = UNDIR2.getNodeIntervals(3);
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].length, actual.get(i).length);
            for (int j = 0; j < expected[i].length; j++)
                assertEquals(expected[i][j], actual.get(i)[j], 0.0);
        }

        assertNull(UNDIR.getNodeIntervals(12));
        assertNull(DIR.getNodeIntervals(12));
    }

    @Test
    public void testGetEdgeIntervals() {
        System.out.println("getEdgeIntervals");

        double[][] expected = new double[][]{{2.0,5.0}};
        List<double[]> actual = UNDIR.getEdgeIntervals(1, 2);
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].length, actual.get(i).length);
            for (int j = 0; j < expected[i].length; j++)
                assertEquals(expected[i][j], actual.get(i)[j], 0.0);
        }

        expected = new double[][]{{2.0,3.0},{4.0,5.0}};
        actual = UNDIR2.getEdgeIntervals(1, 2);
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].length, actual.get(i).length);
            for (int j = 0; j < expected[i].length; j++)
                assertEquals(expected[i][j], actual.get(i)[j], 0.0);
        }

        expected = new double[][]{{2.0,5.0}};
        actual = DIR.getEdgeIntervals(1, 2);
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].length, actual.get(i).length);
            for (int j = 0; j < expected[i].length; j++)
                assertEquals(expected[i][j], actual.get(i)[j], 0.0);
        }

        assertNull(DIR.getEdgeIntervals(2, 2));
        assertNull(DIR.getEdgeIntervals(2, 1));
        assertNull(UNDIR.getEdgeIntervals(1, 6));
    }

    @Test
    public void testGetMinimumTime_getMaximumTime() {
        System.out.println("getMinimumTime/getMaximumTime");
        assertEquals(0.0, UNDIR.getMinimumTime(), 0.0);
        assertEquals(20.0, UNDIR.getMaximumTime(), 0.0);
        assertEquals(0.0, DIR.getMinimumTime(), 0.0);
        assertEquals(20.0, DIR.getMaximumTime(), 0.0);
        assertEquals(0.0, UNDIR2.getMinimumTime(), 0.0);
        assertEquals(20.0, UNDIR2.getMaximumTime(), 0.0);
    }

    @Test
    public void testGetTimes() {
        System.out.println("getTimes");
        assertEquals(100, UNDIR.getTimes().size());
        assertEquals(100, DIR.getTimes().size());
    }

    @Test
    public void testSlice() {
        System.out.println("slice");

        System.out.println("UNDIR PARENT GRAPH: " + UNDIR);
        Graph<Integer> slice = UNDIR.slice(0.0, true);
        System.out.println("SLICE 0: " + GraphUtils.printGraph(slice));
        assertFalse(slice.isDirected());
        assertEquals(1, slice.nodeCount());
        assertEquals(0, slice.edgeCount());

        slice = UNDIR.slice(3.0, true);
        System.out.println("SLICE 3: " + GraphUtils.printGraph(slice));
        assertFalse(slice.isDirected());
        assertEquals(4, slice.nodeCount());
        assertEquals(2, slice.edgeCount());
        assertEquals(2, slice.degree(1));
        assertCollectionContentsSame(Arrays.asList(1,2,3,4), slice.nodes());
        assertTrue(slice.contains(1));
        assertFalse(slice.contains(5));
        assertCollectionContentsSame(Arrays.asList(2,4), slice.neighbors(1));
        assertCollectionContentsSame(Arrays.asList(1), slice.neighbors(2));
        assertCollectionContentsSame(Arrays.asList(1), slice.neighbors(4));
        assertTrue(slice.adjacent(1,2));
        assertTrue(slice.adjacent(2,1));
        assertFalse(slice.adjacent(5,4));
        assertTrue(slice.adjacent(1,4));
        assertTrue(slice.adjacent(4,1));

        slice = UNDIR.slice(-3.5, true);
        System.out.println("SLICE -3.5: " + GraphUtils.printGraph(slice));
        assertFalse(slice.isDirected());
        assertEquals(0, slice.nodeCount());
        assertEquals(0, slice.edgeCount());

        System.out.println("DIR PARENT GRAPH: " + DIR);
        slice = DIR.slice(3.0, true);
        System.out.println("SLICE 3: " + GraphUtils.printGraph(slice));
        assertTrue(slice.isDirected());
        assertEquals(4, slice.nodeCount());
        assertEquals(3, slice.edgeCount());
        assertEquals(3, slice.degree(1));
        assertCollectionContentsSame(Arrays.asList(1,2,3,4), slice.nodes());
        assertTrue(slice.contains(1));
        assertFalse(slice.contains(5));
        assertCollectionContentsSame(Arrays.asList(2,4), slice.neighbors(1));
        assertCollectionContentsSame(Arrays.asList(1), slice.neighbors(2));
        assertCollectionContentsSame(Arrays.asList(1), slice.neighbors(4));
        assertTrue(slice.adjacent(1,2));
        assertTrue(slice.adjacent(2,1));
        assertFalse(slice.adjacent(5,4));
        assertTrue(slice.adjacent(1,4));
        assertTrue(slice.adjacent(4,1));

        slice = DIR.slice(4.0, true);
        System.out.println("SLICE 4: " + GraphUtils.printGraph(slice));
        assertTrue(slice.isDirected());
        assertEquals(3, slice.nodeCount());
        assertEquals(1, slice.edgeCount());
        assertEquals(1, slice.degree(1));

        System.out.println("UNDIR2 PARENT GRAPH: " + UNDIR2);
        slice = UNDIR2.slice(3.0, true);
        System.out.println("SLICE 3: " + GraphUtils.printGraph(slice));
        assertFalse(slice.isDirected());
        assertEquals(4, slice.nodeCount());
        assertEquals(2, slice.edgeCount());

        slice = UNDIR2.slice(3.5, true);
        System.out.println("SLICE 3.5: " + GraphUtils.printGraph(slice));
        assertFalse(slice.isDirected());
        assertEquals(3, slice.nodeCount());
        assertEquals(0, slice.edgeCount());
    }
}
