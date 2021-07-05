package com.googlecode.blaisemath.graphics.swing.render

import com.google.common.base.Strings
import com.google.common.collect.Lists
import com.googlecode.blaisemath.graphics.Renderer
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.primitive.Anchor
import com.googlecode.blaisemath.primitive.AnchoredText
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Styles
import junit.framework.TestCase
import java.awt.Graphics2D
import java.awt.Point
import java.awt.RenderingHints
import java.awt.Toolkit
import java.awt.font.FontRenderContext
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.util.*

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
 * Draw text on multiple lines, using line breaks provided with the text. By default, the text is anchored at the upper
 * left, so that text is drawn to the right and below the anchor point. For alternate anchors, all lines of text are
 * positioned in the same way, and the text may be centered, left-aligned, or right-aligned, depending on the anchor.
 *
 * @author Elisha Peterson
 */
class MultilineTextRenderer : Renderer<AnchoredText?, Graphics2D?> {
    override fun toString(): String {
        return "MultilineTextRenderer"
    }

    override fun contains(point: Point2D?, primitive: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        return boundingBox(primitive, style, canvas).contains(point)
    }

    override fun intersects(rect: Rectangle2D?, primitive: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        return boundingBox(primitive, style, canvas).intersects(rect)
    }

    override fun render(text: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?) {
        if (Strings.isNullOrEmpty(text.getText())) {
            return
        }
        val font = Styles.fontOf(style)
        canvas.setFont(font)
        canvas.setColor(style.getColor(Styles.FILL))
        canvas.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        val frc = canvas.getFontRenderContext()
        val textAnchor = Styles.anchorOf(style, Anchor.SOUTHWEST)
        val lineHeight = font.getLineMetrics("", frc).height.toDouble()
        val bounds = boundingBox(text, style, canvas)
        val offset = style.getPoint2D(Styles.OFFSET, Point())!!
        val x0 = bounds.getMinX()
        var y0 = bounds.getMaxY()
        for (line in Lists.reverse(Arrays.asList(*lines(text)))) {
            val wid = font.getStringBounds(line, frc).width
            val dx = (textAnchor.offsetForRectangle(bounds.getWidth() - wid, 0.0).x
                    + 0.5 * (bounds.getWidth() - wid))
            canvas.drawString(line, (x0 + dx) as Float, y0 as Float)
            y0 -= lineHeight
        }
    }

    override fun boundingBox(text: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?): Rectangle2D? {
        if (Strings.isNullOrEmpty(text.getText())) {
            return null
        }
        val font = Styles.fontOf(style)
        canvas?.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        val frc = if (canvas == null) FontRenderContext(font.transform, true, false) else canvas.fontRenderContext
        var width = 0.0
        val lines = lines(text)
        for (line in lines) {
            width = Math.max(width, font.getStringBounds(line, frc).width)
        }
        val lineHeight = font.getLineMetrics("", frc).height.toDouble()
        var height = lineHeight * lines.size
        height -= lineHeight - font.size * 72.0 / Toolkit.getDefaultToolkit().screenResolution
        val textAnchor = Styles.anchorOf(style, Anchor.NORTHWEST)
        val offset = style.getPoint2D(Styles.OFFSET, Point())!!
        return textAnchor.rectangleAnchoredAt(text.getX() + offset.x, text.getY() + offset.y,
                width, height)
    }

    companion object {
        private val INST: MultilineTextRenderer? = MultilineTextRenderer()
        fun getInstance(): Renderer<AnchoredText?, Graphics2D?>? {
            return INST
        }

        private fun lines(text: AnchoredText?): Array<String?>? {
            return text.getText().split("\n|\r\n".toRegex()).toTypedArray()
        }
    }
}