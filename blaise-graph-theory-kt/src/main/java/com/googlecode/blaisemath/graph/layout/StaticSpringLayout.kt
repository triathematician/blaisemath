package com.googlecode.blaisemath.graph.layout
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
 */
import com.google.common.base.Joiner
import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import com.google.common.collect.Multiset
import com.google.common.collect.Sets
import com.google.common.graph.EndpointPair
import com.google.common.graph.Graph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graph.GraphUtils
import com.googlecode.blaisemath.graph.OptimizedGraph
import com.googlecode.blaisemath.graph.StaticGraphLayout
import com.googlecode.blaisemath.graph.app.AnimationUtils
import com.googlecode.blaisemath.graph.app.GraphApp
import com.googlecode.blaisemath.graph.app.GraphAppCanvas
import com.googlecode.blaisemath.graph.app.GraphAppFrameView
import com.googlecode.blaisemath.graph.app.MetricScaler
import com.googlecode.blaisemath.graph.generate.GraphGrowthRule
import com.googlecode.blaisemath.graph.generate.GraphSeedRule
import com.googlecode.blaisemath.graph.generate.HopGrowthRule
import com.googlecode.blaisemath.graph.layout.CircleLayout.CircleLayoutParameters
import com.googlecode.blaisemath.graph.layout.SpringLayoutPerformanceTest
import com.googlecode.blaisemath.graph.layout.StaticSpringLayout.StaticSpringLayoutParameters
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
import com.googlecode.blaisemath.util.geom.Points
import junit.framework.TestCase
import org.junit.BeforeClass
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Collectors

/**
 * Positions nodes in a graph using a force-based layout technique.
 *
 * @author Elisha Peterson
 */
class StaticSpringLayout : StaticGraphLayout<StaticSpringLayoutParameters?> {
    override fun toString(): String {
        return "Position nodes using \"spring layout\" algorithm"
    }

    override fun createParameters(): StaticSpringLayoutParameters? {
        return StaticSpringLayoutParameters()
    }

    override fun <N> layout(
            originalGraph: Graph<N?>?, ic: MutableMap<N?, Point2D.Double?>?,
            parameters: StaticSpringLayoutParameters?
    ): MutableMap<N?, Point2D.Double?>? {
        LOG.log(Level.FINE, "originalGraph, |N|={0}, |E|={1}, #components={2}, degrees={3}\n", arrayOf<Any?>(originalGraph.nodes().size, originalGraph.edges().size,
                GraphUtils.components(originalGraph).size,
                nicer(GraphUtils.degreeDistribution(originalGraph))))

        // reduce graph size for layout, by removing degree 1 nodes
        val graphForInfo = OptimizedGraph(originalGraph)
        val keepNodes: MutableSet<N?>? = Sets.newHashSet(graphForInfo.connectorNodes())
        keepNodes.addAll(graphForInfo.coreNodes())
        val keepEdges: Iterable<EndpointPair<N?>?>? = graphForInfo.edges().stream()
                .filter { input: EndpointPair<N?>? -> keepNodes.contains(input.nodeU()) && keepNodes.contains(input.nodeV()) }
                .collect(Collectors.toList())
        val graphForLayout = OptimizedGraph(false, keepNodes, keepEdges)
        LOG.log(Level.FINE, "graphForLayout, |N|={0}, |E|={1}, #components={2}, degrees={3}\n", arrayOf<Any?>(graphForLayout.nodes().size, graphForLayout.edges().size,
                GraphUtils.components(graphForLayout).size,
                nicer(GraphUtils.degreeDistribution(graphForLayout))
        ))

        // perform the physics-based layout
        val initialLocations = INITIAL_LAYOUT.layout(graphForLayout, null, parameters.initialLayoutParams)
        val mgr = IterativeGraphLayoutManager()
        mgr.layout = SpringLayout()
        mgr.graph = graphForLayout
        val params = mgr.parameters as SpringLayoutParameters
        val state = mgr.state as SpringLayoutState<N?>
        params.setDistScale(parameters.distScale)
        state.requestPositions(initialLocations, false)
        var lastEnergy = Double.MAX_VALUE
        var energyChange = Double.MAX_VALUE
        var step = 0
        while (step < parameters.minSteps || step < parameters.maxSteps && Math.abs(energyChange) > parameters.energyChangeThreshold) {
            // adjust cooling parameter
            val coolingAt = 1.0 - step * step / (parameters.maxSteps * parameters.maxSteps) as Double
            params.dampingC = parameters.coolStart * coolingAt + parameters.coolEnd * (1 - coolingAt)
            var energy: Double
            energy = try {
                mgr.runOneLoop()
            } catch (ex: InterruptedException) {
                throw IllegalStateException("Unexpected interrupt", ex)
            }
            energyChange = energy - lastEnergy
            lastEnergy = energy
            step += mgr.iterationsPerLoop
            if (step % 500 == 0) {
                LOG.log(Level.INFO, "|Energy at step {0}: {1} {2}", arrayOf<Any?>(step, energy, energyChange))
            }
        }

        // add positions of isolates and leaf nodes back in
        val res = state.positionsCopy
        val distScale = params.getDistScale()
        addLeafNodes(graphForInfo, res, distScale, distScale * parameters.leafScale)
        addIsolates(graphForInfo.isolates(), res, distScale, distScale * parameters.isolateScale)

        // report and clean up
        LOG.log(Level.FINE, "StaticSpringLayout completed in {0} steps. The final energy change was {1}, and the " +
                "final energy was {2}", arrayOf<Any?>(step, energyChange, lastEnergy))
        return res
    }
    //endregion
    //region INNER CLASSES
    /** Parameters associated with the static spring layout.  */
    class StaticSpringLayoutParameters {
        private var initialLayoutParams: CircleLayoutParameters? = CircleLayoutParameters()

        /** Approximate distance between nodes  */
        private var distScale: Double = SpringLayoutParameters.Companion.DEFAULT_DIST_SCALE.toDouble()

        /** Distance between leaf and adjacent node, as percentage of distScale  */
        private var leafScale = .5

        /** Distance between isolates, as percentage of distScale  */
        private var isolateScale = .5
        private var minSteps = 100
        private var maxSteps = 5000
        private var coolStart = 0.5
        private var coolEnd = 0.05
        private var energyChangeThreshold = distScale * distScale * 1e-6

        //region PROPERTIES
        fun getInitialLayoutParams(): CircleLayoutParameters? {
            return initialLayoutParams
        }

        fun setInitialLayoutParams(initialLayoutParams: CircleLayoutParameters?) {
            this.initialLayoutParams = initialLayoutParams
        }

        fun getDistScale(): Double {
            return distScale
        }

        fun setDistScale(distScale: Double) {
            this.distScale = distScale
            energyChangeThreshold = distScale * distScale * 1e-6
        }

        fun getLeafScale(): Double {
            return leafScale
        }

        fun setLeafScale(leafScale: Double) {
            this.leafScale = leafScale
        }

        fun getIsolateScale(): Double {
            return isolateScale
        }

        fun setIsolateScale(isolateScale: Double) {
            this.isolateScale = isolateScale
        }

        fun getMinSteps(): Int {
            return minSteps
        }

        fun setMinSteps(minSteps: Int) {
            this.minSteps = minSteps
        }

        fun getMaxSteps(): Int {
            return maxSteps
        }

        fun setMaxSteps(maxSteps: Int) {
            this.maxSteps = maxSteps
        }

        fun getEnergyChangeThreshold(): Double {
            return energyChangeThreshold
        }

        fun setEnergyChangeThreshold(energyChangeThreshold: Double) {
            this.energyChangeThreshold = energyChangeThreshold
        }

        fun getCoolStart(): Double {
            return coolStart
        }

        fun setCoolStart(coolStart: Double) {
            this.coolStart = coolStart
        }

        fun getCoolEnd(): Double {
            return coolEnd
        }

        fun setCoolEnd(coolEnd: Double) {
            this.coolEnd = coolEnd
        } //endregion
    } //endregion

    companion object {
        private val LOG = Logger.getLogger(StaticSpringLayout::class.java.name)
        private val INITIAL_LAYOUT: CircleLayout? = CircleLayout.Companion.getInstance()
        //region ADD ELEMENTS AFTER MAIN LAYOUT
        /**
         * Add leaf nodes that are adjacent to the given positions.
         * @param og the graph
         * @param pos current positions
         * @param distScale distance between nodes
         * @param leafScale distance between leaves and adjacent nodes
         * @param <N> graph node type
        </N> */
        private fun <N> addLeafNodes(og: OptimizedGraph<N?>?, pos: MutableMap<N?, Point2D.Double?>?, distScale: Double, leafScale: Double) {
            val leafs = og.leafNodes()
            val n = leafs.size
            if (n > 0) {
                val bounds: Rectangle2D = Points.boundingBox(pos.values, distScale)
                if (bounds == null) {
                    // no points exist, so must be all pairs
                    val sqSide = leafScale * Math.sqrt(n.toDouble())
                    val pairRegion: Rectangle2D = Rectangle2D.Double(-sqSide, -sqSide, 2 * sqSide, 2 * sqSide)
                    val orderedLeafs = orderByAdjacency(leafs, og)
                    addPointsToBox(orderedLeafs, pairRegion, pos, leafScale, true)
                } else {
                    // add close to their neighboring point
                    val cores: MutableSet<N?> = Sets.newHashSet()
                    val pairs: MutableSet<N?> = Sets.newHashSet()
                    for (o in leafs) {
                        val nbr = og.neighborOfLeaf(o)
                        if (leafs.contains(nbr)) {
                            pairs.add(o)
                            pairs.add(nbr)
                        } else {
                            cores.add(nbr)
                        }
                    }
                    for (o in cores) {
                        val leaves = og.leavesAdjacentTo(o)
                        val ctr = pos.get(o)
                        val theta = Math.atan2(ctr.y, ctr.x)
                        if (leaves.size == 1) {
                            pos[Iterables.getFirst(leaves, null)] = Point2D.Double(
                                    ctr.getX() + leafScale * Math.cos(theta), ctr.getY() + leafScale * Math.sin(theta))
                        } else {
                            var th0 = theta - Math.PI / 3
                            val dth = 2 * Math.PI / 3 / (leaves.size - 1)
                            for (l in leaves) {
                                pos[l] = Point2D.Double(ctr.getX() + leafScale * Math.cos(th0), ctr.getY() + leafScale * Math.sin(th0))
                                th0 += dth
                            }
                        }
                    }

                    // put the pairs to the right side
                    val area = n * leafScale * leafScale
                    val ht = Math.min(bounds.height, 2 * Math.sqrt(area))
                    val wid = area / ht
                    val pairRegion: Rectangle2D = Rectangle2D.Double(
                            bounds.maxX + .1 * bounds.width, bounds.centerY - ht / 2,
                            wid, ht)
                    val orderedPairs = orderByAdjacency(pairs, og)
                    addPointsToBox(orderedPairs, pairRegion, pos, leafScale, true)
                }
            }
        }

        private fun <N> orderByAdjacency(leafs: MutableSet<N?>?, og: OptimizedGraph<N?>?): MutableSet<N?>? {
            val res: MutableSet<N?> = Sets.newLinkedHashSet()
            for (o in leafs) {
                if (!res.contains(o)) {
                    res.add(o)
                    res.add(og.neighborOfLeaf(o))
                }
            }
            return res
        }

        /**
         * Add isolate nodes in the given graph based on the current positions in the map
         * @param <N> graph node type
         * @param isolates the isolate nodes
         * @param pos position map
         * @param distScale distance between nodes
         * @param isoScale distance between isolates
        </N> */
        private fun <N> addIsolates(isolates: MutableSet<N?>?, pos: MutableMap<N?, Point2D.Double?>?, distScale: Double, isoScale: Double) {
            val n = isolates.size
            if (n > 0) {
                val bounds: Rectangle2D = Points.boundingBox(pos.values, isoScale)
                val isolateRegion: Rectangle2D
                isolateRegion = if (bounds == null) {
                    // put them all in the middle
                    val sqSide = isoScale * Math.sqrt(n.toDouble())
                    Rectangle2D.Double(-sqSide, -sqSide, 2 * sqSide, 2 * sqSide)
                } else {
                    // put them to the right side
                    val area = n * isoScale * isoScale
                    val ht = Math.min(bounds.height, 2 * Math.sqrt(area))
                    val wid = area / ht
                    Rectangle2D.Double(
                            bounds.maxX + .1 * bounds.width, bounds.centerY - ht / 2,
                            wid, ht)
                }
                addPointsToBox(isolates, isolateRegion, pos, isoScale, false)
            }
        }

        private fun <N> addPointsToBox(
                `is`: MutableSet<N?>?, rect: Rectangle2D?, pos: MutableMap<N?, Point2D.Double?>?,
                nomSz: Double, even: Boolean
        ) {
            var x = rect.getMinX()
            var y = rect.getMinY()
            var added = 0
            for (o in `is`) {
                pos[o] = Point2D.Double(x, y)
                added++
                x += nomSz
                if (x > rect.getMaxX() && (!even || added % 2 == 0)) {
                    x = rect.getMinX()
                    y += nomSz
                }
            }
        }

        //endregion
        //region UTILS
        private fun nicer(set: Multiset<*>?): String? {
            val ss: MutableList<String?>? = Lists.newArrayList()
            for (el in Sets.newTreeSet<Comparable<*>?>(set.elementSet())) {
                ss.add(el.toString() + ":" + set.count(el))
            }
            return "[" + Joiner.on(",").join(ss) + "]"
        }
    }
}