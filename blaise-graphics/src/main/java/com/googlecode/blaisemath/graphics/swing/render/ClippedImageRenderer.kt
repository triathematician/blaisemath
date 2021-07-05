package com.googlecode.blaisemath.graphics.swing.render

import com.googlecode.blaisemath.graphics.Renderer
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.primitive.ClippedImage
import com.googlecode.blaisemath.style.AttributeSet
import junit.framework.TestCase
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.Shape
import java.awt.geom.Area
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

/*-
* #%L
* blaise-graphics
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
 * Renders a clipped image to a swing canvas. Draws the image first, then draws a depiction of the clip if desired.
 * @author Elisha Peterson
 */
class ClippedImageRenderer : Renderer<ClippedImage?, Graphics2D?> {
    override fun render(primitive: ClippedImage?, style: AttributeSet?, canvas: Graphics2D?) {
        val curClip = canvas.getClip()
        val customClip = Area(curClip)
        customClip.intersect(Area(primitive.getShape()))
        canvas.setClip(customClip)
        canvas.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        canvas.drawImage(primitive.getImage(), primitive.imageTransform(), null)
        canvas.setClip(curClip)
        CLIP_RENDERER.render(primitive.getShape(), style, canvas)
    }

    override fun boundingBox(primitive: ClippedImage?, style: AttributeSet?, canvas: Graphics2D?): Rectangle2D? {
        return CLIP_RENDERER.boundingBox(primitive.getShape(), style, canvas)
    }

    override fun contains(point: Point2D?, primitive: ClippedImage?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        return CLIP_RENDERER.contains(point, primitive.getShape(), style, canvas)
    }

    override fun intersects(rect: Rectangle2D?, primitive: ClippedImage?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        return CLIP_RENDERER.intersects(rect, primitive.getShape(), style, canvas)
    }

    companion object {
        private val CLIP_RENDERER: Renderer<Shape?, Graphics2D?>? = PathRenderer.Companion.getInstance()
    }
}