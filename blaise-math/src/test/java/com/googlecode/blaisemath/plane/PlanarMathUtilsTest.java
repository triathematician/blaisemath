/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.plane;

/*
 * #%L
 * BlaiseMath
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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

import java.awt.geom.Point2D.Double;
import static com.googlecode.blaisemath.plane.PlanarMathUtils.*;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ae3263
 */
public class PlanarMathUtilsTest {
    
    public static final double TOL = 1e-7;
    public static void assertEqualsPt(Double p1, Double p2, double tolerance) { assertEqualsPt(p1.x, p1.y, p2, tolerance); }
    public static void assertEqualsPt(double x1, double y1, Double p2, double tolerance) { assertEquals(x1, p2.x, tolerance); assertEquals(y1, p2.y, tolerance); }
//    public static void assertArrayEquals(double[] arr1, double[] arr2, double tolerance) { assertEquals(arr1.length, arr2.length); for (int i = 0; i < arr1.length; i++) { assertEquals(arr1[i], arr2[i], tolerance); } }


    Double[] polar = {
        new Double(1.0, 0.0),
        new Double(1.0, Math.PI / 4),
        new Double(1.0, Math.PI / 2),
        new Double(2.0, Math.PI),
        new Double(4.0, -Math.PI / 2)};
    Double[] inputs = {
        new Double(1.0, 0.0),
        new Double(0.707106781, 0.707106781),
        new Double(0.0, 1.0),
        new Double(-2.0, 0.0),
        new Double(0.0, -4.0)};
    double[] angles = {
        0.0,
        Math.PI / 4,
        Math.PI / 2,
        Math.PI,
        -Math.PI / 2};
    double[] norms = {
        1.0,
        1.0,
        1.0,
        2.0,
        4.0};
    Double[] normed = {
        new Double(1.0, 0.0),
        new Double(0.707106781, 0.707106781),
        new Double(0.0, 1.0),
        new Double(-1.0, 0.0),
        new Double(0.0, -1.0)};

    public PlanarMathUtilsTest() { }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testToCartesianFromPolar() {
        System.out.println("toCartesianFromPolar");
        for (int i = 0; i < polar.length; i++) {
            assertEqualsPt(inputs[i], toCartesianFromPolar(polar[i].x, polar[i].y), TOL);
        }
    }

    @Test
    public void testToPolarFromCartesian() {
        System.out.println("toPolarFromCartesian");
        for (int i = 0; i < polar.length; i++) {
            assertEqualsPt(polar[i], toPolarFromCartesian(inputs[i]), TOL);
        }
    }

    @Test
    public void testNormalize() {
        System.out.println("normalize");
        for (int i = 0; i < inputs.length; i++) {
            assertEqualsPt(normed[i], normalize(inputs[i]), TOL);
        }
    }

    @Test
    public void testRotate() {
        System.out.println("rotate");
        assertEqualsPt(new Double(0,1), rotate(new Double(1,0), Math.PI/2), TOL);
        assertEqualsPt(new Double(0,1), rotate(new Double(0,0), new Double(1,0), Math.PI/2), TOL);
        assertEqualsPt(new Double(1,-2.41421356), rotate(new Double(1,-1), new Double(2,0), -3*Math.PI/4), TOL);
    }

    @Test
    public void testAngle() {
        System.out.println("angle");
        for (int i = 0; i < inputs.length; i++) {
            assertEquals(angles[i], angle(inputs[i]), TOL);
            double angle2 = positiveAngle(inputs[i].x, inputs[i].y);
            assertTrue(angle2 >= 0);
            assertTrue(angle2 < 2*Math.PI);
        }
    }

    @Test
    public void testDotProduct() {
        System.out.println("dotProduct");
        assertEquals(11.0, dotProduct(new Double(1, 2), new Double(3, 4)), TOL);
        assertEquals(0.0, dotProduct(new Double(1, 2), new Double(0, 0)), TOL);
        assertEquals(-25.0, dotProduct(new Double(1, 7), new Double(3, -4)), TOL);
    }

    @Test
    public void testCrossProduct() {
        System.out.println("crossProduct");
        assertEquals(-2.0, crossProduct(new Double(1, 2), new Double(3, 4)), TOL);
        assertEquals(0.0, crossProduct(new Double(1, 2), new Double(0, 0)), TOL);
        assertEquals(0.0, crossProduct(new Double(1, 2), new Double(3, 6)), TOL);
        assertEquals(-25.0, crossProduct(new Double(1, 7), new Double(3, -4)), TOL);

        assertEquals(0.0, crossProductOf3Points(new Double(1, 2), new Double(3, 4), new Double(5, 6)), TOL);
        assertEquals(4.0, crossProductOf3Points(new Double(1, 2), new Double(0, 0), new Double(5, 6)), TOL);
        assertEquals(42.0, crossProductOf3Points(new Double(1, 7), new Double(3, -4), new Double(5, 6)), TOL);
    }



    //==================================================================================================
    //
    // 5. PROJECTION FORMULAS
    //

    @Test
    public void testVectorProjection() {
        System.out.println("projectVector");
        assertEqualsPt(new Double(0.5, 0.5), vectorProjection(new Double(0,1), new Double(5,5)), TOL);
        assertEqualsPt(new Double(-1.5, -1.5), vectorProjection(new Double(-2, -1), new Double(5,5)), TOL);
        assertEqualsPt(new Double(0, 5), vectorProjection(new Double(5, 5), new Double(0, 1)), TOL);
    }

    @Test
    public void testPerpendicularVectorProjection() {
        System.out.println("perpProjectVector");
        assertEqualsPt(new Double(-0.5, 0.5), perpendicularVectorProjection(new Double(0,1), new Double(5,5)), TOL);
        assertEqualsPt(new Double(0.5, -0.5), perpendicularVectorProjection(new Double(-1, -2), new Double(5,5)), TOL);
        assertEqualsPt(new Double(5, 0), perpendicularVectorProjection(new Double(5, 5), new Double(0, 1)), TOL);
    }

    
    
    //==================================================================================================
    //
    // 6. DISTANCE FORMULAS
    //

    Double end1 = new Double(-1,0);
    Double end2 = new Double(3,2);

    @Test
    public void testClosestPointOnLine() {
        System.out.println("closestOnLine");

        assertEqualsPt(end1, closestPointOnLine(end1, end1, end2), TOL);
        assertEqualsPt(new Double(3.8,2.4), closestPointOnLine(new Double(3,4), end1, end2), TOL);
        assertEqualsPt(new Double(3.8,2.4), closestPointOnLine(new Double(5,0), end1, end2), TOL);
        assertEqualsPt(new Double(7.8,4.4), closestPointOnLine(new Double(10,0), end1, end2), TOL);
        assertEqualsPt(new Double(-4.2,-1.6), closestPointOnLine(new Double(-5,0), end1, end2), TOL);
        assertEqualsPt(new Double(-1,0), closestPointOnLine(new Double(-2,2), end1, end2), TOL);
        assertEqualsPt(new Double(1,1), closestPointOnLine(new Double(-1,5), end1, end2), TOL);

        assertEqualsPt(end1, closestPointOnLine(new Double(3,4), end1, end1), TOL);
    }

    @Test
    public void testClosesPointtOnSegment() {
        System.out.println("closestOnSegment");

        assertEqualsPt(end1, closestPointOnSegment(end1, end1, end2), TOL);
        assertEqualsPt(end2, closestPointOnSegment(new Double(3,4), end1, end2), TOL);
        assertEqualsPt(end2, closestPointOnSegment(new Double(5,0), end1, end2), TOL);
        assertEqualsPt(end2, closestPointOnSegment(new Double(10,0), end1, end2), TOL);
        assertEqualsPt(end1, closestPointOnSegment(new Double(-5,0), end1, end2), TOL);
        assertEqualsPt(end1, closestPointOnSegment(new Double(-2,2), end1, end2), TOL);
        assertEqualsPt(new Double(1,1), closestPointOnSegment(new Double(-1,5), end1, end2), TOL);

        assertEqualsPt(end1, closestPointOnSegment(new Double(3,4), end1, end1), TOL);
    }

    @Test
    public void testClosestPointOnRay() {
        System.out.println("closestOnRay");

        assertEqualsPt(end1, closestPointOnRay(end1, end1, end2), TOL);
        assertEqualsPt(new Double(3.8,2.4), closestPointOnRay(new Double(3,4), end1, end2), TOL);
        assertEqualsPt(new Double(3.8,2.4), closestPointOnRay(new Double(5,0), end1, end2), TOL);
        assertEqualsPt(new Double(7.8,4.4), closestPointOnRay(new Double(10,0), end1, end2), TOL);
        assertEqualsPt(end1, closestPointOnRay(new Double(-5,0), end1, end2), TOL);
        assertEqualsPt(end1, closestPointOnRay(new Double(-2,2), end1, end2), TOL);
        assertEqualsPt(new Double(1,1), closestPointOnRay(new Double(-1,5), end1, end2), TOL);

        assertEqualsPt(end1, closestPointOnRay(new Double(3,4), end1, end1), TOL);
    }
}
