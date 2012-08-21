/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.math.plane;

import java.awt.geom.Point2D.Double;
import static org.blaise.math.plane.PlanarGeometryUtils.*;
import static org.blaise.math.plane.PlanarMathUtilsTest.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author ae3263
 */
public class PlanarGeometryUtilsTest {

    public PlanarGeometryUtilsTest() {}

    /**
     * Test of verticalLineAt method, of class PlanarGeometryUtils.
     */
    @Test
    public void testVerticalLineAt() {
        System.out.println("verticalLineAt");
        assertArrayEquals(new double[]{java.lang.Double.POSITIVE_INFINITY, 1.0}, verticalLineAt(1.0), TOL);
    }

    /**
     * Test of slope method, of class PlanarGeometryUtils.
     */
    @Test
    public void testSlope() {
        System.out.println("slope");
        assertEquals(1.0, slopeOfLineBetween(new Double(1, 2), new Double(3, 4)), TOL);
        assertEquals(2.0, slopeOfLineBetween(new Double(1, 2), new Double(0, 0)), TOL);
        assertEquals(-1.5, slopeOfLineBetween(new Double(1, 7), new Double(3, 4)), TOL);
    }

    /**
     * Test of lineThrough method, of class PlanarGeometryUtils.
     */
    @Test
    public void testLineThrough() {
        System.out.println("lineThrough");
        assertArrayEquals(new double[]{1,1}, lineThrough(new Double(0,1), new Double(-1,0)), TOL);
        assertArrayEquals(new double[]{0,1}, lineThrough(new Double(0,1), new Double(1,1)), TOL);
        assertArrayEquals(new double[]{-1,1}, lineThrough(new Double(0,1), new Double(1,0)), TOL);
        assertArrayEquals(verticalLineAt(0), lineThrough(new Double(0,0), new Double(0,1)), TOL);
        assertArrayEquals(NO_LINE, lineThrough(new Double(0,0), new Double(0,0)), TOL);
    }


    /**
     * Test of intersectionOfLines method, of class PlanarGeometryUtils.
     */
    @Test
    public void testIntersectionOfLines() {
        System.out.println("intersectionOfLines");
        assertEqualsPt(1., 1., intersectionOfLines(new double[]{0, 1}, new double[]{java.lang.Double.POSITIVE_INFINITY, 1}), TOL);
        assertEqualsPt(.5, .5, intersectionOfLines(new double[]{1, 0}, new double[]{-1, 1}), TOL);
    }


    /**
     * Test of lineEquidistantFrom method, of class PlanarGeometryUtils.
     */
    @Test
    public void testLineOfPointsEquidistantTo() {
        System.out.println("lineEquidistantFrom");
        assertArrayEquals(new double[]{java.lang.Double.POSITIVE_INFINITY, .5}, lineEquidistantTo(new Double(0,0), new Double(1,0)), TOL);
        assertArrayEquals(new double[]{0, .5}, lineEquidistantTo(new Double(0,0), new Double(0,1)), TOL);
        assertArrayEquals(new double[]{-1, 1}, lineEquidistantTo(new Double(0,0), new Double(1,1)), TOL);
        assertArrayEquals(new double[]{java.lang.Double.NaN, java.lang.Double.NaN}, lineEquidistantTo(new Double(0,0), new Double(0,0)), TOL);
    }

    /**
     * Test of pointEquidistantTo method, of class PlanarGeometryUtils.
     */
    @Test
    public void testPointEquidistantTo_3Points() {
        System.out.println("pointEquidistantTo");
        Double[] inputs = {
            new Double(1.0, 0.0),
            new Double(0.707106781, 0.707106781),
            new Double(0.0, 1.0),
            new Double(-2.0, 0.0),
            new Double(0.0, -4.0)
        };
        Double[] normed = {
            new Double(1.0, 0.0),
            new Double(0.707106781, 0.707106781),
            new Double(0.0, 1.0),
            new Double(-1.0, 0.0),
            new Double(0.0, -1.0)        
        };
        
        assertEqualsPt(new Double(),  pointEquidistantTo(normed[0], normed[2], normed[3]), TOL);
        assertEqualsPt(new Double(),  pointEquidistantTo(inputs[0], inputs[1], inputs[2]), TOL);
        assertEqualsPt(new Double(0, -1.5),  pointEquidistantTo(inputs[2], inputs[3], inputs[4]), TOL);
        assertEqualsPt(new Double(0, -1.5),  pointEquidistantTo(inputs[2], inputs[4], inputs[3]), TOL);
        assertEqualsPt(new Double(0, -1.5),  pointEquidistantTo(inputs[3], inputs[2], inputs[4]), TOL);
        assertEqualsPt(new Double(-0.14354413889357145, -1.5717720694467858),  pointEquidistantTo(inputs[1], inputs[3], inputs[4]), TOL);

        assertEqualsPt(new Double(.5,.5), pointEquidistantTo(new Double(0,0), new Double(1,0), new Double(0,1)), TOL);
        assertEqualsPt(new Double(.5,.5), pointEquidistantTo(new Double(0,0), new Double(0,1), new Double(1,0)), TOL);
        assertEqualsPt(new Double(.5,.5), pointEquidistantTo(new Double(1,0), new Double(0,0), new Double(0,1)), TOL);
        assertEqualsPt(new Double(.5,.5), pointEquidistantTo(new Double(0,1), new Double(1,0), new Double(0,0)), TOL);
        assertEqualsPt(new Double(.5,.5), pointEquidistantTo(new Double(1,0), new Double(0,1), new Double(0,0)), TOL);
        assertEqualsPt(new Double(.5,.5), pointEquidistantTo(new Double(0,1), new Double(0,0), new Double(1,0)), TOL);

        // equal coords
        assertEqualsPt(PlanarMathUtils.NO_POINT, pointEquidistantTo(inputs[0], inputs[0], inputs[3]), TOL);
        assertEqualsPt(PlanarMathUtils.NO_POINT, pointEquidistantTo(inputs[0], inputs[0], inputs[0]), TOL);
    }

    /**
     * Test of pointEquidistantTo method, of class PlanarGeometryUtils.
     */
    @Test
    public void testPointEquidistantTo_2Points_1Line() {
        System.out.println("pointEquidistantTo");
        Double[] result = pointsEquidistantTo(new Double(0,0), new Double(0,2), new double[]{java.lang.Double.POSITIVE_INFINITY, 1+Math.sqrt(2.)});
        assertEqualsPt(1,1, result[0], TOL);
        assertEquals(1, result.length);
        result = pointsEquidistantTo(new Double(0,0), new Double(1,1), new double[]{java.lang.Double.POSITIVE_INFINITY, 2});
        assertEqualsPt(1,0, result[0], TOL);
        assertEqualsPt(-3,4, result[1], TOL);
    }



}