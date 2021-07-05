package com.googlecode.blaisemath.graph.view

import com.google.common.base.Function
import com.google.common.collect.Ordering
import com.google.common.graph.EndpointPair
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graph.ContractedGraphTest
import com.googlecode.blaisemath.graph.GraphUtilsTest
import com.googlecode.blaisemath.graph.SubgraphTest
import com.googlecode.blaisemath.graph.app.AnimationUtils
import com.googlecode.blaisemath.graph.app.GraphApp
import com.googlecode.blaisemath.graph.app.GraphAppCanvas
import com.googlecode.blaisemath.graph.app.GraphAppFrameView
import com.googlecode.blaisemath.graph.app.MetricScaler
import com.googlecode.blaisemath.graph.generate.GraphGrowthRule
import com.googlecode.blaisemath.graph.generate.GraphSeedRule
import com.googlecode.blaisemath.graph.generate.HopGrowthRule
import com.googlecode.blaisemath.graph.layout.SpringLayoutPerformanceTest
import com.googlecode.blaisemath.graph.metrics.AdditiveSubsetMetricTest
import com.googlecode.blaisemath.graph.metrics.BetweenCentralityTest
import com.googlecode.blaisemath.graph.metrics.ClosenessCentralityTest
import com.googlecode.blaisemath.graph.metrics.CooperationMetric
import com.googlecode.blaisemath.graph.metrics.EigenCentralityTest
import com.googlecode.blaisemath.graph.metrics.GraphCentralityTest
import com.googlecode.blaisemath.graph.metrics.SubsetMetricsTest
import com.googlecode.blaisemath.graph.test.DynamicGraphTestFrame
import com.googlecode.blaisemath.graph.test.GraphTestFrame
import com.googlecode.blaisemath.graph.test.MyTestGraph
import com.googlecode.blaisemath.graphics.editor.AttributeSetPropertyModelTestFrame
import com.googlecode.blaisemath.graphics.svg.SvgElementGraphicConverter
import com.googlecode.blaisemath.graphics.svg.SvgGraphic
import com.googlecode.blaisemath.graphics.svg.SvgGraphicComponent
import com.googlecode.blaisemath.graphics.svg.SvgUtils
import com.googlecode.blaisemath.graphics.swing.AnchorTestFrame
import com.googlecode.blaisemath.graphics.testui.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.testui.HelloWorldTest
import com.googlecode.blaisemath.graphics.testui.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.testui.SelectionTestFrame
import com.googlecode.blaisemath.graphics.testui.TooltipTestFrame
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
import com.googlecode.blaisemath.test.AssertUtils
import com.googlecode.blaisemath.ui.PropertyActionPanel
import com.googlecode.blaisemath.util.Images
import junit.framework.TestCase
import org.junit.BeforeClass
import java.awt.Color

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
 * Provides an edge styler for changing the appearance of edges in a weighted graph. Provides unique styles for positive
 * and negative weights.
 *
 * @param <E> edge type
 * @author Elisha Peterson
</E> */
class WeightedEdgeStyler<E : EndpointPair?>(
        /** Parent style  */
        protected val parent: AttributeSet?,
        /** Edge weights  */
        protected var weights: MutableMap<E?, Double?>?
) : Function<E?, AttributeSet?> {
    /** The maximum edge weight  */
    protected var maxWeight: Double
    fun getWeights(): MutableMap<E?, Double?>? {
        return weights
    }

    fun setWeights(weights: MutableMap<E?, Double?>?) {
        if (this.weights !== weights) {
            this.weights = weights
            maxWeight = if (weights.isEmpty()) 1.0 else Ordering.natural<Comparable<*>?>().max(weights.values)
        }
    }

    override fun apply(o: E?): AttributeSet? {
        val wt = weights.get(o)
        maxWeight = Math.max(maxWeight, Math.abs(wt))
        val positive = wt >= 0
        val relativeWeight = Math.abs(wt) / maxWeight
        val stroke = parent.getColor(Styles.STROKE)
        val c = if (positive) positiveColor(stroke, relativeWeight) else negativeColor(stroke, relativeWeight)
        return AttributeSet.withParent(parent)
                .and(Styles.STROKE, c)
                .and(Styles.STROKE_WIDTH, (2 * relativeWeight) as Float)
    }

    companion object {
        private const val HUE_RANGE = 0.1f

        //region UTILS
        private fun positiveColor(c: Color?, weight: Double): Color? {
            val wt = Math.min(1.0, Math.max(0.0, weight))
            val alpha = 100 + (155 * wt) as Int
            return if (c == null) {
                Color(
                        25 - (25 * wt) as Int,
                        205 + (50 * wt) as Int,
                        100 - (50 * wt) as Int,
                        alpha)
            } else {
                val hsb = Color.RGBtoHSB(c.red, c.green, c.blue, null)
                hsb[0] += HUE_RANGE * wt
                hsb[1] *= .5 + .5 * wt
                hsbColor(hsb, alpha)
            }
        }

        private fun negativeColor(c: Color?, weight: Double): Color? {
            val wt = Math.min(1.0, Math.max(0.0, weight))
            val alpha = 100 + (155 * wt) as Int
            return if (c == null) {
                Color(
                        205 + (50 * wt) as Int,
                        0,
                        100 - (50 * wt) as Int,
                        alpha)
            } else {
                val hsb = Color.RGBtoHSB(c.red, c.green, c.blue, null)
                hsb[0] -= 2 * HUE_RANGE * wt
                hsb[1] *= .5 + .5 * wt
                hsbColor(hsb, alpha)
            }
        }

        private fun hsbColor(hsb: FloatArray?, alpha: Int): Color? {
            val col = Color(Color.HSBtoRGB(hsb.get(0), hsb.get(1), hsb.get(2)))
            return Color(col.red, col.green, col.blue, alpha)
        } //endregion
    }

    /**
     * Construct the customizer
     * @param parent the parent style
     * @param weights weightings for edges in graph
     */
    init {
        maxWeight = if (weights.isEmpty()) 1.0 else Ordering.natural<Comparable<*>?>().max(weights.values)
    }
}