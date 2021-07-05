package com.googlecode.blaisemath.graphics.swing.render

import com.googlecode.blaisemath.graphics.Renderer
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.primitive.AnchoredText
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Styles
import junit.framework.TestCase
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.Arc2D
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
 * Renders text in one or more lines along a given arc.
 *
 * @author Elisha Peterson
 */
class MultiArcStringRenderer(private val arcMinR: Double, private val arcMaxR: Double, private val arcStart: Double, private val arcExtent: Double) : Renderer<AnchoredText?, Graphics2D?> {
    override fun render(primitive: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?) {
        canvas.setFont(Styles.fontOf(style))
        canvas.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        val fm = canvas.getFontMetrics()
        val avgR = .5 * (arcMinR + arcMaxR)
        val rend = WrappedTextRenderer()
        val lines = rend.computeLineBreaks(primitive.getText(), canvas.getFont(),
                arcExtent * arcMinR * Math.PI / 180, arcMaxR - arcMinR)
        var lineR = avgR - .5 * fm.height + .5 * lines.size * fm.height - fm.descent
        for (line in lines) {
            val newExtent = Math.min(arcExtent, fm.getStringBounds(line, canvas).width / lineR * 180 / Math.PI)
            val mid = arcStart + .5 * arcExtent
            val arc = Arc2D.Double(
                    primitive.getX() - lineR, primitive.getY() - lineR, 2 * lineR, 2 * lineR,
                    mid + .5 * newExtent, -newExtent, Arc2D.OPEN)
            TextPathRenderer().pathText(line)
                    .textStyle(style)
                    .render(arc, style, canvas)
            lineR -= fm.height.toDouble()
        }
    }

    override fun contains(point: Point2D?, primitive: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        // not supported yet
        return false
    }

    override fun intersects(rect: Rectangle2D?, primitive: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        // not supported yet
        return false
    }

    override fun boundingBox(primitive: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?): Rectangle2D? {
        // not supported yet
        return null
    }
}