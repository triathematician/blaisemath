package com.googlecode.blaisemath.graphics.swing.render

import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.util.Colors.withReducedSaturation
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
*/

/**
 * Draws a shape using a stroke (with thickness) and a fill color. Uses a gradient with a slight color variation to fill the shape.
 */
class GradientShapeRenderer : ShapeRenderer() {
    override fun render(primitive: Shape, style: AttributeSet, canvas: Graphics2D) {
        if (style.contains(Styles.FILL)) {
            val bds = primitive.bounds2D
            val fill = style.getColorOrNull(Styles.FILL)!!
            canvas.paint = GradientPaint(
                    bds.minX.toFloat(), bds.minY.toFloat(), fill,
                    bds.maxX.toFloat(), bds.maxY.toFloat(), fill.withReducedSaturation())
            canvas.fill(primitive)
        }
        val stroke = style.getColorOrNull(Styles.STROKE)
        val strokeWidth = style.getFloatOrNull(Styles.STROKE_WIDTH)
        if (stroke != null && strokeWidth != null && strokeWidth > 0) {
            canvas.color = stroke
            canvas.stroke = BasicStroke(strokeWidth)
            PathRenderer.drawPatched(primitive, canvas)
        }
    }
}