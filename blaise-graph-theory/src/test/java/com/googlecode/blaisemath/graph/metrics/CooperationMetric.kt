package com.googlecode.blaisemath.graph.metrics

import com.google.common.annotations.Beta
import com.google.common.graph.Graph
import com.google.common.graph.Graphs
import com.googlecode.blaisemath.graph.GraphSubsetMetric
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
 * Measures difference in team performance with and without a node/subset of
 * nodes. Result contains several values: first is selfish contribution, second
 * is altruistic; third is entire team value, fourth is partial assessment
 * value, fifth is partial team value.
 *
 * @param <T> metric result type
 *
 * @author Elisha Peterson
</T> */
@Beta
internal class CooperationMetric<T : Number?>
/**
 * Construct with base subset metric.
 *
 * @param baseM base metric
 */(private val baseM: GraphSubsetMetric<T?>?) : GraphSubsetMetric<DoubleArray?> {
    override fun toString(): String {
        return "CooperationMetric[$baseM]"
    }

    override fun <N> getValue(graph: Graph<N?>?, nodes: MutableSet<N?>?): DoubleArray? {
        val n = graph.nodes().size
        val m = nodes.size
        val all = graph.nodes()
        val complement: MutableSet<N?> = HashSet(all)
        complement.removeAll(nodes)
        val outcomeAll = if (n == 0) 0.0 else baseM.getValue(graph, all).toDouble()
        val outcomeAll2 = if (m == n) 0.0 else baseM.getValue(graph, complement).toDouble()
        val outcomePart = if (m == n) 0.0 else baseM.getValue(Graphs.inducedSubgraph(graph, complement), complement).toDouble()
        return doubleArrayOf( // selfish contribution
                outcomeAll - outcomeAll2,  // altruistic contribution
                outcomeAll2 - outcomePart,  // value of the outcome for the whole team
                outcomeAll,  // value of the outcome for the whole team, perceived by complement
                outcomeAll2,  // value of the outcome for the complement
                outcomePart)
    }
}