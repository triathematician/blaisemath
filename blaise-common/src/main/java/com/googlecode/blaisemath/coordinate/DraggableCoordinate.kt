package com.googlecode.blaisemath.coordinate

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.geom.Point2D

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
 * Marks object methods that are used to get/set coordinates.
 * A third method allows the point to be set based on an initial point, and
 * coordinates for the start and end of a drag gesture.
 *
 * @param <C> coordinate of the point
 *
 * @author Elisha Peterson
</C> */
interface DraggableCoordinate<C> : CoordinateBean<C?> {
    /**
     * Sets the point by movement from an initial point
     * @param initial starting position
     * @param dragStart start of drag
     * @param dragFinish end of drag
     */
    open fun setPoint(initial: C?, dragStart: C?, dragFinish: C?)

    companion object {
        /**
         * Wraps a point as a [DraggableCoordinate] object.
         * @param pt the point
         * @return wrapped instance of point as a point bean
         */
        open fun create(pt: Point2D?): DraggableCoordinate<Point2D?>? {
            return object : DraggableCoordinate<Point2D?> {
                override fun getPoint(): Point2D? {
                    return pt
                }

                override fun setPoint(p: Point2D?) {
                    pt.setLocation(p)
                }

                override fun setPoint(initial: Point2D?, dragStart: Point2D?, dragFinish: Point2D?) {
                    pt.setLocation(initial.getX() + dragFinish.getX() - dragStart.getX(),
                            initial.getY() + dragFinish.getY() - dragStart.getY())
                }
            }
        }
    }
}