package com.googlecode.blaisemath.graphics.core

import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Renderer
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
 */

/**
 * A collection of points that are treated as a single graphic. Customization is provided for tooltips and for dragging individual points,
 * but to customize any other attribute of graphics for individual points, use [DelegatingPointSetGraphic] instead.
 */
class BasicPointSetGraphic<G : Any> @JvmOverloads constructor(p: Array<Point2D> = arrayOf(), style: AttributeSet? = null, rend: Renderer<Point2D, G> = null)
    : PrimitiveArrayGraphic<Point2D, G>(p, style, rend) {

    /** Optional delegate for tooltips  */
    @Nullable
    protected var pointTipper: Function<Point2D?, String?>? = null

    //region PROPERTIES
    override fun setPrimitive(p: Array<Point2D?>?) {
        if (!Arrays.equals(primitives, p)) {
            val old: Any? = primitives
            primitives = p.clone()
            fireGraphicChanged()
            pcs.firePropertyChange(P_POINT, old, primitives)
        }
    }

    fun getPoint(i: Int): Point2D? {
        return primitives[i]
    }

    fun setPoint(i: Int, pt: Point2D?) {
        if (primitives[i] !== pt) {
            val old = primitives[i]
            primitives[i] = pt
            fireGraphicChanged()
            pcs.fireIndexedPropertyChange(P_POINT, i, old, primitives[i])
        }
    }

    fun getPointCount(): Int {
        return primitives.size
    }

    fun getPointTipDelegate(): Function<Point2D?, String?>? {
        return pointTipper
    }

    fun setPointTipDelegate(pointTipper: Function<Point2D?, String?>?) {
        this.pointTipper = pointTipper
    }

    //endregion
    override fun getTooltip(p: Point2D?, canvas: G?): String? {
        val i = indexOf(p, canvas)
        return if (i == -1) null else getPointTooltip(primitives[i])
    }

    /**
     * Overridable method that generates the default tooltip on a point
     * @param pt the point
     * @return formatted location of the point
     */
    fun getPointTooltip(pt: Point2D?): String? {
        Preconditions.checkNotNull(pt)
        return if (pointTipper == null) Points.format(pt, 1) else pointTipper.apply(pt)
    }
    //region INNER CLASSES
    /** Handles dragging of individual points  */
    inner class IndexedPointMover : GMouseDragHandler() {
        /** Index of point being dragged  */
        private var indexStart = 0

        /** Location at start of drag  */
        private var beanStart: Point2D? = null
        override fun mouseDragInitiated(e: GMouseEvent?, start: Point2D?) {
            // TODO - get canvas reference from somewhere??
            indexStart = indexOf(start, null)
            if (indexStart != -1) {
                beanStart = getPrimitive(indexStart)
            }
        }

        override fun mouseDragInProgress(e: GMouseEvent?, start: Point2D?) {
            if (indexStart != -1) {
                val dragPos = e.getGraphicLocation()
                val nueLoc: Point2D = Point2D.Double(beanStart.getX() + dragPos.x - start.getX(),
                        beanStart.getY() + dragPos.y - start.getY())
                setPrimitive(indexStart, nueLoc)
            }
        }

        override fun mouseDragCompleted(e: GMouseEvent?, start: Point2D?) {
            beanStart = null
            indexStart = -1
        }
    } //endregion

    companion object {
        val P_POINT: String? = "point"
    }
    /**
     * Construct with given primitive and style.
     * @param p initial point
     * @param style the style
     * @param rend renders the points
     */
    /**
     * Construct with no point (defaults to origin)
     */
    /**
     * Construct with no style (will use the default)
     * @param p initial point
     */
    init {
        val dragger = IndexedPointMover()
        addMouseListener(dragger)
        addMouseMotionListener(dragger)
    }
}