package com.googlecode.blaisemath.coordinate

import com.googlecode.blaisemath.util.geom.Point2
import com.googlecode.blaisemath.util.geom.minus
import com.googlecode.blaisemath.util.geom.plus
import java.awt.geom.Point2D
import java.util.*
import kotlin.math.atan2

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
 * Marks object methods that are used to get/set coordinates.
 */
interface HasCoordinate<C> {
    var point : C
}

/**
 * Marks object methods that are used to get/set coordinates. A third method allows the point to be set based on an
 * initial point, and coordinates for the start and end of a drag gesture.
 */
interface DraggableHasCoordinate<C> : HasCoordinate<C> {
    /**
     * Sets the point by movement from an initial point
     * @param initial starting position
     * @param dragStart start of drag
     * @param dragFinish end of drag
     */
    fun setPoint(initial: C, dragStart: C, dragFinish: C)
}

//region Point2D IMPLEMENTATION

/**
 * An instance of [Point2D] that is also a [HasCoordinate].
 */
open class HasPoint2D(x: kotlin.Double = 0.0, y: kotlin.Double = 0.0) : Point2(x, y), HasCoordinate<Point2D> {
    override fun toString(): String = "HasPoint2D {${getX()},${getY()}}"
    override var point: Point2D
        get() = this
        set(value) = setLocation(value)
}

/** A point with an orientation represented as a double. */
class OrientedPoint2D(x: kotlin.Double = 0.0, y: kotlin.Double = 0.0, var angle: kotlin.Double = 0.0) : HasPoint2D(x, y) {

    override fun toString(): String = "OrientedPoint2D[$x, $y; $angle]"

    /** Set angle to be in direction of given second point. */
    fun toward(p2: Point2D) { angle = atan2(p2.y - getY(), p2.x - getX()) }
    /** Set angle to be in direction away from given second point. */
    fun awayFrom(p2: Point2D) { angle = atan2(-p2.y + getY(), -p2.x + getX()) }
}

/** Wraps a point as a [DraggableHasCoordinate] object. */
fun draggable(pt: Point2D) = object : DraggableHasCoordinate<Point2D> {
    override var point = pt
    override fun setPoint(initial: Point2D, dragStart: Point2D, dragFinish: Point2D) {
        point = initial + dragFinish - dragStart
    }
}

//endregion