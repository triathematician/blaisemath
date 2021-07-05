package com.googlecode.blaisemath.graph

import com.google.common.collect.Maps
import com.google.errorprone.annotations.concurrent.GuardedBy
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graph.ContractedGraphTest
import com.googlecode.blaisemath.graph.GraphUtilsTest
import com.googlecode.blaisemath.graph.SubgraphTest
import com.googlecode.blaisemath.graph.app.AnimationUtils
import com.googlecode.blaisemath.graph.app.GraphApp
import com.googlecode.blaisemath.graph.app.GraphAppCanvas
import com.googlecode.blaisemath.graph.app.GraphAppFrameView
import com.googlecode.blaisemath.graph.app.MetricScaler
import com.googlecode.blaisemath.graph.generate.GraphGrowthRule
import com.googlecode.blaisemath.graph.generate.GraphSeedRule
import com.googlecode.blaisemath.graph.generate.HopGrowthRule
import com.googlecode.blaisemath.graph.layout.SpringLayoutPerformanceTest
import com.googlecode.blaisemath.graph.metrics.AdditiveSubsetMetricTest
import com.googlecode.blaisemath.graph.metrics.BetweenCentralityTest
import com.googlecode.blaisemath.graph.metrics.ClosenessCentralityTest
import com.googlecode.blaisemath.graph.metrics.CooperationMetric
import com.googlecode.blaisemath.graph.metrics.EigenCentralityTest
import com.googlecode.blaisemath.graph.metrics.GraphCentralityTest
import com.googlecode.blaisemath.graph.metrics.SubsetMetricsTest
import com.googlecode.blaisemath.graph.test.DynamicGraphTestFrame
import com.googlecode.blaisemath.graph.test.GraphTestFrame
import com.googlecode.blaisemath.graph.test.MyTestGraph
import com.googlecode.blaisemath.graphics.editor.AttributeSetPropertyModelTestFrame
import com.googlecode.blaisemath.graphics.svg.SvgElementGraphicConverter
import com.googlecode.blaisemath.graphics.svg.SvgGraphic
import com.googlecode.blaisemath.graphics.svg.SvgGraphicComponent
import com.googlecode.blaisemath.graphics.svg.SvgUtils
import com.googlecode.blaisemath.graphics.swing.AnchorTestFrame
import com.googlecode.blaisemath.graphics.testui.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.testui.HelloWorldTest
import com.googlecode.blaisemath.graphics.testui.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.testui.SelectionTestFrame
import com.googlecode.blaisemath.graphics.testui.TooltipTestFrame
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
import com.googlecode.blaisemath.test.AssertUtils
import com.googlecode.blaisemath.ui.PropertyActionPanel
import com.googlecode.blaisemath.util.Images
import junit.framework.TestCase
import org.junit.BeforeClass
import java.awt.geom.Point2D
import java.util.*

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
*/ /**
 * Core state properties required for iterative graph layouts. Allows for inserting
 * location updates from alternate threads.
 *
 * @param <N> graph node type
 *
 * @author Elisha Peterson
</N> */
abstract class IterativeGraphLayoutState<N> {
    /** Current locations of nodes in the graph.  */
    @GuardedBy("this")
    protected val loc: MutableMap<N?, Point2D.Double?>? = Maps.newHashMap()

    /** Current velocities of nodes in the graph.  */
    @GuardedBy("this")
    protected val vel: MutableMap<N?, Point2D.Double?>? = Maps.newHashMap()

    /** Interim update locations to be applied at next opportunity.  */
    @GuardedBy("this")
    private val updateLoc: MutableMap<N?, Point2D.Double?>? = Maps.newHashMap()

    /** If true, the in-memory state will be updated to include only nodes in the update.  */
    @GuardedBy("this")
    private var resetNodes = false

    /** Cooling parameter, used to gradually reduce the impact of the layout  */
    private var coolingParameter = 0.0

    //region ThreadSafe GETTERS/MUTATORS
    fun getCoolingParameter(): Double {
        return coolingParameter
    }

    fun setCoolingParameter(`val`: Double) {
        coolingParameter = `val`
    }

    @Synchronized
    fun getPositionsCopy(): MutableMap<N?, Point2D.Double?>? {
        return Maps.newHashMap(loc)
    }

    /**
     * Request the specified locations to be applied at the next opportunity in the layout algorithm.
     * @param loc new locations
     * @param resetNodes if true, the keys in the provided map will be used to alter the set of nodes
     */
    @Synchronized
    fun requestPositions(loc: MutableMap<N?, Point2D.Double?>?, resetNodes: Boolean) {
        // in some race conditions, the request positions is called with empty loc, because the coordinate manager's
        // active location copy is empty (no current active locations). we don't want to clear the update locs in this
        // case, because it erases the entire state
        if (!loc.isEmpty()) {
            updateLoc.clear()
            updateLoc.putAll(loc)
            this.resetNodes = resetNodes
        }
    }
    //endregion
    //region LOCATION UPDATES
    /**
     * Synchronizes pending updates to node locations, executed prior to each layout step.
     * This method locks the entire layout state to prevent clashing updates.
     * @param nodes the set of nodes to be retained
     */
    @Synchronized
    fun nodeLocationSync(nodes: MutableSet<N?>?) {
        loc.keys.retainAll(nodes)
        for ((n, value) in updateLoc) {
            if (nodes.contains(n)) {
                loc[n] = value
                if (vel.containsKey(n)) {
                    vel.get(n).setLocation(0.0, 0.0)
                } else {
                    vel[n] = Point2D.Double()
                }
            }
        }
        if (resetNodes) {
            val keep: MutableSet<N?> = HashSet(nodes)
            keep.addAll(updateLoc.keys)
            loc.keys.retainAll(keep)
            vel.keys.retainAll(keep)
        }
        resetNodes = false
        updateLoc.clear()
        for (v in nodes) {
            if (!loc.containsKey(v)) {
                loc[v] = Point2D.Double()
                vel[v] = Point2D.Double()
            }
        }
    } //endregion
}