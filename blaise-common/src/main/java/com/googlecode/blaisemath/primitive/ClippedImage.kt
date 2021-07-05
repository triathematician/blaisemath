package com.googlecode.blaisemath.primitive

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.geom.AffineTransformBuilder
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
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
*/ /**
 * Encapsulates an image with a target (clipped) render region. May be treated as a shape, using the
 * clip boundaries.
 * @author Elisha Peterson
 */
class ClippedImage : Shape {
    private var shape: Shape? = Rectangle()
    private var image: Image? = null

    //base64 encoded data from the image (optional)
    private var base64: String? = null

    constructor() {}
    constructor(resource: URL?, shape: Shape?) {
        image = ImageIO.read(resource)
        this.shape = shape
    }

    constructor(image: Image?, shape: Shape?, base64: String?) {
        this.image = image
        this.shape = shape
        this.base64 = base64
    }

    //region PROPERTIES
    fun getShape(): Shape? {
        return shape
    }

    fun setShape(shape: Shape?) {
        this.shape = shape
    }

    fun getImage(): Image? {
        return image
    }

    fun setImage(image: Image?) {
        this.image = image
    }

    fun getBase64(): String? {
        return base64
    }

    fun setBase64(base64: String?) {
        this.base64 = base64
    }

    //endregion
    //region DELEGATES
    override fun getBounds(): Rectangle? {
        return shape.getBounds()
    }

    override fun getBounds2D(): Rectangle2D? {
        return shape.getBounds2D()
    }

    override fun contains(x: Double, y: Double): Boolean {
        return shape.contains(x, y)
    }

    override fun contains(p: Point2D?): Boolean {
        return shape.contains(p)
    }

    override fun intersects(x: Double, y: Double, w: Double, h: Double): Boolean {
        return shape.intersects(x, y, w, h)
    }

    override fun intersects(r: Rectangle2D?): Boolean {
        return shape.intersects(r)
    }

    override fun contains(x: Double, y: Double, w: Double, h: Double): Boolean {
        return shape.contains(x, y, w, h)
    }

    override fun contains(r: Rectangle2D?): Boolean {
        return shape.contains(r)
    }

    override fun getPathIterator(at: AffineTransform?): PathIterator? {
        return shape.getPathIterator(at)
    }

    override fun getPathIterator(at: AffineTransform?, flatness: Double): PathIterator? {
        return shape.getPathIterator(at, flatness)
    }
    //endregion
    /**
     * Compute transform that can be used to render image inside shape.
     * @return transform
     */
    fun imageTransform(): AffineTransform? {
        val src: Rectangle2D = Rectangle2D.Double(0, 0, image.getWidth(null), image.getHeight(null))
        val tgt = shape.getBounds2D()
        return AffineTransformBuilder.Companion.transformingTo(tgt, src)
    }
}