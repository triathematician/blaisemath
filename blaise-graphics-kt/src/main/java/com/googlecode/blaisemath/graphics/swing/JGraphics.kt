package com.googlecode.blaisemath.graphics.swing
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
import com.googlecode.blaisemath.graphics.AnchoredIcon
import com.googlecode.blaisemath.graphics.AnchoredImage
import com.googlecode.blaisemath.graphics.AnchoredText
import com.googlecode.blaisemath.graphics.core.DelegatingNodeLinkGraphic
import com.googlecode.blaisemath.graphics.core.DelegatingPrimitiveGraphic
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic
import com.googlecode.blaisemath.graphics.editor.AttributeSetPropertyModelTestFrame
import com.googlecode.blaisemath.graphics.svg.SvgElementGraphicConverter
import com.googlecode.blaisemath.graphics.svg.SvgGraphic
import com.googlecode.blaisemath.graphics.svg.SvgGraphicComponent
import com.googlecode.blaisemath.graphics.svg.SvgUtils
import com.googlecode.blaisemath.graphics.swing.AnchorTestFrame
import com.googlecode.blaisemath.graphics.swing.render.*
import com.googlecode.blaisemath.graphics.testui.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.testui.HelloWorldTest
import com.googlecode.blaisemath.graphics.testui.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.testui.SelectionTestFrame
import com.googlecode.blaisemath.graphics.testui.TooltipTestFrame
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.ObjectStyler
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
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.junit.BeforeClass
import java.awt.*
import java.awt.geom.Point2D
import javax.swing.Icon

/**
 * Factory methods for creating basic java2d-based graphics.
 *
 * @author Elisha Peterson
 */
object JGraphics {
    /** Default stroke of 1 unit width.  */
    val DEFAULT_STROKE: BasicStroke? = BasicStroke(1.0f)

    /** Default composite  */
    val DEFAULT_COMPOSITE: Composite? = AlphaComposite.getInstance(AlphaComposite.SRC_OVER)

    //<editor-fold defaultstate="collapsed" desc="FACTORY METHODS FOR SWING GRAPHICS">
    fun path(primitive: Shape?): PrimitiveGraphic<Shape?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, Styles.DEFAULT_PATH_STYLE.copy(), PathRenderer.Companion.getInstance())
    }

    fun path(primitive: Shape?, style: AttributeSet?): PrimitiveGraphic<Shape?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, style, PathRenderer.Companion.getInstance())
    }

    fun <S> path(source: S?, primitive: Shape?, styler: ObjectStyler<S?>?): DelegatingPrimitiveGraphic<S?, Shape?, Graphics2D?>? {
        return DelegatingPrimitiveGraphic(source, primitive, styler, PathRenderer.Companion.getInstance())
    }

    fun shape(primitive: Shape?): PrimitiveGraphic<Shape?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, Styles.DEFAULT_SHAPE_STYLE.copy(), ShapeRenderer.Companion.getInstance())
    }

    fun shape(primitive: Shape?, style: AttributeSet?): PrimitiveGraphic<Shape?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, style, ShapeRenderer.Companion.getInstance())
    }

    fun <S> shape(source: S?, primitive: Shape?, styler: ObjectStyler<S?>?): DelegatingPrimitiveGraphic<S?, Shape?, Graphics2D?>? {
        return DelegatingPrimitiveGraphic(source, primitive, styler, ShapeRenderer.Companion.getInstance())
    }

    fun point(primitive: Point2D?): PrimitiveGraphic<Point2D?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, Styles.DEFAULT_POINT_STYLE.copy(), MarkerRenderer.Companion.getInstance())
    }

    fun point(primitive: Point2D?, style: AttributeSet?): PrimitiveGraphic<Point2D?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, style, MarkerRenderer.Companion.getInstance())
    }

    fun <S> point(source: S?, primitive: Point2D?, styler: ObjectStyler<S?>?): DelegatingPrimitiveGraphic<S?, Point2D?, Graphics2D?>? {
        return DelegatingPrimitiveGraphic(source, primitive, styler, MarkerRenderer.Companion.getInstance())
    }

    fun marker(primitive: OrientedPoint2D?): PrimitiveGraphic<Point2D?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, Styles.DEFAULT_POINT_STYLE.copy(), MarkerRenderer.Companion.getInstance())
    }

    fun marker(primitive: OrientedPoint2D?, style: AttributeSet?): PrimitiveGraphic<Point2D?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, style, MarkerRenderer.Companion.getInstance())
    }

    fun <S> marker(source: S?, primitive: OrientedPoint2D?, styler: ObjectStyler<S?>?): DelegatingPrimitiveGraphic<S?, Point2D?, Graphics2D?>? {
        return DelegatingPrimitiveGraphic(source, primitive, styler, MarkerRenderer.Companion.getInstance())
    }

    fun text(primitive: AnchoredText?): PrimitiveGraphic<AnchoredText?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, Styles.DEFAULT_TEXT_STYLE.copy(), TextRenderer.Companion.getInstance())
    }

    fun text(primitive: AnchoredText?, style: AttributeSet?): PrimitiveGraphic<AnchoredText?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, style, TextRenderer.Companion.getInstance())
    }

    fun <S> text(source: S?, primitive: AnchoredText?, styler: ObjectStyler<S?>?): DelegatingPrimitiveGraphic<S?, AnchoredText?, Graphics2D?>? {
        return DelegatingPrimitiveGraphic(source, primitive, styler, TextRenderer.Companion.getInstance())
    }

    fun image(primitive: AnchoredImage?): PrimitiveGraphic<AnchoredImage?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, AttributeSet.EMPTY, ImageRenderer.Companion.getInstance())
    }

    fun image(x: Double, y: Double, wid: Double, ht: Double, image: Image?, ref: String?): PrimitiveGraphic<AnchoredImage?, Graphics2D?>? {
        return PrimitiveGraphic(AnchoredImage(x, y, wid, ht, image, ref), AttributeSet.EMPTY, ImageRenderer.Companion.getInstance())
    }

    fun icon(icon: AnchoredIcon?): PrimitiveGraphic<AnchoredIcon?, Graphics2D?>? {
        return PrimitiveGraphic(icon, AttributeSet.EMPTY, IconRenderer.Companion.getInstance())
    }

    fun icon(icon: Icon?, x: Double, y: Double): PrimitiveGraphic<AnchoredIcon?, Graphics2D?>? {
        return PrimitiveGraphic(AnchoredIcon(x, y, icon), AttributeSet.EMPTY, IconRenderer.Companion.getInstance())
    }

    fun <S> nodeLink(): DelegatingNodeLinkGraphic<S?, EndpointPair<S?>?, Graphics2D?>? {
        return DelegatingNodeLinkGraphic<S?, EndpointPair<S?>?, Graphics2D?>(MarkerRenderer.Companion.getInstance(), TextRenderer.Companion.getInstance(), PathRenderer.Companion.getInstance())
    } //endregion
}