package com.googlecode.blaisemath.graphics.svg

import com.google.common.annotations.Beta
import com.googlecode.blaisemath.geom.AffineTransformBuilder
import com.googlecode.blaisemath.graphics.GraphicComposite
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import junit.framework.TestCase
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.geom.NoninvertibleTransformException
import java.awt.geom.Rectangle2D
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

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
 * A graphic designed to contain SVG content, to be rendered on a canvas. When drawn the source content will draw within
 * the target location on the canvas (defined by the `canvasBounds` property). To do this mapping, source content
 * should set either the `viewBox` field, to specify the boundaries to be fit explicitly, or else the
 *
 * @author Elisha Peterson
 */
@Beta
abstract class SvgGraphic : GraphicComposite<Graphics2D?>() {
    /** View box, representing the dimensions of the source SVG content.  */
    protected var viewBox: Rectangle2D = Rectangle2D.Double(0, 0, 100, 100)

    /** Viewport, representing the target rectangle on the canvas for content.  */
    protected var viewport: Rectangle2D = Rectangle2D.Double(0, 0, 100, 100)

    //region PROPERTIES
    fun getSize(): Dimension {
        return Dimension(viewport.width as Int, viewport.height as Int)
    }

    fun getViewBox(): Rectangle2D {
        return viewBox
    }

    fun setViewBox(viewBox: Rectangle2D) {
        if (this.viewBox !== Objects.requireNonNull(viewBox)) {
            val old: Any = this.viewBox
            this.viewBox = viewBox
            fireGraphicChanged()
            pcs.firePropertyChange(VIEW_BOX, old, viewBox)
        }
    }

    fun getViewport(): Rectangle2D {
        return viewport
    }

    fun setViewport(bounds: Rectangle2D) {
        if (viewport !== Objects.requireNonNull(bounds)) {
            val old: Any = viewport
            val oldSize: Any = getSize()
            viewport = bounds
            fireGraphicChanged()
            pcs.firePropertyChange(CANVAS_BOUNDS, old, bounds)
            pcs.firePropertyChange(SIZE, oldSize, getSize())
        }
    }
    //endregion
    /** Generate transform used to scale/translate the SVG. Transforms the viewbox to the viewport.  */
    protected fun transform(): AffineTransform? {
        return AffineTransformBuilder.transformingTo(viewport, viewBox)
    }

    /** Inverse transform. Transforms the graphic bounds to the view box.  */
    protected fun inverseTransform(): AffineTransform? {
        return try {
            transform().createInverse()
        } catch (ex: NoninvertibleTransformException) {
            LOG.log(Level.SEVERE, "Target viewbox has 0 width or height!", ex)
            null
        }
    }

    companion object {
        private val LOG = Logger.getLogger(SvgGraphic::class.java.name)
        val CANVAS_BOUNDS: String? = "canvasBounds"
        val VIEW_BOX: String? = "viewBox"
        val SIZE: String? = "size"
    }
}