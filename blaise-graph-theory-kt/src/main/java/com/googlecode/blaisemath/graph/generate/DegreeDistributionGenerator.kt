package com.googlecode.blaisemath.graph.generate

import com.google.common.base.Preconditions
import com.google.common.collect.Iterables
import com.google.common.graph.Graph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graph.GraphGenerator
import com.googlecode.blaisemath.graph.GraphUtils
import com.googlecode.blaisemath.graph.app.AnimationUtils
import com.googlecode.blaisemath.graph.app.GraphApp
import com.googlecode.blaisemath.graph.app.GraphAppCanvas
import com.googlecode.blaisemath.graph.app.GraphAppFrameView
import com.googlecode.blaisemath.graph.app.MetricScaler
import com.googlecode.blaisemath.graph.generate.DegreeDistributionGenerator.DegreeDistributionParameters
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
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.IntStream

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
 * Provides a random-graph model based on a degree sequence. If the graph is directed, the degree sequence is for the
 * out-degrees. If the graph is undirected, the degree sequence is as usual (and must sum to an even number).
 *
 * @author Elisha Peterson
 */
class DegreeDistributionGenerator : GraphGenerator<DegreeDistributionParameters?, Int?> {
    override fun toString(): String {
        return "Random Graph (fixed Degree Distribution)"
    }

    override fun createParameters(): DegreeDistributionParameters? {
        return DegreeDistributionParameters()
    }

    override fun apply(p: DegreeDistributionParameters?): Graph<Int?>? {
        return if (p.isDirected()) generateDirected(p.getDegreeSequence()) else generateUndirected(p.getDegreeSequence())
    }
    //endregion
    //region PARAMETERS CLASS
    /** Parameters associated with a particular degree distribution.  */
    class DegreeDistributionParameters {
        private var directed = false
        private var degSequence: IntArray?
        fun isDirected(): Boolean {
            return directed
        }

        fun setDirected(directed: Boolean) {
            this.directed = directed
        }

        fun getDegreeSequence(): IntArray? {
            return degSequence
        }

        fun setDegreeSequence(degrees: IntArray?) {
            degSequence = Arrays.copyOf(degrees, degrees.size)
        }
    } //endregion

    companion object {
        private val LOG = Logger.getLogger(DegreeDistributionGenerator::class.java.name)

        /**
         * Generate a random (directed) graph with specified out-degrees.
         * @param deg the outdegree distribution of the graph
         * @return directed instance of this type of random graph
         */
        fun generateDirected(deg: IntArray?): Graph<Int?>? {
            val n = IntStream.of(*deg).sum()
            require(deg.size <= n) { "Maximum degree of sequence " + Arrays.toString(deg) + " is too large!" }
            val edges: MutableSet<Array<Int?>?> = TreeSet<Array<Int?>?>(EdgeCountGenerator.Companion.PAIR_COMPARE)
            var i = 0
            for (iDeg in deg.indices) {
                for (nDegI in 0 until deg.get(iDeg)) {
                    // each iteration here is a separate node of degree iDeg
                    // need to generate this many edges at random
                    val subset = randomSubset(n, iDeg, i)
                    for (i2 in subset) {
                        edges.add(arrayOf(i, i2))
                    }
                    i++
                }
            }
            return GraphUtils.createFromArrayEdges(true, GraphGenerators.intList(0, n), edges)
        }

        /**
         * @param deg a specified degree sequence
         * @return undirected instance of this type of random graph; not guaranteed
         * to have the actual degree sum
         * @throws IllegalArgumentException if the degree sequence is not good (i.e.
         * sums to an odd total degree, or the maximum degree is too large)
         */
        fun generateUndirected(deg: IntArray?): Graph<Int?>? {
            Objects.requireNonNull(deg)
            val n = IntStream.of(*deg).sum()
            require(deg.size <= n) { "Maximum degree of sequence " + Arrays.toString(deg) + " is too large!" }
            var degSum = 0
            for (i in deg.indices) {
                degSum += i * deg.get(i)
            }
            require(degSum % 2 == 0) { "Degree sequence " + Arrays.toString(deg) + " has odd total degree!" }

            // stores the mapping of nodes to desired degrees
            val vxLeft: MutableMap<Int?, Int?> = TreeMap()
            var i = 0
            for (iDeg in 1 until deg.size)  // ignore the degree 0 nodes
            {
                for (nDegI in 0 until deg.get(iDeg)) {
                    vxLeft[i++] = iDeg
                }
            }

            // stores the edges in the resulting graph
            val edges: MutableSet<Array<Int?>?> = TreeSet<Array<Int?>?>(EdgeCountGenerator.Companion.PAIR_COMPARE_UNDIRECTED)
            while (vxLeft.size > 1) {
                val vv = vxLeft.keys
                var edge: Array<Int?>? = arrayOf(0, 0)
                while (edge.get(0) == edge.get(1)) {
                    edge = arrayOf(random<Int?>(vv), random<Int?>(vv))
                }
                var attempts = 1

                // attempt to find new edge at random
                while ((edges.contains(edge) || edge.get(0) == edge.get(1)) && attempts < 20) {
                    edge = arrayOf(random<Int?>(vv), random<Int?>(vv))
                    attempts++
                }

                // if it takes too long, brute-force check to ensure edges are not there
                if (edges.contains(edge) || edge.get(0) == edge.get(1)) {
                    val edgesLeft: MutableSet<Array<Int?>?> = TreeSet<Array<Int?>?>(EdgeCountGenerator.Companion.PAIR_COMPARE_UNDIRECTED)
                    for (i1 in vv) {
                        for (i2 in vv) {
                            if (i1 != i2) {
                                val e = arrayOf(i1, i2)
                                if (vxLeft.containsKey(i1) && vxLeft.containsKey(i2) && !edges.contains(e)) {
                                    edgesLeft.add(e)
                                }
                            }
                        }
                    }
                    edge = if (edgesLeft.isEmpty()) {
                        break
                    } else {
                        random<Array<Int?>?>(edgesLeft)
                    }
                }
                check(!edges.contains(edge))
                edges.add(edge)
                if (vxLeft[edge.get(0)] == 1) {
                    vxLeft.remove(edge.get(0))
                } else {
                    vxLeft[edge.get(0)] = vxLeft[edge.get(0)] - 1
                }
                if (vxLeft[edge.get(1)] == 1) {
                    vxLeft.remove(edge.get(1))
                } else {
                    vxLeft[edge.get(1)] = vxLeft[edge.get(1)] - 1
                }
            }
            if (!vxLeft.isEmpty()) {
                LOG.log(Level.WARNING, "Unable to find edges for all nodes. Remaining list={0}", vxLeft)
            }
            return GraphUtils.createFromArrayEdges(false, GraphGenerators.intList(0, n), edges)
        }

        //region ALGORITHM
        private fun <E> random(set: MutableSet<E?>?): E? {
            return Iterables.get(set, Random().nextInt(set.size))
        }

        /**
         * Generate a random subset of the integers 0,...,n-1, possibly with the exclusion of a specific value.
         *
         * @param n the overall set size
         * @param k the subset size
         * @param omit an integer value to omit from the sequence; if outside the range 0,...,n-1, it is ignored
         * @return random subset of integers 0,...,n-1 of given size
         * @throws IllegalArgumentException if k is not in the range 0,...,n (if omit is in the sequence),
         * or the range 0,...,n-1 (if omit is not in the sequence)
         */
        private fun randomSubset(n: Int, k: Int, omit: Int): IntArray? {
            Preconditions.checkElementIndex(k, n + 1)
            require(!(k == n && omit >= 0 && omit <= n - 1)) {
                ("Cannot construct subset of size "
                        + k + " from " + n + " values omitting " + omit)
            }
            val result = IntArray(k)
            val left: MutableSet<Int?> = TreeSet()
            for (i in 0 until n) {
                left.add(i)
            }
            // will be ignored if omit is outside of range
            left.remove(omit)
            for (i in 0 until k) {
                val value = random<Int?>(left)
                result[i] = value
                left.remove(value)
            }
            return result
        }
    }
}