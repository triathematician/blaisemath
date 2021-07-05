package com.googlecode.blaisemath.graphics

import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import junit.framework.TestCase
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.util.*

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
 * Adds an array of primitive objects and a renderer to a [Graphic]. Also
 * implements default drag functionality that will be supported when the primitive
 * is either a [Point2D] or a [DraggableCoordinate]. Attempts to
 * make other kinds of graphics draggable will result in an exception being thrown.
 *
 * @param <O> type of object being drawn
 * @param <G> type of graphics canvas to render to
 *
 * @author Elisha Peterson
</G></O> */
abstract class PrimitiveArrayGraphicSupport<O, G> : Graphic<G?>() {
    /** What is being drawn  */
    protected var primitive: Array<O?>?

    /** Draws the primitive on the graphics canvas  */
    protected var renderer: Renderer<O?, G?>? = null
    //region PROPERTIES
    /**
     * Return the shape for the graphic.
     * @return shape
     */
    fun getPrimitive(): Array<O?>? {
        return primitive
    }

    /**
     * Set the shape for the graphic.
     * @param primitive shape
     */
    open fun setPrimitive(primitive: Array<O?>?) {
        if (this.primitive != primitive) {
            val old: Any? = this.primitive
            this.primitive = primitive
            fireGraphicChanged()
            pcs.firePropertyChange(PrimitiveGraphicSupport.Companion.P_PRIMITIVE, old, primitive)
        }
    }

    /**
     * Return the i'th primitive
     * @param i index of primitive
     * @return primitive
     */
    fun getPrimitive(i: Int): O? {
        return primitive.get(i)
    }

    /**
     * Set the i'th primitive
     * @param i index of primitive
     * @param prim the primitive
     */
    fun setPrimitive(i: Int, prim: O?) {
        if (primitive.get(i) !== prim) {
            val old: Any? = primitive.get(i)
            primitive.get(i) = prim
            fireGraphicChanged()
            pcs.fireIndexedPropertyChange(PrimitiveGraphicSupport.Companion.P_PRIMITIVE, i, old, prim)
        }
    }

    fun getRenderer(): Renderer<O?, G?>? {
        return renderer
    }

    fun setRenderer(renderer: Renderer<O?, G?>?) {
        if (this.renderer !== renderer) {
            val old: Any? = this.renderer
            this.renderer = renderer
            fireGraphicChanged()
            pcs.firePropertyChange(PrimitiveGraphicSupport.Companion.P_RENDERER, old, renderer)
        }
    }

    //endregion
    fun indexOf(nearby: Point2D?, canvas: G?): Int {
        if (renderer == null) {
            return -1
        }
        val style = renderStyle()
        for (i in primitive.indices.reversed()) {
            if (renderer.contains(nearby, primitive.get(i), style, canvas)) {
                return i
            }
        }
        return -1
    }

    override fun renderTo(canvas: G?) {
        if (renderer == null) {
            return
        }
        val style = renderStyle()
        for (o in primitive) {
            renderer.render(o, style, canvas)
        }
    }

    override fun boundingBox(canvas: G?): Rectangle2D? {
        val style = renderStyle()
        return GraphicUtils.boundingBox(Arrays.asList(*primitive), { p: O? -> renderer.boundingBox(p, style, canvas) }, null)
    }

    override fun contains(point: Point2D?, canvas: G?): Boolean {
        return indexOf(point, canvas) != -1
    }

    override fun intersects(box: Rectangle2D?, canvas: G?): Boolean {
        if (renderer == null) {
            return false
        }
        val style = renderStyle()
        return Arrays.stream(primitive).anyMatch { o: O? -> renderer.intersects(box, o, style, canvas) }
    }
}