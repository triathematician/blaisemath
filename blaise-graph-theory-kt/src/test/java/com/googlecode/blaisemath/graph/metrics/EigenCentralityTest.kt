package com.googlecode.blaisemath.graph.metrics

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
import org.junit.Assert
import org.junit.BeforeClass
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
*/   class EigenCentralityTest {
    @Test
    fun testApply() {
        Assert.assertEquals(.475349771, INST.apply(TEST2, 1), 1e-8)
        Assert.assertEquals(.564129165, INST.apply(TEST2, 3), 1e-8)
        Assert.assertEquals(.296008301, INST.apply(TEST2, 4), 1e-8)
    }

    @Test
    fun testApply_All() {
        val values = INST.apply(TEST2)
        Assert.assertEquals(6, values.size.toLong())
        for (i in 0..5) Assert.assertEquals(INST.apply(TEST2, i + 1), values[i + 1])
    }

    companion object {
        private var TEST2: Graph<Int?>? = null
        private var INST: EigenCentrality? = null
        @BeforeClass
        fun setUpClass() {
            INST = EigenCentrality()
            TEST2 = GraphUtils.createFromArrayEdges(false, Arrays.asList(1, 2, 3, 4, 5, 6),
                    Arrays.asList(arrayOf<Int?>(1, 2), arrayOf<Int?>(1, 3), arrayOf<Int?>(2, 3), arrayOf<Int?>(2, 6), arrayOf<Int?>(3, 4), arrayOf<Int?>(4, 5)))
            //   1
            //  / \
            // 2---3--4--5
            // |
            // 6
            //
        }
    }
}