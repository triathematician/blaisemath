package com.googlecode.blaisemath.geom

import com.google.common.base.Preconditions
import com.google.common.collect.Iterables
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

/*
* #%L
* BlaiseGraphTheory
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
*/ /**
 * Utilities for working with points.
 *
 * @author Elisha Peterson
 */
object Points {
    /**
     * Formats a point with n decimal places in the form (a, b).
     * @param p the point to format
     * @param n number of decimal places
     * @return formatted point, e.g. (2.1,-3.0)
     */
    fun format(p: Point2D?, n: Int): String? {
        return String.format("(%." + n + "f, %." + n + "f)", p.getX(), p.getY())
    }

    /**
     * Create and return bounding box around a given set of pounds. Returns null
     * if there is 0 points, and a box with side length `margin`
     * around the point if there is just 1 point. (If the `margin` is 0, returns
     * a box of side length 1.)
     * @param pts the points
     * @param inset additional padding to include around the box
     * @return bounding box, null if there are no points
     */
    fun boundingBox(pts: Iterable<out Point2D?>?, inset: Double): Rectangle2D.Double? {
        var minx = Double.MAX_VALUE
        var miny = Double.MAX_VALUE
        var maxx = -Double.MAX_VALUE
        var maxy = -Double.MAX_VALUE
        var count = 0
        for (p in pts) {
            minx = Math.min(minx, p.getX())
            miny = Math.min(miny, p.getY())
            maxx = Math.max(maxx, p.getX())
            maxy = Math.max(maxy, p.getY())
            count++
        }
        return if (count == 0) {
            null
        } else if (count == 1) {
            val m = if (inset == 0.0) .5 else inset
            Rectangle2D.Double(minx - m, miny - m, 2 * m, 2 * m)
        } else {
            Rectangle2D.Double(minx - inset, miny - inset,
                    maxx - minx + 2 * inset, maxy - miny + 2 * inset)
        }
    }

    /**
     * Compute the average location of a set of points.
     * @param locs points (should be non-empty)
     * @return average loc
     * @throws IllegalArgumentException if argument is empty
     */
    fun average(locs: Iterable<out Point2D?>?): Point2D? {
        Preconditions.checkArgument(locs != null && Iterables.size(locs) > 0)
        var sumx = 0.0
        var sumy = 0.0
        var count = 0
        for (p in locs) {
            sumx += p.getX()
            sumy += p.getY()
            count++
        }
        return Point2D.Double(sumx / count, sumy / count)
    }
}