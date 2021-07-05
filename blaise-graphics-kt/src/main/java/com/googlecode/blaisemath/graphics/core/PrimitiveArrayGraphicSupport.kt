package com.googlecode.blaisemath.graphics.core

import java.awt.geom.Point2D
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
 * Adds an array of primitive objects and a renderer to a [Graphic]. Also implements default drag functionality that will
 * be supported when the primitive is either a [Point2D] or a [DraggableHasCoordinate]. Attempts to make other kinds of
 * graphics draggable will result in an exception being thrown.
 *
 * @param <O> type of object being drawn
 * @param <G> type of graphics canvas to render to
 */
abstract class PrimitiveArrayGraphicSupport<O : Any, G : Any> : Graphic<G>() {

    /** What is being drawn  */
    var primitives: MutableList<O> = mutableListOf()
        set(value) {
            if (!field.contentEquals(value)) {
                val old = field
                field = value
                fireGraphicChanged()
                pcs.firePropertyChange(PrimitiveGraphicSupport.P_PRIMITIVE, old, value)
            }
        }

    /** Draws the primitive on the graphics canvas  */
    var renderer: Renderer<O, G>? = null
        set(value) {
            if (field != value) {
                val old = field
                field = value
                fireGraphicChanged()
                pcs.firePropertyChange(PrimitiveGraphicSupport.P_RENDERER, old, value)
            }
        }

    fun getPrimitive(i: Int) = primitives[i]
    fun setPrimitive(i: Int, prim: O) {
        if (primitives[i] !== prim) {
            val old = primitives[i]
            primitives[i] = prim
            fireGraphicChanged()
            pcs.fireIndexedPropertyChange(PrimitiveGraphicSupport.P_PRIMITIVE, i, old, prim)
        }
    }

    fun indexOf(nearby: Point2D, canvas: G?): Int {
        if (renderer == null) return -1

        val style = renderStyle()
        return primitives.indices.reversed().firstOrNull { renderer!!.contains(nearby, primitives[it], style, canvas) } ?: -1
    }

    override fun renderTo(canvas: G) {
        if (renderer == null) return
        val style = renderStyle()
        primitives.forEach { renderer!!.render(it, style, canvas) }
    }

    override fun boundingBox(canvas: G?): Rectangle2D? {
        val style = renderStyle()
        return boundingBox(primitives, { renderer!!.boundingBox(it, style, canvas) }, null)
    }

    override fun contains(point: Point2D, canvas: G?) = indexOf(point, canvas) != -1

    override fun intersects(box: Rectangle2D, canvas: G?): Boolean {
        if (renderer == null) return false
        val style = renderStyle()
        return primitives.any { renderer!!.intersects(box, it, style, canvas) }
    }
}