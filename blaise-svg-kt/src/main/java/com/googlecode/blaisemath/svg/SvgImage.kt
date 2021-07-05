/**
 * SvgImage.java
 * Created Sep 26, 2014
 */
package com.googlecode.blaisemath.svg
/*
 * #%L
 * BlaiseGraphics
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
import com.googlecode.blaisemath.graphics.AnchoredImage
import com.googlecode.blaisemath.graphics.editor.AttributeSetPropertyModelTestFrame
import com.googlecode.blaisemath.graphics.swing.AnchorTestFrame
import com.googlecode.blaisemath.graphics.testui.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.testui.HelloWorldTest
import com.googlecode.blaisemath.graphics.testui.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.testui.SelectionTestFrame
import com.googlecode.blaisemath.graphics.testui.TooltipTestFrame
import com.googlecode.blaisemath.svg.HelloWorldSvg
import com.googlecode.blaisemath.svg.SvgPathTest
import com.googlecode.blaisemath.svg.SvgRootTest
import com.googlecode.blaisemath.svg.SvgTool
import com.googlecode.blaisemath.test.AssertUtils
import com.googlecode.blaisemath.ui.PropertyActionPanel
import com.googlecode.blaisemath.util.Images
import junit.framework.TestCase
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.junit.BeforeClass
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URL
import java.util.logging.Level
import java.util.logging.Logger
import javax.imageio.ImageIO
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement

/**
 *
 *
 * Svg-compatible image, with size parameters and a reference URL.
 *
 * @author elisha
 */
@XmlRootElement(name = "image")
class SvgImage @JvmOverloads constructor(private var x: Double = 0.0, private var y: Double = 0.0, private var width: Double? = null, private var height: Double? = null, ref: String? = "") : SvgElement("image") {
    private var imageRef: String? = null
    private var image: Image? = null

    constructor(x: Double, y: Double, ref: String?) : this(x, y, null, null, ref) {}

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    @XmlAttribute
    fun getX(): Double {
        return x
    }

    fun setX(x: Double) {
        this.x = x
    }

    @XmlAttribute
    fun getY(): Double {
        return y
    }

    fun setY(y: Double) {
        this.y = y
    }

    @XmlAttribute
    fun getWidth(): Double? {
        return width
    }

    fun setWidth(width: Double?) {
        this.width = width
    }

    @XmlAttribute
    fun getHeight(): Double? {
        return height
    }

    fun setHeight(height: Double?) {
        this.height = height
    }

    @XmlAttribute(name = "href", namespace = "http://www.w3.org/1999/xlink")
    fun getImageRef(): String? {
        return imageRef
    }

    fun setImageRef(imageRef: String?) {
        if (this.imageRef != imageRef) {
            this.imageRef = imageRef
            image = null
        }
    }

    /**
     * Load (if necessary) and return the image corresponding to the image reference.
     * @return loaded image, or null if it couldn't be loaded
     */
    @Nullable
    fun getImage(): Image? {
        if (image == null && !Strings.isNullOrEmpty(imageRef)) {
            loadImage()
        }
        return image
    }

    private fun loadImage() {
        val img: BufferedImage?
        img = try {
            if (imageRef.startsWith(Images.DATA_URI_PREFIX)) Images.decodeDataUriBase64(imageRef) else ImageIO.read(URL(imageRef))
        } catch (ex: IOException) {
            LOG.log(Level.SEVERE, "Failed to load image from $imageRef", ex)
            return
        }
        if (width == null || height == null) {
            image = img
            width = img.getWidth() as Double
            height = img.getHeight() as Double
        } else if (width == img.getWidth().toDouble() && height == img.getHeight().toDouble()) {
            image = img
        } else {
            val iw = if (width == null) img.getWidth() else width.toInt()
            val ih = if (height == null) img.getHeight() else height.toInt()
            image = img.getScaledInstance(iw, ih, Image.SCALE_SMOOTH)
        }
    }

    private class ImageConverter : Converter<SvgImage?, AnchoredImage?>() {
        protected fun doBackward(r: AnchoredImage?): SvgImage? {
            return SvgImage(r.getX(), r.getY(), r.getWidth(), r.getHeight(), r.getReference())
        }

        protected fun doForward(r: SvgImage?): AnchoredImage? {
            val bi = r.getImage()
            return if (bi == null) {
                null
            } else if (r.width == null || r.height == null) {
                AnchoredImage(r.x, r.y, bi.getWidth(null) as Double, bi.getHeight(null) as Double, bi, r.imageRef)
            } else {
                AnchoredImage(r.x, r.y, r.width, r.height, r.getImage(), r.imageRef)
            }
        }
    }

    companion object {
        private val LOG = Logger.getLogger(SvgImage::class.java.name)
        private val CONVERTER_INST: ImageConverter? = ImageConverter()

        //</editor-fold>
        fun imageConverter(): Converter<SvgImage?, AnchoredImage?>? {
            return CONVERTER_INST
        }
    }

    init {
        imageRef = ref
    }
}