package com.googlecode.blaisemath.primitive

import com.googlecode.blaisemath.geom.AffineTransformBuilder
import com.googlecode.blaisemath.geom.rectangle2
import java.awt.Image
import java.awt.Rectangle
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.PathIterator
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.net.URL
import javax.imageio.ImageIO

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
 * An image with a target (clipped) render region. May be treated as a shape, using the clip boundaries.
 */
class ClippedImage(_image: Image, _shape: Shape, _base64: String? = null) : Shape {

    var shape = _shape
    var image: Image = _image
    /** Image as a base-64 encoded string. */
    var base64: String? = _base64

    constructor(resource: URL, shape: Shape): this(ImageIO.read(resource), shape)

    //region SHAPE DELEGATES

    override fun getBounds(): Rectangle = shape.bounds
    override fun getBounds2D(): Rectangle2D = shape.bounds2D

    override fun contains(x: Double, y: Double) = shape.contains(x, y)
    override fun contains(p: Point2D) = shape.contains(p)

    override fun intersects(x: Double, y: Double, w: Double, h: Double) = shape.intersects(x, y, w, h)
    override fun intersects(r: Rectangle2D) = shape.intersects(r)

    override fun contains(x: Double, y: Double, w: Double, h: Double) = shape.contains(x, y, w, h)
    override fun contains(r: Rectangle2D) = shape.contains(r)

    override fun getPathIterator(at: AffineTransform): PathIterator = shape.getPathIterator(at)
    override fun getPathIterator(at: AffineTransform?, flatness: Double): PathIterator = shape.getPathIterator(at, flatness)

    //endregion

    /**
     * Compute transform that can be used to render image inside shape.
     */
    fun imageTransform(): AffineTransform {
        val src = rectangle2(0, 0, image.getWidth(null), image.getHeight(null))
        val tgt = shape.bounds2D
        return AffineTransformBuilder.transformingTo(tgt, src)
    }

}