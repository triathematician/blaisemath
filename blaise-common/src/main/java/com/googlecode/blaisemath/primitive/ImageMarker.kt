package com.googlecode.blaisemath.primitive

import com.googlecode.blaisemath.geom.AffineTransformBuilder
import com.googlecode.blaisemath.geom.rectangle2FromCenter
import java.awt.Image
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import kotlin.math.sqrt

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

/** Marker defined by an image and clip path. */
class ImageMarker(val name: String, val clip: Shape, val image: Image) : Marker {

    override fun create(point: Point2D, orientation: Double, r: Float): Shape {
        val tgt = rectangle2FromCenter(point, r)
        val src = clip.bounds2D
        val at = AffineTransformBuilder.transformingTo(tgt, src)
        return ClippedImage(image, at.createTransformedShape(clip), null)
    }

}