package com.googlecode.blaisemath.graph.app
/*
 * #%L
 * BlaiseGraphTheory (v3)
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
import com.google.common.collect.Maps
import com.googlecode.blaisemath.annotation.InvokedFromThread
import com.googlecode.blaisemath.coordinate.CoordinateManager
import com.googlecode.blaisemath.graph.StaticGraphLayout
import com.googlecode.blaisemath.graph.layout.GraphLayoutManager
import com.googlecode.blaisemath.graphics.svg.SvgElementGraphicConverter
import com.googlecode.blaisemath.graphics.svg.SvgGraphic
import com.googlecode.blaisemath.graphics.svg.SvgGraphicComponent
import com.googlecode.blaisemath.graphics.svg.SvgUtils
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler
import com.googlecode.blaisemath.style.xml.AttributeSetAdapter
import com.googlecode.blaisemath.svg.HelloWorldSvg
import com.googlecode.blaisemath.svg.SvgCircle
import com.googlecode.blaisemath.svg.SvgCircle.CircleConverter
import com.googlecode.blaisemath.svg.SvgElement
import com.googlecode.blaisemath.svg.SvgElements
import com.googlecode.blaisemath.svg.SvgEllipse
import com.googlecode.blaisemath.svg.SvgEllipse.EllipseConverter
import com.googlecode.blaisemath.svg.SvgGroup
import com.googlecode.blaisemath.svg.SvgImage
import com.googlecode.blaisemath.svg.SvgImage.ImageConverter
import com.googlecode.blaisemath.svg.SvgIo
import com.googlecode.blaisemath.svg.SvgLine
import com.googlecode.blaisemath.svg.SvgLine.LineConverter
import com.googlecode.blaisemath.svg.SvgNamespaceFilter
import com.googlecode.blaisemath.svg.SvgPath
import com.googlecode.blaisemath.svg.SvgPath.SvgPathOperator
import com.googlecode.blaisemath.svg.SvgPathTest
import com.googlecode.blaisemath.svg.SvgPolygon
import com.googlecode.blaisemath.svg.SvgPolygon.PolygonConverter
import com.googlecode.blaisemath.svg.SvgPolyline
import com.googlecode.blaisemath.svg.SvgPolyline.PolylineConverter
import com.googlecode.blaisemath.svg.SvgRectangle
import com.googlecode.blaisemath.svg.SvgRectangle.RectangleConverter
import com.googlecode.blaisemath.svg.SvgRoot
import com.googlecode.blaisemath.svg.SvgRootTest
import com.googlecode.blaisemath.svg.SvgText
import com.googlecode.blaisemath.svg.SvgText.TextConverter
import com.googlecode.blaisemath.svg.SvgTool
import com.googlecode.blaisemath.util.Images
import com.googlecode.blaisemath.util.geom.Points
import com.googlecode.blaisemath.util.swing.AnimationStep
import com.googlecode.blaisemath.util.swing.AnimationStep.Companion.animate
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

/**
 * Helps generate animations with coordinate manager and graph layout algorithm.
 * @author Elisha Peterson
 */
internal object AnimationUtils {
    /** Default number of steps to use in animating pan/zoom  */
    private const val ANIM_STEPS = 25

    /** How long between animation steps  */
    private const val ANIM_DELAY_MILLIS = 5

    /**
     * Animate change of node positions from current to new values.
     * @param <N> graph node type
     * @param <P> parameters type
     * @param gc graphic component, for coordinated zooming
     * @param glm layout manager
     * @param layout layout class
     * @param p layout parameters
     * @param margin margin for setting boundaries of graph component
    </P></N> */
    fun <N, P> animateCoordinateChange(
            glm: GraphLayoutManager<N?>?, layout: StaticGraphLayout<P?>?, p: P?,
            gc: JGraphicComponent?, margin: Double
    ) {
        val newLocations = layout.layout(glm.getGraph(), glm.getNodeLocationCopy(), p)
        if (gc == null) {
            animateCoordinateChange(glm.getCoordinateManager(), newLocations)
        } else {
            animateAndZoomCoordinateChange(glm.getCoordinateManager(), newLocations, gc, margin)
        }
    }

    /**
     * Animate change of node positions from current to new values.
     * @param <S> type of source object
     * @param cm coordinate manager
     * @param locations new locations to animate to
    </S> */
    private fun <S> animateCoordinateChange(cm: CoordinateManager<S?, Point2D.Double?>?, locations: MutableMap<S?, Point2D.Double?>?) {
        val oldLocations: MutableMap<S?, Point2D.Double?> = cm.getLocationCopy(locations.keys)
        animate(0, ANIM_STEPS, ANIM_DELAY_MILLIS, object : AnimationStep() {
            @InvokedFromThread("AnimationStep")
            override fun run(idx: Int, pct: Double) {
                val requestLocations: MutableMap<S?, Point2D.Double?> = Maps.newHashMap()
                for (s in locations.keys) {
                    val old = oldLocations[s]
                    val nue = locations.get(s)
                    requestLocations[s] = if (old == null) nue else Point2D.Double(old.x * (1 - pct) + nue.x * pct, old.y * (1 - pct) + nue.y * pct)
                }
                cm.setCoordinateMap(requestLocations)
            }
        })
    }

    /**
     * Animate change of node positions from current to new values, where the animation
     * is coordinated with setting the graphic component's bounds.
     * @param <S> type of source object
     * @param cm coordinate manager
     * @param locations new locations to animate to
     * @param gc graphic component, for coordinated zooming
     * @param margin margin for setting boundaries of graph component
    </S> */
    private fun <S> animateAndZoomCoordinateChange(
            cm: CoordinateManager<S?, Point2D.Double?>?, locations: MutableMap<S?, Point2D.Double?>?,
            gc: JGraphicComponent?, margin: Double
    ) {
        if (locations.isEmpty()) {
            return
        }
        val oldBounds: Rectangle2D.Double = PanAndZoomHandler.Companion.getLocalBounds(gc)
        val xMin = oldBounds.minX
        val yMin = oldBounds.minY
        val xMax = oldBounds.maxX
        val yMax = oldBounds.maxY
        val newBounds: Rectangle2D.Double = Points.boundingBox(locations.values, margin)!!
        val nxMin = newBounds.minX
        val nyMin = newBounds.minY
        val nxMax = newBounds.maxX
        val nyMax = newBounds.maxY
        val oldLocations: MutableMap<S?, Point2D.Double?> = cm.getLocationCopy(locations.keys)
        animate(0, ANIM_STEPS, ANIM_DELAY_MILLIS, object : AnimationStep() {
            @InvokedFromThread("AnimationStep")
            override fun run(idx: Int, pct: Double) {
                val requestLocations: MutableMap<S?, Point2D.Double?> = Maps.newHashMap()
                for (s in locations.keys) {
                    val old = oldLocations[s]
                    val nue = locations.get(s)
                    requestLocations[s] = if (old == null) nue else Point2D.Double(old.x * (1 - pct) + nue.x * pct, old.y * (1 - pct) + nue.y * pct)
                }
                cm.setCoordinateMap(requestLocations)
                val x1 = xMin + (nxMin - xMin) * pct
                val y1 = yMin + (nyMin - yMin) * pct
                val x2 = xMax + (nxMax - xMax) * pct
                val y2 = yMax + (nyMax - yMax) * pct
                PanAndZoomHandler.Companion.setDesiredLocalBounds(gc, Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1))
            }
        })
    }
}