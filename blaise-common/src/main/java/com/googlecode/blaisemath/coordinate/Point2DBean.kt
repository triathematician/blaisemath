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
 * An instance of [Point2D] that is also a [CoordinateBean].
 * @author Elisha Peterson
 */
open class Point2DBean : Point2D.Double, CoordinateBean<Point2D?> {
    constructor() : super(0.0, 0.0) {}
    constructor(x: kotlin.Double, y: kotlin.Double) : super(x, y) {}

    override fun toString(): String {
        return "Point2DBean{" + getX() + ',' + getY() + '}'
    }

    override fun getPoint(): Point2D? {
        return this
    }

    override fun setPoint(p: Point2D?) {
        setLocation(p)
    }
}