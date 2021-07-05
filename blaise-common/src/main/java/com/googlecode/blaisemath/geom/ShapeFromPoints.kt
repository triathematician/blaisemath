package com.googlecode.blaisemath.geom

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Shape
import java.awt.geom.Ellipse2D
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

/*
* #%L
* BlaiseSketch
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
 * Utility methods for creating shapes from two locations.
 *
 * @author Elisha Peterson
 */
abstract class ShapeFromPoints {
    /**
     * Create shape from mouse positions. May generate exceptions if either point
     * is null.
     * @param press where mouse was pressed
     * @param release where it was released
     * @return created shape
     */
    abstract fun create(press: Point2D?, release: Point2D?): Shape?

    /**
     * Create from nullable points.
     * @param press where mouse was pressed
     * @param release where it was released
     * @return created shape, or null if either point was null
     */
    fun createFromNullable(press: Point2D?, release: Point2D?): Shape? {
        return if (press == null || release == null) null else create(press, release)
    }

    /** Creates line from two points  */
    class Line : ShapeFromPoints() {
        override fun create(press: Point2D?, release: Point2D?): Shape? {
            return Line2D.Double(press, release)
        }
    }

    /** Creates circle from two points (center and outside).  */
    class Circle : ShapeFromPoints() {
        override fun create(press: Point2D?, release: Point2D?): Shape? {
            val rad = press.distance(release)
            return Ellipse2D.Double(press.getX() - rad, press.getY() - rad, 2 * rad, 2 * rad)
        }
    }

    /** Creates ellipse from two points (corners of frame).  */
    class Ellipse : ShapeFromPoints() {
        override fun create(press: Point2D?, release: Point2D?): Shape? {
            val res = Ellipse2D.Double()
            res.setFrameFromDiagonal(press, release)
            return res
        }
    }

    /** Creates ellipse from two points (corners of rectangle).  */
    class Rectangle : ShapeFromPoints() {
        override fun create(press: Point2D?, release: Point2D?): Shape? {
            val res = Rectangle2D.Double()
            res.setFrameFromDiagonal(press, release)
            return res
        }
    }
}