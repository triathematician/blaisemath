package com.googlecode.blaisemath.graph

import com.google.common.collect.*
import com.google.common.graph.*
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graph.ContractedGraphTest
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
import com.googlecode.blaisemath.graph.util.Matrices
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
import java.util.function.Consumer

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
 * Utility methods for creating and analyzing graphs.
 *
 * @author Elisha Peterson
 */
object GraphUtils {
    //region COMPARATORS
    /** Used to sort graphs in descending order by size  */
    val GRAPH_SIZE_DESCENDING: Comparator<Graph?>? = Comparator<Graph?> { o1: Graph?, o2: Graph? ->
        val size1: Int = o1.nodes().size
        val size2: Int = o2.nodes().size
        val edges1: Int = o1.edges().size
        val edges2: Int = o2.edges().size
        if (size1 == size2 && edges1 == edges2) o2.nodes().toString().compareTo(o1.nodes().toString()) else if (size1 == size2) edges2 - edges1 else size2 - size1
    }
    //region CREATORS
    /**
     * Create an empty graph for nodes of appropriate type.
     * @param <N> node type
     * @param directed whether result should be a directed graph
     * @return new empty graph
    </N> */
    fun <N> emptyGraph(directed: Boolean): Graph<N?>? {
        return ImmutableGraph.copyOf(if (directed) GraphBuilder.directed().build() else GraphBuilder.undirected().build())
    }

    /**
     * Create a graph using a given list of nodes and edges.
     * @param <N> node type
     * @param directed whether result should be a directed graph
     * @param nodes nodes
     * @param edges edges
     * @return new graph
    </N> */
    fun <N> createFromEdges(directed: Boolean, nodes: Iterable<N?>?, edges: Iterable<EndpointPair<N?>?>?): Graph<N?>? {
        val res: MutableGraph<N?> = if (directed) GraphBuilder.directed().allowsSelfLoops(true).build() else GraphBuilder.undirected().allowsSelfLoops(true).build()
        nodes.forEach(Consumer { n: N? -> res.addNode(n) })
        edges.forEach(Consumer { e: EndpointPair<N?>? -> res.putEdge(e.nodeU(), e.nodeV()) })
        return res
    }

    /**
     * Create a graph using a given list of nodes and edges
     * @param <N> node type
     * @param directed whether result should be a directed graph
     * @param nodes nodes
     * @param edges edges
     * @return new empty graph
    </N> */
    fun <N> createFromArrayEdges(directed: Boolean, nodes: Iterable<N?>?, edges: Iterable<Array<N?>?>?): Graph<N?>? {
        val res: MutableGraph<N?> = if (directed) GraphBuilder.directed().allowsSelfLoops(true).build() else GraphBuilder.undirected().allowsSelfLoops(true).build()
        nodes.forEach(Consumer { n: N? -> res.addNode(n) })
        edges.forEach(Consumer { e: Array<N?>? -> res.putEdge(e.get(0), e.get(1)) })
        return res
    }

    /**
     * Creates an undirected copy of the specified graph.
     * @param <N> graph node type
     * @param graph a graph
     * @return undirected copy with the same collection of edges
    </N> */
    fun <N> copyUndirected(graph: Graph<N?>?): Graph<N?>? {
        return createFromEdges(false, graph.nodes(), graph.edges())
    }
    //endregion
    //region PRINTING
    /**
     * Returns string representation of specified graph
     * @param graph the graph to print
     * @return string representation of graph
     */
    fun printGraph(graph: Graph<*>?): String? {
        return printGraph(graph, true, true)
    }

    /**
     * Returns string representation of specified graph
     * @param <N> graph node type
     * @param graph the graph to print
     * @param printNodes whether to print nodes or not
     * @param printEdges whether to print edges or not
     * @return string representation of graph
    </N> */
    fun <N> printGraph(graph: Graph<N?>?, printNodes: Boolean, printEdges: Boolean): String? {
        if (!printEdges && !printNodes) {
            return "GRAPH"
        }
        var nodes = graph.nodes()
        val sortable = nodes.stream().allMatch { n: N? -> n is Comparable<*> }
        if (sortable) {
            nodes = TreeSet(nodes)
        }
        val result = StringBuilder()
        if (printNodes) {
            result.append("NODES: ").append(nodes).append("  ")
        }
        if (printEdges) {
            result.append("EDGES:")
            for (n in nodes) {
                result.append(" ").append(n).append(": ")
                        .append(if (sortable) TreeSet<Any?>(graph.successors(n)) else graph.successors(n))
            }
        }
        return result.toString().trim { it <= ' ' }
    }
    //endregion
    //region ADJACENCY MATRIX METHODS
    /**
     * Compute adjacency matrix of a graph.
     * @param <N> graph node type
     * @param graph the input graph
     * @param order if empty, will be filled with order of nodes; if non-empty, will be used to order nodes in graph
     * @return matrix of integers describing adjacencies... contains 0's and 1's;  it is symmetric when the graph is
     * undirected, otherwise it may not be symmetric
    </N> */
    fun <N> adjacencyMatrix(graph: Graph<N?>?, order: MutableList<N?>?): Array<BooleanArray?>? {
        Objects.requireNonNull(order)
        if (order.isEmpty()) {
            order.addAll(graph.nodes())
        }
        val n = order.size
        val result = Array<BooleanArray?>(n) { BooleanArray(n) }
        for (i1 in 0 until n) {
            for (i2 in 0 until n) {
                result[i1].get(i2) = if (graph.isDirected()) graph.successors(order.get(i1)).contains(order.get(i2)) else graph.hasEdgeConnecting(order.get(i1), order.get(i2))
            }
        }
        return result
    }

    /**
     * Computes the adjacency matrix and several of its powers.
     * @param <N> graph node type
     * @param graph the input graph
     * @param order if empty, will be filled with order of nodes; if non-empty, will be used to order nodes in graph
     * @param maxPower maximum power of the adjacency matrix to include in result
     * @return matrix of integers describing adjacencies... contains 0's and 1's... it is symmetric when the graph is
     * undirected, otherwise it may not be symmetric
    </N> */
    fun <N> adjacencyMatrixPowers(graph: Graph<N?>?, order: MutableList<N?>?, maxPower: Int): Array<Array<IntArray?>?>? {
        val adj0 = adjacencyMatrix(graph, order)
        val adj1 = Array<IntArray?>(adj0.size) { IntArray(adj0.size) }
        for (i in adj1.indices) {
            for (j in adj1.indices) {
                adj1[i].get(j) = if (adj0.get(i).get(j)) 1 else 0
            }
        }
        val result = Array<Array<IntArray?>?>(maxPower) { Array(adj1.size) { IntArray(adj1[0].length) } }
        result[0] = adj1
        var cur = 2
        while (cur <= maxPower) {
            result[cur - 1] = Matrices.matrixProduct(result[cur - 2], adj1)
            cur++
        }
        return result
    }
    //endregion
    //region DEGREE
    /**
     * Computes and returns degree distribution.
     * @param <N> graph node type
     * @param graph the graph
     * @return map associating degree #s with counts, sorted by degree
    </N> */
    fun <N> degreeDistribution(graph: Graph<N?>?): Multiset<Int?>? {
        return GraphMetrics.distribution(graph) { g: Graph<N?>?, n: N? -> graph.degree(n) }
    }
    //endregion
    //region GEODESIC & SPANNING TREE METHODS
    /**
     * Computes and creates a tree describing geodesic distances from a specified node, traversing the graph in the direction
     * of the edges. When there are multiple paths with the same minimum length, the resulting path is unspecified.
     * @param <N> graph node type
     * @param graph the starting graph
     * @param node the starting node
     * @return map with node distance lengths
    </N> */
    fun <N> geodesicTree(graph: Graph<N?>?, node: N?): MutableMap<N?, Int?>? {
        return geodesicTree(graph, node, Int.MAX_VALUE)
    }

    /**
     * Computes and creates a tree describing geodesic distances from a specified node, up through a distance
     * specified by the max parameter. Choice of geodesic when multiple are possible is unspecified. The graph
     * only contains the nodes that are in the same component as the starting node (forward component if directed).
     * @param <N> graph node type
     * @param graph the starting graph
     * @param node the starting node
     * @param max the maximum distance to proceed from the starting node
     * @return graph with objects associated to each node that describe the distance from the main node.
    </N> */
    fun <N> geodesicTree(graph: Graph<N?>?, node: N?, max: Int): MutableMap<N?, Int?>? {
        // nodes left to add
        val remaining: MutableSet<N?>? = Sets.newHashSet(graph.nodes())
        // nodes added already, by distance
        val added: MutableList<MutableSet<N?>?> = Lists.newArrayList()
        // stores size of remaining nodes
        var sRemaining = -1
        val max2 = if (max == Int.MAX_VALUE) max - 1 else max
        remaining.remove(node)
        added.add(HashSet(listOf(node)))
        while (sRemaining != remaining.size && added.size < max2 + 1) {
            sRemaining = remaining.size
            added.add(HashSet())
            for (n1 in added[added.size - 2]) {
                val toRemove: MutableSet<N?> = Sets.newHashSet()
                for (n2 in remaining) {
                    if (graph.hasEdgeConnecting(n1, n2)) {
                        toRemove.add(n2)
                        added[added.size - 1].add(n2)
                        val arr = java.lang.reflect.Array.newInstance(n1.javaClass, 2) as Array<N?>
                        arr[0] = n1
                        arr[1] = n2
                    }
                }
                remaining.removeAll(toRemove)
            }
        }
        val result: MutableMap<N?, Int?> = HashMap()
        for (i in added.indices) {
            for (n in added[i]) {
                result[n] = i
            }
        }
        return result
    }

    /**
     * Finds geodesic distance between two nodes in a graph. For directed graphs, the path must traverse the graph in
     * the direction of the edges.
     * @param <N> graph node type
     * @param graph the graph
     * @param start first node
     * @param end second node
     * @return geodesic distance between the nodes, or 0 if they are the same node, or -1 if they are not connected
    </N> */
    fun <N> geodesicDistance(graph: Graph<N?>?, start: N?, end: N?): Int {
        if (start == end) {
            return 0
        }
        if (!(graph.nodes().contains(start) && graph.nodes().contains(end))) {
            return -1
        }
        val nodesToAdd: MutableList<N?>? = Lists.newArrayList(graph.nodes())
        val nodesAdded: MutableList<MutableSet<N?>?> = Lists.newArrayList()
        var nodesToAddCount: Int
        nodesToAdd.remove(start)
        nodesAdded.add(Sets.newHashSet(start))
        do {
            nodesToAddCount = nodesToAdd.size
            nodesAdded.add(HashSet())
            for (n1 in nodesAdded[nodesAdded.size - 2]) {
                val toRemove: MutableSet<N?> = HashSet()
                for (n2 in nodesToAdd) {
                    if (graph.hasEdgeConnecting(n1, n2)) {
                        if (n2 == end) {
                            return nodesAdded.size - 1
                        }
                        toRemove.add(n2)
                        nodesAdded[nodesAdded.size - 1].add(n2)
                    }
                }
                nodesToAdd.removeAll(toRemove)
            }
        } while (nodesToAddCount != nodesToAdd.size)
        return -1
    }
    //endregion
    //region NEIGHBORHOOD & COMPONENT METHODS
    /**
     * Generate ordered set of nodes from an adjacency map.
     * @param <N> graph node type
     * @param adj an adjacency map
     * @return list of nodes
    </N> */
    fun <N> nodes(adj: Multimap<N?, N?>?): MutableSet<N?>? {
        val result: MutableSet<N?> = Sets.newLinkedHashSet()
        result.addAll(adj.keySet())
        result.addAll(adj.values())
        return result
    }

    /**
     * Compute neighborhood about provided node up to a given radius, as a set of nodes. The result always includes
     * the node itself. For directed graphs, this only traverses the graph in the direction of the edges.
     * @param <N> graph node type
     * @param graph the graph
     * @param node the starting node
     * @param radius the maximum distance to consider
     * @return a list containing the nodes within the neighborhood
    </N> */
    fun <N> neighborhood(graph: Graph<N?>?, node: N?, radius: Int): MutableSet<N?>? {
        return geodesicTree(graph, node, radius).keys
    }

    /**
     * Generate connected components from an adjacency map.
     * @param <N> graph node type
     * @param dirAdj an adjacency map (may be directed)
     * @return set of components, as a set of sets
    </N> */
    fun <N> components(dirAdj: Multimap<N?, N?>?): MutableCollection<MutableSet<N?>?>? {
        // ensure symmetry
        val adj: Multimap<N?, N?> = HashMultimap.create()
        for ((key, value) in dirAdj.entries()) {
            adj.put(key, value)
            adj.put(value, key)
        }
        val res: MutableList<MutableSet<N?>?> = Lists.newArrayList()
        val toAdd: MutableSet<N?>? = Sets.newHashSet(adj.keySet())
        while (!toAdd.isEmpty()) {
            val next = toAdd.iterator().next()
            val nComp = component(adj, next)
            res.add(nComp)
            toAdd.removeAll(nComp)
        }
        return res
    }

    private fun <N> component(adj: Multimap<N?, N?>?, v0: N?): MutableSet<N?>? {
        var toSearch: MutableSet<N?>? = Sets.newHashSet(v0)
        val res: MutableSet<N?> = Sets.newHashSet()
        while (!toSearch.isEmpty()) {
            val next: MutableSet<N?> = Sets.newHashSet()
            for (n in toSearch) {
                next.addAll(adj.get(n))
            }
            res.addAll(toSearch)
            next.removeAll(res)
            toSearch = next
        }
        return res
    }

    /**
     * Generate connected components from a graph.
     * @param <N> graph node type
     * @param graph the graph
     * @return set of connected components
    </N> */
    fun <N> components(graph: Graph<N?>?): MutableCollection<MutableSet<N?>?>? {
        return components(adjacencies(graph, graph.nodes()))
    }

    /**
     * Generate connected components from a subset of nodes in a graph.
     * @param <N> graph node type
     * @param graph the graph
     * @param nodes subset of nodes
     * @return set of connected components
    </N> */
    fun <N> components(graph: Graph<N?>?, nodes: MutableSet<N?>?): MutableCollection<MutableSet<N?>?>? {
        return components(adjacencies(graph, nodes))
    }

    /**
     * Generate connected components as a list of subgraphs.
     * @param <N> graph node type
     * @param graph the graph of interest
     * @return set of connected component subgraphs
    </N> */
    fun <N> componentGraphs(graph: Graph<N?>?): MutableSet<Graph<N?>?>? {
        return GraphComponents(graph, components(graph)).componentGraphs()
    }

    /**
     * Generate adjacency map from a subgraph.
     * @param <N> graph node type
     * @param graph the graph
     * @param nodes subset of nodes
     * @return adjacency map restricted to the given subset
    </N> */
    fun <N> adjacencies(graph: Graph<N?>?, nodes: MutableSet<N?>?): Multimap<N?, N?>? {
        val res: Multimap<N?, N?> = LinkedHashMultimap.create()
        for (n in nodes) {
            res.putAll(n, Sets.intersection(graph.adjacentNodes(n), nodes))
        }
        return res
    }

    /**
     * Performs breadth-first search algorithm to enumerate the nodes in a graph, starting from the specified start node.
     * @param <N> graph node type
     * @param graph the graph under consideration
     * @param start the starting node.
     * @param numShortest a map that will be filled with info on the # of shortest paths
     * @param lengths a map that will be filled with info on the lengths of shortest paths
     * @param deque a stack (LIFO) that will be filled with elements in non-increasing order of distance
     * @param adjacencies a map that will be filled with adjacency information for the shortest paths
    </N> */
    fun <N> breadthFirstSearch(
            graph: Graph<N?>?, start: N?, numShortest: Multiset<N?>?, lengths: MutableMap<N?, Int?>?,
            deque: Deque<N?>?, adjacencies: Multimap<N?, N?>?
    ) {
        val nodes = graph.nodes()
        numShortest.add(start)
        for (n in nodes) {
            lengths[n] = -1
        }
        lengths[start] = 0

        // breadth-first search algorithm
        val queue: Deque<N?> = Queues.newArrayDeque()
        queue.add(start)
        while (!queue.isEmpty()) {
            val n1 = queue.remove()
            deque.add(n1)
            for (n2 in graph.adjacentNodes(n1)) {
                // if n2 is found for the first time in the tree, add it to the queue, and adjust the length
                if (lengths.get(n2) == -1) {
                    queue.add(n2)
                    lengths[n2] = lengths.get(n1) + 1
                }
                // adjust the number of shortest paths to n2 if shortest path goes through n1
                if (lengths.get(n2) == lengths.get(n1) + 1) {
                    numShortest.add(n2, numShortest.count(n1))
                    adjacencies.get(n2).add(n1)
                }
            }
        }
    }
    //endregion
    //region CONTRACTED ELEMENTS
    /**
     * Creates a contracted graph from a parent graph, where all of a specified
     * subset of nodes are contracted to a single node
     * @param <N> graph node type
     * @param graph parent graph
     * @param contract nodes to contract
     * @param replace node to replace all contracted nodes
     * @return graph where the specified nodes have been contracted
    </N> */
    fun <N> contractedGraph(graph: Graph<N?>?, contract: MutableCollection<N?>?, replace: N?): Graph<N?>? {
        val edges: MutableList<EndpointPair<N?>?> = Lists.newArrayList()
        for (e in graph.edges()) {
            val node1 = if (contract.contains(e.nodeU())) replace else e.nodeU()
            val node2 = if (contract.contains(e.nodeV())) replace else e.nodeV()
            edges.add(if (e.isOrdered) EndpointPair.ordered(node1, node2) else EndpointPair.unordered(node1, node2))
        }
        return createFromEdges(graph.isDirected(), contractedNodeSet(graph.nodes(), contract, replace), edges)
    }

    /**
     * Contracts list of nodes, replacing all the "contract" nodes with
     * "replace".
     * @param <N> graph node type
     * @param nodes collection of nodes
     * @param contract nodes to contract
     * @param replace node to replace all contracted nodes
     * @return node set
    </N> */
    fun <N> contractedNodeSet(nodes: MutableCollection<N?>?, contract: MutableCollection<N?>?, replace: N?): MutableSet<N?>? {
        val result: MutableSet<N?>? = Sets.newHashSet(nodes)
        result.removeAll(contract)
        result.add(replace)
        return result
    }

    /**
     * Contracts list of components, combining all components with nodes in subset.
     * @param <N> graph node type
     * @param components list of components to contract
     * @param subset subset to contract
     * @param node what to replace contracted subset with
     * @return contracted components
    </N> */
    fun <N> contractedComponents(components: MutableCollection<MutableSet<N?>?>?, subset: MutableCollection<N?>?, node: N?): MutableSet<MutableSet<N?>?>? {
        val result: MutableSet<MutableSet<N?>?> = Sets.newHashSet()
        val contracted: MutableSet<N?> = Sets.newHashSet()
        contracted.add(node)
        result.add(contracted)
        for (c in components) {
            if (Collections.disjoint(c, subset)) {
                result.add(Sets.newHashSet(c))
            } else {
                contracted.addAll(c)
            }
        }
        return result
    } //endregion
}