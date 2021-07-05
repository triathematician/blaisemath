package com.googlecode.blaisemath.graph.metrics

import com.google.common.graph.Graph
import com.googlecode.blaisemath.graph.ContractedGraphTest
import com.googlecode.blaisemath.graph.GraphUtils
import com.googlecode.blaisemath.graph.GraphUtilsTest
import com.googlecode.blaisemath.graph.SubgraphTest
import com.googlecode.blaisemath.graph.generate.GraphGrowthRule
import com.googlecode.blaisemath.graph.generate.GraphSeedRule
import com.googlecode.blaisemath.graph.generate.HopGrowthRule
import com.googlecode.blaisemath.graph.internal.Matrices
import com.googlecode.blaisemath.graph.layout.SpringLayoutPerformanceTest
import com.googlecode.blaisemath.graph.metrics.AdditiveSubsetMetricTest
import com.googlecode.blaisemath.graph.metrics.BetweenCentralityTest
import com.googlecode.blaisemath.graph.metrics.ClosenessCentralityTest
import com.googlecode.blaisemath.graph.metrics.CooperationMetric
import com.googlecode.blaisemath.graph.metrics.EigenCentralityTest
import com.googlecode.blaisemath.graph.metrics.GraphCentralityTest
import com.googlecode.blaisemath.graph.metrics.SubsetMetricsTest
import com.googlecode.blaisemath.test.AssertUtils
import com.googlecode.blaisemath.util.Instrument
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
 * Implementation of the eigenvalue centrality calculation. Uses an approximation method to compute the largest eigenvector
 * for the adjacency matrix.
 *
 * @author Elisha Peterson
 */
class EigenCentrality : AbstractGraphNodeMetric<Double?>("Eigenvalue centrality (estimated)") {
    override fun <N> apply(graph: Graph<N?>?, node: N?): Double? {
        return apply(graph).get(node)
    }

    override fun <N> apply(graph: Graph<N?>?): MutableMap<N?, Double?>? {
        val id = Instrument.start("EigenCentrality.allValues", graph.nodes().size.toString() + " nodes", graph.edges().size.toString() + " edges")

        // computes eigenvalue centrality via repeated powers of the adjacency matrix
        // (this finds the largest-magnitude eigenvector)
        val nodes: MutableList<N?> = ArrayList()
        val adj0 = GraphUtils.adjacencyMatrix(graph, nodes)
        val n = nodes.size
        val mx = Array<DoubleArray?>(n) { DoubleArray(n) }
        for (i in mx.indices) {
            for (j in mx.indices) {
                mx[i].get(j) = if (adj0[i][j]) 1 else 0
                mx[i].get(j) = mx[i].get(j)
            }
        }
        var powerMatrix = Matrices.matrixProduct(mx, mx)
        for (i in 0..9) {
            powerMatrix = Matrices.matrixProduct(powerMatrix, powerMatrix)
            normalize(powerMatrix)
        }

        // compute 256 and 257th power vectors
        val vec0 = DoubleArray(n)
        Arrays.fill(vec0, 1.0 / n)
        val powerVector1 = Matrices.matrixProduct(powerMatrix, vec0)
        val powerVector2 = Matrices.matrixProduct(mx, powerVector1)

        // estimate eigenvalue for testing purposes
        val div = DoubleArray(n)
        for (i in 0 until n) {
            div[i] = powerVector2[i] / powerVector1[i]
        }
        Instrument.middle(id, "EigenCentrality.allValues", "eigenvalues=" + Arrays.toString(div))
        Matrices.normalize(powerVector2)
        for (i in 0 until n - 1) {
            if (powerVector2[i] * powerVector2[i] <= 0) {
                // should not happen
                LOG.log(Level.SEVERE, "WARNING -- eigenvector has inconsistent signs")
                break
            }
        }
        val sign = Math.signum(powerVector2[0])
        val result: MutableMap<N?, Double?> = HashMap(n)
        for (i in 0 until n) {
            result[nodes[i]] = sign * powerVector2[i]
        }
        Instrument.end(id)
        return result
    }

    companion object {
        private val LOG = Logger.getLogger(EigenCentrality::class.java.name)

        /** Normalize a matrix by dividing by max value  */
        private fun normalize(mx: Array<DoubleArray?>?) {
            var max = -Double.MAX_VALUE
            for (mx1 in mx) {
                for (j in mx.indices) {
                    max = Math.max(max, mx1.get(j))
                }
            }
            for (mx1 in mx) {
                for (j in mx.indices) {
                    mx1.get(j) /= max
                }
            }
        }
    }
}