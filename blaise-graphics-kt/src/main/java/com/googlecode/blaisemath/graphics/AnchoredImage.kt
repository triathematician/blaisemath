package com.googlecode.blaisemath.graphics

import com.googlecode.blaisemath.coordinate.HasPoint2D
import com.googlecode.blaisemath.util.geom.Rectangle2
import java.awt.Image
import java.awt.image.ImageObserver

/*
* #%L
* BlaiseGraphics
* --
* Copyright (C) 2009 - 2021 Elisha Peterson
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

/** An image anchored at a given location. */
class AnchoredImage(x: kotlin.Double, y: kotlin.Double, private val _width: kotlin.Double? = null, private val _height: kotlin.Double? = null,
                    val originalImage: Image, val reference: String?) : HasPoint2D(x, y) {

    val width: kotlin.Double
        get() = _width ?: image.getWidth(null).toDouble()
    val height: kotlin.Double
        get() = _height ?: image.getHeight(null).toDouble()

    val image: Image = image()

    private fun image(): Image {
        val widthPos = _width != null && _width > 0
        val heightPos = _height != null && _height > 0
        return if (widthPos && heightPos && (originalImage.getWidth(null).toDouble() != _width || originalImage.getHeight(null).toDouble() != _height)) {
            originalImage.getScaledInstance(_width!!.toInt(), _height!!.toInt(), Image.SCALE_DEFAULT)
        } else {
            originalImage
        }
    }

    fun getBounds(io: ImageObserver?) = Rectangle2(x, y, _width ?: image.getWidth(io).toDouble(), _height ?: image.getHeight(io).toDouble())

}