package com.googlecode.blaisemath.graphics.swing.render

import com.google.common.base.Objects
import com.google.common.base.Strings
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
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
import java.awt.*
import java.awt.font.FontRenderContext
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
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
 * Renders a string of text on a canvas. An anchor is used to position the text relative to a point. The default anchor
 * is SOUTHWEST, with the text drawn to the right/above the point.
 *
 * @author Elisha Peterson
 */
open class TextRenderer : Renderer<AnchoredText?, Graphics2D?> {
    override fun render(primitive: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?) {
        render(setOf(primitive), style, canvas)
    }

    /**
     * Render a collection of text primitives at one time.
     * @param primitives the primitives to render
     * @param style the style used for rendering
     * @param canvas where to render it
     */
    fun render(primitives: Iterable<AnchoredText?>?, style: AttributeSet?, canvas: Graphics2D?) {
        canvas.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        canvas.setColor(style.getColor(Styles.FILL, Color.black))
        canvas.setFont(Styles.fontOf(style))
        for (at in primitives) {
            if (!Strings.isNullOrEmpty(at.getText())) {
                val bounds = boundingBox(at, style, canvas)
                canvas.drawString(at.getText(), bounds.getX() as Float, bounds.getMaxY() as Float)
            }
        }
    }

    override fun contains(point: Point2D?, primitive: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        val bounds = boundingBox(primitive, style, canvas)
        return bounds != null && bounds.contains(point)
    }

    override fun intersects(rect: Rectangle2D?, primitive: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        val bounds = boundingBox(primitive, style, canvas)
        return bounds != null && bounds.intersects(rect)
    }

    /**
     * Get the bounding box for the given text/style to be rendered on the given canvas. This computation can be expensive, so the
     * results are cached so that if the text, font, and render context do not change, the cached results are used.
     * @param primitive text/location
     * @param style desired style
     * @param canvas where to render
     * @return bounding box for the result
     */
    override fun boundingBox(primitive: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?): Rectangle2D? {
        if (Strings.isNullOrEmpty(primitive.getText())) {
            return null
        }
        val font = Styles.fontOf(style)
        val frc = if (canvas == null) FontRenderContext(font.transform, true, false) else canvas.fontRenderContext
        val info = TextBoundsInfo(primitive.getText(), font, frc)
        val dimensions: Rectangle2D.Double?
        dimensions = try {
            CACHE[info]
        } catch (e: ExecutionException) {
            LOG.log(Level.FINE, "Unexpected", e)
            textDimensions(info)
        }
        val textAnchor = Styles.anchorOf(style, Anchor.SOUTHWEST)
        val offset = style.getPoint2D(Styles.OFFSET, Point())!!
        return textAnchor.rectangleAnchoredAt(
                primitive.getX() + offset.x,
                primitive.getY() + offset.y,
                dimensions.width, dimensions.height)
    }

    /** Info required for most expensive font computation.  */
    private class TextBoundsInfo private constructor(private val text: String?, private val font: Font?, private val context: FontRenderContext?) {
        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false
            val that = o as TextBoundsInfo?
            return Objects.equal(text, that.text) &&
                    Objects.equal(font, that.font) &&
                    Objects.equal(context, that.context)
        }

        override fun hashCode(): Int {
            return Objects.hashCode(text, font, context)
        }
    }

    companion object {
        /** Assumed monitor resolution, used in bounding box calculations  */
        private const val DOTS_PER_INCH = 72

        /** Logging  */
        private val LOG = Logger.getLogger(TextRenderer::class.java.name)

        /** Static instance  */
        private val INST: TextRenderer? = TextRenderer()

        /** Caches expensive computation of font bounds  */
        private val CACHE = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build(object : CacheLoader<TextBoundsInfo?, Rectangle2D.Double?>() {
                    override fun load(textBoundsInfo: TextBoundsInfo?): Rectangle2D.Double? {
                        return textDimensions(textBoundsInfo)
                    }
                })

        /**
         * Get default static instance of the renderer.
         * @return renderer
         */
        fun getInstance(): TextRenderer? {
            return INST
        }

        private fun textDimensions(info: TextBoundsInfo?): Rectangle2D.Double? {
            return textDimensions(info.text, info.font, info.context)
        }

        private fun textDimensions(text: String?, font: Font?, context: FontRenderContext?): Rectangle2D.Double? {
            val width = font.getStringBounds(text, context).width
            val height = font.getSize() * DOTS_PER_INCH / Toolkit.getDefaultToolkit().screenResolution
            return Rectangle2D.Double(0, 0, width, height)
        }
    }
}