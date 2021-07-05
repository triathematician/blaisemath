package com.googlecode.blaisemath.graph.app

import com.google.common.collect.Maps
import com.google.common.collect.Ordering
import com.google.common.graph.Graph
import com.googlecode.blaisemath.graph.GraphNodeMetric
import com.googlecode.blaisemath.graphics.svg.SvgElementGraphicConverter
import com.googlecode.blaisemath.graphics.svg.SvgGraphic
import com.googlecode.blaisemath.graphics.svg.SvgGraphicComponent
import com.googlecode.blaisemath.graphics.svg.SvgUtils
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Styles
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
import java.util.function.Function

/*
* #%L
* BlaiseGraphTheory (v3)
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
 * Scales nodes based on a metric value, and provides associated style.
 * @author Elisha Peterson
 */
internal class MetricScaler<T> : Function<Any?, AttributeSet?> where T : Number?, T : Comparable<*>? {
    private var graph: Graph? = null
    private var metric: GraphNodeMetric<T?>? = null
    private var scores: MutableMap<Any?, T?>? = null
    private var min = 0.0
    private var max = 0.0
    private val defStyle: AttributeSet? = Styles.DEFAULT_POINT_STYLE.copy()
    private fun recompute() {
        scores = Maps.newLinkedHashMap()
        if (graph == null || metric == null) {
            return
        }
        for (n in graph.nodes()) {
            scores[n] = metric.apply<Any?>(graph, n) as T?
        }
        min = Ordering.natural<Comparable<*>?>().min(scores.values).toDouble()
        max = Ordering.natural<Comparable<*>?>().max(scores.values).toDouble()
    }

    //region PROPERTIES
    fun getGraph(): Graph? {
        return graph
    }

    fun setGraph(graph: Graph?) {
        if (this.graph !== graph) {
            this.graph = graph
            recompute()
        }
    }

    fun getMetric(): GraphNodeMetric<T?>? {
        return metric
    }

    fun setMetric(metric: GraphNodeMetric<T?>?) {
        if (this.metric !== metric) {
            this.metric = metric
            recompute()
        }
    }

    //endregion
    override fun apply(input: Any?): AttributeSet? {
        return if (scores.isEmpty()) {
            defStyle
        } else {
            AttributeSet.withParent(defStyle).and(Styles.MARKER_RADIUS, radScale(input))
        }
    }

    private fun radScale(input: Any?): Double {
        val nScore: Number? = scores.get(input)
        val minRad = 2f
        val maxRad = 10f
        if (nScore == null) {
            return 1f
        } else if (min == max) {
            return .5 * (minRad + maxRad)
        }
        val score = nScore.toDouble()
        return minRad + (maxRad - minRad) * Math.sqrt(score - min) / Math.sqrt(max - min)
    }
}