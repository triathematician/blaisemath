package com.googlecode.blaisemath.graph

import com.google.common.graph.Graph
import com.googlecode.blaisemath.graph.ContractedGraphTest
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
 * Performs an iterative 2D layout on a graph, using a given set of parameters. Implementations should use a state
 * object to track their state and make any changes from step to step.
 *
 * @param <P> object describing layout parameters
 * @param <S> object describing layout state
 *
 * @author Elisha Peterson
</S></P> */
interface IterativeGraphLayout<P, S : IterativeGraphLayoutState<*>?> : ParameterSupplier<P?> {
    /**
     * Create a new state object for the layout.
     * @return new state object
     */
    open fun createState(): S?

    /**
     * Iterate the energy layout algorithm. The data structure provided to this method should not be changed during
     * iteration. However, the graph's nodes may not be exactly the same as for previous calls to iterate (i.e. some may
     * have been added or removed). If nodes are present for the first time, the algorithm should add in support for
     * those nodes. If nodes have been removed since the last iteration, the algorithm should simply ignore those nodes.
     *
     *
     * If a request has been placed for new locations, the algorithm should adjust the positions of the requested nodes.
     *
     * @param <N> graph node type
     * @param graph the graph
     * @param layoutState state object for the layout
     * @param layoutParams parameters object for the layout
     * @return energy computation, to provide a measure of algorithm convergence
    </N> */
    open fun <N> iterate(graph: Graph<N?>?, layoutState: S?, layoutParams: P?): Double
}