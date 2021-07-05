package com.googlecode.blaisemath.graphics.swing.render

import com.google.common.base.Preconditions
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.primitive.ArrowLocation
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Styles
import junit.framework.TestCase
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.GeneralPath
import java.awt.geom.Line2D
import java.awt.geom.PathIterator
import java.util.logging.Level
import java.util.logging.Logger

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
 * Draws a stroke on the screen, with an arrow at the endpoint.
 *
 * @author Elisha Peterson
 */
class ArrowPathRenderer : PathRenderer {
    protected var arrowLoc: ArrowLocation? = ArrowLocation.END

    /**
     * Initialize renderer w/ default arrow location (end).
     */
    constructor() {}

    /**
     * Initialize renderer w/ specified arrow location.
     * @param dir arrow location(s)
     */
    constructor(dir: ArrowLocation?) {
        Preconditions.checkNotNull(dir)
        arrowLoc = dir
    }

    override fun toString(): String {
        return String.format("ArrowPathRenderer[arrowLoc=%s]", arrowLoc)
    }

    fun arrowLocation(loc: ArrowLocation?): ArrowPathRenderer? {
        setArrowLocation(loc)
        return this
    }

    //region PROPERTIES
    fun getArrowLocation(): ArrowLocation? {
        return arrowLoc
    }

    fun setArrowLocation(loc: ArrowLocation?) {
        Preconditions.checkNotNull(loc)
        if (arrowLoc != loc) {
            arrowLoc = loc
        }
    }

    //endregion
    override fun render(s: Shape?, style: AttributeSet?, canvas: Graphics2D?) {
        super.render(s, style, canvas)
        val stroke = style.getColor(Styles.STROKE)
        val strokeWidth = style.getFloat(Styles.STROKE_WIDTH)

        // can only draw if stroke is appropriate
        if (stroke == null || strokeWidth == null || strokeWidth <= 0) {
            return
        }

        // arrow heads can only be drawn on certain shapes
        if (!(s is Line2D || s is GeneralPath)) {
            LOG.log(Level.WARNING, "Unable to draw arrowheads on this shape: {0}", s)
            return
        }

        // create and draw arrowhead shape(s) at end of path
        val arrowShapes = arrowShapes(s, arrowLoc, strokeWidth)
        canvas.setColor(stroke)
        canvas.fill(arrowShapes)
        canvas.setStroke(BasicStroke(strokeWidth))
        PathRenderer.Companion.drawPatched(arrowShapes, canvas)
    }

    companion object {
        private val LOG = Logger.getLogger(ArrowPathRenderer::class.java.name)

        /**
         * Get instance of the arrow renderer.
         * @return instance
         */
        fun getInstance(): ArrowPathRenderer? {
            return ArrowPathRenderer()
        }

        fun arrowShapes(s: Shape?, loc: ArrowLocation?, strokeWidth: Float): GeneralPath? {
            return if (s is Line2D) lineArrowShapes(s as Line2D?, loc, strokeWidth) else pathArrowShapes(s as GeneralPath?, loc, strokeWidth)
        }

        private fun lineArrowShapes(line: Line2D?, loc: ArrowLocation?, strokeWidth: Float): GeneralPath? {
            val res = GeneralPath()
            if (loc == ArrowLocation.END || loc == ArrowLocation.BOTH) {
                res.append(createArrowhead(line.getX1() as Float, line.getY1() as Float,
                        line.getX2() as Float, line.getY2() as Float, strokeWidth), false)
            }
            if (loc == ArrowLocation.START || loc == ArrowLocation.BOTH) {
                res.append(createArrowhead(line.getX2() as Float, line.getY2() as Float,
                        line.getX1() as Float, line.getY1() as Float, strokeWidth), false)
            }
            return res
        }

        private fun pathArrowShapes(path: GeneralPath?, loc: ArrowLocation?, strokeWidth: Float): GeneralPath? {
            val res = GeneralPath()
            val pi = path.getPathIterator(null)
            val cur = FloatArray(6)
            val last = FloatArray(6)
            while (!pi.isDone) {
                val type = pi.currentSegment(cur)
                if (type == PathIterator.SEG_LINETO) {
                    if (loc == ArrowLocation.END || loc == ArrowLocation.BOTH) {
                        res.append(createArrowhead(last[0], last[1], cur[0], cur[1], strokeWidth), false)
                    }
                    if (loc == ArrowLocation.START || loc == ArrowLocation.BOTH) {
                        res.append(createArrowhead(cur[0], cur[1], last[0], last[1], strokeWidth), false)
                    }
                }
                System.arraycopy(cur, 0, last, 0, 6)
                pi.next()
            }
            return res
        }

        /**
         * Returns path representing an arrow from one point to another.
         * @param x1 first x-coord
         * @param y1 first y-coord
         * @param x2 second x-coord
         * @param y2 second y-coord
         * @param thickness width of resulting line (determines size of arrowhead)
         * @return created path
         */
        fun createArrowhead(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float): GeneralPath? {
            var dx = x2 - x1
            var dy = y2 - y1
            val dsq = Math.sqrt(dx * dx + dy * dy.toDouble()) as Float
            val dth = Math.sqrt(thickness.toDouble()) * 3
            dx *= dth / dsq
            dy *= dth / dsq
            val adx = -dy
            val ady = dx
            val gp = GeneralPath()
            gp.moveTo(x2 - 1.5f * dx, y2 - 1.5f * dy)
            gp.lineTo(x2 - 2f * dx + 1f * adx, y2 - 2f * dy + 1f * ady)
            gp.lineTo(x2, y2)
            gp.lineTo(x2 - 2f * dx - 1f * adx, y2 - 2f * dy - 1f * ady)
            gp.closePath()
            return gp
        }
    }
}