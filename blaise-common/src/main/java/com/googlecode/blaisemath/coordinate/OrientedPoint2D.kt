package com.googlecode.blaisemath.coordinate

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.geom.Point2D
import java.util.*

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
*/ /**
 * A point with an orientation represented as a double.
 * @author Elisha Peterson
 */
class OrientedPoint2D : Point2DBean {
    /** The orientation of the point  */
    var angle = 0.0

    constructor() {}
    constructor(pt: Point2D?) : super(pt.getX(), pt.getY()) {
        if (pt is OrientedPoint2D) {
            angle = (pt as OrientedPoint2D?).angle
        }
    }

    constructor(x: kotlin.Double, y: kotlin.Double) : super(x, y) {}

    override fun toString(): String {
        return "OrientedPoint2D[$x, $y; $angle]"
    }

    override fun hashCode(): Int {
        return Objects.hash(x, y, angle)
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is OrientedPoint2D) {
            return false
        }
        val opt = obj as OrientedPoint2D?
        return opt.x == x && opt.y == y && opt.angle == angle
    }

    /**
     * Builder method, updating angle to be in direction of given second point.
     * @param p2 second point
     * @return this
     */
    fun toward(p2: Point2D?): OrientedPoint2D? {
        setAngle(Math.atan2(p2.getY() - getY(), p2.getX() - getX()))
        return this
    }

    /**
     * Builder method, updating angle to be in direction away from given second point.
     * @param p2 second point
     * @return this
     */
    fun awayFrom(p2: Point2D?): OrientedPoint2D? {
        setAngle(Math.atan2(-p2.getY() + getY(), -p2.getX() + getX()))
        return this
    }

    fun getAngle(): kotlin.Double {
        return angle
    }

    fun setAngle(angle: kotlin.Double) {
        this.angle = angle
    }
}