package com.googlecode.blaisemath.svg
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
import com.google.common.base.Splitter
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
import com.googlecode.blaisemath.graphics.editor.AttributeSetPropertyModelTestFrame
import com.googlecode.blaisemath.graphics.svg.SvgUtils
import com.googlecode.blaisemath.graphics.swing.AnchorTestFrame
import com.googlecode.blaisemath.graphics.testui.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.testui.HelloWorldTest
import com.googlecode.blaisemath.graphics.testui.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.testui.SelectionTestFrame
import com.googlecode.blaisemath.graphics.testui.TooltipTestFrame
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.svg.HelloWorldSvg
import com.googlecode.blaisemath.svg.SvgPathTest
import com.googlecode.blaisemath.svg.SvgRootTest
import com.googlecode.blaisemath.svg.SvgTool
import com.googlecode.blaisemath.test.AssertUtils
import com.googlecode.blaisemath.ui.PropertyActionPanel
import junit.framework.TestCase
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.junit.BeforeClass
import java.awt.geom.Rectangle2D
import java.io.*
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Collectors
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlTransient

/**
 * Root element for Svg object tree.
 * @author petereb1
 */
@XmlRootElement(name = "svg")
class SvgRoot : SvgGroup() {
    private var viewBox: Rectangle2D? = null
    private var height = 100.0
    private var width = 100.0

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    @XmlAttribute
    fun getViewBox(): String? {
        return if (viewBox == null) null else String.format("%d %d %d %d", viewBox.getMinX() as Int, viewBox.getMinY() as Int,
                viewBox.getWidth() as Int, viewBox.getHeight() as Int)
    }

    fun setViewBox(viewBox: String?) {
        if (Strings.isNullOrEmpty(viewBox)) {
            return
        }
        try {
            val vals: MutableList<Double?> = Splitter.onPattern("\\s+").splitToList(viewBox).stream()
                    .map { s -> if (s.contains(".")) java.lang.Double.valueOf(s) else Integer.valueOf(s) }
                    .collect(Collectors.toList())
            this.viewBox = Rectangle2D.Double(vals[0], vals[1], vals[2], vals[3])
        } catch (x: NumberFormatException) {
            LOG.log(Level.WARNING, "Invalid view box: $viewBox", x)
        } catch (x: IndexOutOfBoundsException) {
            LOG.log(Level.WARNING, "Invalid view box: $viewBox", x)
        }
    }

    @XmlTransient
    fun getViewBoxAsRectangle(): Rectangle2D? {
        return viewBox
    }

    fun setViewBoxAsRectangle(viewBox: Rectangle2D?) {
        this.viewBox = viewBox
    }

    @XmlTransient
    fun getHeight(): Double {
        return height
    }

    fun setHeight(height: Double) {
        this.height = height
    }

    @XmlAttribute(name = "height")
    private fun getHeightString(): String? {
        return height.toString() + ""
    }

    private fun setHeightString(ht: String?) {
        setHeight(SvgUtils.parseLength(ht))
    }

    @XmlTransient
    fun getWidth(): Double {
        return width
    }

    fun setWidth(width: Double) {
        this.width = width
    }

    @XmlAttribute(name = "width")
    private fun getWidthString(): String? {
        return width.toString() + ""
    }

    private fun setWidthString(ht: String?) {
        setWidth(SvgUtils.parseLength(ht))
    }

    companion object {
        private val LOG = Logger.getLogger(SvgRoot::class.java.name)
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="STATIC UTILITIES">
        /**
         * Attempt to load an Svg root object from the given string.
         * @param input string
         * @return root object, if loaded properly
         * @throws java.io.IOException if input fails
         */
        @Throws(IOException::class)
        fun load(input: String?): SvgRoot? {
            return SvgIo.read(input)
        }

        /**
         * Attempt to load an Svg root object from the given source.
         * @param input source
         * @return root object, if loaded properly
         * @throws java.io.IOException if input fails
         */
        @Throws(IOException::class)
        fun load(input: InputStream?): SvgRoot? {
            return SvgIo.read(input)
        }

        /**
         * Attempt to load an Svg root object from the given source.
         * @param reader source
         * @return root object, if loaded properly
         * @throws java.io.IOException if input fails
         */
        @Throws(IOException::class)
        fun load(reader: Reader?): SvgRoot? {
            return SvgIo.read(reader)
        }

        /**
         * Attempt to save an Svg root object to the given source.
         * @param root object to save
         * @return Svg string
         * @throws java.io.IOException if save fails
         */
        @Throws(IOException::class)
        fun saveToString(root: SvgRoot?): String? {
            return SvgIo.writeToString(root)
        }

        /**
         * Attempt to save an Svg element to the given source, wrapping in a root
         * Svg if necessary.
         * @param el object to save
         * @return Svg string
         * @throws java.io.IOException if save fails
         */
        @Throws(IOException::class)
        fun saveToString(el: SvgElement?): String? {
            return if (el is SvgRoot) {
                saveToString(el as SvgRoot?)
            } else {
                val root = SvgRoot()
                root.addElement(el)
                saveToString(root)
            }
        }

        /**
         * Attempt to save an Svg root object to the given source.
         * @param root object to save
         * @param output where to save it
         * @throws java.io.IOException if save fails
         */
        @Throws(IOException::class)
        fun save(root: SvgRoot?, output: OutputStream?) {
            SvgIo.write(root, output)
        }

        /**
         * Attempt to save an Svg root object to the given source.
         * @param root object to save
         * @param writer where to save it
         * @throws java.io.IOException if save fails
         */
        @Throws(IOException::class)
        fun save(root: SvgRoot?, writer: Writer?) {
            SvgIo.write(root, writer)
        } //</editor-fold>
    }

    init {
        style = AttributeSet.of("font-family", "sans-serif")
    }
}