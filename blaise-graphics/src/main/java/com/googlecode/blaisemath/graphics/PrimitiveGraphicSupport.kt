package com.googlecode.blaisemath.graphics

import com.google.common.base.Preconditions
import com.googlecode.blaisemath.coordinate.CoordinateBean
import com.googlecode.blaisemath.coordinate.DraggableCoordinate
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import junit.framework.TestCase
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
*/ /**
 * Adds a primitive object and a renderer to a [Graphic]. Also
 * implements default drag functionality that will be supported when the primitive
 * is either a [Point2D] or a [DraggableCoordinate]. Attempts to
 * make other kinds of graphics draggable will result in an exception being thrown.
 * Implementations must provide the style used for rendering the primitive.
 *
 * @param <O> type of object being drawn
 * @param <G> type of graphics canvas to render to
 *
 * @author Elisha Peterson
</G></O> */
abstract class PrimitiveGraphicSupport<O, G> : Graphic<G?>() {
    /** What is being drawn  */
    protected var primitive: O? = null

    /** Draws the primitive on the graphics canvas  */
    protected var renderer: Renderer<O?, G?>? = null

    /** Whether graphic can be dragged  */
    protected var dragEnabled = false

    /** Handles the drag movement  */
    protected var dragger: GraphicMouseDragHandler? = null
    //region PROPERTIES
    /**
     * Return the shape for the graphic.
     * @return shape
     */
    fun getPrimitive(): O? {
        return primitive
    }

    /**
     * Set the shape for the graphic.
     * @param primitive shape
     */
    fun setPrimitive(primitive: O?) {
        if (this.primitive !== primitive) {
            val old: Any? = this.primitive
            this.primitive = primitive

            // if the primitive changes to something not supporting drag, make sure its turned off
            if (!isDragCapable()) {
                setDragEnabled(false)
            }
            fireGraphicChanged()
            pcs.firePropertyChange(P_PRIMITIVE, old, primitive)
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
            pcs.firePropertyChange(P_RENDERER, old, renderer)
        }
    }

    //endregion
    //region RENDERING
    override fun renderTo(canvas: G?) {
        if (renderer != null && primitive != null) {
            renderer.render(primitive, renderStyle(), canvas)
        }
    }

    override fun boundingBox(canvas: G?): Rectangle2D? {
        return if (renderer == null || primitive == null) null else renderer.boundingBox(primitive, renderStyle(), canvas)
    }

    override fun contains(point: Point2D?, canvas: G?): Boolean {
        return renderer != null && primitive != null && renderer.contains(point, primitive, renderStyle(), canvas)
    }

    override fun intersects(box: Rectangle2D?, canvas: G?): Boolean {
        return renderer != null && primitive != null && renderer.intersects(box, primitive, renderStyle(), canvas)
    }

    //endregion
    //region DRAGGING
    fun isDragCapable(): Boolean {
        return (primitive is Point2D || primitive is Shape
                || primitive is DraggableCoordinate<*> && (primitive as CoordinateBean<*>?).getPoint() is Point2D)
    }

    fun isDragEnabled(): Boolean {
        return dragEnabled
    }

    fun setDragEnabled(`val`: Boolean) {
        if (dragEnabled != `val`) {
            Preconditions.checkArgument(!`val` || isDragCapable())
            dragEnabled = `val`
            if (dragEnabled) {
                if (primitive is Shape) {
                    dragger = ShapeDragHandler()
                } else {
                    val bean = (if (primitive is DraggableCoordinate<*>) primitive as DraggableCoordinate<*>? else if (primitive is Point2D) ProxyPointDraggable() else null)!!
                    dragger = GraphicMouseMoveHandler(bean)
                }
                addMouseListener(dragger)
                addMouseMotionListener(dragger)
            } else {
                if (dragger != null) {
                    removeMouseListener(dragger)
                    removeMouseMotionListener(dragger)
                    dragger = null
                }
            }
        }
    }
    //endregion
    //region INNER CLASSES
    /** A draggable point generating events when it's position changes.  */
    private inner class ProxyPointDraggable : DraggableCoordinate<Point2D?> {
        override fun getPoint(): Point2D? {
            return primitive as Point2D?
        }

        override fun setPoint(p: Point2D?) {
            (primitive as Point2D?).setLocation(p)
        }

        override fun setPoint(initial: Point2D?, dragStart: Point2D?, dragFinish: Point2D?) {
            (primitive as Point2D?).setLocation(
                    initial.getX() + dragFinish.getX() - dragStart.getX(),
                    initial.getY() + dragFinish.getY() - dragStart.getY())
            fireGraphicChanged()
        }
    }

    /** A draggable shape generating events when it's position changes.  */
    private inner class ShapeDragHandler : GraphicMouseDragHandler() {
        private var initialShape: Shape? = null
        private var x0 = 0.0
        private var y0 = 0.0
        override fun mouseDragInitiated(e: GraphicMouseEvent?, start: Point2D?) {
            initialShape = primitive as Shape?
            if (initialShape is RectangularShape) {
                x0 = (initialShape as RectangularShape?).getX()
                y0 = (initialShape as RectangularShape?).getY()
            } else if (initialShape is Line2D) {
                x0 = (initialShape as Line2D?).getX1()
                y0 = (initialShape as Line2D?).getY1()
            }
        }

        override fun mouseDragInProgress(e: GraphicMouseEvent?, start: Point2D?) {
            val dx = e.getGraphicLocation().x - start.getX()
            val dy = e.getGraphicLocation().y - start.getY()
            if (dx == 0.0 && dy == 0.0) {
                return
            }
            if (initialShape is RectangularShape) {
                val rsh = initialShape as RectangularShape?
                rsh.setFrame(x0 + dx, y0 + dy, rsh.getWidth(), rsh.getHeight())
            } else if (initialShape is Line2D) {
                val line = initialShape as Line2D?
                setPrimitive(Line2D.Double(x0 + dx, y0 + dy, line.getX2() + dx, line.getY2() + dy) as O)
            } else {
                val at = AffineTransform()
                at.translate(dx, dy)
                setPrimitive(at.createTransformedShape(initialShape) as O)
            }
            fireGraphicChanged()
        }

        override fun mouseDragCompleted(e: GraphicMouseEvent?, start: Point2D?) {
            mouseDragInProgress(e, start)
        }
    } //endregion

    companion object {
        val P_PRIMITIVE: String? = "primitive"
        val P_RENDERER: String? = "renderer"
    }
}