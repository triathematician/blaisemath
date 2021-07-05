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
import java.awt.Graphics2D
import java.awt.Shape
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
open class ShapeRenderer : Renderer<Shape?, Graphics2D?> {
    override fun toString(): String {
        return "ShapeRenderer"
    }

    override fun render(primitive: Shape?, style: AttributeSet?, canvas: Graphics2D?) {
        if (Styles.hasFill(style)) {
            canvas.setColor(Styles.fillColorOf(style))
            canvas.fill(primitive)
        }
        if (Styles.hasStroke(style)) {
            canvas.setColor(Styles.strokeColorOf(style))
            canvas.setStroke(Styles.strokeOf(style))
            PathRenderer.Companion.drawPatched(primitive, canvas)
        }
    }

    override fun boundingBox(primitive: Shape?, style: AttributeSet?, canvas: Graphics2D?): Rectangle2D? {
        val filled = Styles.hasFill(style)
        val sh: Shape = PathRenderer.Companion.strokedShape(primitive, style)
        return if (filled && sh != null) {
            primitive.getBounds2D().createUnion(sh.bounds2D)
        } else if (filled) {
            primitive.getBounds2D()
        } else sh?.bounds2D
    }

    override fun contains(point: Point2D?, primitive: Shape?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        return if (Styles.hasFill(style) && primitive.contains(point)) {
            true
        } else {
            val sh: Shape = PathRenderer.Companion.strokedShape(primitive, style)
            sh != null && sh.contains(point)
        }
    }

    override fun intersects(rect: Rectangle2D?, primitive: Shape?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        return if (Styles.hasFill(style) && primitive.intersects(rect)) {
            true
        } else {
            val sh: Shape = PathRenderer.Companion.strokedShape(primitive, style)
            sh != null && sh.intersects(rect)
        }
    }

    companion object {
        private val INST: ShapeRenderer? = ShapeRenderer()
        fun getInstance(): ShapeRenderer? {
            return INST
        }
    }
}