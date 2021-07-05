package com.googlecode.blaisemath.graph.generate

import com.google.common.annotations.Beta
import com.google.common.graph.Graph
import com.googlecode.blaisemath.graph.GraphUtils
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
 * Expands subset of a graph by adding connected nodes, looking out a specified number of hops.
 *
 * @author Elisha Peterson
 */
@Beta
class HopGrowthRule private constructor(private var n: Int) : GraphGrowthRule {
    private var directed = false

    constructor() : this(2) {}

    override fun getName(): String? {
        return "$n-Hop"
    }

    //region PROPERTIES
    fun getN(): Int {
        return n
    }

    fun setN(n: Int) {
        this.n = Math.max(0, n)
    }

    fun isDirected(): Boolean {
        return directed
    }

    fun setDirected(directed: Boolean) {
        this.directed = directed
    }

    //endregion
    override fun <N> grow(graph: Graph<N?>?, seed: MutableSet<N?>?): MutableSet<N?>? {
        return grow(if (directed || !graph.isDirected()) graph else GraphUtils.copyUndirected(graph), seed, n)
    }

    companion object {
        /**
         * Grows the seed set by n hops.
         * @param <N> node type
         * @param graph graph
         * @param seed seed nodes
         * @param n # of steps to grow
         * @return nodes in grown set
        </N> */
        private fun <N> grow(graph: Graph<N?>?, seed: MutableSet<N?>?, n: Int): MutableSet<N?>? {
            if (n == 0) {
                return seed
            }
            val grown = grow1(graph, seed)
            return if (grown.containsAll(seed) && seed.containsAll(grown)) {
                seed
            } else {
                grow<N?>(graph, grown, n - 1)
            }
        }

        /**
         * Grows the seed set by 1 hop.
         * @param graph graph
         * @param seed seed nodes
         * @return nodes in grown set
         */
        private fun <N> grow1(graph: Graph<N?>?, seed: MutableSet<N?>?): MutableSet<N?>? {
            val result: MutableSet<N?> = HashSet(seed)
            seed.forEach(Consumer { o: N? -> result.addAll(graph.adjacentNodes(o)) })
            return result
        }
    }
}