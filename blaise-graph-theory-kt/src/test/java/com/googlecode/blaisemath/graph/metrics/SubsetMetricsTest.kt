package com.googlecode.blaisemath.graph.metrics

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
*/   class SubsetMetricsTest {
    // 1--2
    // |  |
    // 3--4--5--6
    // |
    // 7
    @Test
    fun testGetValue_CooperationMetric() {
        val result1 = METRIC1.getValue(TEST_GRAPH, HashSet(Arrays.asList(1, 2, 3, 4)))
        val result2 = METRIC1.getValue(TEST_GRAPH, HashSet(Arrays.asList(1, 4, 5)))
        Assert.assertEquals(5, result1.size.toLong())
        Assert.assertEquals(5, result2.size.toLong())
        Assert.assertArrayEquals(doubleArrayOf(10.0, 2.0, 14.0, 4.0, 2.0), result1, 1e-10)
        Assert.assertArrayEquals(doubleArrayOf(7.0, 5.0, 14.0, 7.0, 2.0), result2, 1e-10)
    }

    @Test
    fun testGetValue_ContractiveMetric() {
        Assert.assertEquals(4, METRIC2.getValue(TEST_GRAPH, HashSet(Arrays.asList(1, 2, 3, 4))) as Long) // 4 not 2 because of the presence of the loop
        Assert.assertEquals(4, METRIC2.getValue(TEST_GRAPH, HashSet(Arrays.asList(4, 5, 6))) as Long) // 4 not 2 because of the presence of the loop
    }

    companion object {
        private val METRIC1: CooperationMetric<Int?>? = CooperationMetric<Any?>(SubsetMetrics.additiveSubsetMetric(Degree()))
        private val METRIC2 = SubsetMetrics.contractiveSubsetMetric(Degree())
        private val TEST_GRAPH = GraphUtils.createFromArrayEdges(false, Arrays.asList(1, 2, 3, 4, 5, 6, 7),
                Arrays.asList(arrayOf<Int?>(1, 2), arrayOf<Int?>(1, 3), arrayOf<Int?>(2, 4), arrayOf<Int?>(3, 4), arrayOf<Int?>(3, 7), arrayOf<Int?>(4, 5), arrayOf<Int?>(5, 6)))
    }
}