package com.googlecode.blaisemath.graph.layout

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
import java.util.*

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
 * Parameters of the SpringLayout algorithm.
 */
class SpringLayoutParameters {
    /** Desired distance between nodes  */
    var distScale = DEFAULT_DIST_SCALE.toDouble()

    /** Global attractive constant (keeps nodes closer to origin)  */
    var globalC = 1.0

    /** Attractive constant  */
    var springC = .1

    /** Natural spring length  */
    var springL = .5 * distScale

    /** Repelling constant  */
    var repulsiveC = distScale * distScale

    /** Damping constant, reducing node speeds  */
    var dampingC = 0.7

    /** Time step per iteration  */
    var stepT = 1.0

    /** The maximum speed (movement per unit time)  */
    var maxSpeed = 10 * distScale

    /** Distance outside which global force acts  */
    var minGlobalForceDist = distScale

    /** Maximum force that can be applied between nodes  */
    var maxForce = distScale * distScale / 100

    /** Min distance between nodes  */
    var minDist = distScale / 100

    /** Max distance to apply repulsive force  */
    var maxRepelDist = 2 * distScale

    /** Layout constraints  */
    var constraints: GraphLayoutConstraints<*>? = GraphLayoutConstraints<Any?>()

    //region PROPERTIES
    fun getDistScale(): Double {
        return distScale
    }

    fun setDistScale(distScale: Double) {
        this.distScale = distScale
        springL = .5 * distScale
        repulsiveC = distScale * distScale
        maxSpeed = 10 * distScale
        minGlobalForceDist = distScale
        maxForce = distScale * distScale / 100
        minDist = distScale / 100
        maxRepelDist = 2 * distScale
    }

    fun getDampingConstant(): Double {
        return dampingC
    }

    fun setDampingConstant(dampingC: Double) {
        this.dampingC = dampingC
    }

    fun getGlobalForce(): Double {
        return globalC
    }

    fun setGlobalForce(globalC: Double) {
        this.globalC = globalC
    }

    fun getMaxSpeed(): Double {
        return maxSpeed
    }

    fun setMaxSpeed(maxSpeed: Double) {
        this.maxSpeed = maxSpeed
    }

    fun getRepulsiveForce(): Double {
        return repulsiveC
    }

    fun setRepulsiveForce(repulsiveC: Double) {
        this.repulsiveC = repulsiveC
    }

    fun getSpringForce(): Double {
        return springC
    }

    fun setSpringForce(springC: Double) {
        this.springC = springC
    }

    fun getSpringLength(): Double {
        return springL
    }

    fun setSpringLength(springL: Double) {
        this.springL = springL
    }

    fun getStepTime(): Double {
        return stepT
    }

    fun setStepTime(stepT: Double) {
        this.stepT = stepT
    }

    fun getConstraints(): GraphLayoutConstraints<*>? {
        return constraints
    }

    fun setConstraints(constraints: GraphLayoutConstraints<*>?) {
        this.constraints = Objects.requireNonNull(constraints)
    } //endregion

    companion object {
        /** Default distance scale  */
        const val DEFAULT_DIST_SCALE = 50
    }
}