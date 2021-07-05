package com.googlecode.blaisemath.graph.layout

import com.google.common.base.Joiner
import com.google.common.collect.Lists
import com.google.common.collect.Multiset
import com.google.common.collect.Sets
import com.googlecode.blaisemath.graph.GraphUtils
import com.googlecode.blaisemath.graph.generate.EdgeLikelihoodGenerator
import com.googlecode.blaisemath.graph.generate.WattsStrogatzGenerator
import com.googlecode.blaisemath.util.Instrument
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
*/   object SpringLayoutPerformanceTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val randomSeed = Random(1290309812)
        val sl = StaticSpringLayout()
        val graphs = Arrays.asList(
                EdgeLikelihoodGenerator(randomSeed).apply(EdgeLikelihoodGenerator.EdgeLikelihoodParameters(false, 100, .01f)),
                EdgeLikelihoodGenerator(randomSeed).apply(EdgeLikelihoodGenerator.EdgeLikelihoodParameters(false, 100, .05f)),
                EdgeLikelihoodGenerator(randomSeed).apply(EdgeLikelihoodGenerator.EdgeLikelihoodParameters(false, 100, .1f)),
                EdgeLikelihoodGenerator(randomSeed).apply(EdgeLikelihoodGenerator.EdgeLikelihoodParameters(false, 300, .01f)),
                EdgeLikelihoodGenerator(randomSeed).apply(EdgeLikelihoodGenerator.EdgeLikelihoodParameters(false, 1000, .002f)),  //            new EdgeLikelihoodGenerator(false, 300, .05f).get(),
                //            new EdgeLikelihoodGenerator(false, 300, .1f).get(),
                //            new EdgeLikelihoodGenerator(true, 300, .05f).get(),
                WattsStrogatzGenerator(randomSeed).apply(WattsStrogatzGenerator.WattsStrogatzParameters(false, 100, 4, .05f)) //            new WattsStrogatzGenerator(false, 1000, 4, .01f).randomGenerator(randomSeed).get()
        )
        for (g in graphs) {
            System.out.printf("\nGraph dir=%s, |V|=%s, |E|=%s, #components=%s, degrees=%s\n",
                    g.isDirected, g.nodes().size, g.edges().size,
                    GraphUtils.components(g).size,
                    nicer<Int?>(GraphUtils.degreeDistribution(g)))
            val id = Instrument.start("EdgePD", g.toString() + "")
            sl.layout(g, null, StaticSpringLayout.StaticSpringLayoutParameters())
            Instrument.end(id)
        }
        println("\n\n")
        Instrument.print(System.out)
    }

    private fun <X : Comparable<*>?> nicer(set: Multiset<X?>?): String? {
        val ss: MutableList<String?>? = Lists.newArrayList()
        for (el in Sets.newTreeSet(set.elementSet())) {
            ss.add(el.toString() + ":" + set.count(el))
        }
        return "[" + Joiner.on(",").join(ss) + "]"
    }
}