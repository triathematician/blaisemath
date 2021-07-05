package com.googlecode.blaisemath.primitive

import com.googlecode.blaisemath.coordinate.Point2DBean
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Image
import java.awt.geom.Rectangle2D
import java.awt.image.ImageObserver

/*
* #%L
* BlaiseGraphics
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
 * An image anchored at a given location.
 * @author Elisha Peterson
 */
class AnchoredImage(x: kotlin.Double, y: kotlin.Double, private val width: kotlin.Double?, private val height: kotlin.Double?, private val originalImage: Image?, private val ref: String?) : Point2DBean(x, y) {
    private val scaledImage: Image? = null

    constructor(x: kotlin.Double, y: kotlin.Double, image: Image?, ref: String?) : this(x, y, null, null, image, ref) {}

    override fun toString(): String {
        return "AnchoredImage{" + getX() + ',' + getY() + ',' + ref + '}'
    }

    //region PROPERTIES
    fun getReference(): String? {
        return ref
    }

    fun getWidth(): kotlin.Double {
        return width ?: scaledImage.getWidth(null)
    }

    fun getHeight(): kotlin.Double {
        return height ?: scaledImage.getHeight(null)
    }

    fun getBounds(io: ImageObserver?): Rectangle2D? {
        val iw: kotlin.Double = width ?: scaledImage.getWidth(io)
        val ih: kotlin.Double = height ?: scaledImage.getHeight(io)
        return Rectangle2D.Double(x, y, iw, ih)
    }

    fun getImage(): Image? {
        return scaledImage
    }

    fun getOriginalImage(): Image? {
        return originalImage
    } //endregion

    init {
        if (width != null && width > 0 && height != null && height > 0 && (originalImage.getWidth(null).toDouble() != width || originalImage.getHeight(null).toDouble() != height)) {
            scaledImage = originalImage.getScaledInstance(width.toInt(), height.toInt(), Image.SCALE_DEFAULT)
        } else {
            scaledImage = originalImage
        }
    }
}