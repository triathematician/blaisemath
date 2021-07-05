package com.googlecode.blaisemath.graphics.swing.render

import com.google.common.base.Preconditions
import com.googlecode.blaisemath.coordinate.OrientedPoint2D
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.style.AttributeSet
import junit.framework.TestCase
import java.awt.Graphics2D
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.awt.geom.RectangularShape

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
 * Draws a point along with a ray from the point to the outer edge of the graphics canvas.
 *
 * @author Elisha Peterson
 */
class MarkerRendererToClip : MarkerRenderer() {
    /** Line style for drawing the ray  */
    protected var rayRenderer: PathRenderer? = ArrowPathRenderer.Companion.getInstance()

    /** Whether to extend in both directions, or just forward  */
    protected var extendBothDirections = false
    //region BUILDER PATTERNS
    /**
     * Sets ray style and returns pointer to this object.
     * @param rayStyle the style for rays
     * @return this
     */
    fun rayStyle(rayStyle: PathRenderer?): MarkerRendererToClip? {
        setRayRenderer(rayStyle)
        return this
    }

    /**
     * Sets extension rule and returns pointer to this object.
     * @param extendBoth whether to extend line in both directions
     * @return this
     */
    fun extendBothDirections(extendBoth: Boolean): MarkerRendererToClip? {
        setExtendBothDirections(extendBoth)
        return this
    }

    //endregion
    override fun toString(): String {
        return String.format("PointStyleInfinite[rayStyle=%s, extendBoth=%s]",
                rayRenderer, extendBothDirections)
    }

    //region PROPERTIES
    fun getRayRenderer(): PathRenderer? {
        return rayRenderer
    }

    fun setRayRenderer(rayStyle: PathRenderer?) {
        rayRenderer = Preconditions.checkNotNull(rayStyle)
    }

    fun isExtendBothDirections(): Boolean {
        return extendBothDirections
    }

    fun setExtendBothDirections(extendBoth: Boolean) {
        extendBothDirections = extendBoth
    }

    //endregion
    override fun render(p: Point2D?, style: AttributeSet?, canvas: Graphics2D?) {
        val angle: Double = if (p is OrientedPoint2D) (p as OrientedPoint2D?).angle else 0
        val p2: Point2D = Point2D.Double(p.getX() + Math.cos(angle), p.getY() + Math.sin(angle))
        val endpoint: Point2D? = boundaryHit(p, p2, canvas.getClipBounds())
        if (extendBothDirections) {
            val endpoint1: Point2D? = boundaryHit(p2, p, canvas.getClipBounds())
            rayRenderer.render(Line2D.Double(endpoint1, endpoint), style, canvas)
        } else {
            rayRenderer.render(Line2D.Double(p, endpoint), style, canvas)
        }
        super.render(p, style, canvas)
    }

    companion object {
        /**
         * Returns points at which the ray beginning at p1 and passing through p2 intersects the boundary of the window.
         * @param p1p first point
         * @param p2p second point
         * @param bounds the window boundaries
         * @return the point on the boundary
         */
        fun boundaryHit(p1p: Point2D?, p2p: Point2D?, bounds: RectangularShape?): Point2D.Double? {
            val p1 = Point2D.Double(p1p.getX(), p1p.getY())
            val p2 = Point2D.Double(p2p.getX(), p2p.getY())
            if (p2.x > p1.x && p1.x <= bounds.getMaxX()) {
                // line goes to the right
                val slope = (p2.y - p1.y) / (p2.x - p1.x)
                val yRight = slope * (bounds.getMaxX() - p1.x) + p1.y
                if (yRight <= bounds.getMaxY() && yRight >= bounds.getMinY()) {
                    // point is on the right
                    return Point2D.Double(bounds.getMaxX(), yRight)
                } else if (p2.y > p1.y && p1.y <= bounds.getMaxY()) {
                    // line goes up
                    return Point2D.Double((bounds.getMaxY() - p1.y) / slope + p1.x, bounds.getMaxY())
                } else if (p1.y > p2.y && p1.y >= bounds.getMinY()) {
                    // line goes down
                    return Point2D.Double((bounds.getMinY() - p1.y) / slope + p1.x, bounds.getMinY())
                }
            } else if (p2.x < p1.x && p1.x >= bounds.getMinX()) {
                // line goes to the left
                val slope = (p2.y - p1.y) / (p2.x - p1.x)
                val yLeft = slope * (bounds.getMinX() - p1.x) + p1.y
                if (yLeft <= bounds.getMaxY() && yLeft >= bounds.getMinY()) {
                    // point is on the right
                    return Point2D.Double(bounds.getMinX(), yLeft)
                } else if (p2.y > p1.y && p1.y <= bounds.getMaxY()) {
                    // line goes up
                    return Point2D.Double((bounds.getMaxY() - p1.y) / slope + p1.x, bounds.getMaxY())
                } else if (p1.y > p2.y && p1.y >= bounds.getMinY()) {
                    // line goes down
                    return Point2D.Double((bounds.getMinY() - p1.y) / slope + p1.x, bounds.getMinY())
                }
            } else if (p1.x == p2.x) {
                // line is vertical
                if (p2.y < p1.y && p1.y >= bounds.getMinY()) {
                    // line goes up
                    return Point2D.Double(p1.x, bounds.getMinY())
                } else if (p1.y <= bounds.getMaxY()) {
                    return Point2D.Double(p1.x, bounds.getMaxY())
                }
            }
            return Point2D.Double(p2.x, p2.y)
        }
    }
}