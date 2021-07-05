package com.googlecode.blaisemath.graph

import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Multimap
import com.google.common.collect.Ordering
import com.google.common.graph.Graph
import com.google.common.graph.Graphs
import com.googlecode.blaisemath.graphics.svg.SvgElementGraphicConverter
import com.googlecode.blaisemath.graphics.svg.SvgGraphic
import com.googlecode.blaisemath.graphics.svg.SvgGraphicComponent
import com.googlecode.blaisemath.graphics.svg.SvgUtils
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
import com.googlecode.blaisemath.util.Images
import org.junit.Assert
import org.junit.Test
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
*/   class GraphUtilsTest {
    @Test
    fun testPrintGraph() {
        val test1: Graph<Int?>? = Graphs.inducedSubgraph(UNDIRECTED_INSTANCE, HashSet(Arrays.asList(1, 2, 3, 6, 10)))
        val test2: Graph<Int?>? = Graphs.inducedSubgraph(DIRECTED_INSTANCE, HashSet(Arrays.asList(1, 2, 3, 6, 10)))
        Assert.assertEquals("NODES: [1, 2, 3, 6, 10]  EDGES: 1: [2, 6] 2: [1, 3] 3: [2] 6: [1, 6, 10] 10: [6]", GraphUtils.printGraph(test1))
        Assert.assertEquals("NODES: [1, 2, 3, 6, 10]", GraphUtils.printGraph(test1, true, false))
        Assert.assertEquals("EDGES: 1: [2, 6] 2: [1, 3] 3: [2] 6: [1, 6, 10] 10: [6]", GraphUtils.printGraph(test1, false, true))
        Assert.assertEquals("GRAPH", GraphUtils.printGraph(test1, false, false))
        Assert.assertEquals("NODES: [1, 2, 3, 6, 10]  EDGES: 1: [2, 6] 2: [1, 3] 3: [] 6: [6, 10] 10: []", GraphUtils.printGraph(test2))
        Assert.assertEquals("NODES: [1, 2, 3, 6, 10]", GraphUtils.printGraph(test2, true, false))
        Assert.assertEquals("EDGES: 1: [2, 6] 2: [1, 3] 3: [] 6: [6, 10] 10: []", GraphUtils.printGraph(test2, false, true))
        Assert.assertEquals("GRAPH", GraphUtils.printGraph(test2, false, false))
    }

    @Test
    fun testCopyGraph() {
        val copy1: Graph<Int?>? = Graphs.copyOf(UNDIRECTED_INSTANCE)
        val copy2: Graph<Int?>? = Graphs.copyOf(DIRECTED_INSTANCE)
        Assert.assertEquals(UNDIRECTED_INSTANCE.nodes().size.toLong(), copy1.nodes().size.toLong())
        Assert.assertEquals(DIRECTED_INSTANCE.nodes().size.toLong(), copy2.nodes().size.toLong())
        Assert.assertEquals(UNDIRECTED_INSTANCE.edges().size.toLong(), copy1.edges().size.toLong())
        Assert.assertEquals(DIRECTED_INSTANCE.edges().size.toLong(), copy2.edges().size.toLong())
        for (e in EE) {
            Assert.assertTrue(copy1.hasEdgeConnecting(e.get(0), e.get(1)))
            Assert.assertTrue(copy2.hasEdgeConnecting(e.get(0), e.get(1)))
        }
    }

    @Test
    fun testAdjacencyMatrix() {
        val result1 = GraphUtils.adjacencyMatrix(UNDIRECTED_INSTANCE, Arrays.asList(*VV))
        val result2 = GraphUtils.adjacencyMatrix(DIRECTED_INSTANCE, Arrays.asList(*VV))
        Assert.assertEquals("[[false, true, false, false, false, true, false, true, false, false, false], [true, false, true, true, true, false, false, false, false, false, false], [false, true, false, false, false, false, false, false, false, false, false], [false, true, false, false, false, false, false, false, false, false, false], [false, true, false, false, false, false, false, false, false, false, false], [true, false, false, false, false, true, true, false, false, false, false], [false, false, false, false, false, true, false, true, false, false, false], [true, false, false, false, false, false, true, false, false, false, false], [false, false, false, false, false, false, false, false, true, false, false], [false, false, false, false, false, false, false, false, false, false, true], [false, false, false, false, false, false, false, false, false, true, false]]", Arrays.deepToString(result1))
        Assert.assertEquals("[[false, true, false, false, false, true, false, false, false, false, false], [true, false, true, true, true, false, false, false, false, false, false], [false, false, false, false, false, false, false, false, false, false, false], [false, false, false, false, false, false, false, false, false, false, false], [false, false, false, false, false, false, false, false, false, false, false], [false, false, false, false, false, true, true, false, false, false, false], [false, false, false, false, false, false, false, true, false, false, false], [true, false, false, false, false, false, false, false, false, false, false], [false, false, false, false, false, false, false, false, true, false, false], [false, false, false, false, false, false, false, false, false, false, true], [false, false, false, false, false, false, false, false, false, false, false]]", Arrays.deepToString(result2))
        val vv = Arrays.asList("A", "B", "C", "D", "E")
        val edges = arrayOf<Array<String?>?>(arrayOf("A", "B"), arrayOf("B", "C"), arrayOf("C", "D"), arrayOf("D", "A"), arrayOf("E", "E"))
        val result3 = GraphUtils.createFromArrayEdges(false, vv, Arrays.asList(*edges))
        val result4 = GraphUtils.createFromArrayEdges(true, vv, Arrays.asList(*edges))
        Assert.assertEquals("[[false, true, false, true, false], [true, false, true, false, false], [false, true, false, true, false], [true, false, true, false, false], [false, false, false, false, true]]",
                Arrays.deepToString(GraphUtils.adjacencyMatrix(result3, vv)))
        Assert.assertEquals("[[false, true, false, false, false], [false, false, true, false, false], [false, false, false, true, false], [true, false, false, false, false], [false, false, false, false, true]]",
                Arrays.deepToString(GraphUtils.adjacencyMatrix(result4, vv)))
    }

    @Test
    fun testAdjacencyMatrixPowers() {
        val vv = Arrays.asList("A", "B", "C", "D", "E")
        val edges = arrayOf<Array<String?>?>(arrayOf("A", "B"), arrayOf("B", "C"), arrayOf("C", "D"), arrayOf("D", "A"), arrayOf("E", "E"))
        val result1 = GraphUtils.createFromArrayEdges(false, vv, Arrays.asList(*edges))
        val result2 = GraphUtils.createFromArrayEdges(true, vv, Arrays.asList(*edges))
        Assert.assertEquals("[[0, 1, 0, 1, 0], [1, 0, 1, 0, 0], [0, 1, 0, 1, 0], [1, 0, 1, 0, 0], [0, 0, 0, 0, 1]]",
                Arrays.deepToString(GraphUtils.adjacencyMatrixPowers(result1, vv, 12)[0]))
        Assert.assertEquals("[[2, 0, 2, 0, 0], [0, 2, 0, 2, 0], [2, 0, 2, 0, 0], [0, 2, 0, 2, 0], [0, 0, 0, 0, 1]]",
                Arrays.deepToString(GraphUtils.adjacencyMatrixPowers(result1, vv, 12)[1]))
        Assert.assertEquals("[[0, 0, 1, 0, 0], [0, 0, 0, 1, 0], [1, 0, 0, 0, 0], [0, 1, 0, 0, 0], [0, 0, 0, 0, 1]]",
                Arrays.deepToString(GraphUtils.adjacencyMatrixPowers(result2, vv, 12)[1]))
        Assert.assertEquals("[[0, 0, 0, 1, 0], [1, 0, 0, 0, 0], [0, 1, 0, 0, 0], [0, 0, 1, 0, 0], [0, 0, 0, 0, 1]]",
                Arrays.deepToString(GraphUtils.adjacencyMatrixPowers(result2, vv, 12)[2]))
    }

    @Test
    fun testDegreeDistribution() {
        val result1 = GraphUtils.degreeDistribution(UNDIRECTED_INSTANCE)
        val result2 = GraphUtils.degreeDistribution(DIRECTED_INSTANCE)
        Assert.assertEquals("[1 x 5, 2 x 3, 3, 4 x 2]", result1.toString())
        Assert.assertEquals("[1 x 5, 2 x 3, 4 x 2, 5]", result2.toString())
    }

    @Test
    fun testGeodesicTree() {
        val gd1_2 = GraphUtils.geodesicTree(UNDIRECTED_INSTANCE, 2)
        Assert.assertEquals(8, gd1_2.size.toLong())
        val gd1_11 = GraphUtils.geodesicTree(UNDIRECTED_INSTANCE, 11)
        Assert.assertEquals(8, gd1_11.size.toLong())
        val gd1_15 = GraphUtils.geodesicTree(UNDIRECTED_INSTANCE, 15)
        Assert.assertEquals(1, gd1_15.size.toLong())
        val gd1_20 = GraphUtils.geodesicTree(UNDIRECTED_INSTANCE, 20)
        Assert.assertEquals(2, gd1_20.size.toLong())
        val gd2_2 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 2)
        Assert.assertEquals(8, gd2_2.size.toLong())
        val gd2_3 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 3)
        Assert.assertEquals(1, gd2_3.size.toLong())
        val gd2_11 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 11)
        Assert.assertEquals(8, gd2_11.size.toLong())
        val gd2_15 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 15)
        Assert.assertEquals(1, gd2_15.size.toLong())
        val gd2_20 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 20)
        Assert.assertEquals(2, gd2_20.size.toLong())
        val gd2_21 = GraphUtils.geodesicTree(DIRECTED_INSTANCE, 21)
        Assert.assertEquals(1, gd2_21.size.toLong())
        for (i in 1..6) {
            for (j in 1..6) {
                val uDist = GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, i, j)
                Assert.assertEquals(if (uDist == -1) null else uDist, GraphUtils.geodesicTree(UNDIRECTED_INSTANCE, i)[j])
                val dDist = GraphUtils.geodesicDistance(DIRECTED_INSTANCE, i, j)
                Assert.assertEquals(if (dDist == -1) null else dDist, GraphUtils.geodesicTree(DIRECTED_INSTANCE, i)[j])
            }
        }
    }

    @Test
    fun testGeodesicDistance() {
        Assert.assertEquals(0, GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, 1, 1).toLong())
        Assert.assertEquals(1, GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, 1, 6).toLong())
        Assert.assertEquals(2, GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, 2, 11).toLong())
        Assert.assertEquals(1, GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, 3, 2).toLong())
        Assert.assertEquals(-1, GraphUtils.geodesicDistance(UNDIRECTED_INSTANCE, 1, 15).toLong())
        Assert.assertEquals(0, GraphUtils.geodesicDistance(DIRECTED_INSTANCE, 1, 1).toLong())
        Assert.assertEquals(1, GraphUtils.geodesicDistance(DIRECTED_INSTANCE, 1, 6).toLong())
        Assert.assertEquals(4, GraphUtils.geodesicDistance(DIRECTED_INSTANCE, 2, 11).toLong())
        Assert.assertEquals(-1, GraphUtils.geodesicDistance(DIRECTED_INSTANCE, 3, 2).toLong())
        Assert.assertEquals(-1, GraphUtils.geodesicDistance(DIRECTED_INSTANCE, 1, 15).toLong())
    }

    @Test
    fun testNeighborhood() {
        AssertUtils.assertCollectionContentsSame(listOf<Int?>(2), GraphUtils.neighborhood(UNDIRECTED_INSTANCE, 2, 0))
        AssertUtils.assertCollectionContentsSame(Arrays.asList(1, 2, 3, 4, 5), GraphUtils.neighborhood(UNDIRECTED_INSTANCE, 2, 1))
        AssertUtils.assertCollectionContentsSame(Arrays.asList(1, 2, 3, 4, 5, 6, 11), GraphUtils.neighborhood(UNDIRECTED_INSTANCE, 2, 2))
        AssertUtils.assertCollectionContentsSame(Arrays.asList(1, 2, 3, 4, 5, 6, 10, 11), GraphUtils.neighborhood(UNDIRECTED_INSTANCE, 2, 3))
        AssertUtils.assertCollectionContentsSame(Arrays.asList(1, 2, 3, 4, 5, 6, 10, 11), GraphUtils.neighborhood(UNDIRECTED_INSTANCE, 2, 20))
        AssertUtils.assertCollectionContentsSame(Arrays.asList(1, 2, 3, 4, 5), GraphUtils.neighborhood(DIRECTED_INSTANCE, 2, 1))
        AssertUtils.assertCollectionContentsSame(Arrays.asList(1, 2, 3, 4, 5, 6), GraphUtils.neighborhood(DIRECTED_INSTANCE, 2, 2))
        AssertUtils.assertCollectionContentsSame(Arrays.asList(1, 2, 3, 4, 5, 6, 10), GraphUtils.neighborhood(DIRECTED_INSTANCE, 2, 3))
        AssertUtils.assertCollectionContentsSame(Arrays.asList(1, 2, 3, 4, 5, 6, 10, 11), GraphUtils.neighborhood(DIRECTED_INSTANCE, 2, 20))
        AssertUtils.assertCollectionContentsSame(listOf<Int?>(5), GraphUtils.neighborhood(DIRECTED_INSTANCE, 5, 20))
    }

    @Test
    fun testComponents() {
        for (g in Arrays.asList(UNDIRECTED_INSTANCE, DIRECTED_INSTANCE)) {
            val result1: MutableList<MutableSet<Int?>?> = ArrayList(GraphUtils.components(g))
            result1.sort(object : Ordering<MutableCollection<*>?>() {
                override fun compare(left: MutableCollection<*>?, right: MutableCollection<*>?): Int {
                    return left.size - right.size
                }
            }.reverse())
            AssertUtils.assertCollectionContentsSame(Arrays.asList(1, 2, 3, 4, 5, 6, 10, 11), result1[0])
            AssertUtils.assertCollectionContentsSame(Arrays.asList(20, 21), result1[1])
            AssertUtils.assertCollectionContentsSame(listOf<Int?>(15), result1[2])
        }
    }

    @Test
    fun testComponents_Multimap() {
        AssertUtils.assertSets(GraphUtils.components(HashMultimap.create<Any?, Any?>()))
        AssertUtils.assertSets(GraphUtils.components(multimap<Int?>(0, 0)), ImmutableSet.of(0))
        AssertUtils.assertSets(GraphUtils.components(multimap<Int?>(0, 1, 1, 2)), ImmutableSet.of(0, 1, 2))
        AssertUtils.assertSets(GraphUtils.components(multimap<Int?>(0, 1, 2, 3)), ImmutableSet.of(0, 1), ImmutableSet.of(2, 3))
    }

    @Test
    fun testComponentGraphs() {
        for (g in Arrays.asList(UNDIRECTED_INSTANCE, DIRECTED_INSTANCE)) {
            val result1: MutableSet<Graph<Int?>?> = TreeSet(GraphUtils.GRAPH_SIZE_DESCENDING)
            result1.addAll(GraphUtils.componentGraphs(g))
            val graphs: Array<Graph?> = result1.toTypedArray()
            AssertUtils.assertCollectionContentsSame(Arrays.asList(1, 2, 3, 4, 5, 6, 10, 11), graphs[0].nodes())
            Assert.assertEquals(if (g.isDirected) 10 else 9.toLong(), graphs[0].edges().size.toLong())
            AssertUtils.assertCollectionContentsSame(Arrays.asList(20, 21), graphs[1].nodes())
            Assert.assertEquals(1, graphs[1].edges().size.toLong())
            AssertUtils.assertCollectionContentsSame(listOf<Int?>(15), graphs[2].nodes())
            Assert.assertEquals(1, graphs[2].edges().size.toLong())
        }
    }

    companion object {
        private val VV: Array<Int?>? = arrayOf(1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21)
        private val EE: Array<Array<Int?>?>? = arrayOf(arrayOf(1, 2), arrayOf(2, 1), arrayOf(2, 3), arrayOf(2, 4), arrayOf(2, 5), arrayOf(1, 6), arrayOf(6, 6), arrayOf(6, 10), arrayOf(10, 11), arrayOf(11, 1), arrayOf(15, 15), arrayOf(20, 21))
        private val UNDIRECTED_INSTANCE = GraphUtils.createFromArrayEdges(false, Arrays.asList(*VV), Arrays.asList(*EE))
        private val DIRECTED_INSTANCE = GraphUtils.createFromArrayEdges(true, Arrays.asList(*VV), Arrays.asList(*EE))
        private fun <X> multimap(k: X?, v: X?): Multimap<X?, X?>? {
            val res: Multimap<X?, X?> = HashMultimap.create()
            res.put(k, v)
            return res
        }

        private fun <X> multimap(k1: X?, v1: X?, k2: X?, v2: X?): Multimap<X?, X?>? {
            val res: Multimap<X?, X?> = HashMultimap.create()
            res.put(k1, v1)
            res.put(k2, v2)
            return res
        }
    }
}