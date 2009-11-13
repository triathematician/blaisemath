/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scio.coordinate.utils;

import scio.coordinate.utils.PlanarMathUtils;
import java.awt.geom.Point2D;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ae3263
 */
public class PlanarMathUtilsTest {
    
    private static final double TOL = 1e-6;



    Point2D.Double[] polar = {
        new Point2D.Double(1.0, 0.0),
        new Point2D.Double(1.0, Math.PI / 4),
        new Point2D.Double(1.0, Math.PI / 2),
        new Point2D.Double(2.0, Math.PI),
        new Point2D.Double(4.0, -Math.PI / 2)};
    Point2D.Double[] inputs = {
        new Point2D.Double(1.0, 0.0),
        new Point2D.Double(0.707106781, 0.707106781),
        new Point2D.Double(0.0, 1.0),
        new Point2D.Double(-2.0, 0.0),
        new Point2D.Double(0.0, -4.0)};
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
    Point2D.Double[] normed = {
        new Point2D.Double(1.0, 0.0),
        new Point2D.Double(0.707106781, 0.707106781),
        new Point2D.Double(0.0, 1.0),
        new Point2D.Double(-1.0, 0.0),
        new Point2D.Double(0.0, -1.0)};

    static void assertEqualsPt(Point2D.Double p1, Point2D.Double p2, double tolerance) {
        assertEquals(p1.x, p2.x, tolerance);
        assertEquals(p1.y, p2.y, tolerance);
    }

    public PlanarMathUtilsTest() {
    }

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
            assertEqualsPt(inputs[i], PlanarMathUtils.toCartesianFromPolar(polar[i].x, polar[i].y), TOL);
            assertEqualsPt(inputs[i], PlanarMathUtils.toCartesianFromPolar(polar[i]), TOL);
        }
    }

    @Test
    public void testToPolarFromCartesian() {
        System.out.println("toPolarFromCartesian");
        for (int i = 0; i < polar.length; i++) {
            assertEqualsPt(polar[i], PlanarMathUtils.toPolarFromCartesian(inputs[i]), TOL);
        }
    }

    @Test
    public void testGetNearInfinitePointAtAngle() {
        System.out.println("getNearInfinitePointAtAngle");
        for (int i = 0; i < angles.length; i++) {
            assertEquals(PlanarMathUtils.INFINITE_RADIUS, PlanarMathUtils.getNearInfinitePointAtAngle(angles[i]).distance(0,0), TOL);
            assertEquals(angles[i], PlanarMathUtils.angle(PlanarMathUtils.getNearInfinitePointAtAngle(angles[i])), TOL);
        }
    }

    @Test
    public void testNormalize() {
        System.out.println("normalize");
        for (int i = 0; i < inputs.length; i++) {
            assertEqualsPt(normed[i], PlanarMathUtils.normalize(inputs[i]), TOL);
        }
    }

    @Test
    public void testRotate() {
        System.out.println("rotate");
        assertEqualsPt(new Point2D.Double(0,1), PlanarMathUtils.rotate(new Point2D.Double(1,0), Math.PI/2), TOL);
        assertEqualsPt(new Point2D.Double(0,1), PlanarMathUtils.rotate(new Point2D.Double(0,0), new Point2D.Double(1,0), Math.PI/2), TOL);
        assertEqualsPt(new Point2D.Double(1,-2.41421356), PlanarMathUtils.rotate(new Point2D.Double(1,-1), new Point2D.Double(2,0), -3*Math.PI/4), TOL);
    }

    @Test
    public void testAngle() {
        System.out.println("angle");
        for (int i = 0; i < inputs.length; i++) {
            assertEquals(angles[i], PlanarMathUtils.angle(inputs[i]), TOL);
            assertEquals(angles[i], PlanarMathUtils.angle(inputs[i].x, inputs[i].y), TOL);
        }
    }

    @Test
    public void testSlope() {
        System.out.println("slope");
        assertEquals(1.0, PlanarMathUtils.slope(new Point2D.Double(1, 2), new Point2D.Double(3, 4)), TOL);
        assertEquals(2.0, PlanarMathUtils.slope(new Point2D.Double(1, 2), new Point2D.Double(0, 0)), TOL);
        assertEquals(-1.5, PlanarMathUtils.slope(new Point2D.Double(1, 7), new Point2D.Double(3, 4)), TOL);
    }

    @Test
    public void testDotProduct() {
        System.out.println("dotProduct");
        assertEquals(11.0, PlanarMathUtils.dotProduct(new Point2D.Double(1, 2), new Point2D.Double(3, 4)), TOL);
        assertEquals(0.0, PlanarMathUtils.dotProduct(new Point2D.Double(1, 2), new Point2D.Double(0, 0)), TOL);
        assertEquals(-25.0, PlanarMathUtils.dotProduct(new Point2D.Double(1, 7), new Point2D.Double(3, -4)), TOL);
    }

    @Test
    public void testCrossProduct() {
        System.out.println("crossProduct");
        assertEquals(-2.0, PlanarMathUtils.crossProduct(new Point2D.Double(1, 2), new Point2D.Double(3, 4)), TOL);
        assertEquals(0.0, PlanarMathUtils.crossProduct(new Point2D.Double(1, 2), new Point2D.Double(0, 0)), TOL);
        assertEquals(0.0, PlanarMathUtils.crossProduct(new Point2D.Double(1, 2), new Point2D.Double(3, 6)), TOL);
        assertEquals(-25.0, PlanarMathUtils.crossProduct(new Point2D.Double(1, 7), new Point2D.Double(3, -4)), TOL);

        assertEquals(0.0, PlanarMathUtils.crossProduct(new Point2D.Double(1, 2), new Point2D.Double(3, 4), new Point2D.Double(5, 6)), TOL);
        assertEquals(4.0, PlanarMathUtils.crossProduct(new Point2D.Double(1, 2), new Point2D.Double(0, 0), new Point2D.Double(5, 6)), TOL);
        assertEquals(42.0, PlanarMathUtils.crossProduct(new Point2D.Double(1, 7), new Point2D.Double(3, -4), new Point2D.Double(5, 6)), TOL);
    }

    @Test
    public void testProjectVector() {
        System.out.println("projectVector");
        assertEqualsPt(new Point2D.Double(0.5, 0.5), PlanarMathUtils.projectVector(new Point2D.Double(0,1), new Point2D.Double(5,5)), TOL);
        assertEqualsPt(new Point2D.Double(-1.5, -1.5), PlanarMathUtils.projectVector(new Point2D.Double(-2, -1), new Point2D.Double(5,5)), TOL);
        assertEqualsPt(new Point2D.Double(0, 5), PlanarMathUtils.projectVector(new Point2D.Double(5, 5), new Point2D.Double(0, 1)), TOL);
    }

    @Test
    public void testPerpProjectVector() {
        System.out.println("perpProjectVector");
        assertEqualsPt(new Point2D.Double(-0.5, 0.5), PlanarMathUtils.perpProjectVector(new Point2D.Double(0,1), new Point2D.Double(5,5)), TOL);
        assertEqualsPt(new Point2D.Double(0.5, -0.5), PlanarMathUtils.perpProjectVector(new Point2D.Double(-1, -2), new Point2D.Double(5,5)), TOL);
        assertEqualsPt(new Point2D.Double(5, 0), PlanarMathUtils.perpProjectVector(new Point2D.Double(5, 5), new Point2D.Double(0, 1)), TOL);
    }

    @Test
    public void testPointAtInfinityEquidistantTo() {
        System.out.println("pointAtInfinityEquidistantTo");
        assertEquals(9*Math.PI/8, PlanarMathUtils.angle(PlanarMathUtils.pointAtInfinityEquidistantTo(inputs[0], inputs[1])), TOL);
        assertEquals(11*Math.PI/8, PlanarMathUtils.angle(PlanarMathUtils.pointAtInfinityEquidistantTo(inputs[1], inputs[2])), TOL);
        assertEquals(Math.PI + Math.atan(.5), PlanarMathUtils.angle(PlanarMathUtils.pointAtInfinityEquidistantTo(inputs[4], inputs[3])), TOL);
        assertEquals(Math.PI/2 + Math.atan(.5), PlanarMathUtils.angle(PlanarMathUtils.pointAtInfinityEquidistantTo(inputs[3], inputs[2])), TOL);
        
        assertEquals(PlanarMathUtils.INFINITE_RADIUS, PlanarMathUtils.pointAtInfinityEquidistantTo(inputs[0], inputs[1]).distance(0,0), TOL);
    }

    @Test
    public void testCenterOfCircleThrough() {
        System.out.println("centerOfCircleThrough");
        assertEqualsPt(new Point2D.Double(),  PlanarMathUtils.centerOfCircleThrough(normed[0], normed[2], normed[3]), TOL);
        assertEqualsPt(new Point2D.Double(),  PlanarMathUtils.centerOfCircleThrough(inputs[0], inputs[1], inputs[2]), TOL);
        assertEqualsPt(new Point2D.Double(0, -1.5),  PlanarMathUtils.centerOfCircleThrough(inputs[2], inputs[3], inputs[4]), TOL);
        assertEqualsPt(new Point2D.Double(0, -1.5),  PlanarMathUtils.centerOfCircleThrough(inputs[2], inputs[4], inputs[3]), TOL);
        assertEqualsPt(new Point2D.Double(0, -1.5),  PlanarMathUtils.centerOfCircleThrough(inputs[3], inputs[2], inputs[4]), TOL);
        assertEqualsPt(new Point2D.Double(-0.14354413889357145, -1.5717720694467858),  PlanarMathUtils.centerOfCircleThrough(inputs[1], inputs[3], inputs[4]), TOL);

        // equal coords
        assertEquals(3*Math.PI/2, PlanarMathUtils.angle(PlanarMathUtils.centerOfCircleThrough(inputs[0], inputs[0], inputs[3])), TOL);
        assertEquals(3*Math.PI/2,  PlanarMathUtils.angle(PlanarMathUtils.centerOfCircleThrough(inputs[0], inputs[3], inputs[3])), TOL);
        assertEquals(Math.PI/2,  PlanarMathUtils.angle(PlanarMathUtils.centerOfCircleThrough(inputs[3], inputs[0], inputs[3])), TOL);
        assertEqualsPt(PlanarMathUtils.INFINITY,  PlanarMathUtils.centerOfCircleThrough(inputs[0], inputs[0], inputs[0]), TOL);
    }

    Point2D.Double end1 = new Point2D.Double(-1,0);
    Point2D.Double end2 = new Point2D.Double(3,2);

    @Test
    public void testClosestOnLine() {
        System.out.println("closestOnLine");

        assertEqualsPt(end1, PlanarMathUtils.closestOnLine(end1, end1, end2), TOL);
        assertEqualsPt(new Point2D.Double(3.8,2.4), PlanarMathUtils.closestOnLine(new Point2D.Double(3,4), end1, end2), TOL);
        assertEqualsPt(new Point2D.Double(3.8,2.4), PlanarMathUtils.closestOnLine(new Point2D.Double(5,0), end1, end2), TOL);
        assertEqualsPt(new Point2D.Double(7.8,4.4), PlanarMathUtils.closestOnLine(new Point2D.Double(10,0), end1, end2), TOL);
        assertEqualsPt(new Point2D.Double(-4.2,-1.6), PlanarMathUtils.closestOnLine(new Point2D.Double(-5,0), end1, end2), TOL);
        assertEqualsPt(new Point2D.Double(-1,0), PlanarMathUtils.closestOnLine(new Point2D.Double(-2,2), end1, end2), TOL);
        assertEqualsPt(new Point2D.Double(1,1), PlanarMathUtils.closestOnLine(new Point2D.Double(-1,5), end1, end2), TOL);

        assertEqualsPt(end1, PlanarMathUtils.closestOnLine(new Point2D.Double(3,4), end1, end1), TOL);
    }

    @Test
    public void testClosestOnSegment() {
        System.out.println("closestOnSegment");

        assertEqualsPt(end1, PlanarMathUtils.closestOnSegment(end1, end1, end2), TOL);
        assertEqualsPt(end2, PlanarMathUtils.closestOnSegment(new Point2D.Double(3,4), end1, end2), TOL);
        assertEqualsPt(end2, PlanarMathUtils.closestOnSegment(new Point2D.Double(5,0), end1, end2), TOL);
        assertEqualsPt(end2, PlanarMathUtils.closestOnSegment(new Point2D.Double(10,0), end1, end2), TOL);
        assertEqualsPt(end1, PlanarMathUtils.closestOnSegment(new Point2D.Double(-5,0), end1, end2), TOL);
        assertEqualsPt(end1, PlanarMathUtils.closestOnSegment(new Point2D.Double(-2,2), end1, end2), TOL);
        assertEqualsPt(new Point2D.Double(1,1), PlanarMathUtils.closestOnSegment(new Point2D.Double(-1,5), end1, end2), TOL);

        assertEqualsPt(end1, PlanarMathUtils.closestOnSegment(new Point2D.Double(3,4), end1, end1), TOL);
    }

    @Test
    public void testClosestOnRay() {
        System.out.println("closestOnRay");

        assertEqualsPt(end1, PlanarMathUtils.closestOnRay(end1, end1, end2), TOL);
        assertEqualsPt(new Point2D.Double(3.8,2.4), PlanarMathUtils.closestOnRay(new Point2D.Double(3,4), end1, end2), TOL);
        assertEqualsPt(new Point2D.Double(3.8,2.4), PlanarMathUtils.closestOnRay(new Point2D.Double(5,0), end1, end2), TOL);
        assertEqualsPt(new Point2D.Double(7.8,4.4), PlanarMathUtils.closestOnRay(new Point2D.Double(10,0), end1, end2), TOL);
        assertEqualsPt(end1, PlanarMathUtils.closestOnRay(new Point2D.Double(-5,0), end1, end2), TOL);
        assertEqualsPt(end1, PlanarMathUtils.closestOnRay(new Point2D.Double(-2,2), end1, end2), TOL);
        assertEqualsPt(new Point2D.Double(1,1), PlanarMathUtils.closestOnRay(new Point2D.Double(-1,5), end1, end2), TOL);

        assertEqualsPt(end1, PlanarMathUtils.closestOnRay(new Point2D.Double(3,4), end1, end1), TOL);
    }
}