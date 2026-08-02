package com.googlecode.blaisemath.graphics.impl

import com.googlecode.blaisemath.graphics.PrimitiveGraphic
import com.googlecode.blaisemath.graphics.Renderer
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.primitive.Markers
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.StyleHints
import com.googlecode.blaisemath.style.Styles
import junit.framework.TestCase
import java.awt.Color
import java.awt.Shape
import java.awt.geom.Line2D
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
 * Displays a line segment between two points, with the possibility of adding
 * arrows on one or both ends.
 *
 * @param <G> graphics canvas type
 *
 * @author Elisha Peterson
</G> */
class SegmentGraphic<G>(ps: Point2D?, pe: Point2D?, loc: ArrowLocation?, renderer: Renderer<Point2D?, G?>?, pathRenderer: Renderer<Shape?, G?>?) : TwoPointGraphic<G?>(ps, pe, renderer) {
    /** Entry with the line  */
    protected var lineGraphic: PrimitiveGraphic<Shape?, G?>? = PrimitiveGraphic(null, Styles.DEFAULT_PATH_STYLE.copy(), null)

    /** Where arrows are displayed  */
    protected var arrowLoc: ArrowLocation? = null
    override fun pointsUpdated() {
        super.pointsUpdated()
        if (lineGraphic == null) {
            lineGraphic = PrimitiveGraphic(null, Styles.DEFAULT_PATH_STYLE.copy(), null)
        }
        lineGraphic.setPrimitive(Line2D.Double(start.primitive, end.primitive))
    }

    fun getLineStyle(): AttributeSet? {
        return lineGraphic.getStyle()
    }

    fun setLineStyle(s: AttributeSet?) {
        lineGraphic.setStyle(s)
    }

    fun getArrowLocation(): ArrowLocation? {
        return arrowLoc
    }

    fun setArrowLocation(arrowLoc: ArrowLocation?) {
        this.arrowLoc = arrowLoc
        setArrow(start, arrowLoc == ArrowLocation.BOTH || arrowLoc == ArrowLocation.START)
        setArrow(end, arrowLoc == ArrowLocation.BOTH || arrowLoc == ArrowLocation.END)
    }

    companion object {
        private fun setArrow(gr: PrimitiveGraphic<*, *>?, `val`: Boolean) {
            gr.getStyle().put(Styles.MARKER, if (`val`) Markers.ARROWHEAD else null)
            gr.setStyleHint(StyleHints.HIDDEN_FUNCTIONAL_HINT, !`val`)
        }
    }

    /**
     * Construct segment between specified points
     * @param ps start of segment
     * @param pe end of segment
     * @param loc where to position arrows, relative to start and end
     * @param renderer renderer for points
     * @param pathRenderer renderer for paths
     */
    init {
        setArrowLocation(loc)
        start.style = Styles.marker(Markers.CIRCLE, Color.black, 2f)
        start.setStyleHint(StyleHints.HIDDEN_FUNCTIONAL_HINT, true)
        end.style = Styles.DEFAULT_POINT_STYLE.copy().and(Styles.MARKER, Markers.ARROWHEAD)
        lineGraphic.setRenderer(pathRenderer)
        addGraphic(lineGraphic)
    }
}