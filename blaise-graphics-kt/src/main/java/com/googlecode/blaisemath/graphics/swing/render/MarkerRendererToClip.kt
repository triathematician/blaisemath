package com.googlecode.blaisemath.graphics.swing.render
/*
 * #%L
 * BlaiseGraphics
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
 */
import com.google.common.base.Preconditions.checkNotNull
import com.google.common.graph.ElementOrder
import com.google.common.graph.EndpointPair
import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import com.google.common.graph.ImmutableGraph
import com.google.common.graph.MutableGraph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.coordinate.OrientedPoint2D
import com.googlecode.blaisemath.graph.ContractedGraphTest
import com.googlecode.blaisemath.graph.GraphComponents
import com.googlecode.blaisemath.graph.GraphGenerator
import com.googlecode.blaisemath.graph.GraphMetric
import com.googlecode.blaisemath.graph.GraphMetrics
import com.googlecode.blaisemath.graph.GraphNodeMetric
import com.googlecode.blaisemath.graph.GraphNodeStats
import com.googlecode.blaisemath.graph.GraphServices
import com.googlecode.blaisemath.graph.GraphSubsetMetric
import com.googlecode.blaisemath.graph.GraphUtilsTest
import com.googlecode.blaisemath.graph.IterativeGraphLayout
import com.googlecode.blaisemath.graph.IterativeGraphLayoutState
import com.googlecode.blaisemath.graph.NodeInGraph
import com.googlecode.blaisemath.graph.OptimizedGraph
import com.googlecode.blaisemath.graph.StaticGraphLayout
import com.googlecode.blaisemath.graph.SubgraphTest
import com.googlecode.blaisemath.graph.app.AnimationUtils
import com.googlecode.blaisemath.graph.app.GraphApp
import com.googlecode.blaisemath.graph.app.GraphAppCanvas
import com.googlecode.blaisemath.graph.app.GraphAppFrameView
import com.googlecode.blaisemath.graph.app.MetricScaler
import com.googlecode.blaisemath.graph.generate.AbstractGraphGenerator
import com.googlecode.blaisemath.graph.generate.CompleteGraphGenerator
import com.googlecode.blaisemath.graph.generate.CycleGraphGenerator
import com.googlecode.blaisemath.graph.generate.DefaultGeneratorParameters
import com.googlecode.blaisemath.graph.generate.DegreeDistributionGenerator
import com.googlecode.blaisemath.graph.generate.DegreeDistributionGenerator.DegreeDistributionParameters
import com.googlecode.blaisemath.graph.generate.EdgeCountGenerator
import com.googlecode.blaisemath.graph.generate.EdgeLikelihoodGenerator
import com.googlecode.blaisemath.graph.generate.EdgeLikelihoodGenerator.EdgeLikelihoodParameters
import com.googlecode.blaisemath.graph.generate.EmptyGraphGenerator
import com.googlecode.blaisemath.graph.generate.ExtendedGeneratorParameters
import com.googlecode.blaisemath.graph.generate.GraphGenerators
import com.googlecode.blaisemath.graph.generate.GraphGrowthRule
import com.googlecode.blaisemath.graph.generate.GraphSeedRule
import com.googlecode.blaisemath.graph.generate.HopGrowthRule
import com.googlecode.blaisemath.graph.generate.PreferentialAttachmentGenerator
import com.googlecode.blaisemath.graph.generate.PreferentialAttachmentGenerator.PreferentialAttachmentParameters
import com.googlecode.blaisemath.graph.generate.ProximityGraphGenerator.ProximityGraphParameters
import com.googlecode.blaisemath.graph.generate.StarGraphGenerator
import com.googlecode.blaisemath.graph.generate.WattsStrogatzGenerator
import com.googlecode.blaisemath.graph.generate.WattsStrogatzGenerator.WattsStrogatzParameters
import com.googlecode.blaisemath.graph.generate.WheelGraphGenerator
import com.googlecode.blaisemath.graph.layout.CircleLayout
import com.googlecode.blaisemath.graph.layout.CircleLayout.CircleLayoutParameters
import com.googlecode.blaisemath.graph.layout.GraphLayoutConstraints
import com.googlecode.blaisemath.graph.layout.GraphLayoutManager
import com.googlecode.blaisemath.graph.layout.IterativeGraphLayoutManager
import com.googlecode.blaisemath.graph.layout.IterativeGraphLayoutService
import com.googlecode.blaisemath.graph.layout.LayoutRegion
import com.googlecode.blaisemath.graph.layout.PositionalAddingLayout
import com.googlecode.blaisemath.graph.layout.RandomBoxLayout
import com.googlecode.blaisemath.graph.layout.RandomBoxLayout.BoxLayoutParameters
import com.googlecode.blaisemath.graph.layout.SpringLayoutParameters
import com.googlecode.blaisemath.graph.layout.SpringLayoutPerformanceTest
import com.googlecode.blaisemath.graph.layout.SpringLayoutState
import com.googlecode.blaisemath.graph.layout.StaticSpringLayout
import com.googlecode.blaisemath.graph.layout.StaticSpringLayout.StaticSpringLayoutParameters
import com.googlecode.blaisemath.graph.metrics.AbstractGraphMetric
import com.googlecode.blaisemath.graph.metrics.AbstractGraphNodeMetric
import com.googlecode.blaisemath.graph.metrics.AdditiveSubsetMetricTest
import com.googlecode.blaisemath.graph.metrics.BetweenCentrality
import com.googlecode.blaisemath.graph.metrics.BetweenCentralityTest
import com.googlecode.blaisemath.graph.metrics.ClosenessCentrality
import com.googlecode.blaisemath.graph.metrics.ClosenessCentralityTest
import com.googlecode.blaisemath.graph.metrics.ClusteringCoefficient
import com.googlecode.blaisemath.graph.metrics.CooperationMetric
import com.googlecode.blaisemath.graph.metrics.DecayCentrality
import com.googlecode.blaisemath.graph.metrics.EigenCentrality
import com.googlecode.blaisemath.graph.metrics.EigenCentralityTest
import com.googlecode.blaisemath.graph.metrics.GraphCentrality
import com.googlecode.blaisemath.graph.metrics.GraphCentralityTest
import com.googlecode.blaisemath.graph.metrics.GraphDiameter
import com.googlecode.blaisemath.graph.metrics.SubsetMetrics
import com.googlecode.blaisemath.graph.metrics.SubsetMetrics.AdditiveSubsetMetric
import com.googlecode.blaisemath.graph.metrics.SubsetMetrics.ContractiveSubsetMetric
import com.googlecode.blaisemath.graph.metrics.SubsetMetricsTest
import com.googlecode.blaisemath.graph.test.DynamicGraphTestFrame
import com.googlecode.blaisemath.graph.test.GraphTestFrame
import com.googlecode.blaisemath.graph.test.MyTestGraph
import com.googlecode.blaisemath.graph.util.Matrices
import com.googlecode.blaisemath.graph.view.GraphComponent
import com.googlecode.blaisemath.graph.view.VisualGraph
import com.googlecode.blaisemath.graph.view.WeightedEdgeStyler
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
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.junit.BeforeClass
import java.awt.Graphics2D
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.awt.geom.RectangularShape

/**
 * Draws a point along with a ray from the point to the outer edge of the graphics canvas.
 *
 * @author Elisha Peterson
 */
class MarkerRendererToClip : MarkerRenderer() {
    /** Line style for drawing the ray  */
    protected var rayRenderer: PathRenderer? = ArrowPathRenderer.Companion.getInstance()

    /** Whether to extend in both directions, or just forward  */
    protected var extendBothDirections = false
    //region BUILDER PATTERNS
    /**
     * Sets ray style and returns pointer to this object.
     * @param rayStyle the style for rays
     * @return this
     */
    fun rayStyle(rayStyle: PathRenderer?): MarkerRendererToClip? {
        setRayRenderer(rayStyle)
        return this
    }

    /**
     * Sets extension rule and returns pointer to this object.
     * @param extendBoth whether to extend line in both directions
     * @return this
     */
    fun extendBothDirections(extendBoth: Boolean): MarkerRendererToClip? {
        setExtendBothDirections(extendBoth)
        return this
    }

    //endregion
    override fun toString(): String {
        return String.format("PointStyleInfinite[rayStyle=%s, extendBoth=%s]",
                rayRenderer, extendBothDirections)
    }

    //region PROPERTIES
    fun getRayRenderer(): PathRenderer? {
        return rayRenderer
    }

    fun setRayRenderer(rayStyle: PathRenderer?) {
        rayRenderer = checkNotNull(rayStyle)
    }

    fun isExtendBothDirections(): Boolean {
        return extendBothDirections
    }

    fun setExtendBothDirections(extendBoth: Boolean) {
        extendBothDirections = extendBoth
    }

    //endregion
    override fun render(p: Point2D?, style: AttributeSet?, canvas: Graphics2D?) {
        val angle: Double = if (p is OrientedPoint2D) (p as OrientedPoint2D?).angle else 0
        val p2: Point2D = Point2D.Double(p.getX() + Math.cos(angle), p.getY() + Math.sin(angle))
        val endpoint: Point2D? = boundaryHit(p, p2, canvas.getClipBounds())
        if (extendBothDirections) {
            val endpoint1: Point2D? = boundaryHit(p2, p, canvas.getClipBounds())
            rayRenderer.render(Line2D.Double(endpoint1, endpoint), style, canvas)
        } else {
            rayRenderer.render(Line2D.Double(p, endpoint), style, canvas)
        }
        super.render(p, style, canvas)
    }

    companion object {
        /**
         * Returns points at which the ray beginning at p1 and passing through p2 intersects the boundary of the window.
         * @param p1p first point
         * @param p2p second point
         * @param bounds the window boundaries
         * @return the point on the boundary
         */
        fun boundaryHit(p1p: Point2D?, p2p: Point2D?, bounds: RectangularShape?): Point2D.Double? {
            val p1 = Point2D.Double(p1p.getX(), p1p.getY())
            val p2 = Point2D.Double(p2p.getX(), p2p.getY())
            if (p2.x > p1.x && p1.x <= bounds.getMaxX()) {
                // line goes to the right
                val slope = (p2.y - p1.y) / (p2.x - p1.x)
                val yRight = slope * (bounds.getMaxX() - p1.x) + p1.y
                if (yRight <= bounds.getMaxY() && yRight >= bounds.getMinY()) {
                    // point is on the right
                    return Point2D.Double(bounds.getMaxX(), yRight)
                } else if (p2.y > p1.y && p1.y <= bounds.getMaxY()) {
                    // line goes up
                    return Point2D.Double((bounds.getMaxY() - p1.y) / slope + p1.x, bounds.getMaxY())
                } else if (p1.y > p2.y && p1.y >= bounds.getMinY()) {
                    // line goes down
                    return Point2D.Double((bounds.getMinY() - p1.y) / slope + p1.x, bounds.getMinY())
                }
            } else if (p2.x < p1.x && p1.x >= bounds.getMinX()) {
                // line goes to the left
                val slope = (p2.y - p1.y) / (p2.x - p1.x)
                val yLeft = slope * (bounds.getMinX() - p1.x) + p1.y
                if (yLeft <= bounds.getMaxY() && yLeft >= bounds.getMinY()) {
                    // point is on the right
                    return Point2D.Double(bounds.getMinX(), yLeft)
                } else if (p2.y > p1.y && p1.y <= bounds.getMaxY()) {
                    // line goes up
                    return Point2D.Double((bounds.getMaxY() - p1.y) / slope + p1.x, bounds.getMaxY())
                } else if (p1.y > p2.y && p1.y >= bounds.getMinY()) {
                    // line goes down
                    return Point2D.Double((bounds.getMinY() - p1.y) / slope + p1.x, bounds.getMinY())
                }
            } else if (p1.x == p2.x) {
                // line is vertical
                if (p2.y < p1.y && p1.y >= bounds.getMinY()) {
                    // line goes up
                    return Point2D.Double(p1.x, bounds.getMinY())
                } else if (p1.y <= bounds.getMaxY()) {
                    return Point2D.Double(p1.x, bounds.getMaxY())
                }
            }
            return Point2D.Double(p2.x, p2.y)
        }
    }
}