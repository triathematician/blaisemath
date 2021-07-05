package com.googlecode.blaisemath.graph.layout

import com.google.common.base.Joiner
import com.google.common.collect.Lists
import com.google.common.collect.Multiset
import com.google.common.collect.Sets
import com.googlecode.blaisemath.graph.GraphUtils
import com.googlecode.blaisemath.graph.generate.EdgeLikelihoodGenerator
import com.googlecode.blaisemath.graph.generate.EdgeLikelihoodGenerator.EdgeLikelihoodParameters
import com.googlecode.blaisemath.graph.generate.WattsStrogatzGenerator
import com.googlecode.blaisemath.graph.generate.WattsStrogatzGenerator.WattsStrogatzParameters
import com.googlecode.blaisemath.graph.layout.StaticSpringLayout.StaticSpringLayoutParameters
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
import com.googlecode.blaisemath.util.Instrument.end
import com.googlecode.blaisemath.util.Instrument.print
import com.googlecode.blaisemath.util.Instrument.start
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
                EdgeLikelihoodGenerator(randomSeed).apply(EdgeLikelihoodParameters(false, 100, .01f)),
                EdgeLikelihoodGenerator(randomSeed).apply(EdgeLikelihoodParameters(false, 100, .05f)),
                EdgeLikelihoodGenerator(randomSeed).apply(EdgeLikelihoodParameters(false, 100, .1f)),
                EdgeLikelihoodGenerator(randomSeed).apply(EdgeLikelihoodParameters(false, 300, .01f)),
                EdgeLikelihoodGenerator(randomSeed).apply(EdgeLikelihoodParameters(false, 1000, .002f)),  //            new EdgeLikelihoodGenerator(false, 300, .05f).get(),
                //            new EdgeLikelihoodGenerator(false, 300, .1f).get(),
                //            new EdgeLikelihoodGenerator(true, 300, .05f).get(),
                WattsStrogatzGenerator(randomSeed).apply(WattsStrogatzParameters(false, 100, 4, .05f)) //            new WattsStrogatzGenerator(false, 1000, 4, .01f).randomGenerator(randomSeed).get()
        )
        for (g in graphs) {
            System.out.printf("\nGraph dir=%s, |V|=%s, |E|=%s, #components=%s, degrees=%s\n",
                    g.isDirected, g.nodes().size, g.edges().size,
                    GraphUtils.components(g).size,
                    nicer<Int?>(GraphUtils.degreeDistribution(g)))
            val id = start("EdgePD", g.toString() + "")
            sl.layout(g, null, StaticSpringLayoutParameters())
            end(id)
        }
        println("\n\n")
        print(System.out)
    }

    private fun <X : Comparable<*>?> nicer(set: Multiset<X?>?): String? {
        val ss: MutableList<String?>? = Lists.newArrayList()
        for (el in Sets.newTreeSet(set.elementSet())) {
            ss.add(el.toString() + ":" + set.count(el))
        }
        return "[" + Joiner.on(",").join(ss) + "]"
    }
}