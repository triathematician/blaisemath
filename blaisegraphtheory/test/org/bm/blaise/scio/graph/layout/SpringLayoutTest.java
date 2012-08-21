/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.layout;

import java.awt.geom.Point2D.Double;
import java.util.Map;
import org.bm.blaise.scio.graph.Graph;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class SpringLayoutTest {

    public SpringLayoutTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testGetRepulsiveForce() {
        System.out.println("getRepulsiveForce");
        SpringLayout instance = null;
        double expResult = 0.0;
        double result = instance.getRepulsiveForce();
        assertEquals(expResult, result, 0.0);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetRepulsiveForce() {
        System.out.println("setRepulsiveForce");
        double value = 0.0;
        SpringLayout instance = null;
        instance.setRepulsiveForce(value);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetSpringForce() {
        System.out.println("getSpringForce");
        SpringLayout instance = null;
        double expResult = 0.0;
        double result = instance.getSpringForce();
        assertEquals(expResult, result, 0.0);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetSpringForce() {
        System.out.println("setSpringForce");
        double value = 0.0;
        SpringLayout instance = null;
        instance.setSpringForce(value);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetSpringLength() {
        System.out.println("getSpringLength");
        SpringLayout instance = null;
        double expResult = 0.0;
        double result = instance.getSpringLength();
        assertEquals(expResult, result, 0.0);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetSpringLength() {
        System.out.println("setSpringLength");
        double value = 0.0;
        SpringLayout instance = null;
        instance.setSpringLength(value);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetGlobalForce() {
        System.out.println("getGlobalForce");
        SpringLayout instance = null;
        double expResult = 0.0;
        double result = instance.getGlobalForce();
        assertEquals(expResult, result, 0.0);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetGlobalForce() {
        System.out.println("setGlobalForce");
        double value = 0.0;
        SpringLayout instance = null;
        instance.setGlobalForce(value);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetDampingConstant() {
        System.out.println("getDampingConstant");
        SpringLayout instance = null;
        double expResult = 0.0;
        double result = instance.getDampingConstant();
        assertEquals(expResult, result, 0.0);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetDampingConstant() {
        System.out.println("setDampingConstant");
        double value = 0.0;
        SpringLayout instance = null;
        instance.setDampingConstant(value);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetTimeStep() {
        System.out.println("getTimeStep");
        SpringLayout instance = null;
        double expResult = 0.0;
        double result = instance.getTimeStep();
        assertEquals(expResult, result, 0.0);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetTimeStep() {
        System.out.println("setTimeStep");
        double value = 0.0;
        SpringLayout instance = null;
        instance.setTimeStep(value);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetCoolingParameter() {
        System.out.println("getCoolingParameter");
        SpringLayout instance = null;
        double expResult = 0.0;
        double result = instance.getCoolingParameter();
        assertEquals(expResult, result, 0.0);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetEnergyStatus() {
        System.out.println("getEnergyStatus");
        SpringLayout instance = null;
        double expResult = 0.0;
        double result = instance.getEnergyStatus();
        assertEquals(expResult, result, 0.0);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetIteration() {
        System.out.println("getIteration");
        SpringLayout instance = null;
        int expResult = 0;
        int result = instance.getIteration();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetPositions() {
        System.out.println("getPositions");
        SpringLayout instance = null;
        Map expResult = null;
        Map result = instance.getPositions();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testReset() {
        System.out.println("reset");
        fail("The test case is a prototype.");
    }

    @Test
    public void testRequestPositions() {
        System.out.println("requestPositions");
        Map<Object, Double> positions = null;
        SpringLayout instance = null;
        instance.requestPositions(positions);
        fail("The test case is a prototype.");
    }

    @Test
    public void testIterate() {
        System.out.println("iterate");
        Graph g = null;
        SpringLayout instance = null;
        instance.iterate(g);
        fail("The test case is a prototype.");
    }

}