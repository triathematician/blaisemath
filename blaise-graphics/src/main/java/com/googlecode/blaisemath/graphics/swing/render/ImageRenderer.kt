package com.googlecode.blaisemath.graphics.swing.render

import com.googlecode.blaisemath.graphics.Renderer
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.primitive.Anchor
import com.googlecode.blaisemath.primitive.AnchoredImage
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Styles
import junit.framework.TestCase
import java.awt.Graphics2D
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
 * Renderer for drawing images on a canvas. Anchor is used to position the icon relative to a point. The default anchor
 * * is NORTHWEST, with the image drawn to the right/below the point.
 *
 * @author Elisha Peterson
 */
class ImageRenderer : Renderer<AnchoredImage?, Graphics2D?> {
    override fun render(primitive: AnchoredImage?, style: AttributeSet?, canvas: Graphics2D?) {
        val rect = boundingBox(primitive, style, canvas)
        canvas.drawImage(primitive.getImage(), rect.getX() as Int, rect.getY() as Int, null)
    }

    override fun boundingBox(primitive: AnchoredImage?, style: AttributeSet?, canvas: Graphics2D?): Rectangle2D? {
        val anchor = Styles.anchorOf(style, Anchor.NORTHWEST)
        return anchor.rectangleAnchoredAt(primitive, primitive.getWidth(), primitive.getHeight())
    }

    override fun contains(point: Point2D?, primitive: AnchoredImage?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        return boundingBox(primitive, style, canvas).contains(point)
    }

    override fun intersects(rect: Rectangle2D?, primitive: AnchoredImage?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        return boundingBox(primitive, style, canvas).intersects(rect)
    }

    companion object {
        private val INST: ImageRenderer? = ImageRenderer()
        fun getInstance(): Renderer<AnchoredImage?, Graphics2D?>? {
            return INST
        }
    }
}