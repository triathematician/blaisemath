package com.googlecode.blaisemath.style

import com.googlecode.blaisemath.util.geom.*
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
*/

/**
 * Represents anchor locations and utilities for using them in practice. These anchors can be used for finding points
 * around a circle or for shifting rectangles so various corners are anchored at a target point.
 */
enum class Anchor(val angle: Double, private val xOff: Double, private val yOff: Double) {
    CENTER(0.0, 0.0, 0.0),
    WEST(Math.PI, -.5, 0.0),
    NORTHWEST(-0.75 * Math.PI, -.5, -0.5),
    NORTH(-0.5 * Math.PI, 0.0, -.5),
    NORTHEAST(-0.25 * Math.PI, .5, -.5),
    EAST(0.0, .5, 0.0),
    SOUTHEAST(0.25 * Math.PI, .5, .5),
    SOUTH(0.5 * Math.PI, 0.0, .5),
    SOUTHWEST(0.75 * Math.PI, -.5, .5);

    /** Get the opposite anchor point. */
    fun opposite() = when (this) {
        CENTER -> CENTER
        WEST -> EAST
        NORTHWEST -> SOUTHEAST
        NORTH -> SOUTH
        NORTHEAST -> SOUTHWEST
        EAST -> WEST
        SOUTHEAST -> NORTHWEST
        SOUTH -> NORTH
        SOUTHWEST -> NORTHEAST
    }

    /** Returns the relative offset of an anchor point for a circle of radius r. */
    fun offsetForCircle(r: Double) = if (this == CENTER) Point2() else pointPolar(r, angle)
    /** Returns the absolute location of an anchor point on a circle of radius r. */
    fun onCircle(center: Point2D, r: Double) = center + offsetForCircle(r)

    /**
     * Returns the relative offset of an anchor point for a rectangle of given size. This indicates how much the given anchor
     * point on the outside of the rectangle is shifted from the center of the rectangle.
     * */
    fun offsetForRectangle(wid: Double, ht: Double) = if (this == CENTER) Point2() else Point2(xOff * wid, yOff * ht)
    /** Returns the absolute location of an anchor point on a rectangle of given size. */
    fun onRectangle(rectangle: Rectangle2D) = rectangle.center + offsetForRectangle(rectangle.width, rectangle.height)

    /** Get the rectangle of given size whose corner matching this anchor is pt. */
    fun rectangleAnchoredAt(pt: Point2D, wid: Double, ht: Double) = rectangleAnchoredAt(pt.x, pt.y, wid, ht)
    /** Get the rectangle of given size whose corner matching this anchor is pt. */
    fun rectangleAnchoredAt(x: Double, y: Double, wid: Double, ht: Double) = rectangle2FromCenter(x, y, wid, ht) - offsetForRectangle(wid, ht)

}