package com.googlecode.blaisemath.primitive

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Path2D
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
*/ /**
 * Marker defined by a path.
 * @author Elisha Peterson
 */
class PathMarker(private val name: String?, private val path: Path2D?) : Marker {
    private val bds: Rectangle2D?
    override fun toString(): String {
        return name
    }

    fun getName(): String? {
        return name
    }

    fun getPath(): Path2D? {
        return path
    }

    override fun create(point: Point2D?, orientation: Double, markerRadius: Float): Shape? {
        val at = AffineTransform()
        val scale = 2 * markerRadius / Math.max(bds.getWidth(), bds.getHeight())
        at.translate(point.getX(), point.getY())
        at.scale(scale, scale)
        at.rotate(orientation)
        at.translate(-bds.getX() - .5 * bds.getWidth(), -bds.getY() - .5 * bds.getHeight())
        return path.createTransformedShape(at)
    }

    init {
        bds = path.getBounds2D()
    }
}