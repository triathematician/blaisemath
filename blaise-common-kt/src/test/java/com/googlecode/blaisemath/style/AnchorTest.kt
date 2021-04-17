package com.googlecode.blaisemath.style

import com.googlecode.blaisemath.util.geom.Point2
import com.googlecode.blaisemath.util.geom.point2
import com.googlecode.blaisemath.util.geom.rectangle2
import org.junit.Assert
import org.junit.Test
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

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
*/   class AnchorTest {
    @Test
    fun testOpposite() {
        Assert.assertEquals(Anchor.WEST, Anchor.EAST.opposite())
        Assert.assertEquals(Anchor.CENTER, Anchor.CENTER.opposite())
    }

    @Test
    fun testAngle() {
        Assert.assertEquals(0.0, Anchor.EAST.angle, 1E-6)
        Assert.assertEquals(Math.PI, Anchor.WEST.angle, 1E-6)
    }

    @Test
    fun testOffsetForCircle() {
        assertEqualsPoint(point2(-1, 0), Anchor.WEST.offsetForCircle(1.0), 1E-6)
        assertEqualsPoint(point2(Math.sqrt(2.0) / 2, Math.sqrt(2.0) / 2), Anchor.SOUTHEAST.offsetForCircle(1.0), 1E-6)
    }

    @Test
    fun testOnCircle() {
        assertEqualsPoint(point2(1 + Math.sqrt(2.0), 1 + Math.sqrt(2.0)),
                Anchor.SOUTHEAST.onCircle(point2(1, 1), 2.0), 1E-6)
    }

    @Test
    fun testOffsetForRectangle() {
        assertEqualsPoint(point2(1.5, 2), Anchor.SOUTHEAST.offsetForRectangle(3.0, 4.0), 1E-6)
    }

    @Test
    fun testOnRectangle() {
        assertEqualsPoint(point2(4, 6), Anchor.SOUTHEAST.onRectangle(rectangle2(1, 2, 3, 4)), 1E-6)
    }

    @Test
    fun testRectangleAnchoredAt() {
        Assert.assertEquals(rectangle2(1, 2, 2, 5),
                Anchor.SOUTHEAST.rectangleAnchoredAt(point2(3, 7), 2.0, 5.0))
        Assert.assertEquals(rectangle2(3, 5, 2, 4),
                Anchor.WEST.rectangleAnchoredAt(point2(3, 7), 2.0, 4.0))
    }

    private fun assertEqualsPoint(p: Point2, q: Point2D, err: Double) {
        Assert.assertEquals(p.getX(), q.getX(), err)
        Assert.assertEquals(p.getY(), q.getY(), err)
    }
}