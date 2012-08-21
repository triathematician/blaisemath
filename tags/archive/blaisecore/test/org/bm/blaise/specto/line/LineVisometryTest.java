/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.specto.line;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ae3263
 */
public class LineVisometryTest {

    public LineVisometryTest() {
    }

    static LineVisometry instance1;
    static LineVisometry instance2;

    @BeforeClass
    public static void setUpClass() throws Exception {
        instance1 = new LineVisometry();
        instance1.setWindowBounds(new Rectangle2D.Double(25, 25, 100, 50));
        instance1.setDesiredRange(0, 1);
        instance1.computeTransformation();

        instance2 = new LineVisometry();
        instance2.setWindowBounds(new Rectangle2D.Double(0, 0, 50, 100));
        instance2.setDesiredRange(-5, 5);
        instance2.computeTransformation();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testGetWindowBounds() {
        System.out.println("getWindowBounds");
        assertEquals("java.awt.geom.Rectangle2D$Double[x=25.0,y=25.0,w=100.0,h=50.0]", instance1.getWindowBounds().toString());
    }

    @Test
    public void testGetMinPointVisible() {
        System.out.println("getMinPointVisible");
        assertEquals("0.0", instance1.getMinPointVisible().toString());
    }

    @Test
    public void testGetMaxPointVisible() {
        System.out.println("getMaxPointVisible");
        assertEquals("1.0", instance1.getMaxPointVisible().toString());
    }

    @Test
    public void testSetDesiredRange() {
        System.out.println("setDesiredRange");
        try { new LineVisometry().setDesiredRange(0, 0); fail("Should be impossible!");
        } catch (IllegalArgumentException e) { }
        new LineVisometry().setDesiredRange(0, 10);
        try {
            LineVisometry testInstance = new LineVisometry();
            testInstance.setWindowBounds(new Rectangle2D.Double(0, 0, 100, 50));
            testInstance.setDesiredRange(0, 1);
            assertEquals("AffineTransform[[80.0, 0.0, 10.0], [0.0, 1.0, 25.0]]", testInstance.at.toString());
        } catch (Exception e) {
            fail("Error setting desired range!");
        }
    }

    @Test
    public void testSetWindowBounds() {
        System.out.println("setWindowBounds");
        RectangularShape old = instance1.getWindowBounds();
        instance1.setWindowBounds(new Rectangle2D.Double(0,0,50,50));
        assertEquals("java.awt.geom.Rectangle2D$Double[x=0.0,y=0.0,w=50.0,h=50.0]", instance1.getWindowBounds().toString());
        assertEquals("AffineTransform[[30.0, 0.0, 10.0], [0.0, 1.0, 25.0]]", instance1.at.toString());
        instance1.setWindowBounds(old);
        assertEquals("AffineTransform[[80.0, 0.0, 35.0], [0.0, 1.0, 50.0]]", instance1.at.toString());
    }

    @Test
    public void testComputeTransformation() {
        System.out.println("computeTransformation");
        try {
            new LineVisometry().computeTransformation();
            fail("Shouldn't compute!");
        } catch (IllegalStateException e) {
        }

        assertEquals("AffineTransform[[80.0, 0.0, 35.0], [0.0, 1.0, 50.0]]", instance1.at.toString());
        assertEquals("AffineTransform[[3.0, 0.0, 25.0], [0.0, 1.0, 50.0]]", instance2.at.toString());
    }

    @Test
    public void testGetWindowPointOf() {
        System.out.println("getWindowPointOf");
        assertEquals(new Point2D.Double(35.0, 50.0), instance1.getWindowPointOf(0.0));
        assertEquals(new Point2D.Double(115.0, 50.0), instance1.getWindowPointOf(1.0));
        assertEquals(new Point2D.Double(-5.0, 50.0), instance1.getWindowPointOf(-.5));
    }

    @Test
    public void testGetCoordinateOf() {
        System.out.println("getCoordinateOf");
        assertEquals(-0.5, instance1.getCoordinateOf(new Point2D.Double(-5.0, 0)), 0.001);
        assertEquals(0.8125, instance1.getCoordinateOf(new Point2D.Double(100.0, 50.0)), 0.001);
        assertEquals(0.1, instance1.getCoordinateOf(new Point2D.Double(43.0, 50.0)), 0.001);
    }
}