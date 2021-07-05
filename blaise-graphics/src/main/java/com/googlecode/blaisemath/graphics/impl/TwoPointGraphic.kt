package com.googlecode.blaisemath.graphics.impl

import com.googlecode.blaisemath.coordinate.OrientedPoint2D
import com.googlecode.blaisemath.graphics.Graphic
import com.googlecode.blaisemath.graphics.GraphicComposite
import com.googlecode.blaisemath.graphics.PrimitiveGraphic
import com.googlecode.blaisemath.graphics.Renderer
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.style.Styles
import junit.framework.TestCase
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
*/ /**
 * Provides methods for managing a graphic that depends on two underlying points,
 * e.g. a segment, arrow, etc. While this class can be instantiated and used,
 * it is intended mostly to provide a convenient superclass.
 *
 * @param <G> graphics canvas type
 *
 * @author Elisha Peterson
</G> */
open class TwoPointGraphic<G>(start: Point2D?, end: Point2D?, renderer: Renderer<Point2D?, G?>?) : GraphicComposite<G?>() {
    /** Point at start of arrow  */
    protected val start: PrimitiveGraphic<Point2D?, G?>?

    /** Point at end of arrow  */
    protected val end: PrimitiveGraphic<Point2D?, G?>?
    fun getStartGraphic(): PrimitiveGraphic<Point2D?, G?>? {
        return start
    }

    fun getEndGraphic(): PrimitiveGraphic<Point2D?, G?>? {
        return end
    }

    /**
     * Updates the points. This should be called whenever the points change.
     * The functionality here computes and adjusts the angles at the points,
     * so that the points are directed away from each other, and then calls
     * [GraphicComposite.fireGraphicChanged].
     */
    protected open fun pointsUpdated() {
        if (start.getPrimitive() !is OrientedPoint2D) {
            start.setPrimitive(OrientedPoint2D(start.getPrimitive()))
        }
        if (end.getPrimitive() !is OrientedPoint2D) {
            end.setPrimitive(OrientedPoint2D(end.getPrimitive()))
        }
        (start.getPrimitive() as OrientedPoint2D?).awayFrom(end.getPrimitive())
        (end.getPrimitive() as OrientedPoint2D?).awayFrom(start.getPrimitive())
        fireGraphicChanged()
    }

    //region EVENTS
    fun isDragEnabled(): Boolean {
        return start.isDragEnabled() && end.isDragEnabled()
    }

    fun setDragEnabled(`val`: Boolean) {
        start.setDragEnabled(`val`)
        end.setDragEnabled(`val`)
    }

    override fun graphicChanged(source: Graphic<*>?) {
        if (source === start || source === end) {
            pointsUpdated()
        } else {
            super.graphicChanged(source)
        }
    } //endregion

    /**
     * Construct graphic with specified base points
     * @param start starting point
     * @param end ending point
     * @param renderer renderer for points
     */
    init {
        style = Styles.DEFAULT_POINT_STYLE.copy()
        this.start = PrimitiveGraphic(OrientedPoint2D(start), style, renderer)
        this.end = PrimitiveGraphic(OrientedPoint2D(end), style, renderer)
        addGraphic(this.start)
        addGraphic(this.end)
        pointsUpdated()
    }
}