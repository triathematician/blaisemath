package com.googlecode.blaisemath.graph.layout

import com.google.common.base.Objects
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.google.common.graph.Graph
import com.googlecode.blaisemath.annotation.InvokedFromThread
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graph.GraphUtils
import com.googlecode.blaisemath.graph.IterativeGraphLayout
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
import java.util.logging.Level
import java.util.logging.Logger

/*
* #%L
* BlaiseGraphTheory
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
 * Graph layout modeled after repulsive charges between nodes, and spring forces between nodes.
 *
 * @author Elisha Peterson
 */
class SpringLayout : IterativeGraphLayout<SpringLayoutParameters?, SpringLayoutState<*>?> {
    override fun toString(): String {
        return "Spring layout algorithm"
    }

    override fun createState(): SpringLayoutState<*>? {
        return SpringLayoutState<Any?>()
    }

    override fun createParameters(): SpringLayoutParameters? {
        return SpringLayoutParameters()
    }

    @InvokedFromThread("unknown")
    @Synchronized
    override fun <N> iterate(og: Graph<N?>?, state: SpringLayoutState<*>?, params: SpringLayoutParameters?): Double {
        val g = if (og.isDirected()) GraphUtils.copyUndirected(og) else og
        val nodes = g.nodes()
        val pinned: MutableSet<N?>? = params.getConstraints().getPinnedNodes()
        val unpinned: MutableSet<N?>? = Sets.difference(nodes, pinned).immutableCopy()
        val energy: Double
        state.nodeLocationSync(nodes)
        state.updateRegions(params.maxRepelDist)
        val forces: MutableMap<N?, Point2D.Double?> = Maps.newHashMap()
        computeNonRepulsiveForces(g, nodes, pinned, forces, state, params)
        computeRepulsiveForces(pinned, forces, state, params)
        checkForces(unpinned, forces)
        energy = move(g, unpinned, forces, state, params)
        return energy
    }

    //region FORCE COMPUTATIONS
    protected fun <N> computeNonRepulsiveForces(
            g: Graph<N?>?, nodes: MutableSet<N?>?, pinned: MutableSet<N?>?, forces: MutableMap<N?, Point2D.Double?>?,
            state: SpringLayoutState<N?>?, params: SpringLayoutParameters?
    ) {
        for (io in nodes) {
            var iLoc = state.getLoc(io)
            if (iLoc == null) {
                iLoc = newNodeLocation(g, io, state, params)
                state.putLoc(io, iLoc)
            }
            var iVel = state.getVel(io)
            if (iVel == null) {
                iVel = Point2D.Double()
                state.putVel(io, iVel)
            }
            if (!pinned.contains(io)) {
                val netForce = Point2D.Double()
                addGlobalForce(netForce, iLoc, params)
                addSpringForces(g, netForce, io, iLoc, state, params)
                addAdditionalForces(g, netForce, io, iLoc, state, params)
                forces[io] = netForce
            }
        }
    }

    protected fun <N> addAdditionalForces(
            g: Graph<N?>?,
            sum: Point2D.Double?, io: N?, iLoc: Point2D.Double?,
            state: SpringLayoutState<N?>?, params: SpringLayoutParameters?
    ) {
        // hook for adding additional forces per the needs of child layouts
    }

    protected fun <N> computeRepulsiveForces(
            pinned: MutableSet<N?>?, forces: MutableMap<N?, Point2D.Double?>?,
            state: SpringLayoutState<N?>?, params: SpringLayoutParameters?
    ) {
        for (rr in state.regions) {
            for (r in rr) {
                for (io in r.points()) {
                    if (!pinned.contains(io)) {
                        addRepulsiveForces(r, forces.get(io), io, r[io], params)
                    }
                }
            }
        }
        for (io in state.oRegion.points()) {
            if (!pinned.contains(io)) {
                addRepulsiveForces(state.oRegion, forces.get(io), io, state.oRegion[io], params)
            }
        }
    }

    companion object {
        private val LOG = Logger.getLogger(SpringLayout::class.java.name)
        //endregion
        //region STATIC METHODS
        /**
         * Get a position for a node that doesn't currently have a position.
         * @param node the node to get new location of
         */
        private fun <N> newNodeLocation(g: Graph<N?>?, node: N?, state: SpringLayoutState<N?>?, params: SpringLayoutParameters?): Point2D.Double? {
            val len = params.springL
            var sx = 0.0
            var sy = 0.0
            var n = 0
            for (o in g.adjacentNodes(node)) {
                val p = state.getLoc(o)
                if (p != null) {
                    sx += p.x
                    sy += p.y
                    n++
                }
            }
            return if (n == 0) {
                Point2D.Double(sx + 2 * len * Math.random(), sy + 2 * len * Math.random())
            } else if (n == 1) {
                Point2D.Double(sx + len * Math.random(), sy + len * Math.random())
            } else {
                Point2D.Double(sx / n, sy / n)
            }
        }

        /**
         * Add a global attractive force pushing node at specified location toward the origin.
         * @param sum vector representing the sum of forces (will be adjusted)
         * @param iLoc location of first node
         * @param params algorithm parameters
         */
        private fun addGlobalForce(sum: Point2D.Double?, iLoc: Point2D.Double?, params: SpringLayoutParameters?) {
            val dist = iLoc.distance(0.0, 0.0)
            if (dist > params.minGlobalForceDist) {
                sum.x += -params.globalC * iLoc.x / dist
                sum.y += -params.globalC * iLoc.y / dist
            }
        }

        /**
         * Add all repulsive forces for a particular node.
         * @param region the region for the node
         * @param sum vector representing the sum of forces (will be adjusted)
         * @param io the node of interest
         * @param iLoc location of first node
         * @param params algorithm parameters
         */
        private fun <N> addRepulsiveForces(
                region: LayoutRegion<N?>?, sum: Point2D.Double?, io: N?, iLoc: Point2D.Double?,
                params: SpringLayoutParameters?
        ) {
            var jLoc: Point2D.Double
            var dist: Double
            for (r in region.adjacentRegions()) {
                for ((jo, value) in r.entries()) {
                    if (io !== jo) {
                        jLoc = value
                        dist = iLoc.distance(jLoc)
                        // repulsive force from other nodes
                        if (dist < params.maxRepelDist) {
                            addRepulsiveForce(sum, iLoc, jLoc, dist, params)
                        }
                    }
                }
            }
        }

        /**
         * Add repulsive force at node i1 pointing away from node i2.
         * @param sum vector representing the sum of forces (will be adjusted)
         * @param iLoc location of first node
         * @param jLoc location of second node
         * @param dist distance between nodes
         * @param params algorithm parameters
         */
        private fun addRepulsiveForce(
                sum: Point2D.Double?, iLoc: Point2D.Double?, jLoc: Point2D.Double?, dist: Double,
                params: SpringLayoutParameters?
        ) {
            if (iLoc === jLoc) {
                return
            }
            if (dist == 0.0) {
                val angle = Math.random() * 2 * Math.PI
                sum.x += params.repulsiveC * Math.cos(angle)
                sum.y += params.repulsiveC * Math.sin(angle)
            } else {
                val multiplier = Math.min(params.repulsiveC / (dist * dist), params.maxForce) / dist
                sum.x += multiplier * (iLoc.x - jLoc.x)
                sum.y += multiplier * (iLoc.y - jLoc.y)
            }
        }

        /**
         * Add symmetric attractive force from adjacencies.
         * @param g the graph
         * @param sum the total force for the current object
         * @param io the node of interest
         * @param iLoc position of node of interest
         * @param params algorithm parameters
         */
        private fun <N> addSpringForces(
                g: Graph<N?>?, sum: Point2D.Double?, io: N?, iLoc: Point2D.Double?,
                state: SpringLayoutState<N?>?, params: SpringLayoutParameters?
        ) {
            var jLoc: Point2D.Double?
            var dist: Double
            for (o in g.adjacentNodes(io)) {
                if (!Objects.equal(o, io)) {
                    jLoc = state.getLoc(o)
                    dist = iLoc.distance(jLoc)
                    addSpringForce(sum, io, iLoc, o, jLoc, dist, params)
                }
            }
        }

        /** Add spring force at node i1 pointing to node i2.
         * @param sum vector representing the sum of forces (will be adjusted)
         * @param io the node of interest
         * @param iLoc location of first node
         * @param jo the second node of interest
         * @param jLoc location of second node
         * @param dist distance between nodes
         */
        private fun <N> addSpringForce(
                sum: Point2D.Double?, io: N?, iLoc: Point2D.Double?, jo: N?, jLoc: Point2D.Double?,
                dist: Double, params: SpringLayoutParameters?
        ) {
            if (dist == 0.0) {
                LOG.log(Level.WARNING, "Distance 0 between {0} and {1}: {2}, {3}", arrayOf(io, jo, iLoc, jLoc))
                sum.x += params.springC / (params.minDist * params.minDist)
                sum.y += 0.0
            } else {
                val displacement = dist - params.springL
                sum.x += params.springC * displacement * (jLoc.x - iLoc.x) / dist
                sum.y += params.springC * displacement * (jLoc.y - iLoc.y) / dist
            }
        }

        private fun <N> checkForces(unpinned: MutableSet<N?>?, forces: MutableMap<N?, Point2D.Double?>?) {
            for (io in unpinned) {
                val netForce = forces.get(io)
                val test = (!java.lang.Double.isNaN(netForce.x) && !java.lang.Double.isNaN(netForce.y)
                        && !java.lang.Double.isInfinite(netForce.x) && !java.lang.Double.isInfinite(netForce.y))
                if (!test) {
                    LOG.log(Level.SEVERE, "Computed infinite force: {0} for {1}", arrayOf<Any?>(netForce, io))
                }
            }
        }

        private fun <N> move(
                g: Graph<N?>?, unpinned: MutableSet<N?>?, forces: MutableMap<N?, Point2D.Double?>?,
                state: SpringLayoutState<N?>?, params: SpringLayoutParameters?
        ): Double {
            var energy = 0.0
            for (io in unpinned) {
                energy += adjustVelocity(state.getVel(io), forces.get(io), g.degree(io).toDouble(), params)
            }
            for (io in unpinned) {
                adjustPosition(state.getLoc(io), state.getVel(io), params.stepT)
            }
            return energy
        }

        /**
         * Adjusts the velocity vector with the specified net force, possibly by applying damping. SpringLayout uses
         * iVel = dampingC*(iVel + stepT*netForce), and caps maximum speed.
         * @param iVel velocity to adjust
         * @param netForce force vector to use
         * @param iDeg node's degree, used to increase damping for high degree nodes
         * @param params layout parameters
         * @return node's energy
         */
        private fun adjustVelocity(iVel: Point2D.Double?, netForce: Point2D.Double?, iDeg: Double, params: SpringLayoutParameters?): Double {
            val maxForce = if (iDeg <= 15) params.maxForce else params.maxForce * (.2 + .8 / (iDeg - 15))
            val fm = netForce.distance(0.0, 0.0)
            if (fm > maxForce) {
                netForce.x *= maxForce / fm
                netForce.y *= maxForce / fm
            }
            iVel.x = params.dampingC * (iVel.x + params.stepT * netForce.x)
            iVel.y = params.dampingC * (iVel.y + params.stepT * netForce.y)
            var speed = iVel.x * iVel.x + iVel.y * iVel.y
            if (speed > params.maxSpeed) {
                iVel.x *= params.maxSpeed / speed
                iVel.y *= params.maxSpeed / speed
                speed = params.maxSpeed
            }
            return .5 * speed * speed
        }

        /**
         * Adjusts a node's position using specified initial position and velocity.
         * SpringLayout uses iLoc += stepT*iVel
         * @param iLoc position to change
         * @param iVel velocity to adjust
         * @param stepT step time
         */
        private fun adjustPosition(iLoc: Point2D.Double?, iVel: Point2D.Double?, stepT: Double) {
            iLoc.x += stepT * iVel.x
            iLoc.y += stepT * iVel.y
        } //endregion
    }
}