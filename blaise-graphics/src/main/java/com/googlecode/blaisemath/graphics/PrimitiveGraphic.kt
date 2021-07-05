package com.googlecode.blaisemath.graphics

import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.style.AttributeSet
import junit.framework.TestCase

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
 * A basic graphic object with a style set.
 *
 * @param <O> type of primitive
 * @param <G> type of graphics canvas to render to
 *
 * @author Elisha Peterson
</G></O> */
open class PrimitiveGraphic<O, G> : PrimitiveGraphicSupport<O?, G?> {
    /** The style set for this graphic  */
    protected var style: AttributeSet? = AttributeSet()

    constructor() {}
    constructor(primitive: O?, style: AttributeSet?, renderer: Renderer<O?, G?>?) {
        setPrimitive(primitive)
        setStyle(style)
        setRenderer(renderer)
    }

    override fun toString(): String {
        return "PrimitiveGraphic{$primitive}"
    }

    //region PROPERTIES
    override fun getStyle(): AttributeSet? {
        return style
    }

    fun setStyle(sty: AttributeSet?) {
        if (style !== sty) {
            val old: Any? = style
            style = sty
            fireGraphicChanged()
            pcs.firePropertyChange(P_STYLE, old, style)
        }
    } //endregion

    companion object {
        val P_STYLE: String? = "style"
    }
}