/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph;

import java.util.List;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class FilteredWeightedGraphTest {

    public FilteredWeightedGraphTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetThreshold() {
        System.out.println("getThreshold");
        FilteredWeightedGraph instance = null;
        double expResult = 0.0;
        double result = instance.getThreshold();
        assertEquals(expResult, result, 0.0);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetThreshold() {
        System.out.println("setThreshold");
        double value = 0.0;
        FilteredWeightedGraph instance = null;
        instance.setThreshold(value);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetBaseGraph() {
        System.out.println("getBaseGraph");
        FilteredWeightedGraph instance = null;
        WeightedGraph expResult = null;
        WeightedGraph result = instance.getBaseGraph();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testIsDirected() {
        System.out.println("isDirected");
        FilteredWeightedGraph instance = null;
        boolean expResult = false;
        boolean result = instance.isDirected();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testOrder() {
        System.out.println("order");
        FilteredWeightedGraph instance = null;
        int expResult = 0;
        int result = instance.order();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testNodes() {
        System.out.println("nodes");
        FilteredWeightedGraph instance = null;
        List expResult = null;
        List result = instance.nodes();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testContains() {
        System.out.println("contains");
        Object x = null;
        FilteredWeightedGraph instance = null;
        boolean expResult = false;
        boolean result = instance.contains(x);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testAdjacent() {
        System.out.println("adjacent");
        Object x = null;
        Object y = null;
        FilteredWeightedGraph instance = null;
        boolean expResult = false;
        boolean result = instance.adjacent(x, y);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testDegree() {
        System.out.println("degree");
        Object x = null;
        FilteredWeightedGraph instance = null;
        int expResult = 0;
        int result = instance.degree(x);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testNeighbors() {
        System.out.println("neighbors");
        Object x = null;
        FilteredWeightedGraph instance = null;
        Set expResult = null;
        Set result = instance.neighbors(x);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testEdgeNumber() {
        System.out.println("edgeNumber");
        FilteredWeightedGraph instance = null;
        int expResult = 0;
        int result = instance.edgeCount();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

}