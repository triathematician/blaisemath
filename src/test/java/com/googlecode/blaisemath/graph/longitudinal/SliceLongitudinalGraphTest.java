package com.googlecode.blaisemath.graph.longitudinal;

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

import static com.googlecode.blaisemath.graph.AssertUtils.assertCollectionContentsSame;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.SparseGraph;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class SliceLongitudinalGraphTest {

    static Integer[] VV;
    static Integer[][][] EE;
    static SliceLongitudinalGraph<Integer> UNDIR, DIR, EMPTY;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- ListTimeGraphTest --");
        VV = new Integer[] { 1, 2, 3, 4, 5, 6 };
        EE = new Integer[][][] {
            {{1,2},{2,3},{3,4},{4,5},{5,6}},
            {{1,3},{3,5},{5,1},{2,4},{4,6},{6,2}},
            {{1,4},{2,5},{3,6}},
            {} };
        UNDIR = new SliceLongitudinalGraph<Integer>();
          UNDIR.addGraph(SparseGraph.createFromArrayEdges(false, Arrays.asList(VV), Arrays.asList(EE[0])), 0.0);
          UNDIR.addGraph(SparseGraph.createFromArrayEdges(false, Arrays.asList(VV), Arrays.asList(EE[1])), 1.0);
          UNDIR.addGraph(SparseGraph.createFromArrayEdges(false, Arrays.asList(VV), Arrays.asList(EE[2])), 2.0);
          UNDIR.addGraph(SparseGraph.createFromArrayEdges(false, Arrays.asList(VV), Arrays.asList(EE[3])), 3.0);
          UNDIR.addGraph(SparseGraph.createFromArrayEdges(false, Arrays.asList(VV), Arrays.asList(EE[0])), 4.0);
        DIR = new SliceLongitudinalGraph<Integer>();
          DIR.addGraph(SparseGraph.createFromArrayEdges(true, Arrays.asList(VV), Arrays.asList(EE[0])), 5.0);
          DIR.addGraph(SparseGraph.createFromArrayEdges(true, Arrays.asList(VV), Arrays.asList(EE[1])), -10.0);
          DIR.addGraph(SparseGraph.createFromArrayEdges(true, Arrays.asList(VV), Arrays.asList(EE[2])), 20.0);
          DIR.addGraph(SparseGraph.createFromArrayEdges(true, Arrays.asList(VV), Arrays.asList(EE[3])), 0.0);
        EMPTY = new SliceLongitudinalGraph<Integer>();
    }

    @Test
    public void testToString() {
        System.out.println("toString: not tested");
        assertEquals("SliceLongitudinalGraph {}", EMPTY.toString());
    }

    @Test
    public void testAddGraph() {
        System.out.println("addGraph: tested as part of overall suite");
    }

    @Test
    public void testIsDirected() {
        System.out.println("isDirected");
        assertTrue(DIR.isDirected());
        assertFalse(UNDIR.isDirected());
        assertFalse(EMPTY.isDirected());
    }

    @Test
    public void testGetAllNodes() {
        System.out.println("getAllNodes");
        List<Integer> expected = Arrays.asList(VV);
        assertCollectionContentsSame(expected, UNDIR.getAllNodes());
        assertCollectionContentsSame(expected, DIR.getAllNodes());
        assertCollectionContentsSame(Collections.emptyList(), EMPTY.getAllNodes());
    }

    @Test
    public void testGetNodeIntervals() {
        System.out.println("getNodeIntervals");

        double[][] expected = new double[][]{{0.0,0.0},{1.0,1.0},{2.0,2.0},{3.0,3.0},{4.0,4.0}};
        List<double[]> actual = UNDIR.getNodeIntervals(1);
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].length, actual.get(i).length);
            for (int j = 0; j < expected[i].length; j++)
                assertEquals(expected[i][j], actual.get(i)[j], 0.0);
        }

        expected = new double[][]{{-10.0,-10.0},{0.0,0.0},{5.0,5.0},{20.0,20.0}};
        actual = DIR.getNodeIntervals(1);
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].length, actual.get(i).length);
            for (int j = 0; j < expected[i].length; j++)
                assertEquals(expected[i][j], actual.get(i)[j], 0.0);
        }

        assertNull(UNDIR.getNodeIntervals(12));
        assertNull(DIR.getNodeIntervals(12));
        assertNull(EMPTY.getNodeIntervals(12));
    }

    @Test
    public void testGetEdgeIntervals() {
        System.out.println("getEdgeIntervals");

        double[][] expected = new double[][]{{2.0,2.0}};
        List<double[]> actual = UNDIR.getEdgeIntervals(3, 6);
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].length, actual.get(i).length);
            for (int j = 0; j < expected[i].length; j++)
                assertEquals(expected[i][j], actual.get(i)[j], 0.0);
        }

        expected = new double[][]{{0.0,0.0},{4.0,4.0}};
        actual = UNDIR.getEdgeIntervals(1, 2);
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].length, actual.get(i).length);
            for (int j = 0; j < expected[i].length; j++)
                assertEquals(expected[i][j], actual.get(i)[j], 0.0);
        }
        actual = UNDIR.getEdgeIntervals(2, 1);
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].length, actual.get(i).length);
            for (int j = 0; j < expected[i].length; j++)
                assertEquals(expected[i][j], actual.get(i)[j], 0.0);
        }

        expected = new double[][]{{5.0,5.0}};
        actual = DIR.getEdgeIntervals(1, 2);
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].length, actual.get(i).length);
            for (int j = 0; j < expected[i].length; j++)
                assertEquals(expected[i][j], actual.get(i)[j], 0.0);
        }

        System.out.println(DIR.getEdgeIntervals(2,2));
        assertNull(DIR.getEdgeIntervals(2, 2));
        assertNull(DIR.getEdgeIntervals(2, 1));
        assertNull(UNDIR.getEdgeIntervals(1, 6));
        assertNull(EMPTY.getEdgeIntervals(0, 1));
    }

    @Test
    public void testGetMinimumTime_getMaximumTime() {
        System.out.println("getMinimumTime/getMaximumTime");
        assertEquals(0.0, UNDIR.getMinimumTime(), 0.0);
        assertEquals(4.0, UNDIR.getMaximumTime(), 0.0);
        assertEquals(-10.0, DIR.getMinimumTime(), 0.0);
        assertEquals(20.0, DIR.getMaximumTime(), 0.0);
        assertEquals(-Double.MAX_VALUE, EMPTY.getMinimumTime(), 0.0);
        assertEquals(Double.MAX_VALUE, EMPTY.getMaximumTime(), 0.0);
    }

    @Test
    public void testGetTimes() {
        System.out.println("getTimes");
        assertCollectionContentsSame(Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0), UNDIR.getTimes());
        assertCollectionContentsSame(Arrays.asList(-10.0, 0.0, 5.0, 20.0), DIR.getTimes());
        assertCollectionContentsSame(Collections.emptyList(), EMPTY.getTimes());
    }

    @Test
    public void testSlice() {
        System.out.println("slice");
        Graph<Integer> slice = UNDIR.slice(0.0, true);
        assertFalse(slice.isDirected());
        assertEquals(6, slice.nodeCount());
        assertEquals(5, slice.edgeCount());
        assertEquals(2, slice.degree(2));

        slice = DIR.slice(0.0, true);
        assertTrue(slice.isDirected());
        assertEquals(6, slice.nodeCount());
        assertEquals(0, slice.edgeCount());
        assertEquals(0, slice.degree(2));

        slice = DIR.slice(19.0, false);
        assertTrue(slice.isDirected());
        assertEquals(6, slice.nodeCount());
        assertEquals(3, slice.edgeCount());
        assertEquals(1, slice.degree(2));

        assertNull(UNDIR.slice(1.5, true));
        assertNull(DIR.slice(1.0, true));
        assertNull(EMPTY.slice(0.0, true));
        assertNull(EMPTY.slice(0.0, false));
    }

    @Test
    public void testSize() {
        System.out.println("size");
        assertEquals(5, UNDIR.size());
        assertEquals(4, DIR.size());
        assertEquals(0, EMPTY.size());
    }

}
