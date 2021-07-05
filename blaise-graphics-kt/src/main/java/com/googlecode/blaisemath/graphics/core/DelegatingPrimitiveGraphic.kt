package com.googlecode.blaisemath.graphics.core

import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.ObjectStyler
import com.googlecode.blaisemath.style.Renderer
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
*/

/**
 * A graphic that maintains a source object and uses an [ObjectStyler] delegate to retrieve its style set.
 * @param <S> type of source object
 * @param <O> type of primitive
 * @param <G> type of graphics canvas to render to
 */
open class DelegatingPrimitiveGraphic<S : Any, O : Any, G : Any>(_source: S, _primitive: O, _styler: ObjectStyler<S>, _renderer: Renderer<O, G>) : PrimitiveGraphicSupport<O, G>() {

    init {
        primitive = _primitive
        renderer = _renderer
    }

    /** The source object  */
    var sourceObject: S = _source
        set(value) {
            if (field != value) {
                field = value
                sourceUpdated()
            }
        }
    /** The style set for this graphic  */
    var styler: ObjectStyler<S> = _styler
        set(value) {
            if (field != value) {
                field = value
                sourceUpdated()
            }
        }

    override val style: AttributeSet
        get() = styler.style(sourceObject)

    override fun initContextMenu(menu: JPopupMenu, src: Graphic<G>, point: Point2D, focus: Any?, selection: Set<Graphic<G>>, canvas: G) {
        // use primitive source for focus parameter
        super.initContextMenu(menu, src, point, sourceObject, selection, canvas)
    }

    override fun getTooltip(p: Point2D, canvas: G?) = styler.tooltip(sourceObject)

    /**
     * Hook method for updating the shape attributes after the source graphic or style has changed.
     * This version of the method notifies listeners that the graphic has changed.
     */
    protected fun sourceUpdated() {
        fireGraphicChanged()
    }
}