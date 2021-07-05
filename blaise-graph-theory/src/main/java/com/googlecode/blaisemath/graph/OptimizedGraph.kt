package com.googlecode.blaisemath.graph

import com.google.common.base.Preconditions
import com.google.common.collect.*
import com.google.common.graph.ElementOrder
import com.google.common.graph.EndpointPair
import com.google.common.graph.Graph
import com.google.common.graph.Graphs
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
 * A storage-optimized graph component that caches degrees and divides nodes into isolates, leaf nodes, connector nodes
 * (degree 2), and core nodes (degree more than 2). This maximizes speed for algorithms that make large numbers of calls to
 * graph API methods.
 *
 * @param <N> graph node type
 *
 * @author Elisha Peterson
</N> */
class OptimizedGraph<N> : Graph<N?> {
    /** Base graph  */
    private val base: Graph<N?>?

    /** Degree cache  */
    private val degrees: MutableMap<N?, Int?>? = Maps.newHashMap()

    /** Isolate nodes (deg = 0)  */
    private val isolates: MutableSet<N?>? = Sets.newHashSet()

    /** Leaf nodes (deg = 1)  */
    private val leafNodes: MutableSet<N?>? = Sets.newHashSet()

    /** Connector nodes (deg = 2)  */
    private val connectorNodes: MutableSet<N?>? = Sets.newHashSet()

    /** Non-leaf nodes (deg >= 3)  */
    private val coreNodes: MutableSet<N?>? = Sets.newHashSet()

    /** General objects adjacent to each node  */
    private val neighbors: SetMultimap<N?, N?>? = HashMultimap.create()

    /**
     * Leaf objects adjacent to each node. Values consist of objects that
     * have degree 1 ONLY.
     */
    private val adjLeaves: SetMultimap<N?, N?>? = HashMultimap.create()

    /**
     * Construct optimized graph version of the given graph.
     * @param graph graph to optimize
     */
    constructor(graph: Graph<N?>?) {
        base = graph
        initCachedElements()
    }

    /**
     * Construct optimized graph with specific nodes and edges.
     * @param directed whether graph is directed
     * @param nodes nodes in the graph
     * @param edges edges in the graph, as ordered node pairs; each must have a 0 element and a 1 element
     */
    constructor(directed: Boolean, nodes: MutableCollection<N?>?, edges: Iterable<EndpointPair<N?>?>?) {
        base = GraphUtils.createFromEdges(directed, nodes, edges)
        initCachedElements()
    }
    //region INITIALIZATION
    /** Initializes set of pre-computed elements.  */
    private fun initCachedElements() {
        for (n in base.nodes()) {
            val deg = base.degree(n)
            degrees[n] = deg
            when (deg) {
                0 -> isolates.add(n)
                1 -> leafNodes.add(n)
                2 -> connectorNodes.add(n)
                else -> coreNodes.add(n)
            }
            neighbors.putAll(n, base.adjacentNodes(n))
        }
        for (n in base.nodes()) {
            for (y in neighbors.get(n)) {
                val get = degrees.get(y)
                Preconditions.checkState(get != null, "Node $y (neighbor of $n) was not found in provided node set")
                if (degrees.get(y) == 1) {
                    adjLeaves.get(n).add(y)
                }
            }
        }
    }
    //endregion
    //region PROPERTIES
    /**
     * Nodes with deg 0
     * @return nodes
     */
    fun isolates(): MutableSet<N?>? {
        return Collections.unmodifiableSet(isolates)
    }

    /**
     * Nodes with deg 1
     * @return nodes
     */
    fun leafNodes(): MutableSet<N?>? {
        return Collections.unmodifiableSet(leafNodes)
    }

    /**
     * Nodes with deg 2
     * @return nodes
     */
    fun connectorNodes(): MutableSet<N?>? {
        return Collections.unmodifiableSet(connectorNodes)
    }

    /**
     * Nodes with deg &gt;= 3
     * @return nodes
     */
    fun coreNodes(): MutableSet<N?>? {
        return Collections.unmodifiableSet(coreNodes)
    }

    /**
     * Get copy of neighbors.
     * @return neighbors
     */
    fun neighborMap(): Multimap<N?, N?>? {
        return Multimaps.unmodifiableSetMultimap(neighbors)
    }

    //endregion
    //region OVERRIDES
    override fun nodes(): MutableSet<N?>? {
        return base.nodes()
    }

    override fun edges(): MutableSet<EndpointPair<N?>?>? {
        return base.edges()
    }

    override fun isDirected(): Boolean {
        return base.isDirected()
    }

    override fun allowsSelfLoops(): Boolean {
        return base.allowsSelfLoops()
    }

    override fun nodeOrder(): ElementOrder<N?>? {
        return base.nodeOrder()
    }

    override fun incidentEdgeOrder(): ElementOrder<N?>? {
        return base.incidentEdgeOrder()
    }

    override fun adjacentNodes(node: N?): MutableSet<N?>? {
        return neighbors.get(node)
    }

    override fun degree(x: N?): Int {
        return degrees.get(x)
    }

    override fun inDegree(node: N?): Int {
        return 0
    }

    override fun outDegree(node: N?): Int {
        return 0
    }

    override fun predecessors(node: N?): MutableSet<N?>? {
        return base.predecessors(node)
    }

    override fun successors(node: N?): MutableSet<N?>? {
        return base.successors(node)
    }

    override fun incidentEdges(node: N?): MutableSet<EndpointPair<N?>?>? {
        return base.incidentEdges(node)
    }

    override fun hasEdgeConnecting(x: N?, y: N?): Boolean {
        return neighbors.containsEntry(x, y)
    }

    override fun hasEdgeConnecting(pair: EndpointPair<N?>?): Boolean {
        return hasEdgeConnecting(pair.nodeU(), pair.nodeV())
    }
    //endregion
    //region ADDITIONAL QUERIES
    /**
     * Extract the core graph, consisting of only nodes with degree at least 2.
     * @return graph with isolates and leaves pruned
     */
    fun core(): Graph<N?>? {
        return Graphs.inducedSubgraph(base, Iterables.concat(coreNodes, connectorNodes))
    }

    /**
     * Return the node adjacent to a leaf
     * @param leaf leaf to check
     * @return adjacent node
     * @throws IllegalArgumentException if node is not a leaf
     */
    fun neighborOfLeaf(leaf: N?): N? {
        Preconditions.checkArgument(leafNodes.contains(leaf))
        val res = Iterables.getFirst(neighbors.get(leaf), null)
        Preconditions.checkState(res != null)
        return res
    }

    /**
     * Return leaf nodes adjacent to specified node
     * @param n node to check
     * @return leaf nodes
     */
    fun leavesAdjacentTo(n: N?): MutableSet<N?>? {
        return adjLeaves.get(n)
    } //endregion
}