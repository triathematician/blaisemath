package com.googlecode.blaisemath.graphics.swing.render

import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.util.geom.minus
import com.googlecode.blaisemath.util.geom.point2
import com.googlecode.blaisemath.util.geom.times
import com.googlecode.blaisemath.util.kotlin.warning
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.GeneralPath
import java.awt.geom.Line2D
import java.awt.geom.PathIterator
import kotlin.math.sqrt

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
 * Draws a stroke on the screen, with an arrow at the endpoint.
 */
class ArrowPathRenderer(var arrowLocation: ArrowLocation = ArrowLocation.END) : PathRenderer() {

    override fun render(s: Shape, style: AttributeSet, canvas: Graphics2D) {
        super.render(s, style, canvas)
        val stroke = style.getColorOrNull(Styles.STROKE)
        val strokeWidth = style.getFloatOrNull(Styles.STROKE_WIDTH)

        // can only draw if stroke is appropriate
        if (stroke == null || strokeWidth == null || strokeWidth <= 0) {
            return
        }

        // arrow heads can only be drawn on certain shapes
        if (!(s is Line2D || s is GeneralPath)) {
            warning<ArrowPathRenderer>("Unable to draw arrowheads on shape $s")
            return
        }

        // create arrowhead shape(s) at end of path
        val arrowShapes = GeneralPath()
        if (s is Line2D) {
            if (arrowLocation == ArrowLocation.END || arrowLocation == ArrowLocation.BOTH) {
                arrowShapes.append(createArrowhead(s.x1.toFloat(), s.y1.toFloat(), s.x2.toFloat(), s.y2.toFloat(), strokeWidth), false)
            }
            if (arrowLocation == ArrowLocation.START || arrowLocation == ArrowLocation.BOTH) {
                arrowShapes.append(createArrowhead(s.x2.toFloat(), s.y2.toFloat(), s.x1.toFloat(), s.y1.toFloat(), strokeWidth), false)
            }
        } else {
            val gp = s as GeneralPath
            val pi = gp.getPathIterator(null)
            val cur = FloatArray(6)
            val last = FloatArray(6)
            while (!pi.isDone) {
                val type = pi.currentSegment(cur)
                if (type == PathIterator.SEG_LINETO) {
                    if (arrowLocation == ArrowLocation.END || arrowLocation == ArrowLocation.BOTH) {
                        arrowShapes.append(createArrowhead(last[0], last[1], cur[0], cur[1], strokeWidth), false)
                    }
                    if (arrowLocation == ArrowLocation.START || arrowLocation == ArrowLocation.BOTH) {
                        arrowShapes.append(createArrowhead(cur[0], cur[1], last[0], last[1], strokeWidth), false)
                    }
                }
                System.arraycopy(cur, 0, last, 0, 6)
                pi.next()
            }
        }

        // draw filled arrowhead on top of path
        canvas.color = stroke
        canvas.fill(arrowShapes)
        canvas.stroke = BasicStroke(strokeWidth)
        drawPatched(arrowShapes, canvas)
    }

    /** Defines directions for arrowheads  */
    enum class ArrowLocation {
        NONE, START, END, BOTH
    }

    companion object {

        /**
         * Returns path representing an arrow from one point to another.
         * @param x1 first x-coord
         * @param y1 first y-coord
         * @param x2 second x-coord
         * @param y2 second y-coord
         * @param thickness width of resulting line (determines size of arrowhead)
         * @return created path
         */
        fun createArrowhead(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float): GeneralPath {
            var dp = point2(x2, y2) - point2(x1, y1)
            val dsq = dp.distanceSq(0.0, 0.0)
            val dth = sqrt(thickness.toDouble()) * 3
            dp *= (dth / dsq)
            val adx = -dp.y
            val ady = dp.x
            val gp = GeneralPath()
            gp.moveTo(x2 - 1.5f * dp.x, y2 - 1.5f * dp.y)
            gp.lineTo(x2 - 2f * dp.x + 1f * adx, y2 - 2f * dp.y + 1f * ady)
            gp.lineTo(x2, y2)
            gp.lineTo(x2 - 2f * dp.x - 1f * adx, y2 - 2f * dp.y - 1f * ady)
            gp.closePath()
            return gp
        }
    }
}