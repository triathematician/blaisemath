/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.math;

import java.awt.geom.Point2D;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ae3263
 */
public class BasicMathUtilsTest {

    public static final double TOL = 1e-6;
    public static void assertEqualsPt(Point2D.Double p1, Point2D.Double p2, double tolerance) { assertEqualsPt(p1.x, p1.y, p2, tolerance); }
    public static void assertEqualsPt(double x1, double y1, Point2D.Double p2, double tolerance) { assertEquals(x1, p2.x, tolerance); assertEquals(y1, p2.y, tolerance); }
    public static void assertArrayEquals(double[] arr1, double[] arr2, double tolerance) { assertEquals(arr1.length, arr2.length); for (int i = 0; i < arr1.length; i++) { assertEquals(arr1[i], arr2[i], tolerance); } }


    public BasicMathUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of quadraticRoots method, of class BasicMathUtils.
     */
    @Test
    public void testQuadraticRoots() {
    }

    /**
     * Test of positiveIntervalsOfQuadratic method, of class BasicMathUtils.
     */
    @Test
    public void testPositiveIntervalsOfQuadratic() {
    }

    /**
     * Test of solveLinear method, of class BasicMathUtils.
     */
    @Test
    public void testSolveLinear() {
        assertArrayEquals(new double[]{-1,-5}, BasicMathUtils.solveLinear(new double[]{3,-1,-2}, new double[]{-1,-1,-6}), TOL);
    }

}