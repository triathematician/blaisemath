package com.googlecode.blaisemath.graphics.svg
/*
 * #%L
 * BlaiseSvg
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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
import com.google.common.base.Converter
import com.google.common.base.Strings
import com.google.common.graph.ElementOrder
import com.google.common.graph.EndpointPair
import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import com.google.common.graph.ImmutableGraph
import com.google.common.graph.MutableGraph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
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
import com.googlecode.blaisemath.graphics.core.Graphic
import com.googlecode.blaisemath.graphics.core.GraphicComposite
import com.googlecode.blaisemath.graphics.core.PrimitiveArrayGraphicSupport
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphicSupport
import com.googlecode.blaisemath.graphics.editor.AttributeSetPropertyModelTestFrame
import com.googlecode.blaisemath.graphics.swing.*
import com.googlecode.blaisemath.graphics.swing.render.TextRenderer
import com.googlecode.blaisemath.graphics.swing.render.WrappedTextRenderer
import com.googlecode.blaisemath.graphics.testui.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.testui.HelloWorldTest
import com.googlecode.blaisemath.graphics.testui.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.testui.SelectionTestFrame
import com.googlecode.blaisemath.graphics.testui.TooltipTestFrame
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.AttributeSetCoder
import com.googlecode.blaisemath.style.Renderer
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.style.xml.AttributeSetAdapter
import com.googlecode.blaisemath.svg.*
import com.googlecode.blaisemath.svg.HelloWorldSvg
import com.googlecode.blaisemath.svg.SvgPathTest
import com.googlecode.blaisemath.svg.SvgRootTest
import com.googlecode.blaisemath.svg.SvgTool
import com.googlecode.blaisemath.test.AssertUtils
import com.googlecode.blaisemath.ui.PropertyActionPanel
import junit.framework.TestCase
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.junit.BeforeClass
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.awt.geom.RectangularShape
import java.util.function.Consumer
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Adapter for converting Svg objects to/from Blaise [PrimitiveGraphicSupport] objects.
 * The conversion is imperfect in this implementation. Supported Svg types include:
 *
 *  * [SvgRectangle], [SvgEllipse], [SvgCircle], [SvgPolygon]
 *  * [SvgLine], [SvgPolyline]
 *  * [SvgPath]
 *  * [SvgImage]
 *  * [SvgText]
 *  * [SvgGroup]
 *
 * This adapter also converts [SvgGroup] to/from [GraphicComposite].
 *
 * @author elisha
 */
class SvgElementGraphicConverter : Converter<SvgElement?, Graphic<Graphics2D?>?>() {
    /**
     * Convert an [SvgElement] to a [Graphic]. The resulting object will
     * be a [GraphicComposite] if the argument is an instance of [SvgGroup]
     * or [SvgRoot], and otherwise a [PrimitiveGraphicSupport]. In the case of
     * a group, recursive calls are made to convert all elements in the group.
     *
     * @param sh the element to convert
     * @return the corresponding graphic
     */
    fun doForward(sh: SvgElement?): Graphic<Graphics2D?>? {
        var prim: Graphic<Graphics2D?>? = null
        val style = aggregateStyle(sh)
        if (sh is SvgRectangle) {
            val rsh: RectangularShape = SvgRectangle.Companion.shapeConverter().convert(sh as SvgRectangle?)
            prim = JGraphics.shape(rsh, style)
        } else if (sh is SvgEllipse) {
            val rsh: Shape = SvgEllipse.Companion.shapeConverter().convert(sh as SvgEllipse?)
            prim = JGraphics.shape(rsh, style)
        } else if (sh is SvgCircle) {
            val rsh: Shape = SvgCircle.Companion.shapeConverter().convert(sh as SvgCircle?)
            prim = JGraphics.shape(rsh, style)
        } else if (sh is SvgPolygon) {
            val rsh: Shape = SvgPolygon.Companion.shapeConverter().convert(sh as SvgPolygon?)
            prim = JGraphics.shape(rsh, style)
        } else if (sh is SvgLine) {
            val line: Line2D = SvgLine.Companion.shapeConverter().convert(sh as SvgLine?)
            prim = JGraphics.path(line, style)
        } else if (sh is SvgPolyline) {
            val rsh: Shape = SvgPolyline.Companion.shapeConverter().convert(sh as SvgPolyline?)
            prim = JGraphics.shape(rsh, style)
        } else if (sh is SvgPath) {
            val rsh: Shape = SvgPath.Companion.shapeConverter().convert(sh as SvgPath?)
            prim = JGraphics.shape(rsh, style)
        } else if (sh is SvgImage) {
            val img: AnchoredImage = SvgImage.Companion.imageConverter().convert(sh as SvgImage?)
            prim = JGraphics.image(img)
            prim.setMouseDisabled(true)
        } else if (sh is SvgText) {
            val text: AnchoredText = SvgText.Companion.textConverter().convert(sh as SvgText?)
            prim = JGraphics.text(text, style)
            prim.setMouseDisabled(true)
        } else if (sh is SvgGroup) {
            prim = GraphicComposite()
            (prim as GraphicComposite<*>?).setStyle(style)
            for (el in (sh as SvgGroup?).getElements()) {
                (prim as GraphicComposite<Graphics2D?>?).addGraphic(doForward(el))
            }
        } else {
            throw IllegalStateException("Unexpected Svg element: $sh")
        }
        prim.setDefaultTooltip(sh.id)
        return prim
    }

    private fun aggregateStyle(element: SvgElement?): AttributeSet? {
        val shapeStyle = element.getStyle()
        val res = shapeStyle?.copy() ?: AttributeSet()
        val attr = element.getOtherAttributes()
        if (attr != null) {
            for ((key, value) in attr) {
                val `val`: Any = AttributeSetCoder().decode(value as String)
                res.put(key.toString(), `val`)
            }
        }
        if (element.getId() != null) {
            res.put(Styles.ID, element.getId())
        }
        AttributeSetAdapter.Companion.updateColorFields(res)
        return res
    }

    fun doBackward(v: Graphic<Graphics2D?>?): SvgElement? {
        return graphicToSvg(v)
    }

    companion object {
        private val LOG = Logger.getLogger(SvgElementGraphicConverter::class.java.name)
        private val INST: SvgElementGraphicConverter? = SvgElementGraphicConverter()

        /**
         * Get global instance of the converter.
         * @return instance
         */
        fun getInstance(): Converter<SvgElement?, Graphic<Graphics2D?>?>? {
            return INST
        }

        /**
         * Convert a graphic component to an Svg object, including a view box.
         * @param compt component to convert
         * @return result
         */
        fun componentToSvg(compt: JGraphicComponent?): SvgRoot? {
            val root = SvgRoot()
            root.width = compt.getWidth().toDouble()
            root.height = compt.getHeight().toDouble()
            root.viewBoxAsRectangle = PanAndZoomHandler.Companion.getLocalBounds(compt)
            root.style.put("background", ColorCoder().encode(compt.getBackground()))
            root.style.put(Styles.FONT_SIZE, Styles.DEFAULT_TEXT_STYLE[Styles.FONT_SIZE])
            val group = getInstance().reverse()
                    .convert(compt.getGraphicRoot()) as SvgGroup
            group.elements.forEach(Consumer { obj: SvgElement? -> root.addElement(obj) })
            return root
        }
        //<editor-fold defaultstate="collapsed" desc="PRIVATE UTILITIES">
        /** Converts a graphic element to an Svg element  */
        private fun graphicToSvg(v: Graphic<Graphics2D?>?): SvgElement? {
            var res: SvgElement? = null
            res = if (v is LabeledShapeGraphic<*>) {
                labeledShapeToSvg(v as LabeledShapeGraphic<Graphics2D?>?)
            } else if (v is PrimitiveGraphicSupport<*, *>) {
                val pgs = v as PrimitiveGraphicSupport<*, *>?
                primitiveStyleToSvg(pgs.getPrimitive(), v.getStyle(), pgs.getRenderer())
            } else if (v is GraphicComposite<*>) {
                compositeToSvg(v as GraphicComposite<Graphics2D?>?)
            } else if (v is PrimitiveArrayGraphicSupport<*, *>) {
                primitiveArrayToSvg(v as PrimitiveArrayGraphicSupport<*, *>?)
            } else {
                throw IllegalArgumentException("Graphic conversion not supported for " + v.javaClass)
            }
            val id = v.renderStyle().getString(Styles.ID, null)
            if (id != null && res != null) {
                res.id = id
            }
            if (res != null && res.style != null && res.style.attributes.isEmpty()) {
                res.style = null
            }
            if (res != null && res.style != null) {
                for (c in res.style.attributes) {
                    val col = res.style[c]
                    if (col is Color) {
                        res.style.put(c, col as Color?. alpha 255)
                    }
                }
            }
            return res
        }

        /** Converts a blaise composite to an Svg group  */
        private fun compositeToSvg(gc: GraphicComposite<Graphics2D?>?): SvgElement? {
            val grp = SvgGroup()
            if (gc.getStyle() != null) {
                grp.style = AttributeSet.create(gc.getStyle().attributeMap)
            }
            for (g in gc.getGraphics()) {
                try {
                    val el = graphicToSvg(g)
                    if (el != null) {
                        grp.addElement(el)
                    } else {
                        LOG.log(Level.WARNING, "Null graphic for {0}", g)
                    }
                } catch (x: IllegalArgumentException) {
                    LOG.log(Level.WARNING, "Graphic not added to result", x)
                }
            }
            return grp
        }

        /** Converts a blaise array graphic to Svg group  */
        private fun primitiveArrayToSvg(pags: PrimitiveArrayGraphicSupport<*, *>?): SvgElement? {
            val grp = SvgGroup()
            grp.style = pags.renderStyle().flatCopy()
            for (o in pags.getPrimitive()) {
                grp.addElement(primitiveStyleToSvg(o, pags.renderStyle().flatCopy(), pags.getRenderer()))
            }
            return grp
        }

        /** Creates an Svg element from given primitive/style  */
        private fun primitiveStyleToSvg(primitive: Any?, sty: AttributeSet?, rend: Renderer<*, *>?): SvgElement? {
            return if (primitive is Shape) {
                SvgElements.create(null, primitive as Shape?, sty)
            } else if (primitive is AnchoredText) {
                SvgElements.create(null, primitive as AnchoredText?, sty, rend)
            } else if (primitive is AnchoredImage) {
                SvgElements.create(null, primitive as AnchoredImage?, sty)
            } else if (primitive is AnchoredIcon) {
                SvgElements.create(null, primitive as AnchoredIcon?, sty)
            } else if (primitive is Point2D) {
                SvgElements.create(null, primitive as Point2D?, sty)
            } else {
                throw IllegalArgumentException("Graphic conversion not supported for primitive $primitive")
            }
        }

        /** Converts a labeled shape to svg  */
        private fun labeledShapeToSvg(gfc: LabeledShapeGraphic<Graphics2D?>?): SvgElement? {
            val shape = primitiveStyleToSvg(gfc.getPrimitive(), gfc.renderStyle().flatCopy(), gfc.getRenderer())
            val text = labelToSvg(gfc)
            return if (text == null) shape else SvgGroup.Companion.create(shape, text)
        }

        /** Generates element for object label  */
        private fun labelToSvg(gfc: LabeledShapeGraphic<Graphics2D?>?): SvgElement? {
            val styler = gfc.getObjectStyler() ?: return null
            val label = styler.label(gfc.getSourceObject())
            val style = styler.labelStyle(gfc.getSourceObject())
            if (Strings.isNullOrEmpty(label) || style == null) {
                return null
            }
            val textRend = gfc.getTextRenderer()
            return if (textRend is WrappedTextRenderer) {
                SvgElements.createWrappedText(label, style, LabeledShapeGraphic.Companion.wrappedLabelBounds(gfc.getPrimitive()))
            } else if (textRend is TextRenderer) {
                primitiveStyleToSvg(AnchoredText(label), style.flatCopy(), textRend)
            } else {
                LOG.log(Level.WARNING, "Unsupported text renderer: {0}", textRend)
                null
            }
        } //</editor-fold>
    }
}