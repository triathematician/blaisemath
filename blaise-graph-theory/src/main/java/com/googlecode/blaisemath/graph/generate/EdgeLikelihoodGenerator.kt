package com.googlecode.blaisemath.graph.generate

import com.google.common.base.Preconditions
import com.google.common.graph.Graph
import com.googlecode.blaisemath.graph.GraphGenerator
import com.googlecode.blaisemath.graph.GraphUtils
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
import com.googlecode.blaisemath.test.AssertUtils
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
 * Generate random graph with specified edge probability.
 *
 * @author Elisha Peterson
 */
class EdgeLikelihoodGenerator : GraphGenerator<EdgeLikelihoodGenerator.EdgeLikelihoodParameters?, Int?> {
    private var seed: Random? = null

    constructor() {}
    constructor(seed: Random?) {
        this.seed = seed
    }

    override fun toString(): String {
        return "Random Graph (fixed Edge Probability)"
    }

    override fun createParameters(): EdgeLikelihoodParameters? {
        return EdgeLikelihoodParameters()
    }

    override fun apply(p: EdgeLikelihoodParameters?): Graph<Int?>? {
        val directed = p.isDirected()
        val r = if (seed == null) Random() else seed
        val nn = GraphGenerators.intList(0, p.getNodeCount())
        val edges: MutableList<Array<Int?>?> = ArrayList()
        for (i in 0 until p.getNodeCount()) {
            for (j in if (directed) 0 else i + 1 until p.getNodeCount()) {
                if (r.nextDouble() < p.getProbability()) {
                    edges.add(arrayOf(nn[i], nn[j]))
                }
            }
        }
        return GraphUtils.createFromArrayEdges(directed, nn, edges)
    }
    //region PARAMETERS CLASS
    /** Parameters for edge probability generator  */
    class EdgeLikelihoodParameters : DefaultGeneratorParameters {
        private var probability = .1f

        constructor() {}
        constructor(directed: Boolean, nodes: Int, prob: Float) : super(directed, nodes) {
            setProbability(prob)
        }

        fun getProbability(): Float {
            return probability
        }

        fun setProbability(probability: Float) {
            Preconditions.checkArgument(probability >= 0 && probability <= 1)
            this.probability = probability
        }
    } //endregion
}