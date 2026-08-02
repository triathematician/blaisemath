package com.googlecode.blaisemath.geom

import com.googlecode.blaisemath.util.kotlin.fine
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import kotlin.math.max

/*
* #%L
* BlaiseSketch
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
 * Builder object for [AffineTransform].
 */
class AffineTransformBuilder {

    val result = AffineTransform()

    //region BUILDER PATTERNS

    /** Concatenates this transform with a translation transformation. */
    fun translate(dx: Double, dy: Double): AffineTransformBuilder {
        result.translate(dx, dy)
        return this
    }

    /** Concatenates this transform with a scale transformation. */
    fun scale(sx: Double, sy: Double): AffineTransformBuilder {
        result.scale(sx, sy)
        return this
    }

    /** Concatenates this transform with a rotation transformation. */
    fun rotate(theta: Double): AffineTransformBuilder {
        result.rotate(theta)
        return this
    }

    /** Concatenates this transform with a rotation transformation about a given point. */
    fun rotate(theta: Double, anchorx: Double, anchory: Double): AffineTransformBuilder? {
        result.rotate(theta, anchorx, anchory)
        return this
    }

    //endregion

    companion object {

        /**
         * Create a transform that maps the "scaleFrom" rectangle into the "scaleTo" region.
         * @param scaleTo region to scale to
         * @param scaleFrom region to scale from
         * @return transform
         */
        @JvmStatic
        fun transformingTo(scaleTo: Rectangle2D, scaleFrom: Rectangle2D): AffineTransform {
            if (scaleTo.width == 0.0 || scaleTo.height == 0.0 || scaleFrom.width == 0.0 || scaleFrom.height == 0.0) {
                fine<AffineTransformBuilder>("Scaling with zero area rectangles: $scaleFrom, $scaleTo. Returning identity transform.")
                return AffineTransform()
            }
            val scaleX = scaleFrom.width / scaleTo.width
            val scaleY = scaleFrom.height / scaleTo.height
            val scale = max(scaleX, scaleY)
            return AffineTransform().apply {
                translate(scaleTo.centerX, scaleTo.centerY)
                scale(1 / scale, 1 / scale)
                translate(-scaleFrom.centerX, -scaleFrom.centerY)
            }
        }
    }
}