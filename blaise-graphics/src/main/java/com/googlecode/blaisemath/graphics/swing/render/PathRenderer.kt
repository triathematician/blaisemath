package com.googlecode.blaisemath.graphics.swing.render

import com.google.common.primitives.Doubles
import com.google.common.primitives.Ints
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
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.Shape
import java.awt.geom.Area
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

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
 *
 * @author Elisha Peterson
 */
open class PathRenderer : Renderer<Shape?, Graphics2D?> {
    override fun toString(): String {
        return "PathRenderer"
    }

    override fun render(primitive: Shape?, style: AttributeSet?, canvas: Graphics2D?) {
        if (Styles.hasStroke(style)) {
            canvas.setColor(Styles.strokeColorOf(style))
            canvas.setStroke(Styles.strokeOf(style))
            drawPatched(primitive, canvas)
        }
    }

    override fun boundingBox(primitive: Shape?, style: AttributeSet?, canvas: Graphics2D?): Rectangle2D? {
        val sh = strokedShape(primitive, style)
        return sh?.bounds2D
    }

    override fun contains(point: Point2D?, primitive: Shape?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        val sh = strokedShape(primitive, style)
        return sh != null && sh.contains(point)
    }

    override fun intersects(rect: Rectangle2D?, primitive: Shape?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        val sh = strokedShape(primitive, style)
        return sh != null && sh.intersects(rect)
    }

    companion object {
        private val INST: PathRenderer? = PathRenderer()
        fun getInstance(): Renderer<Shape?, Graphics2D?>? {
            return INST
        }

        fun strokedShape(primitive: Shape?, style: AttributeSet?): Shape? {
            return if (Styles.hasStroke(style)) BasicStroke(style.getFloat(Styles.STROKE_WIDTH)).createStrokedShape(primitive) else null
        }

        /**
         * Method to draw a path shape on the canvas that addresses a performance issue.
         * For dashed lines, it limits render to the canvas clip because of a JDK bug.
         * See https://bugs.openjdk.java.net/browse/JDK-6620013.
         * @param primitive to draw
         * @param canvas target canvas
         */
        fun drawPatched(primitive: Shape?, canvas: Graphics2D?) {
            if (canvas.getStroke() !is BasicStroke || (canvas.getStroke() as BasicStroke).dashArray == null) {
                // draw normally
                canvas.draw(primitive)
                return
            }
            val r = canvas.getClipBounds()
            // use a large padding because we still want the dashes to be in the right place
            val pad = Ints.max(if (canvas.getStroke() is BasicStroke) Math.ceil((canvas.getStroke() as BasicStroke).lineWidth.toDouble()) as Int else 5,
                    r.width * 50, r.height * 50)
            val paddedClip = Rectangle(r.x - pad, r.y - pad,
                    r.width + 2 * pad, r.height + 2 * pad)
            val toDraw = intersectPath(paddedClip, primitive)
            if (toDraw != null) {
                canvas.draw(toDraw)
            }
        }

        /**
         * Compute intersection of path with rectangular area.
         * @param rectangle area
         * @param path path
         * @return intersecting shape, or null if none
         */
        private fun intersectPath(rectangle: Rectangle2D?, path: Shape?): Shape? {
            var path = path
            val r2 = path.getBounds2D()
            if (r2.width == 0.0 && r2.height == 0.0) {
                return null
            } else if (rectangle.contains(r2)) {
                return path
            }
            if (r2.width == 0.0 || r2.height == 0.0) {
                // we have a flat shape, so area intersection doesn't work -- this is not precisely correct for multi-part paths, but close enough?
                path = Line2D.Double(r2.minX, r2.minY, r2.maxX, r2.maxY)
            }
            return if (path is Line2D.Double) {
                val line = path as Line2D?
                if (line.intersects(rectangle)) intersect(toDouble(line), rectangle) else null
            } else {
                val a = Area(rectangle)
                a.intersect(Area(path))
                a
            }
        }

        private fun toDouble(line: Line2D?): Line2D.Double? {
            return Line2D.Double(line.getP1(), line.getP2())
        }

        /**
         * Compute the line segment from intersecting given line with rectangle.
         * @param l line to use
         * @param r rectangle
         * @return portion of line inside the rectangle, null if none
         */
        private fun intersect(l: Line2D.Double?, r: Rectangle2D?): Line2D.Double? {
            if (r.contains(l.getP1()) && r.contains(l.getP2())) {
                return l
            }

            // parameterize line as x=x1+t*(x2-x1), y=y1+t*(y2-y1), so line is between 0 and 1
            // then compute t values for lines bounding rectangles, and intersect the three intervals
            // [0,1], [tx1,tx2], and [ty1,ty2]
            val tx1: Double = if (l.x1 == l.x2) if (between(l.x1, r.getMinX(), r.getMaxX())) 0 else -1 else (r.getMinX() - l.x1) / (l.x2 - l.x1)
            val tx2: Double = if (l.x1 == l.x2) if (between(l.x1, r.getMinX(), r.getMaxX())) 1 else -1 else (r.getMaxX() - l.x1) / (l.x2 - l.x1)
            val ty1: Double = if (l.y1 == l.y2) if (between(l.x1, r.getMinY(), r.getMaxY())) 0 else -1 else (r.getMinY() - l.y1) / (l.y2 - l.y1)
            val ty2: Double = if (l.y1 == l.y2) if (between(l.x1, r.getMinY(), r.getMaxY())) 1 else -1 else (r.getMaxY() - l.y1) / (l.y2 - l.y1)
            val t0 = Doubles.max(0.0, Doubles.min(tx1, tx2), Doubles.min(ty1, ty2))
            val t1 = Doubles.min(1.0, Doubles.max(tx1, tx2), Doubles.max(ty1, ty2))
            return if (t0 > t1) null else Line2D.Double(l.x1 + t0 * (l.x2 - l.x1), l.y1 + t0 * (l.y2 - l.y1),
                    l.x1 + t1 * (l.x2 - l.x1), l.y1 + t1 * (l.y2 - l.y1))
        }

        private fun between(x: Double, t0: Double, t1: Double): Boolean {
            return if (x >= t0) x <= t1 else x >= t1
        }
    }
}