package com.googlecode.blaisemath.graphics.swing.render

import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Renderer
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.util.geom.Line2
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.Shape
import java.awt.geom.Area
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * Draws a shape using a stroke (with thickness) and a fill color.
 * @author Elisha Peterson
 */
open class PathRenderer : Renderer<Shape, Graphics2D> {

    override fun render(primitive: Shape, style: AttributeSet, canvas: Graphics2D) {
        if (Styles.hasStroke(style)) {
            canvas.color = Styles.strokeColorOf(style)
            canvas.stroke = Styles.strokeOf(style)
            drawPatched(primitive, canvas)
        }
    }

    override fun boundingBox(primitive: Shape, style: AttributeSet, canvas: Graphics2D?): Rectangle2D? {
        val sh = strokedShape(primitive, style)
        return sh?.bounds2D
    }

    override fun contains(point: Point2D, primitive: Shape, style: AttributeSet, canvas: Graphics2D?): Boolean {
        val sh = strokedShape(primitive, style)
        return sh != null && sh.contains(point)
    }

    override fun intersects(rect: Rectangle2D, primitive: Shape, style: AttributeSet, canvas: Graphics2D?): Boolean {
        val sh = strokedShape(primitive, style)
        return sh != null && sh.intersects(rect)
    }

    companion object {
        val INST = PathRenderer()

        fun strokedShape(primitive: Shape, style: AttributeSet): Shape? {
            return if (Styles.hasStroke(style)) BasicStroke(style.getFloat(Styles.STROKE_WIDTH)!!).createStrokedShape(primitive) else null
        }

        /**
         * Method to draw a path shape on the canvas that addresses a performance issue.
         * For dashed lines, it limits render to the canvas clip because of a JDK bug.
         * See https://bugs.openjdk.java.net/browse/JDK-6620013.
         * @param primitive to draw
         * @param canvas target canvas
         */
        fun drawPatched(primitive: Shape, canvas: Graphics2D) {
            if (canvas.stroke !is BasicStroke || (canvas.stroke as BasicStroke).dashArray == null) {
                // draw normally
                canvas.draw(primitive)
                return
            }
            val r = canvas.clipBounds
            // use a large padding because we still want the dashes to be in the right place
            val pad = maxOf(if (canvas.stroke is BasicStroke) ceil((canvas.stroke as BasicStroke).lineWidth.toDouble()).toInt() else 5,
                    r.width * 50, r.height * 50)
            val paddedClip = Rectangle(r.x - pad, r.y - pad, r.width + 2 * pad, r.height + 2 * pad)
            val toDraw = intersectPath(paddedClip, primitive)
            if (toDraw != null) canvas.draw(toDraw)
        }

        /**
         * Compute intersection of path with rectangular area.
         * @param rectangle area
         * @param _path path
         * @return intersecting shape, or null if none
         */
        private fun intersectPath(rectangle: Rectangle2D, _path: Shape): Shape? {
            var path = _path
            val r2 = path.bounds2D
            if (r2.width == 0.0 && r2.height == 0.0) {
                return null
            } else if (rectangle.contains(r2)) {
                return path
            }
            if (r2.width == 0.0 || r2.height == 0.0) {
                // we have a flat shape, so area intersection doesn't work -- this is not precisely correct for multi-part paths, but close enough?
                path = Line2(r2.minX, r2.minY, r2.maxX, r2.maxY)
            }
            return if (path is Line2) {
                if (path.intersects(rectangle)) intersect(toDouble(path), rectangle) else null
            } else {
                Area(rectangle).apply { intersect(Area(path)) }
            }
        }

        private fun toDouble(line: Line2D) = Line2(line.p1, line.p2)

        /**
         * Compute the line segment from intersecting given line with rectangle.
         * @param l line to use
         * @param r rectangle
         * @return portion of line inside the rectangle, null if none
         */
        private fun intersect(l: Line2, r: Rectangle2D): Line2D.Double? {
            if (r.contains(l.p1) && r.contains(l.p2)) return l

            // parameterize line as x=x1+t*(x2-x1), y=y1+t*(y2-y1), so line is between 0 and 1
            // then compute t values for lines bounding rectangles, and intersect the three intervals
            // [0,1], [tx1,tx2], and [ty1,ty2]
            val tx1 = if (l.x1 == l.x2) if (between(l.x1, r.minX, r.maxX)) 0.0 else -1.0 else (r.minX - l.x1) / (l.x2 - l.x1)
            val tx2 = if (l.x1 == l.x2) if (between(l.x1, r.minX, r.maxX)) 1.0 else -1.0 else (r.maxX - l.x1) / (l.x2 - l.x1)
            val ty1 = if (l.y1 == l.y2) if (between(l.x1, r.minY, r.maxY)) 0.0 else -1.0 else (r.minY - l.y1) / (l.y2 - l.y1)
            val ty2 = if (l.y1 == l.y2) if (between(l.x1, r.minY, r.maxY)) 1.0 else -1.0 else (r.maxY - l.y1) / (l.y2 - l.y1)
            val t0 = maxOf(0.0, min(tx1, tx2), min(ty1, ty2))
            val t1 = minOf(1.0, max(tx1, tx2), max(ty1, ty2))
            return if (t0 > t1) null else Line2(l.x1 + t0 * (l.x2 - l.x1), l.y1 + t0 * (l.y2 - l.y1),
                    l.x1 + t1 * (l.x2 - l.x1), l.y1 + t1 * (l.y2 - l.y1))
        }

        private fun between(x: Double, t0: Double, t1: Double) = if (x >= t0) x <= t1 else x >= t1
    }
}