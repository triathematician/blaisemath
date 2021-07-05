package com.googlecode.blaisemath.graphics.impl

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
import com.googlecode.blaisemath.primitive.AnchoredText
import com.googlecode.blaisemath.style.ObjectStyler
import junit.framework.TestCase
import java.awt.Point
import java.awt.geom.Point2D

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
 * Uses an [ObjectStyler] and a source object to draw a labeled point on a
 * canvas. The style of the point and label is managed by the styler, along with
 * the tooltip.
 *
 * @param <O> source object type
 * @param <G> graphics canvas type
 * @author Elisha Peterson
</G></O> */
class LabeledPointGraphic<O, G> @JvmOverloads constructor(source: O? = null, primitive: Point2D? = Point(), styler: ObjectStyler<O?>? = ObjectStyler()) : DelegatingPrimitiveGraphic<O?, Point2D?, G?>(source, primitive, styler, null) {
    private var textRenderer: Renderer<AnchoredText?, G?>? = null

    //region PROPERTIES
    fun getLabelRenderer(): Renderer<AnchoredText?, G?>? {
        return textRenderer
    }

    fun setLabelRenderer(textRenderer: Renderer<AnchoredText?, G?>?) {
        if (this.textRenderer !== textRenderer) {
            val old: Any? = this.textRenderer
            this.textRenderer = textRenderer
            pcs.firePropertyChange(P_LABEL_RENDERER, old, textRenderer)
        }
    }
    //endregion
    /**
     * Return label, if its visible.
     * @return label, or null if there is none visible
     */
    private fun visibleLabel(): String? {
        if (styler.getLabelDelegate() == null || textRenderer == null || styler.getLabelFilter() != null && !styler.getLabelFilter().test(source)) {
            return null
        }
        val label = styler.label(source)
        return if (Strings.isNullOrEmpty(label)) null else label
    }

    override fun renderTo(canvas: G?) {
        super.renderTo(canvas)
        val label = visibleLabel()
        if (label != null) {
            val style = styler.labelStyle(source)
            if (style != null) {
                val text = AnchoredText(primitive, label)
                getLabelRenderer().render(text, style, canvas)
            }
        }
    }

    companion object {
        val P_LABEL_RENDERER: String? = "labelRenderer"
    }
}