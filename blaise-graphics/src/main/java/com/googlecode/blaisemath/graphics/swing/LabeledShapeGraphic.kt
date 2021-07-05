package com.googlecode.blaisemath.graphics.swing

import com.google.common.base.Strings
import com.googlecode.blaisemath.graphics.DelegatingPrimitiveGraphic
import com.googlecode.blaisemath.graphics.Renderer
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.graphics.swing.render.ShapeRenderer
import com.googlecode.blaisemath.graphics.swing.render.WrappedTextRenderer
import com.googlecode.blaisemath.primitive.AnchoredText
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.ObjectStyler
import junit.framework.TestCase
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.Shape
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
 * Customizable graphic that represents a labeled item.
 * Uses an [ObjectStyler] to customize appearance.
 *
 * @param <O> type of item
 *
 * @author Elisha Peterson
</O> */
class LabeledShapeGraphic<O> @JvmOverloads constructor(source: O? = null, primitive: Shape? = Rectangle(), styler: ObjectStyler<O?>? = ObjectStyler()) : DelegatingPrimitiveGraphic<O?, Shape?, Graphics2D?>(source, primitive, styler, ShapeRenderer.Companion.getInstance()) {
    private var textRenderer: Renderer<AnchoredText?, Graphics2D?>? = WrappedTextRenderer()

    //region PROPERTIES
    fun getTextRenderer(): Renderer<AnchoredText?, Graphics2D?>? {
        return textRenderer
    }

    fun setTextRenderer(textRenderer: Renderer<AnchoredText?, Graphics2D?>?) {
        if (this.textRenderer !== textRenderer) {
            this.textRenderer = textRenderer
            fireGraphicChanged()
        }
    }

    //endregion
    override fun renderTo(canvas: Graphics2D?) {
        super.renderTo(canvas)
        if (styler.getLabelDelegate() != null) {
            val label = styler.label(source)
            val style = styler.labelStyle(source)
            renderLabel(canvas, primitive, label, style)
        }
    }

    private fun renderLabel(canvas: Graphics2D?, primitive: Shape?, label: String?, style: AttributeSet?) {
        if (Strings.isNullOrEmpty(label) || style == null) {
            return
        }
        if (textRenderer is WrappedTextRenderer) {
            val wtr = textRenderer as WrappedTextRenderer?
            wtr.setTextBounds(wrappedLabelBounds(primitive))
        }
        textRenderer.render(AnchoredText(label), style, canvas)
    }

    companion object {
        /**
         * Get the bounding box used for wrapped text labels for the given shape.
         * @param primitive shape
         * @return label boundaries
         */
        fun wrappedLabelBounds(primitive: Shape?): RectangularShape? {
            return if (primitive is RectangularShape) primitive as RectangularShape? else primitive.getBounds2D()
        }
    }
}