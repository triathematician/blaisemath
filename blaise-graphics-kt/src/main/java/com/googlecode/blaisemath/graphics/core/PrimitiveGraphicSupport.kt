package com.googlecode.blaisemath.graphics.core

import com.googlecode.blaisemath.coordinate.DraggableHasCoordinate
import com.googlecode.blaisemath.coordinate.HasCoordinate
import com.googlecode.blaisemath.style.Renderer
import com.googlecode.blaisemath.util.geom.Line2
import com.googlecode.blaisemath.util.geom.minus
import com.googlecode.blaisemath.util.geom.plus
import java.awt.Shape
import java.awt.geom.*

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
 * Adds a primitive object and a renderer to a [Graphic]. Also
 * implements default drag functionality that will be supported when the primitive
 * is either a [Point2D] or a [DraggableHasCoordinate]. Attempts to
 * make other kinds of graphics draggable will result in an exception being thrown.
 * Implementations must provide the style used for rendering the primitive.
 *
 * @param <O> type of object being drawn
 * @param <G> type of graphics canvas to render to
 */
abstract class PrimitiveGraphicSupport<O: Any, G : Any> : Graphic<G>() {

    /** What is being drawn  */
    var primitive: O? = null
        set(value) {
            val old = field
            if (field != value) {
                field = value
                // if the primitive changes to something not supporting drag, make sure its turned off
                if (!isDragCapable) isDragEnabled = false
                fireGraphicChanged()
                pcs.firePropertyChange(P_PRIMITIVE, old, field)
            }
        }

    /** Draws the primitive on the graphics canvas  */
    var renderer: Renderer<O, G>? = null
        set(value) {
            val old = field
            if (field != value) {
                field = value
                fireGraphicChanged()
                pcs.firePropertyChange(P_RENDERER, old, field)
            }
        }

    /** Whether graphic is capable of being dragged */
    val isDragCapable
        get() = primitive is Point2D || primitive is Shape ||
                (primitive is DraggableHasCoordinate<*> && (primitive as HasCoordinate<*>).point is Point2D)

    /** Whether graphic can currently be dragged */
    var isDragEnabled = false
        set(value) {
            if (field != value) {
                require(!value || isDragCapable)
                field = value
                if (field) {
                    dragger = when (primitive) {
                        is Shape -> ShapeDragHandler()
                        else -> {
                            val bean = (primitive as? DraggableHasCoordinate<Point2D>) ?: if (primitive is Point2D) ProxyPointDraggable() else null
                            GraphicMoveHandler(bean!!)
                        }
                    }
                    addMouseListener(dragger!!)
                    addMouseMotionListener(dragger!!)
                } else {
                    if (dragger != null) {
                        removeMouseListener(dragger!!)
                        removeMouseMotionListener(dragger!!)
                        dragger = null
                    }
                }
            }
        }

    /** Handles the drag movement  */
    protected var dragger: GMouseDragHandler? = null

    override fun renderTo(canvas: G) { whenPresent { p, r -> r.render(p, renderStyle(), canvas) } }
    override fun boundingBox(canvas: G?) = whenPresent { p, r -> r.boundingBox(p, renderStyle(), canvas) }
    override fun contains(point: Point2D, canvas: G?) = whenPresent { p, r -> r.contains(point, p, renderStyle(), canvas) } ?: false
    override fun intersects(box: Rectangle2D, canvas: G?) = whenPresent { p, r -> r.intersects(box, p, renderStyle(), canvas) } ?: false

    /** Performs null checks on primitive and renderer, executes op if both non-null. */
    private fun <X> whenPresent(op: (O, Renderer<O, G>) -> X): X? {
        val p = primitive
        val r = renderer
        return if (p != null && r != null) op(p, r) else null
    }

    //endregion

    //region INNER CLASSES

    /** A draggable point generating events when it's position changes.  */
    private inner class ProxyPointDraggable : DraggableHasCoordinate<Point2D> {
        override var point: Point2D
            get() = primitive as Point2D
            set(value) { (primitive as Point2D).setLocation(value) }

        override fun setPoint(initial: Point2D, dragStart: Point2D, dragFinish: Point2D) {
            (primitive as Point2D).setLocation(initial + dragFinish - dragStart)
            fireGraphicChanged()
        }
    }

    /** A draggable shape generating events when it's position changes.  */
    private inner class ShapeDragHandler : GMouseDragHandler() {
        private var initialShape: Shape? = null
        private var x0 = 0.0
        private var y0 = 0.0

        override fun mouseDragInitiated(e: GMouseEvent, start: Point2D?) {
            initialShape = primitive as Shape?
            if (initialShape is RectangularShape) {
                x0 = (initialShape as RectangularShape).x
                y0 = (initialShape as RectangularShape).y
            } else if (initialShape is Line2D) {
                x0 = (initialShape as Line2D).x1
                y0 = (initialShape as Line2D).y1
            }
        }

        override fun mouseDragInProgress(e: GMouseEvent, start: Point2D) {
            val dx = e.graphicLocation.x - start.x
            val dy = e.graphicLocation.y - start.y
            if (dx == 0.0 && dy == 0.0) {
                return
            }
            when (initialShape) {
                is RectangularShape -> {
                    val rsh = initialShape as RectangularShape
                    rsh.setFrame(x0 + dx, y0 + dy, rsh.width, rsh.height)
                }
                is Line2D -> {
                    val line = initialShape as Line2D
                    primitive = Line2(x0 + dx, y0 + dy, line.x2 + dx, line.y2 + dy) as O
                }
                else -> {
                    val at = AffineTransform()
                    at.translate(dx, dy)
                    primitive = at.createTransformedShape(initialShape) as O
                }
            }
            fireGraphicChanged()
        }

        override fun mouseDragCompleted(e: GMouseEvent, start: Point2D) {
            mouseDragInProgress(e, start)
        }
    }

    //endregion

    companion object {
        val P_PRIMITIVE: String? = "primitive"
        val P_RENDERER: String? = "renderer"
    }
}