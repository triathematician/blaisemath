package com.googlecode.blaisemath.graph.test

import com.google.common.collect.*
import com.google.common.graph.ElementOrder
import com.google.common.graph.EndpointPair
import com.google.common.graph.Graph
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
import com.googlecode.blaisemath.util.Images
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream

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
*/internal class MyTestGraph : Graph<String?> {
    private val directed = false
    private val nodes = Sets.newLinkedHashSet<String?>()
    private val edges: MutableSet<EndpointPair<String?>?>? = LinkedHashSet()
    private val edgeIndex: SetMultimap<String?, EndpointPair<String?>?>? = HashMultimap.create()
    private val edgeTable: Table<String?, String?, MutableSet<EndpointPair<String?>?>?>? = HashBasedTable.create()
    private fun connect(v1: String?, v2: String?) {
        addEdge(v1, v2)
    }

    private fun disconnect(v1: String?, v2: String?) {
        removeEdge(v1, v2)
    }

    fun addNodes(number: Int) {
        var added = 0
        while (added < number) {
            nodes.add("" + nextAvailableNode())
            added++
        }
        check()
    }

    fun removeNodes(number: Int) {
        for (i in 0 until number) {
            val n = randomNode()
            nodes.remove(n)
            edges.removeAll(incidentEdges(n))
            edgeTable.rowKeySet().remove(n)
            edgeTable.columnKeySet().remove(n)
        }
        check()
    }

    fun addEdges(number: Int) {
        for (i in 0 until number) {
            connect(randomNode(), randomNode())
        }
    }

    fun removeEdges(number: Int) {
        for (i in 0 until number) {
            disconnect(randomNode(), randomNode())
        }
    }

    fun rewire(lost: Int, gained: Int) {
        for (i in 0 until lost) {
            removeEdge(randomNode(), randomNode())
        }
        for (i in 0 until gained) {
            addEdge(randomNode(), randomNode())
        }
        check()
    }

    private fun randomNode(): String? {
        val idx = (nodes.size * Math.random()) as Int
        return nodes.toTypedArray()[idx]
    }

    private fun nextAvailableNode(): Int {
        var i = 1
        while (nodes.contains("" + i)) {
            i++
        }
        return i
    }

    private fun check() {
        if (!nodes.containsAll(edgeTable.rowKeySet())) {
            System.err.println("Adjacency table has row nodes not in the node set.")
        }
        if (!nodes.containsAll(edgeTable.columnKeySet())) {
            System.err.println("Adjacency table has column nodes not in the node set.")
        }
    }

    override fun isDirected(): Boolean {
        return false
    }

    override fun nodes(): MutableSet<String?>? {
        return nodes
    }

    private fun edgeCount(): Int {
        return edges.size
    }

    override fun edges(): MutableSet<EndpointPair<String?>?>? {
        return edges
    }

    //region MUTATORS
    @Synchronized
    private fun addEdge(x: String?, y: String?) {
        val edge = if (directed) addDirectedEdge(x, y) else addUndirectedEdge(x, y)
        edges.add(edge)
        edgeIndex.put(x, edge)
        edgeIndex.put(y, edge)
    }

    private fun addDirectedEdge(x: String?, y: String?): EndpointPair<String?>? {
        if (!edgeTable.contains(x, y)) {
            edgeTable.put(x, y, HashSet())
        }
        val edge = EndpointPair.ordered(x, y)
        edgeTable.get(x, y).add(edge)
        return edge
    }

    private fun addUndirectedEdge(x: String?, y: String?): EndpointPair<String?>? {
        if (!edgeTable.contains(x, y)) {
            edgeTable.put(x, y, HashSet())
        }
        if (!edgeTable.contains(y, x)) {
            edgeTable.put(y, x, HashSet())
        }
        val edge = EndpointPair.unordered(x, y)
        edgeTable.get(x, y).add(edge)
        edgeTable.get(y, x).add(edge)
        return edge
    }

    override fun hasEdgeConnecting(x: String?, y: String?): Boolean {
        return if (edgeTable.contains(x, y) && !edgeTable.get(x, y).isEmpty()) {
            true
        } else directed && edgeTable.contains(y, x) && !edgeTable.get(y, x).isEmpty()
    }

    /**
     * Remove edge between two nodes, if it exists
     * @param n1 first node
     * @param n2 second node
     */
    private fun removeEdge(n1: String?, n2: String?) {
        val edge = if (directed) EndpointPair.ordered(n1, n2) else EndpointPair.unordered(n1, n2)
        if (edgeTable.contains(n1, n2)) {
            edgeTable.get(n1, n2).remove(edge)
        }
        if (!directed && edgeTable.contains(n2, n1)) {
            edgeTable.get(n2, n1).remove(edge)
        }
        edgeIndex.remove(n1, edge)
        edgeIndex.remove(n2, edge)
        edges.remove(edge)
    }

    //endregion
    override fun allowsSelfLoops(): Boolean {
        return false
    }

    override fun nodeOrder(): ElementOrder<String?>? {
        return ElementOrder.insertion()
    }

    override fun incidentEdges(x: String?): MutableSet<EndpointPair<String?>?>? {
        return edgeIndex.get(x)
    }

    override fun successors(x: String?): MutableSet<String?>? {
        return if (!directed) {
            adjacentNodes(x)
        } else {
            val result: MutableSet<String?> = HashSet()
            for (e in incidentEdges(x)) {
                if (x == e.nodeU()) {
                    result.add(e.nodeV())
                }
            }
            result
        }
    }

    override fun predecessors(x: String?): MutableSet<String?>? {
        return if (!directed) {
            adjacentNodes(x)
        } else {
            val result: MutableSet<String?> = HashSet()
            for (e in incidentEdges(x)) {
                if (x == e.nodeV()) {
                    result.add(e.nodeU())
                }
            }
            result
        }
    }

    override fun adjacentNodes(x: String?): MutableSet<String?>? {
        val result: MutableSet<String?> = HashSet()
        for (e in incidentEdges(x)) {
            result.add(e.adjacentNode(x))
        }
        return result
    }

    override fun outDegree(x: String?): Int {
        return if (!directed) {
            degree(x)
        } else {
            var result = 0
            for (e in incidentEdges(x)) {
                if (x == e.nodeU()) {
                    result++
                }
            }
            result
        }
    }

    override fun inDegree(x: String?): Int {
        return if (!directed) {
            degree(x)
        } else {
            var result = 0
            for (e in incidentEdges(x)) {
                if (x == e.nodeV()) {
                    result++
                }
            }
            result
        }
    }

    override fun degree(x: String?): Int {
        var result = 0
        for (e in incidentEdges(x)) {
            // permit double counting if both nodes of edge are x
            if (x == e.nodeU()) {
                result++
            }
            if (x == e.nodeV()) {
                result++
            }
        }
        return result
    }

    companion object {
        private fun intList(i0: Int, i1: Int): MutableList<String?>? {
            return IntStream.rangeClosed(i0, i1).mapToObj { i: Int -> Integer.toString(i) }.collect(Collectors.toList())
        }
    }

    init {
        nodes.addAll(intList(1, 100))
        for (i in 0..99) {
            connect(randomNode(), randomNode())
        }
    }
}