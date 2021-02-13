package com.googlecode.blaisemath.style;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.junit.*;
import static org.junit.Assert.*;

public class AnchorTest {

    @Test
    public void testOpposite() {
        assertEquals(Anchor.WEST, Anchor.EAST.opposite());
        assertEquals(Anchor.CENTER, Anchor.CENTER.opposite());
    }

    @Test
    public void testAngle() {
        assertEquals(0.0, Anchor.EAST.angle(), 1E-6);
        assertEquals(Math.PI, Anchor.WEST.angle(), 1E-6);
    }

    @Test
    public void testOffsetForCircle() {
        assertEqualsPoint(new Point2D.Double(-1, 0), Anchor.WEST.offsetForCircle(1), 1E-6);
        assertEqualsPoint(new Point2D.Double(Math.sqrt(2)/2, Math.sqrt(2)/2), Anchor.SOUTHEAST.offsetForCircle(1), 1E-6);
    }

    @Test
    public void testOnCircle() {
        assertEqualsPoint(new Point2D.Double(1 + Math.sqrt(2), 1 + Math.sqrt(2)),
                Anchor.SOUTHEAST.onCircle(new Point2D.Double(1, 1), 2), 1E-6);
    }

    @Test
    public void testOffsetForRectangle() {
        assertEqualsPoint(new Point2D.Double(1.5, 2), Anchor.SOUTHEAST.offsetForRectangle(3, 4), 1E-6);
    }

    @Test
    public void testOnRectangle() {
        assertEqualsPoint(new Point2D.Double(4, 6), Anchor.SOUTHEAST.onRectangle(new Rectangle2D.Double(1, 2, 3, 4)), 1E-6);
    }

    @Test
    public void testRectangleAnchoredAt() {
        assertEquals(new Rectangle2D.Double(1, 2, 2, 5),
                Anchor.SOUTHEAST.rectangleAnchoredAt(new Point2D.Double(3, 7), 2, 5));
        assertEquals(new Rectangle2D.Double(3, 5, 2, 4),
                Anchor.WEST.rectangleAnchoredAt(new Point2D.Double(3, 7), 2, 4));
    }

    private void assertEqualsPoint(Point2D.Double p, Point2D q, double err) {
        assertEquals(p.getX(), q.getX(), err);
        assertEquals(p.getY(), q.getY(), err);
    }

}
