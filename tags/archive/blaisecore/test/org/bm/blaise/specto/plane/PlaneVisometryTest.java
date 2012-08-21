/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bm.blaise.specto.plane;

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
public class PlaneVisometryTest {

    public PlaneVisometryTest() {
    }
    static PlaneVisometry instance;
    static PlaneVisometry instance2;

    @BeforeClass
    public static void setUpClass() throws Exception {
        instance = new PlaneVisometry();
        instance.setWindowBounds(new Rectangle2D.Double(25, 25, 100, 50));
        instance.setDesiredRange(0, 0, 1, 1);
        instance.setAspectRatio(1.0);
        instance.computeTransformation();

        instance2 = new PlaneVisometry();
        instance2.setWindowBounds(new Rectangle2D.Double(0, 0, 50, 100));
        instance2.setDesiredRange(-1, -1, 1, 1);
        instance2.setAspectRatio(1.0);
        instance2.computeTransformation();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testGetAspectRatio() {
        System.out.println("getAspectRatio");
        assertEquals(1.0, instance.getAspectRatio(), 1E-10);
    }

    @Test
    public void testGetDisplayedRange() {
        System.out.println("getDisplayedRange");
        assertEquals("java.awt.geom.Rectangle2D$Double[x=-0.5,y=0.0,w=2.0,h=1.0]", instance.getVisibleRange().toString());
    }

    @Test
    public void testGetWindowBounds() {
        System.out.println("getWindowBounds");
        assertEquals("java.awt.geom.Rectangle2D$Double[x=25.0,y=25.0,w=100.0,h=50.0]", instance.getWindowBounds().toString());
    }

    @Test
    public void testSetAspectRatio() {
        System.out.println("setAspectRatio");
        double prior = instance.getAspectRatio();
        try {
            new PlaneVisometry().setAspectRatio(0.0);
            fail("Should be impossible!");
        } catch (IllegalArgumentException e) {
        }
        try {
            new PlaneVisometry().setAspectRatio(-2.0);
            fail("Should be impossible!");
        } catch (IllegalArgumentException e) {
        }
        try {
            instance.setAspectRatio(2.0);
            assertEquals("java.awt.geom.Rectangle2D$Double[x=0.0,y=0.0,w=1.0,h=1.0]", instance.getVisibleRange().toString());
            instance.setAspectRatio(0.5);
            assertEquals("java.awt.geom.Rectangle2D$Double[x=-1.5,y=0.0,w=4.0,h=1.0]", instance.getVisibleRange().toString());
            instance.setAspectRatio(prior);
        } catch (Exception e) {
            fail("Error setting aspect!");
        }
    }

    @Test
    public void testSetDesiredRange() {
        System.out.println("setDesiredRange");
        try { new PlaneVisometry().setDesiredRange(0, 0, 0, 0); fail("Should be impossible!");
        } catch (IllegalArgumentException e) { }
        new PlaneVisometry().setDesiredRange(0, 0, 5, 10);
        try {
            PlaneVisometry testInstance = new PlaneVisometry();
            testInstance.setWindowBounds(new Rectangle2D.Double(0, 0, 100, 50));
            testInstance.setDesiredRange(0, 0, 1, 1);
            assertEquals("AffineTransform[[50.0, 0.0, 25.0], [0.0, -50.0, 50.0]]", testInstance.at.toString());
        } catch (Exception e) {
            fail("Error setting desired range!");
        }
    }

    @Test
    public void testSetWindowBounds() {
        System.out.println("setWindowBounds");
        RectangularShape old = instance.getWindowBounds();
        instance.setWindowBounds(new Rectangle2D.Double(0,0,50,50));
        assertEquals("java.awt.geom.Rectangle2D$Double[x=0.0,y=0.0,w=50.0,h=50.0]", instance.getWindowBounds().toString());
        assertEquals("AffineTransform[[50.0, 0.0, 0.0], [0.0, -50.0, 50.0]]", instance.at.toString());
        instance.setWindowBounds(old);
        assertEquals("AffineTransform[[50.0, 0.0, 50.0], [0.0, -50.0, 75.0]]", instance.at.toString());
    }

    @Test
    public void testComputeTransformation() {
        System.out.println("computeTransformation");
        try {
            new PlaneVisometry().computeTransformation();
            fail("Shouldn't compute!");
        } catch (IllegalStateException e) {
        }

        assertEquals("AffineTransform[[50.0, 0.0, 50.0], [0.0, -50.0, 75.0]]", instance.at.toString());
        assertEquals("AffineTransform[[25.0, 0.0, 25.0], [0.0, -25.0, 50.0]]", instance2.at.toString());
    }

    @Test
    public void testGetWindowPointOf() {
        System.out.println("getWindowPointOf");
        assertEquals(new Point2D.Double(50.0, 75.0), instance.getWindowPointOf(new Point2D.Double(0, 0)));
        assertEquals(new Point2D.Double(100.0, 75.0), instance.getWindowPointOf(new Point2D.Double(1, 0)));
        assertEquals(new Point2D.Double(25.0, 25.0), instance.getWindowPointOf(new Point2D.Double(-.5, 1)));
    }

    @Test
    public void testGetCoordinateOf() {
        System.out.println("getCoordinateOf");
        assertEquals(new Point2D.Double(-1.0, 1.5), instance.getCoordinateOf(new Point2D.Double(0, 0)));
        assertEquals(new Point2D.Double(1, 0.5), instance.getCoordinateOf(new Point2D.Double(100.0, 50.0)));
        assertEquals(new Point2D.Double(-0.5, 0.5), instance.getCoordinateOf(new Point2D.Double(25.0, 50.0)));
        assertEquals(new Point2D.Double(0.5, 0.5), instance.getCoordinateOf(new Point2D.Double(75.0, 50.0)));
    }
}