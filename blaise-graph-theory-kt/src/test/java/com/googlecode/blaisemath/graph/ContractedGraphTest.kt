package com.googlecode.blaisemath.graph

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
*/   class ContractedGraphTest {
    @Test
    fun testNodeCount() {
        Assert.assertEquals(7, UNDIRECTED_INSTANCE.nodes().size.toLong())
        Assert.assertEquals(7, DIRECTED_INSTANCE.nodes().size.toLong())
    }

    @Test
    fun testNodes() {
        val expected = Arrays.asList(0, 3, 4, 10, 11, 20, 21)
        AssertUtils.assertCollectionContentsSame(expected, UNDIRECTED_INSTANCE.nodes())
        AssertUtils.assertCollectionContentsSame(expected, DIRECTED_INSTANCE.nodes())
    }

    @Test
    fun testContains() {
        val expected = Arrays.asList(0, 3, 4, 10, 11, 20, 21)
        for (i in expected) {
            Assert.assertTrue(UNDIRECTED_INSTANCE.nodes().contains(i))
            Assert.assertTrue(DIRECTED_INSTANCE.nodes().contains(i))
        }
        for (i in NODES_TO_REPLACE) {
            Assert.assertFalse(UNDIRECTED_INSTANCE.nodes().contains(i))
            Assert.assertFalse(DIRECTED_INSTANCE.nodes().contains(i))
        }
    }

    @Test
    fun testIsDirected() {
        Assert.assertTrue(DIRECTED_INSTANCE.isDirected)
        Assert.assertFalse(UNDIRECTED_INSTANCE.isDirected)
    }

    @Test
    fun testHasEdgeConnecting() {
        Assert.assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(0, 0))
        Assert.assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(0, 3))
        Assert.assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(4, 0))
        Assert.assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(10, 0))
        Assert.assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(0, 11))
        Assert.assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(10, 11))
        Assert.assertTrue(UNDIRECTED_INSTANCE.hasEdgeConnecting(20, 21))
        Assert.assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(0, 0))
        Assert.assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(0, 3))
        Assert.assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(0, 4))
        Assert.assertFalse(DIRECTED_INSTANCE.hasEdgeConnecting(4, 0))
        Assert.assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(0, 10))
        Assert.assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(10, 11))
        Assert.assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(11, 0))
        Assert.assertFalse(DIRECTED_INSTANCE.hasEdgeConnecting(0, 11))
        Assert.assertTrue(DIRECTED_INSTANCE.hasEdgeConnecting(20, 21))
    }

    @Test
    fun testDegree() {
        val nodes = intArrayOf(0, 3, 4, 10, 11, 20, 21)
        val expectedDegrees1 = intArrayOf(6, 1, 1, 2, 2, 1, 1)
        val expectedDegrees2 = intArrayOf(6, 1, 1, 2, 2, 1, 1)
        val expectedOut = intArrayOf(4, 0, 0, 1, 1, 1, 0)
        val expectedIn = intArrayOf(2, 1, 1, 1, 1, 0, 1)
        for (i in NODES_TO_REPLACE.indices) {
            Assert.assertEquals(expectedDegrees1[i], UNDIRECTED_INSTANCE.degree(nodes[i]).toLong())
            Assert.assertEquals(expectedDegrees2[i], DIRECTED_INSTANCE.degree(nodes[i]).toLong())
            Assert.assertEquals(expectedOut[i], DIRECTED_INSTANCE.outDegree(nodes[i]).toLong())
            Assert.assertEquals(expectedIn[i], DIRECTED_INSTANCE.inDegree(nodes[i]).toLong())
        }
    }

    @Test
    fun testNeighbors() {
        AssertUtils.assertIllegalArgumentException { UNDIRECTED_INSTANCE.degree(1) }
        AssertUtils.assertIllegalArgumentException { DIRECTED_INSTANCE.degree(1) }
        val nodes = intArrayOf(0, 3, 4, 10, 11, 20, 21)
        val neighbors = arrayOf<Array<Int?>?>(arrayOf(0, 3, 4, 10, 11), arrayOf(0), arrayOf(0), arrayOf(0, 11), arrayOf(0, 10), arrayOf(21), arrayOf(20))
        for (i in NODES_TO_REPLACE.indices) {
            AssertUtils.assertCollectionContentsSame(Arrays.asList(*neighbors[i]), UNDIRECTED_INSTANCE.adjacentNodes(nodes[i]))
            AssertUtils.assertCollectionContentsSame(Arrays.asList(*neighbors[i]), DIRECTED_INSTANCE.adjacentNodes(nodes[i]))
        }
    }

    @Test
    fun testEdgeCount() {
        Assert.assertEquals(7, UNDIRECTED_INSTANCE.edges().size.toLong())
        Assert.assertEquals(7, DIRECTED_INSTANCE.edges().size.toLong())
    }

    companion object {
        private val NODE: Int? = 0
        private val NODES: Array<Int?>? = arrayOf(1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21)
        private val EDGES: Array<Array<Int?>?>? = arrayOf(arrayOf(1, 2), arrayOf(2, 1), arrayOf(2, 3), arrayOf(2, 4), arrayOf(2, 5), arrayOf(1, 6), arrayOf(6, 6), arrayOf(6, 10), arrayOf(10, 11), arrayOf(11, 1), arrayOf(15, 15), arrayOf(20, 21))
        private val NODES_TO_REPLACE = Arrays.asList(1, 2, 5, 6, 15)
        private val UNDIRECTED_INSTANCE = GraphUtils.contractedGraph(GraphUtils.createFromArrayEdges(false, Arrays.asList(*NODES), Arrays.asList(*EDGES)), NODES_TO_REPLACE, NODE)
        private val DIRECTED_INSTANCE = GraphUtils.contractedGraph(GraphUtils.createFromArrayEdges(true, Arrays.asList(*NODES), Arrays.asList(*EDGES)), NODES_TO_REPLACE, NODE)
    }
}