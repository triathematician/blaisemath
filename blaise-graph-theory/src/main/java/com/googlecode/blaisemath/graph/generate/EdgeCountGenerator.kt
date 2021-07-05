package com.googlecode.blaisemath.graph.generate

import com.google.common.graph.Graph
import com.googlecode.blaisemath.graph.ContractedGraphTest
import com.googlecode.blaisemath.graph.GraphGenerator
import com.googlecode.blaisemath.graph.GraphUtilsTest
import com.googlecode.blaisemath.graph.SubgraphTest
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
 * Generate random graph with specified edge count.
 *
 * @author Elisha Peterson
 */
class EdgeCountGenerator : GraphGenerator<ExtendedGeneratorParameters?, Int?> {
    override fun toString(): String {
        return "Random Graph (fixed Edge Count)"
    }

    override fun createParameters(): ExtendedGeneratorParameters? {
        return ExtendedGeneratorParameters()
    }

    override fun apply(p: ExtendedGeneratorParameters?): Graph<Int?>? {
        val directed = p.isDirected()
        val nodes = p.getNodeCount()
        val edgeSet: MutableSet<Array<Int?>?> = TreeSet(if (directed) PAIR_COMPARE else PAIR_COMPARE_UNDIRECTED)
        var potential: Array<Int?>
        for (i in 0 until p.edgeCountBounded()) {
            do {
                potential = arrayOf((nodes * Math.random()) as Int, (nodes * Math.random()) as Int)
            } while (!directed && potential[0] == potential[1] || edgeSet.contains(potential))
            edgeSet.add(potential)
        }
        return GraphGenerators.createGraphWithEdges(p, edgeSet)
    }

    companion object {
        //region COMPARATORS
        /**
         * Used to sort pairs of integers when order of the two matters.
         */
        val PAIR_COMPARE: Comparator<Array<Int?>?>? = Comparator { o1: Array<Int?>?, o2: Array<Int?>? ->
            check(!(o1.size != 2 || o2.size != 2)) { "This object only compares integer pairs." }
            if (o1.get(0) == o2.get(0)) o1.get(1) - o2.get(1) else o1.get(0) - o2.get(0)
        }

        /**
         * Used to sort pairs of integers when order of the two does not matter.
         */
        val PAIR_COMPARE_UNDIRECTED: Comparator<Array<Int?>?>? = Comparator { o1: Array<Int?>?, o2: Array<Int?>? ->
            check(!(o1.size != 2 || o2.size != 2)) { "This object only compares integer pairs." }
            val min1 = Math.min(o1.get(0), o1.get(1))
            val min2 = Math.min(o2.get(0), o2.get(1))
            if (min1 == min2) Math.max(o1.get(0), o1.get(1)) - Math.max(o2.get(0), o2.get(1)) else min1 - min2
        }

        //endregion
        private val INST: EdgeCountGenerator? = EdgeCountGenerator()
        fun getInstance(): EdgeCountGenerator? {
            return INST
        }
    }
}