/*-
 * #%L
 * blaise-common-kt
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
package com.googlecode.blaisemath.util.geom

import java.awt.Shape
import java.awt.geom.Point2D

/** Methods for creating shapes from two locations. */
interface ShapeFromPoints {

    /** Generate shape from a press location and a release location. */
    fun create(press: Point2D, release: Point2D): Shape

    companion object {
        /** Creates line from two points  */
        class Line : ShapeFromPoints {
            override fun create(press: Point2D, release: Point2D) = Line2(press, release)
        }

        /** Creates circle from two points (center and outside).  */
        class Circle : ShapeFromPoints {
            override fun create(press: Point2D, release: Point2D) = circle2(press, press.distance(release))
        }

        /** Creates ellipse from two points (corners of frame).  */
        class Ellipse : ShapeFromPoints {
            override fun create(press: Point2D, release: Point2D) = ellipse2FromDiagonal(press, release)
        }

        /** Creates rectangle from two points (corners of rectangle).  */
        class Rectangle : ShapeFromPoints {
            override fun create(press: Point2D, release: Point2D) = rectangle2FromDiagonal(press, release)
        }
    }
}
