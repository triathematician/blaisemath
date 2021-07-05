package com.googlecode.blaisemath.graphics.swing.render

import com.googlecode.blaisemath.graphics.Renderer
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.primitive.AnchoredText
import com.googlecode.blaisemath.style.AttributeSet
import junit.framework.TestCase
import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

/*
* #%L
* blaise-graphics
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
*/ /**
 * Renders text at an angle, by rotating the canvas around the text's anchor point.
 *
 * @author Elisha
 */
class SlopedTextRenderer @JvmOverloads constructor(private var theta: Double = 0.0) : Renderer<AnchoredText?, Graphics2D?> {
    fun getTheta(): Double {
        return theta
    }

    fun setTheta(theta: Double) {
        this.theta = theta
    }

    override fun render(primitive: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?) {
        val orig = canvas.getTransform()
        canvas.rotate(theta, primitive.getX(), primitive.getY())
        TextRenderer.Companion.getInstance().render(primitive, style, canvas)
        canvas.setTransform(orig)
    }

    fun shape(primitive: AnchoredText?, style: AttributeSet?, gr: Graphics2D?): Shape? {
        val base: Rectangle2D = TextRenderer.Companion.getInstance().boundingBox(primitive, style, gr)
        return transform(primitive).createTransformedShape(base)
    }

    override fun boundingBox(primitive: AnchoredText?, style: AttributeSet?, gr: Graphics2D?): Rectangle2D? {
        val shape = shape(primitive, style, gr)
        return shape?.bounds2D
    }

    override fun contains(point: Point2D?, primitive: AnchoredText?, style: AttributeSet?, gr: Graphics2D?): Boolean {
        val shape = shape(primitive, style, gr)
        return shape != null && shape.contains(point)
    }

    override fun intersects(rect: Rectangle2D?, primitive: AnchoredText?, style: AttributeSet?, gr: Graphics2D?): Boolean {
        val shape = shape(primitive, style, gr)
        return shape != null && shape.intersects(rect)
    }

    private fun transform(pt: Point2D?): AffineTransform? {
        val at = AffineTransform()
        at.translate(pt.getX(), pt.getY())
        at.rotate(theta)
        at.translate(-pt.getX(), -pt.getY())
        return at
    }
}