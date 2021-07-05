package com.googlecode.blaisemath.graphics.core

import java.awt.geom.Rectangle2D

/*
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
 */

/**
 * Encapsulates a set of graphics as a composite, along with elements used to create/update the graphics.
 * @param <T> type of object represented by the composite
 */
class UpdatingGraphicComposite<T, G: Any>(var updater: GraphicUpdater<T, G>) {

    /** Contains the graphic elements  */
    val composite = GraphicComposite<G>()

    /** Index for the graphics, based on source object  */
    private val index = mutableMapOf<T, Graphic<G>>()
    /** Index for the graphics, based on source object  */
    private val reverseIndex = mutableMapOf<Graphic<G>, T>()

    fun objectOf(gfc: Graphic<G>) = reverseIndex[gfc]
    fun graphicOf(obj: T) = index[obj]

    fun setObjects(data: Iterable<T>, locMap: (T) -> Rectangle2D) {
        val toRemove = composite.graphics - data.mapNotNull { index[it]!! }
        composite.removeGraphics(toRemove.toList())
        reverseIndex.keys.removeAll(toRemove)
        index.keys.retainAll(data)
        updateItemGraphics(locMap)
    }

    private fun updateItemGraphics(locMap: (T) -> Rectangle2D) {
        for (obj in index.keySet()) {
            val existing = index[obj]
            val gfc = updater.update(obj, locMap(obj), existing)
            if (existing == null) {
                index[obj] = gfc
                composite.addGraphic(gfc)
            }
        }
    }

}