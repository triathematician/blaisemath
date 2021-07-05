package com.googlecode.blaisemath.graphics.core

import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer
import java.awt.geom.Rectangle2D

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
 * A graphic updater that operates on a per-item basis, and provides built in
 * functions for setting graphic styles, and for initializing graphic context menus.
 * @param <E> type of object being updated
 * @param <G> graphics canvas
 */
abstract class AttributeGraphicUpdater<E, G : Any> : GraphicUpdater<E, G> {

    /** Generates attributes from base object.  */
    var attributeMap: ((E) -> AttributeSet)? = null
    /** Initializes context menus  */
    var menu: ContextMenuInitializer<E>? = null

    /**
     * Create a new graphic for the given object. The [.update]
     * method will first create an [AttributeSet] associated with the object,
     * provided to this method as the `style` parameter, and after creating
     * the graphic will register a [ContextMenuInitializer] if set.
     * @param e the object represented by the graphic
     * @param attr attributes for the graphic, as created by the attribute function
     * @param bounds desired bounding box for the graphic
     * @return graphic
     */
    abstract fun create(e: E, attr: AttributeSet, bounds: Rectangle2D): Graphic<G>

    /**
     * Update an existing graphic for the given object.
     * @param e the object represented by the graphic
     * @param attr attributes for the graphic, as created by the attribute function
     * @param bounds desired bounding box for the graphic
     * @param existing the existing graphic (guaranteed to be non-null)
     */
    abstract fun update(e: E, attr: AttributeSet, bounds: Rectangle2D, existing: Graphic<*>?)

    override fun update(e: E, bounds: Rectangle2D, existing: Graphic<G>?): Graphic<G> {
        val attr = attributeMap?.invoke(e) ?: AttributeSet()
        return if (existing == null) {
            create(e, attr, bounds).apply {
                menu?.let { addContextMenuInitializer(it) }
            }
        } else {
            update(e, attr, bounds, existing)
            existing
        }
    }
}