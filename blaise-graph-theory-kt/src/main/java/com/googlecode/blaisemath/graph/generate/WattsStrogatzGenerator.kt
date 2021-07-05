package com.googlecode.blaisemath.graph.generate

import com.google.common.base.Preconditions
import com.google.common.graph.Graph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graph.ContractedGraphTest
import com.googlecode.blaisemath.graph.GraphGenerator
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
import com.googlecode.blaisemath.graph.generate.WattsStrogatzGenerator.WattsStrogatzParameters
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
import java.util.*
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
 * Generates a Watts-Strogatz Random Graph.
 *
 * @author Elisha Peterson
 */
class WattsStrogatzGenerator @JvmOverloads constructor(private val seed: Random? = null) : GraphGenerator<WattsStrogatzParameters?, Int?> {
    override fun toString(): String {
        return "Watts-Strogatz Graph"
    }

    override fun createParameters(): WattsStrogatzParameters? {
        return WattsStrogatzParameters()
    }

    override fun apply(parameters: WattsStrogatzParameters?): Graph<Int?>? {
        val nodeCount = parameters.getNodeCount()
        val deg = parameters.getInitialDegree()
        val rewire = parameters.getRewiringProbability().toDouble()
        val edges: MutableList<Array<Int?>?> = ArrayList()
        for (i in 0 until nodeCount) {
            for (off in 1..deg / 2) {
                edges.add(arrayOf(i, (i + off) % nodeCount))
            }
        }
        // generate list of edges to rewire
        val r = seed ?: Random()
        for (e in edges) {
            if (r.nextDouble() < rewire) {
                randomlyRewire(r, edges, e, nodeCount)
            }
        }
        return GraphGenerators.createGraphWithEdges(parameters, edges)
    }
    //endregion
    //region PARAMETERS CLASS
    /** Parameters for Watts-Strogatz algorithm  */
    class WattsStrogatzParameters : DefaultGeneratorParameters {
        private var deg = 4
        private var rewire = .5f

        constructor() {}
        constructor(directed: Boolean, nodes: Int, deg: Int, rewiring: Float) : super(directed, nodes) {
            setInitialDegree(deg)
            setRewiringProbability(rewiring)
        }

        fun getInitialDegree(): Int {
            return deg
        }

        fun setInitialDegree(deg: Int) {
            Preconditions.checkArgument(deg >= 0 && deg <= nodeCount - 1, "Degree outside of range [0, " + (nodeCount - 1) + "]")
            if (deg % 2 != 0) {
                Logger.getLogger(WattsStrogatzGenerator::class.java.name).log(Level.WARNING,
                        "Degree must be an even integer: changing from {0} to {1}", arrayOf<Any?>(deg, deg - 1))
                this.deg = deg - 1
            } else {
                this.deg = deg
            }
        }

        fun getRewiringProbability(): Float {
            return rewire
        }

        fun setRewiringProbability(rewire: Float) {
            Preconditions.checkArgument(rewire >= 0 && rewire <= 1, "Invalid rewiring parameter = $rewire (should be between 0 and 1)")
            this.rewire = rewire
        }
    } //endregion

    companion object {
        //region ALGORITHM
        /**
         * Randomly rewires the specified edge, by randomly moving one of the edge's
         * endpoints, provided the resulting edge does not already exist in the set.
         * @param random random seed
         * @param edges current list of edges
         * @param e the edge to rewire
         * @param n total # of nodes
         */
        private fun randomlyRewire(random: Random?, edges: MutableList<Array<Int?>?>?, e: Array<Int?>?, n: Int) {
            if (n <= 1) {
                return
            }
            var potential = arrayOf(e.get(0), e.get(1))
            val edgeTree: MutableSet<Array<Int?>?> = TreeSet<Array<Int?>?>(EdgeCountGenerator.Companion.PAIR_COMPARE_UNDIRECTED)
            edgeTree.addAll(edges)
            while (edgeTree.contains(potential)) {
                potential = if (random.nextBoolean()) {
                    arrayOf(e.get(0), randomNot(random, e.get(0), n))
                } else {
                    arrayOf(randomNot(random, e.get(1), n), e.get(1))
                }
            }
            e.get(0) = potential[0]
            e.get(1) = potential[1]
        }

        /**
         * Get a random value between 0 and n-1, other than exclude.
         */
        private fun randomNot(seed: Random?, exclude: Int, n: Int): Int {
            var result: Int
            do {
                result = seed.nextInt(n)
            } while (result == exclude)
            return result
        }
    }
}