package com.googlecode.blaisemath.graphics

import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.ObjectStyler
import junit.framework.TestCase
import java.awt.geom.Point2D
import javax.swing.JPopupMenu

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
 * A graphic that maintains a source object and uses an [ObjectStyler]
 * delegate to retrieve its style set.
 *
 * @param <S> type of source object
 * @param <O> type of primitive
 * @param <G> type of graphics canvas to render to
 *
 * @author Elisha Peterson
</G></O></S> */
open class DelegatingPrimitiveGraphic<S, O, G> : PrimitiveGraphicSupport<O?, G?> {
    /** The source object  */
    protected var source: S? = null

    /** The style set for this graphic  */
    protected var styler: ObjectStyler<S?>? = null

    constructor() {}
    constructor(source: S?, primitive: O?, styler: ObjectStyler<S?>?, renderer: Renderer<O?, G?>?) {
        setPrimitive(primitive)
        setSourceObject(source)
        setObjectStyler(styler)
        setRenderer(renderer)
    }

    //region PROPERTIES
    override fun getStyle(): AttributeSet? {
        return styler.style(source)
    }

    fun getSourceObject(): S? {
        return source
    }

    fun setSourceObject(source: S?) {
        this.source = source
        sourceGraphicUpdated()
    }

    fun getObjectStyler(): ObjectStyler<S?>? {
        return styler
    }

    fun setObjectStyler(styler: ObjectStyler<S?>?) {
        if (this.styler != styler) {
            this.styler = styler
            sourceGraphicUpdated()
        }
    }

    //endregion
    override fun initContextMenu(menu: JPopupMenu?, src: Graphic<G?>?, point: Point2D?, focus: Any?, selection: MutableSet<Graphic<G?>?>?, canvas: G?) {
        // use primitive source for focus parameter
        super.initContextMenu(menu, src, point, source, selection, canvas)
    }

    /**
     * Return the tooltip provided by the object styler.
     * @param p point for tooltip
     * @param canvas canvas
     * @return tooltip
     */
    override fun getTooltip(p: Point2D?, canvas: G?): String? {
        return if (styler == null) null else styler.tooltip(source, null)
    }

    /**
     * Hook method for updating the shape attributes after the source graphic or style has changed.
     * This version of the method notifies listeners that the graphic has changed.
     */
    protected fun sourceGraphicUpdated() {
        fireGraphicChanged()
    }
}