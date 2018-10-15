package com.googlecode.blaisemath.style;

import junit.framework.TestCase;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 *
 * @author Elisha
 */
public class AnchorTest extends TestCase {

    @Test
    public void testOpposite() {
        assertEquals(Anchor.WEST, Anchor.EAST.opposite());
        assertEquals(Anchor.CENTER, Anchor.CENTER.opposite());
    }

    @Test
    public void testAngle() {
        assertEquals(0.0, Anchor.EAST.angle());
        assertEquals(Math.PI, Anchor.WEST.angle());
    }

    @Test
    public void testOffsetForCircle() {
        assertEquals(new Point2D.Double(-1, 0), Anchor.WEST.offsetForCircle(1), 1E-6);
        assertEquals(new Point2D.Double(Math.sqrt(2)/2, Math.sqrt(2)/2), Anchor.SOUTHEAST.offsetForCircle(1), 1E-6);
    }

    @Test
    public void testOnCircle() {
        assertEquals(new Point2D.Double(1 + Math.sqrt(2), 1 + Math.sqrt(2)),
                Anchor.SOUTHEAST.onCircle(new Point2D.Double(1, 1), 2), 1E-6);
    }

    @Test
    public void testOffsetForRectangle() {
        assertEquals(new Point2D.Double(1.5, 2), Anchor.SOUTHEAST.offsetForRectangle(3, 4), 1E-6);
    }

    @Test
    public void testOnRectangle() {
        assertEquals(new Point2D.Double(4, 6), Anchor.SOUTHEAST.onRectangle(new Rectangle2D.Double(1, 2, 3, 4)), 1E-6);
    }

    @Test
    public void testRectangleAnchoredAt() {
        assertEquals(new Rectangle2D.Double(1, 2, 2, 5),
                Anchor.SOUTHEAST.rectangleAnchoredAt(new Point2D.Double(3, 7), 2, 5));
        assertEquals(new Rectangle2D.Double(3, 5, 2, 4),
                Anchor.WEST.rectangleAnchoredAt(new Point2D.Double(3, 7), 2, 4));
    }

    private void assertEquals(Point2D.Double p, Point2D q, double err) {
        assertEquals(p.getX(), q.getX(), err);
        assertEquals(p.getY(), q.getY(), err);
    }

}