package com.googlecode.blaisemath.graph.generate

import com.google.common.graph.Graph
import com.googlecode.blaisemath.graph.GraphUtils
import com.googlecode.blaisemath.graph.generate.EdgeLikelihoodGenerator.EdgeLikelihoodParameters
import com.googlecode.blaisemath.graph.generate.PreferentialAttachmentGenerator.PreferentialAttachmentParameters
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
import org.junit.Assert
import org.junit.Test

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
*/   class PreferentialAttachmentGeneratorTest {
    @Test
    fun testGetSeededInstance_fixed_add_number() {
        var seed: Graph<Int?>? = null
        while (seed == null || seed.edges().size == 0) {
            seed = EdgeLikelihoodGenerator().apply(EdgeLikelihoodParameters(false, 4, .5f))
        }
        println("  SEEDED with 4 node random graph: " + GraphUtils.printGraph(seed))
        var pref = PreferentialAttachmentGenerator().apply(PreferentialAttachmentParameters(seed, 10, 1))
        println("    result: " + pref.edges().size + " edges, " + GraphUtils.printGraph(pref))
        Assert.assertEquals(seed.edges().size + (pref.nodes().size - seed.nodes().size).toLong(), pref.edges().size.toLong())
        seed = CycleGraphGenerator().apply(DefaultGeneratorParameters(false, 4))
        pref = PreferentialAttachmentGenerator().apply(PreferentialAttachmentParameters(seed, 10, 2))
        println("  SEEDED with 4 node cycle graph: " + GraphUtils.printGraph(seed))
        println("    result: " + pref.edges().size + " edges, " + GraphUtils.printGraph(pref))
        println("    expected " + (seed.edges().size + 2 * (pref.nodes().size - seed.nodes().size)) + " edges or less")
        try {
            PreferentialAttachmentGenerator().apply(PreferentialAttachmentParameters(
                    CycleGraphGenerator().apply(DefaultGeneratorParameters(false, 5)), 20, -1))
            Assert.fail("Should not be able to construct preferential attachment with negative connection numbers.")
        } catch (ex: Exception) {
            // expected
        }
        try {
            PreferentialAttachmentGenerator().apply(PreferentialAttachmentParameters(
                    CycleGraphGenerator().apply(DefaultGeneratorParameters(true, 5)), 20, 1))
            Assert.fail("Should not be able to construct preferential attachment from directed graph.")
        } catch (ex: Exception) {
            // expected
        }
        try {
            PreferentialAttachmentGenerator().apply(PreferentialAttachmentParameters(
                    EmptyGraphGenerator().apply(DefaultGeneratorParameters(false, 5)), 20, 1))
            Assert.fail("Should not be able to construct preferential attachment with empty graph.")
        } catch (ex: Exception) {
            // expected
        }
    }

    @Test
    fun testGetSeededInstance_variable_add_number() {
        var seed = EdgeLikelihoodGenerator().apply(EdgeLikelihoodParameters(false, 4, .5f))
        while (seed.edges().size == 0) {
            seed = EdgeLikelihoodGenerator().apply(EdgeLikelihoodParameters(false, 4, .5f))
        }
        println("  SEEDED with 4 node random graph: " + GraphUtils.printGraph(seed))
        val p1 = floatArrayOf(.5f, .5f)
        var graph = PreferentialAttachmentGenerator().apply(PreferentialAttachmentParameters(seed, 10, p1))
        println("    result (.5, .5): " + graph.edges().size + " edges, " + GraphUtils.printGraph(graph))
        println("    expected " + (seed.edges().size + p1[1] * (graph.nodes().size - seed.nodes().size)) + " edges")
        seed = CycleGraphGenerator().apply(DefaultGeneratorParameters(false, 4))
        println("  SEEDED with 4 node cycle graph: " + GraphUtils.printGraph(seed))
        val p2 = floatArrayOf(0f, .5f, .25f, .25f)
        graph = PreferentialAttachmentGenerator().apply(PreferentialAttachmentParameters(seed, 10, p2))
        println("    result (0, .5, .25, .25): " + graph.edges().size + " edges, " + GraphUtils.printGraph(graph))
        println("    expected " + (seed.edges().size + (p2[1] + 2 * p2[2] + 3 * p2[3]) * (graph.nodes().size - seed.nodes().size)) + " edges")
        try {
            PreferentialAttachmentGenerator().apply(PreferentialAttachmentParameters(
                    EmptyGraphGenerator().apply(DefaultGeneratorParameters(false, 5)), 20, floatArrayOf(.25f, 0f, -1f)))
            Assert.fail("Should not be able to construct preferential attachment with bad probability vector.")
        } catch (ex: Exception) {
            // expected
        }
        try {
            PreferentialAttachmentGenerator().apply(PreferentialAttachmentParameters(
                    CycleGraphGenerator().apply(DefaultGeneratorParameters(true, 5)), 20, floatArrayOf(.5f, .5f)))
            Assert.fail("Should not be able to construct preferential attachment from undirected graph.")
        } catch (ex: Exception) {
            // expected
        }
        try {
            PreferentialAttachmentGenerator().apply(PreferentialAttachmentParameters(
                    EmptyGraphGenerator().apply(DefaultGeneratorParameters(false, 5)), 20, floatArrayOf(.5f, .5f)))
            Assert.fail("Should not be able to construct preferential attachment with empty graph.")
        } catch (ex: Exception) {
            // expected
        }
    }
}