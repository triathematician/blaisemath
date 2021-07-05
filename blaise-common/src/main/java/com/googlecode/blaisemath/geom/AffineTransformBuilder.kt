package com.googlecode.blaisemath.geom

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.util.logging.Level
import java.util.logging.Logger

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
*/ /**
 * Builder object for [AffineTransform].
 * @author Elisha Peterson
 */
class AffineTransformBuilder {
    private val res: AffineTransform? = AffineTransform()
    //endregion
    //region BUILDER PATTERNS
    /**
     * Concatenates this transform with a translation transformation.
     * @param dx x translation
     * @param dy y translation
     * @return builder
     */
    fun translate(dx: Double, dy: Double): AffineTransformBuilder? {
        res.translate(dx, dy)
        return this
    }

    /**
     * Concatenates this transform with a scale transformation.
     * @param rx x scale
     * @param ry y scale
     * @return builder
     */
    fun scale(rx: Double, ry: Double): AffineTransformBuilder? {
        res.scale(rx, ry)
        return this
    }

    /**
     * Concatenates this transform with a rotation transformation.
     * @param theta rotation amount
     * @return builder
     */
    fun rotate(theta: Double): AffineTransformBuilder? {
        res.rotate(theta)
        return this
    }

    /**
     * Concatenates this transform with a rotation transformation about a given point.
     * @param theta rotation amount
     * @param anchorx anchor location x
     * @param anchory anchor location y
     * @return builder
     */
    fun rotate(theta: Double, anchorx: Double, anchory: Double): AffineTransformBuilder? {
        res.rotate(theta, anchorx, anchory)
        return this
    }
    //endregion
    /**
     * Return the resulting transform.
     * @return transform
     */
    fun build(): AffineTransform? {
        return res
    }

    companion object {
        private val LOG = Logger.getLogger(AffineTransformBuilder::class.java.name)
        //region FACTORIES
        /**
         * Create a transform that maps the "scaleFrom" rectangle into the "scaleTo" region.
         * @param scaleTo region to scale to
         * @param scaleFrom region to scale from
         * @return transform
         */
        fun transformingTo(scaleTo: Rectangle2D?, scaleFrom: Rectangle2D?): AffineTransform? {
            if (scaleTo.getWidth() == 0.0 || scaleTo.getHeight() == 0.0 || scaleFrom.getWidth() == 0.0 || scaleFrom.getHeight() == 0.0) {
                LOG.log(Level.FINE, "Scaling with zero area rectangles: {0}, {1}. Returning identity transform.", arrayOf<Any?>(scaleFrom, scaleTo))
                return AffineTransform()
            }
            val scaleX = scaleFrom.getWidth() / scaleTo.getWidth()
            val scaleY = scaleFrom.getHeight() / scaleTo.getHeight()
            val scale = Math.max(scaleX, scaleY)
            val res = AffineTransform()
            res.translate(scaleTo.getCenterX(), scaleTo.getCenterY())
            res.scale(1 / scale, 1 / scale)
            res.translate(-scaleFrom.getCenterX(), -scaleFrom.getCenterY())
            return res
        }
    }
}