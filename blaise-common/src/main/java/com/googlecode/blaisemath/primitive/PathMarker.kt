package com.googlecode.blaisemath.primitive

import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Path2D
import java.awt.geom.Point2D
import kotlin.math.max

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
 * Marker defined by an explicit path.
 */
class PathMarker(val name: String, val path: Path2D) : Marker {

    private val bds = path.bounds2D

    override fun create(point: Point2D, orientation: Double, markerRadius: Float): Shape {
        val at = AffineTransform()
        val scale = 2 * markerRadius / max(bds.width, bds.height)
        at.translate(point.x, point.y)
        at.scale(scale, scale)
        at.rotate(orientation)
        at.translate(-bds.x - .5 * bds.width, -bds.y - .5 * bds.height)
        return path.createTransformedShape(at)
    }

}