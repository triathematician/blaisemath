package com.googlecode.blaisemath.graphics.swing.render

import com.google.common.base.Preconditions
import com.googlecode.blaisemath.coordinate.OrientedPoint2D
import com.googlecode.blaisemath.graphics.Renderer
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.primitive.Marker
import com.googlecode.blaisemath.primitive.Markers
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Styles
import junit.framework.TestCase
import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
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
 * Draws an oriented point on the graphics canvas.
 * See also the [related SVG documentation](http://www.w3.org/TR/SVG/painting.html#Markers) on markers.
 *
 * @author Elisha Peterson
 */
open class MarkerRenderer : Renderer<Point2D?, Graphics2D?> {
    /** Delegate for rendering the shape of the marker  */
    protected var shapeRenderer: Renderer<Shape?, Graphics2D?>? = ShapeRenderer()

    //region PROPERTIES
    fun getShapeRenderer(): Renderer<Shape?, Graphics2D?>? {
        return shapeRenderer
    }

    fun setShapeRenderer(shapeRenderer: Renderer<Shape?, Graphics2D?>?) {
        this.shapeRenderer = Preconditions.checkNotNull(shapeRenderer)
    }

    // </editor-fold>
    fun getShape(primitive: Point2D?, style: AttributeSet?): Shape? {
        val rad = style.getFloat(Styles.MARKER_RADIUS, 4f)
        val angle: Double = if (primitive is OrientedPoint2D) (primitive as OrientedPoint2D?).angle else 0
        val marker = style.get(Styles.MARKER)
        if (marker == null) {
            return Markers.CIRCLE.create(primitive, angle, rad)
        } else if (marker is Marker) {
            return (marker as Marker?).create(primitive, angle, rad)
        } else {
            LOG.log(Level.WARNING, if (marker is String) "Invalid marker object string (not supported yet): {0}" else "Invalid marker object: {0}", marker)
        }
        return null
    }

    override fun render(primitive: Point2D?, style: AttributeSet?, canvas: Graphics2D?) {
        shapeRenderer.render(getShape(primitive, style), style, canvas)
    }

    override fun boundingBox(primitive: Point2D?, style: AttributeSet?, canvas: Graphics2D?): Rectangle2D? {
        return shapeRenderer.boundingBox(getShape(primitive, style), style, canvas)
    }

    override fun contains(point: Point2D?, primitive: Point2D?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        return shapeRenderer.contains(point, getShape(primitive, style), style, canvas)
    }

    override fun intersects(rect: Rectangle2D?, primitive: Point2D?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        return shapeRenderer.intersects(rect, getShape(primitive, style), style, canvas)
    }

    companion object {
        private val LOG = Logger.getLogger(MarkerRenderer::class.java.name)
        fun getInstance(): Renderer<Point2D?, Graphics2D?>? {
            return MarkerRenderer()
        }
    }
}