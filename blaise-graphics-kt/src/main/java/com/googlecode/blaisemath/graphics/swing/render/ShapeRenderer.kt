package com.googlecode.blaisemath.graphics.swing.render

import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Renderer
import com.googlecode.blaisemath.style.Styles
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
*/

/**
 * Draws a shape using a stroke (with thickness) and a fill color.
 * @author Elisha Peterson
 */
open class ShapeRenderer : Renderer<Shape, Graphics2D> {
    override fun render(primitive: Shape, style: AttributeSet, canvas: Graphics2D) {
        if (Styles.hasFill(style)) {
            canvas.color = Styles.fillColorOf(style)
            canvas.fill(primitive)
        }
        if (Styles.hasStroke(style)) {
            canvas.color = Styles.strokeColorOf(style)
            canvas.stroke = Styles.strokeOf(style)
            PathRenderer.drawPatched(primitive, canvas)
        }
    }

    override fun boundingBox(primitive: Shape, style: AttributeSet, canvas: Graphics2D?): Rectangle2D? {
        val filled = Styles.hasFill(style)
        val sh = PathRenderer.strokedShape(primitive, style)
        return when {
            filled && sh != null -> primitive.bounds2D.createUnion(sh.bounds2D)
            filled -> primitive.bounds2D
            else -> sh?.bounds2D
        }
    }

    override fun contains(point: Point2D, primitive: Shape, style: AttributeSet, canvas: Graphics2D?) = when {
        Styles.hasFill(style) && primitive.contains(point) -> true
        else -> PathRenderer.strokedShape(primitive, style)?.contains(point) == true
    }

    override fun intersects(rect: Rectangle2D, primitive: Shape, style: AttributeSet, canvas: Graphics2D?) = when {
        Styles.hasFill(style) && primitive.intersects(rect) -> true
        else -> PathRenderer.strokedShape(primitive, style)?.intersects(rect) == true
    }
}