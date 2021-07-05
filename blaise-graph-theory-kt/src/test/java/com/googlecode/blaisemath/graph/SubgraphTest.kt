package com.googlecode.blaisemath.graph

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
*/   class SubgraphTest {
    @Test
    fun testToString() {
        Assert.assertEquals("NODES: [1, 2, 5, 6, 15]  EDGES: 1: [2, 6] 2: [1, 5] 5: [2] 6: [1, 6] 15: [15]", GraphUtils.printGraph(UNDIRECTED_INSTANCE))
        Assert.assertEquals("NODES: [1, 2, 5, 6, 15]  EDGES: 1: [2, 6] 2: [1, 5] 5: [] 6: [6] 15: [15]", GraphUtils.printGraph(DIRECTED_INSTANCE))
    }

    @Test
    fun testNodes() {
        AssertUtils.assertCollectionContentsSame(SUB, UNDIRECTED_INSTANCE.nodes())
        AssertUtils.assertCollectionContentsSame(SUB, DIRECTED_INSTANCE.nodes())
    }

    @Test
    fun testContains() {
        for (i in SUB) {
            Assert.assertTrue(UNDIRECTED_INSTANCE.nodes().contains(i))
            Assert.assertTrue(DIRECTED_INSTANCE.nodes().contains(i))
        }
        Assert.assertFalse(UNDIRECTED_INSTANCE.nodes().contains(0))
        Assert.assertFalse(DIRECTED_INSTANCE.nodes().contains(0))
        Assert.assertFalse(UNDIRECTED_INSTANCE.nodes().contains(3))
        Assert.assertFalse(DIRECTED_INSTANCE.nodes().contains(3))
    }

    @Test
    fun testIsDirected() {
        Assert.assertTrue(DIRECTED_INSTANCE.isDirected())
        Assert.assertFalse(UNDIRECTED_INSTANCE.isDirected())
    }

    @Test
    fun testHasEdgeConnecting() {
        Assert.assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(1, 2))
        Assert.assertFalse(UNDIRECTED_INSTANCE.hasEdgeConnecting(1, 5))
        Assert.assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(2, 5))
        Assert.assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(5, 2))
        Assert.assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(6, 6))
        Assert.assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(15, 15))
        Assert.assertFalse(UNDIRECTED_INSTANCE.hasEdgeConnecting(6, 10))
        Assert.assertFalse(UNDIRECTED_INSTANCE.hasEdgeConnecting(0, 2))
        Assert.assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(1, 2))
        Assert.assertFalse(DIRECTED_INSTANCE.hasEdgeConnecting(1, 5))
        Assert.assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(2, 5))
        Assert.assertFalse(DIRECTED_INSTANCE.hasEdgeConnecting(5, 2))
        Assert.assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(6, 6))
        Assert.assertFalse(UNDIRECTED_INSTANCE.hasEdgeConnecting(6, 10))
        Assert.assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(15, 15))
    }

    @Test
    fun testDegree() {
        val nodes = intArrayOf(1, 2, 5, 6, 15)
        val expectedDegrees1 = intArrayOf(2, 2, 1, 3, 2)
        val expectedDegrees2 = intArrayOf(3, 3, 1, 3, 2)
        for (i in SUB.indices) {
            Assert.assertEquals(expectedDegrees1[i], UNDIRECTED_INSTANCE.degree(nodes[i]).toLong())
            Assert.assertEquals(expectedDegrees2[i], DIRECTED_INSTANCE.degree(nodes[i]).toLong())
        }
    }

    @Test
    fun testAdjacentNodes() {
        AssertUtils.assertIllegalArgumentException { UNDIRECTED_INSTANCE.adjacentNodes(0).size }
        AssertUtils.assertIllegalArgumentException { DIRECTED_INSTANCE.adjacentNodes(0).size }
        val nodes = intArrayOf(1, 2, 5, 6, 15)
        val result = arrayOf<Array<Int?>?>(arrayOf(2, 6), arrayOf(1, 5), arrayOf(2), arrayOf(1, 6), arrayOf(15))
        for (i in SUB.indices) {
            AssertUtils.assertCollectionContentsSame(Arrays.asList(*result[i]), UNDIRECTED_INSTANCE.adjacentNodes(nodes[i]))
            AssertUtils.assertCollectionContentsSame(Arrays.asList(*result[i]), DIRECTED_INSTANCE.adjacentNodes(nodes[i]))
        }
    }

    @Test
    fun testEdges() {
        Assert.assertEquals(5, UNDIRECTED_INSTANCE.edges().size.toLong())
        Assert.assertEquals(6, DIRECTED_INSTANCE.edges().size.toLong())
    }

    companion object {
        private val VV: Array<Int?>? = arrayOf(1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21)
        private val EE: Array<Array<Int?>?>? = arrayOf(arrayOf(1, 2), arrayOf(2, 1), arrayOf(2, 3), arrayOf(2, 4), arrayOf(2, 5), arrayOf(1, 6), arrayOf(6, 6), arrayOf(6, 10), arrayOf(10, 11), arrayOf(11, 1), arrayOf(15, 15), arrayOf(20, 21))
        private val SUB: MutableSet<Int?>? = HashSet(Arrays.asList(1, 2, 5, 6, 15))
        private val UNDIRECTED_INSTANCE: Graph<Int?>? = Graphs.inducedSubgraph(GraphUtils.createFromArrayEdges(false, Arrays.asList(*VV), Arrays.asList(*EE)), SUB)
        private val DIRECTED_INSTANCE: Graph<Int?>? = Graphs.inducedSubgraph(GraphUtils.createFromArrayEdges(true, Arrays.asList(*VV), Arrays.asList(*EE)), SUB)
    }
}