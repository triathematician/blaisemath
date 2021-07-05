package com.googlecode.blaisemath.graphics.swing.render

import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.util.Colors
import junit.framework.TestCase
import java.awt.BasicStroke
import java.awt.GradientPaint
import java.awt.Graphics2D
import java.awt.Shape

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
*/ /**
 * Draws a shape using a stroke (with thickness) and a fill color.
 * Uses a gradient with a slight color variation to fill the shape.
 *
 * @author Elisha Peterson
 */
class GradientShapeRenderer : ShapeRenderer() {
    override fun render(primitive: Shape?, style: AttributeSet?, canvas: Graphics2D?) {
        if (style.contains(Styles.FILL)) {
            val bds = primitive.getBounds2D()
            val fill = style.getColor(Styles.FILL)
            canvas.setPaint(GradientPaint(
                    bds.minX as Float, bds.minY as Float, fill,
                    bds.maxX as Float, bds.maxY as Float, Colors.blanderThan(fill)))
            canvas.fill(primitive)
        }
        val stroke = style.getColor(Styles.STROKE)
        val strokeWidth = style.getFloat(Styles.STROKE_WIDTH)
        if (stroke != null && strokeWidth != null && strokeWidth > 0) {
            canvas.setColor(stroke)
            canvas.setStroke(BasicStroke(strokeWidth))
            PathRenderer.Companion.drawPatched(primitive, canvas)
        }
    }

    companion object {
        private val INST: GradientShapeRenderer? = GradientShapeRenderer()
        fun getInstance(): GradientShapeRenderer? {
            return INST
        }
    }
}