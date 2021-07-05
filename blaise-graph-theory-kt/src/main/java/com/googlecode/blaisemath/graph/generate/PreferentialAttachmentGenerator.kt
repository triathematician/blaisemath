package com.googlecode.blaisemath.graph.generate

import com.google.common.base.Preconditions
import com.google.common.graph.Graph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graph.GraphGenerator
import com.googlecode.blaisemath.graph.GraphUtils
import com.googlecode.blaisemath.graph.app.AnimationUtils
import com.googlecode.blaisemath.graph.app.GraphApp
import com.googlecode.blaisemath.graph.app.GraphAppCanvas
import com.googlecode.blaisemath.graph.app.GraphAppFrameView
import com.googlecode.blaisemath.graph.app.MetricScaler
import com.googlecode.blaisemath.graph.generate.GraphGrowthRule
import com.googlecode.blaisemath.graph.generate.GraphSeedRule
import com.googlecode.blaisemath.graph.generate.HopGrowthRule
import com.googlecode.blaisemath.graph.generate.PreferentialAttachmentGenerator.PreferentialAttachmentParameters
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
 * Provides static utility methods for generating graphs using preferential
 * attachment.
 *
 * @author Elisha Peterson
 */
class PreferentialAttachmentGenerator : GraphGenerator<PreferentialAttachmentParameters?, Int?> {
    override fun toString(): String {
        return "Preferential Attachment Graph"
    }

    override fun createParameters(): PreferentialAttachmentParameters? {
        return PreferentialAttachmentParameters()
    }

    override fun apply(parameters: PreferentialAttachmentParameters?): Graph<Int?>? {
        return if (parameters.getConnectProbabilities() == null) {
            generate(parameters.generateSeedGraph(), parameters.getNodeCount(), parameters.getEdgesPerStep())
        } else {
            generate(parameters.generateSeedGraph(), parameters.getNodeCount(), parameters.getConnectProbabilities())
        }
    }
    //endregion
    //region PARAMETERS CLASS
    /** Parameters for preferential attachment  */
    class PreferentialAttachmentParameters : DefaultGeneratorParameters {
        /** If using edge count generator, the default seed graph generator  */
        private var seedParameters: ExtendedGeneratorParameters? = null

        /** If specifying a seed graph directly  */
        private var seedGraph: Graph<Int?>? = null

        /** If using fixed # of edges per step  */
        private var edgesPerStep = 1

        /** If using probability-based # of connections per step  */
        private var probabilities: FloatArray? = null

        constructor() {
            seedParameters = ExtendedGeneratorParameters(false, 10, 10)
        }

        constructor(p: ExtendedGeneratorParameters?) {
            setSeedParameters(p)
        }

        constructor(p: ExtendedGeneratorParameters?, nodeCount: Int, edgesPerStep: Int) : super(p.isDirected(), nodeCount) {
            setSeedParameters(p)
            setEdgesPerStep(edgesPerStep)
        }

        constructor(p: ExtendedGeneratorParameters?, nodeCount: Int, probabilities: FloatArray?) : super(p.isDirected(), nodeCount) {
            setSeedParameters(p)
            setConnectProbabilities(probabilities)
        }

        constructor(seed: Graph<Int?>?) {
            setSeedGraph(seed)
        }

        constructor(seed: Graph<Int?>?, nodeCount: Int, edgesPerStep: Int) : super(seed.isDirected(), nodeCount) {
            setSeedGraph(seed)
            setEdgesPerStep(edgesPerStep)
        }

        constructor(seed: Graph<Int?>?, nodeCount: Int, probabilities: FloatArray?) : super(seed.isDirected(), nodeCount) {
            setSeedGraph(seed)
            setConnectProbabilities(probabilities)
        }

        fun generateSeedGraph(): Graph<Int?>? {
            return if (seedGraph != null) seedGraph else EdgeCountGenerator.Companion.getInstance().apply(seedParameters)
        }

        //region PROPERTIES
        fun getSeedGraphParameters(): ExtendedGeneratorParameters? {
            return seedParameters
        }

        fun setSeedParameters(seedParameters: ExtendedGeneratorParameters?) {
            this.seedParameters = seedParameters
        }

        fun getSeedGraph(): Graph<Int?>? {
            return seedGraph
        }

        fun setSeedGraph(seed: Graph<Int?>?) {
            Objects.requireNonNull(seed)
            Preconditions.checkArgument(seed.edges().size > 0, "PreferentialAttachment seed must be non-empty: $seed")
            Preconditions.checkArgument(!seed.isDirected(), "Algorithm not supported for directed graphs: $seed")
            seedGraph = seed
        }

        fun getEdgesPerStep(): Int {
            return edgesPerStep
        }

        fun setEdgesPerStep(edgesPerStep: Int) {
            Preconditions.checkArgument(edgesPerStep >= 0)
            this.edgesPerStep = edgesPerStep
        }

        fun getConnectProbabilities(): FloatArray? {
            return probabilities
        }

        /**
         * Probabilities of initial #s of connections; the i'th entry is the probability that a new node will have i connections, starting at 0.
         * @param probabilities array describing probabilities of connections by degree
         */
        fun setConnectProbabilities(probabilities: FloatArray?) {
            this.probabilities = if (probabilities == null) null else Arrays.copyOf(probabilities, probabilities.size)
        } //endregion
    } //endregion

    companion object {
        //region ALGORITHM
        /**
         * Common method for preferential attachment algorithm
         */
        private fun generate(seedGraph: Graph<Int?>?, nodeCount: Int, edgesPerStep: Any?): Graph<Int?>? {
            // prepare parameters for graph to be created
            val nodes: MutableList<Int?> = ArrayList(seedGraph.nodes())
            val edges: MutableList<Array<Int?>?> = ArrayList()
            val degrees = IntArray(nodeCount)
            Arrays.fill(degrees, 0)
            var degreeSum = 0

            // initialize with values from seed graph
            for (i1 in nodes) {
                for (i2 in nodes) {
                    if (seedGraph.hasEdgeConnecting(i1, i2)) {
                        degreeSum += addEdge(edges, degrees, i1, i2)
                    }
                }
            }
            var cur = 0
            val variableEdgeNumber = edgesPerStep is FloatArray
            var numberEdgesToAdd: Int = if (variableEdgeNumber) 0 else edgesPerStep as Int?
            val connectionProbabilities = if (variableEdgeNumber) edgesPerStep as FloatArray? else floatArrayOf()
            while (nodes.size < nodeCount) {
                while (nodes.contains(cur)) {
                    cur++
                }
                nodes.add(cur)
                if (variableEdgeNumber) {
                    numberEdgesToAdd = sampleRandom(connectionProbabilities)
                }
                degreeSum += addEdge(edges, degrees, cur, *weightedRandomNode(degrees, degreeSum, numberEdgesToAdd))
            }
            return GraphUtils.createFromArrayEdges(false, nodes, edges)
        }

        /**
         * Utility to add specified nodes to the edge set and increment the
         * corresponding degrees.
         *
         * @param edges current list of edges
         * @param degrees current list of degrees
         * @param v1 first node of edge to add
         * @param attachments second node(s) of edges to add
         * @return number of new degrees added
         */
        fun addEdge(edges: MutableList<Array<Int?>?>?, degrees: IntArray?, v1: Int, vararg attachments: Int): Int {
            for (v in attachments) {
                edges.add(arrayOf(v1, v))
                degrees.get(v)++
            }
            degrees.get(v1) += attachments.size
            return attachments.size * 2
        }

        /**
         * Utility to return random nodes in a graph, whose weights are specified by the given array.
         *
         * @param weights array describing the weights of nodes in the graph
         * @param sumWeights the sum of weights
         * @param num the number of results to return
         * @return indices of randomly chosen node; will be distinct
         */
        fun weightedRandomNode(weights: IntArray?, sumWeights: Int, num: Int): IntArray? {
            require(num >= 0) { "weightedRandomNode: requires positive # of results: $num" }
            if (num == 0) {
                return intArrayOf()
            }
            val result = IntArray(num)
            var nFound = 0
            val random = DoubleArray(num)
            for (i in 0 until num) {
                random[i] = Math.random() * sumWeights
            }
            var partialSum = 0.0
            for (i in weights.indices) {
                partialSum += weights.get(i)
                for (j in 0 until num) {
                    if (partialSum > random[j]) {
                        result[j] = i
                        if (++nFound == num) {
                            return result
                        }
                    }
                }
            }
            throw IllegalStateException("""
    weightedRandomNode: should not be here since sum random is less than total degree
    weights = ${Arrays.toString(weights)}, sumWeights = $sumWeights, num = $num
    """.trimIndent())
        }

        /**
         * Generate a random index based on a probability array.
         * @param probabilities the probability array
         * @return index of a randomly chosen # in provided array of probabilities
         */
        fun sampleRandom(probabilities: FloatArray?): Int {
            val rand = Math.random()
            var sum = 0f
            for (i in probabilities.indices) {
                sum += probabilities.get(i)
                if (sum > rand) {
                    return i
                }
            }
            throw IllegalStateException("Should not be here since sum random is less than total")
        }
    }
}