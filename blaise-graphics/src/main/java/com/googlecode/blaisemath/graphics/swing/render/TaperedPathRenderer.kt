package com.googlecode.blaisemath.graphics.swing.render

import com.googlecode.blaisemath.graphics.Renderer
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Styles
import junit.framework.TestCase
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.GeneralPath
import java.awt.geom.Line2D
import java.awt.geom.PathIterator

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
 * Draws a path on the screen using a fancy tapered-outline style.
 *
 * @author Elisha Peterson
 */
class TaperedPathRenderer : PathRenderer() {
    override fun toString(): String {
        return "TaperedPathRenderer"
    }

    override fun render(s: Shape?, style: AttributeSet?, canvas: Graphics2D?) {
        val stroke = style.getColor(Styles.STROKE)
        val strokeWidth = style.getFloat(Styles.STROKE_WIDTH, 1f)
        if (strokeWidth <= 0f || stroke == null) {
            return
        }
        val shape = if (s is Line2D.Double) createBezierShape(s as Line2D.Double?, strokeWidth) else if (s is GeneralPath) createBezierShape(s as GeneralPath?, strokeWidth) else s
        val cAlpha = Color(stroke.red, stroke.green, stroke.blue, stroke.alpha / 2)
        canvas.setColor(cAlpha)
        canvas.fill(shape)
        canvas.setColor(stroke)
        PathRenderer.Companion.drawPatched(shape, canvas)
    }

    companion object {
        private val INST: TaperedPathRenderer? = TaperedPathRenderer()
        fun getInstance(): Renderer<Shape?, Graphics2D?>? {
            return INST
        }

        /**
         * Returns path representing a "fancy shape" between points, using Bezier curves.
         * @param line a line
         * @param strokeWidth stroke width
         * @return created shape
         */
        fun createBezierShape(line: Line2D.Double?, strokeWidth: Float): Shape? {
            return createBezierShape(line.x1 as Float, line.y1 as Float, line.x2 as Float, line.y2 as Float, strokeWidth)
        }

        /**
         * Returns path representing a "fancy shape" between points, using Bezier curves.
         * @param path a multi-step path
         * @param strokeWidth stroke width
         * @return created shape
         */
        fun createBezierShape(path: GeneralPath?, strokeWidth: Float): Shape? {
            val shape = GeneralPath()
            val pi = path.getPathIterator(null)
            val cur = FloatArray(6)
            val last = FloatArray(6)
            while (!pi.isDone) {
                val type = pi.currentSegment(cur)
                if (type == PathIterator.SEG_LINETO) {
                    shape.append(createBezierShape(last[0], last[1], cur[0], cur[1], strokeWidth), false)
                }
                System.arraycopy(cur, 0, last, 0, 6)
                pi.next()
            }
            return shape
        }

        /**
         * Returns path representing a "fancy shape" between points, using Bezier curves.
         * @param x1 first x-coord
         * @param y1 first y-coord
         * @param x2 second x-coord
         * @param y2 second y-coord
         * @param pathWidth width of resulting line (determines size of arrowhead)
         * @return created shape
         */
        fun createBezierShape(x1: Float, y1: Float, x2: Float, y2: Float, pathWidth: Float): GeneralPath? {
            val dx = x2 - x1
            val dy = y2 - y1
            val dsq = Math.sqrt(dx * dx + dy * dy.toDouble()) as Float
            val adx = -dy * pathWidth / dsq
            val ady = dx * pathWidth / dsq
            val gp = GeneralPath()
            gp.moveTo(x1 - adx, y1 - ady)
            gp.lineTo(x1 + adx, y1 + ady)
            gp.curveTo(x1 + .25f * dx + .25f * adx, y1 + .25f * dy + .25f * ady,
                    x1 + .75f * dx + .25f * adx, y1 + .75f * dy + .25f * ady,
                    x2 + .5f * adx, y2 + .5f * ady)
            gp.lineTo(x2 - .5f * adx, y2 - .5f * ady)
            gp.curveTo(x1 + .75f * dx - .25f * adx, y1 + .75f * dy - .25f * ady,
                    x1 + .25f * dx - .25f * adx, y1 + .25f * dy - .25f * ady,
                    x1 - adx, y1 - ady)
            gp.closePath()
            return gp
        }
    }
}