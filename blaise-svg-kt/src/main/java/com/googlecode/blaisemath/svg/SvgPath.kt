/**
 * SvgPath.java
 * Created Dec 9, 2012
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
import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Strings
import com.google.common.collect.Lists
import com.google.common.graph.ElementOrder
import com.google.common.graph.EndpointPair
import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import com.google.common.graph.ImmutableGraph
import com.google.common.graph.MutableGraph
import com.google.common.primitives.Floats
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
import junit.framework.TestCase
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.junit.BeforeClass
import java.awt.geom.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement

/**
 *
 *
 * Svg path object.
 *
 * @author elisha
 */
@XmlRootElement(name = "path")
class SvgPath : SvgElement {
    private var pathStr: String? = null

    constructor() : super("path") {}
    constructor(pathStr: String?) : super("path") {
        this.pathStr = checkSvgPathStr(pathStr)
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    @XmlAttribute(name = "d")
    fun getPathStr(): String? {
        return pathStr
    }

    fun setPathStr(pathStr: String?) {
        this.pathStr = checkSvgPathStr(pathStr)
    }

    private class PathConverter : Converter<SvgPath?, Path2D?>() {
        protected fun doForward(path: SvgPath?): Path2D? {
            return toPath(path.pathStr)
        }

        protected fun doBackward(b: Path2D?): SvgPath? {
            return create(b.getPathIterator(null))
        }
    }

    private enum class SvgPathOperator(private val cmd: Char) {
        MOVE('m') {
            override fun apply(gp: GeneralPath?, coords: FloatArray?, start: FloatArray?, loc: FloatArray?, nextAnchor: FloatArray?, relative: Boolean) {
                var i = 0
                while (i < coords.size - 1) {
                    loc.get(0) = if (relative) loc.get(0) + coords.get(i) else coords.get(i)
                    loc.get(1) = if (relative) loc.get(1) + coords.get(i + 1) else coords.get(i + 1)
                    if (i == 0) {
                        gp.moveTo(loc.get(0), loc.get(1))
                    } else {
                        gp.lineTo(loc.get(0), loc.get(1))
                    }
                    i += 2
                }
                nextAnchor.get(0) = loc.get(0)
                nextAnchor.get(1) = loc.get(1)
                start.get(0) = loc.get(0)
                start.get(1) = loc.get(1)
            }
        },
        LINE('l') {
            override fun apply(gp: GeneralPath?, coords: FloatArray?, start: FloatArray?, loc: FloatArray?, nextAnchor: FloatArray?, relative: Boolean) {
                var i = 0
                while (i < coords.size - 1) {
                    loc.get(0) = if (relative) loc.get(0) + coords.get(i) else coords.get(i)
                    loc.get(1) = if (relative) loc.get(1) + coords.get(i + 1) else coords.get(i + 1)
                    gp.lineTo(loc.get(0), loc.get(1))
                    i += 2
                }
                nextAnchor.get(0) = loc.get(0)
                nextAnchor.get(1) = loc.get(1)
            }
        },
        HLINE('h') {
            override fun apply(gp: GeneralPath?, coords: FloatArray?, start: FloatArray?, loc: FloatArray?, nextAnchor: FloatArray?, relative: Boolean) {
                for (i in coords.indices) {
                    loc.get(0) = if (relative) loc.get(0) + coords.get(i) else coords.get(i)
                    gp.lineTo(loc.get(0), loc.get(1))
                }
                nextAnchor.get(0) = loc.get(0)
                nextAnchor.get(1) = loc.get(1)
            }
        },
        VLINE('v') {
            override fun apply(gp: GeneralPath?, coords: FloatArray?, start: FloatArray?, loc: FloatArray?, nextAnchor: FloatArray?, relative: Boolean) {
                for (i in coords.indices) {
                    loc.get(1) = if (relative) loc.get(1) + coords.get(i) else coords.get(i)
                    gp.lineTo(loc.get(0), loc.get(1))
                }
                nextAnchor.get(0) = loc.get(0)
                nextAnchor.get(1) = loc.get(1)
            }
        },
        CUBIC_CURVE('c') {
            override fun apply(gp: GeneralPath?, coords: FloatArray?, start: FloatArray?, loc: FloatArray?, nextAnchor: FloatArray?, relative: Boolean) {
                var i = 0
                while (i < coords.size - 5) {
                    val x1 = if (relative) loc.get(0) + coords.get(i) else coords.get(i)
                    val y1 = if (relative) loc.get(1) + coords.get(i + 1) else coords.get(i + 1)
                    val x2 = if (relative) loc.get(0) + coords.get(i + 2) else coords.get(i + 2)
                    val y2 = if (relative) loc.get(1) + coords.get(i + 3) else coords.get(i + 3)
                    loc.get(0) = if (relative) loc.get(0) + coords.get(i + 4) else coords.get(i + 4)
                    loc.get(1) = if (relative) loc.get(1) + coords.get(i + 5) else coords.get(i + 5)
                    gp.curveTo(x1, y1, x2, y2, loc.get(0), loc.get(1))
                    nextAnchor.get(0) = 2 * loc.get(0) - x2
                    nextAnchor.get(1) = 2 * loc.get(1) - y2
                    i += 6
                }
            }
        },
        SMOOTH_CURVE('s') {
            override fun apply(gp: GeneralPath?, coords: FloatArray?, start: FloatArray?, loc: FloatArray?, nextAnchor: FloatArray?, relative: Boolean) {
                var i = 0
                while (i < coords.size - 3) {
                    val x1 = nextAnchor.get(0)
                    val y1 = nextAnchor.get(1)
                    val x2 = if (relative) loc.get(0) + coords.get(i) else coords.get(i)
                    val y2 = if (relative) loc.get(1) + coords.get(i + 1) else coords.get(i + 1)
                    loc.get(0) = if (relative) loc.get(0) + coords.get(i + 2) else coords.get(i + 2)
                    loc.get(1) = if (relative) loc.get(1) + coords.get(i + 3) else coords.get(i + 3)
                    gp.curveTo(x1, y1, x2, y2, loc.get(0), loc.get(1))
                    nextAnchor.get(0) = 2 * loc.get(0) - x2
                    nextAnchor.get(1) = 2 * loc.get(1) - y2
                    i += 4
                }
            }
        },
        QUAD_CURVE('q') {
            override fun apply(gp: GeneralPath?, coords: FloatArray?, start: FloatArray?, loc: FloatArray?, nextAnchor: FloatArray?, relative: Boolean) {
                var i = 0
                while (i < coords.size - 3) {
                    val x1 = if (relative) loc.get(0) + coords.get(i) else coords.get(i)
                    val y1 = if (relative) loc.get(1) + coords.get(i + 1) else coords.get(i + 1)
                    loc.get(0) = if (relative) loc.get(0) + coords.get(i + 2) else coords.get(i + 2)
                    loc.get(1) = if (relative) loc.get(1) + coords.get(i + 3) else coords.get(i + 3)
                    gp.quadTo(x1, y1, loc.get(0), loc.get(1))
                    nextAnchor.get(0) = 2 * loc.get(0) - x1
                    nextAnchor.get(1) = 2 * loc.get(1) - y1
                    i += 4
                }
            }
        },
        SMOOTH_QUAD_CURVE('t') {
            override fun apply(gp: GeneralPath?, coords: FloatArray?, start: FloatArray?, loc: FloatArray?, nextAnchor: FloatArray?, relative: Boolean) {
                var i = 0
                while (i < coords.size - 1) {
                    val x1 = nextAnchor.get(0)
                    val y1 = nextAnchor.get(1)
                    loc.get(0) = if (relative) loc.get(0) + coords.get(i) else coords.get(i)
                    loc.get(1) = if (relative) loc.get(1) + coords.get(i + 1) else coords.get(i + 1)
                    gp.quadTo(x1, y1, loc.get(0), loc.get(1))
                    nextAnchor.get(0) = 2 * loc.get(0) - x1
                    nextAnchor.get(1) = 2 * loc.get(1) - y1
                    i += 2
                }
            }
        },
        ARC('a') {
            override fun apply(gp: GeneralPath?, coords: FloatArray?, start: FloatArray?, loc: FloatArray?, nextAnchor: FloatArray?, relative: Boolean) {
                var i = 0
                while (i < coords.size - 6) {
                    val rx = coords.get(i)
                    val ry = coords.get(i + 1)
                    val xAxisRotation = coords.get(i + 2)
                    val largeArcFlag = coords.get(i + 3) == 1f
                    val sweepFlag = coords.get(i + 4) == 1f
                    loc.get(0) = if (relative) loc.get(0) + coords.get(i + 5) else coords.get(i + 5)
                    loc.get(1) = if (relative) loc.get(1) + coords.get(i + 6) else coords.get(i + 6)
                    arcTo(gp, rx, ry, xAxisRotation, largeArcFlag, sweepFlag, loc.get(0), loc.get(1))
                    nextAnchor.get(0) = loc.get(0)
                    nextAnchor.get(1) = loc.get(1)
                    i += 7
                }
            }
        },
        CLOSE_PATH('z') {
            override fun apply(gp: GeneralPath?, coords: FloatArray?, start: FloatArray?, loc: FloatArray?, nextAnchor: FloatArray?, relative: Boolean) {
                gp.closePath()
                loc.get(0) = start.get(0)
                loc.get(1) = start.get(1)
                nextAnchor.get(0) = start.get(0)
                nextAnchor.get(1) = start.get(1)
            }
        };

        /**
         * Apply the Svg command, adding the results onto the path.
         * @param gp path to add results onto
         * @param coords coordinates associated with the current command
         * @param start starting location for current subpath (modified by move commands)
         * @param loc current location (modified by most commands)
         * @param nextAnchor next anchor location (modified for curve commands)
         * @param relative whether coordinates are relative (true) or absolute (false)
         */
        abstract fun apply(gp: GeneralPath?, coords: FloatArray?, start: FloatArray?, loc: FloatArray?, nextAnchor: FloatArray?, relative: Boolean)
    }

    companion object {
        private val LOG = Logger.getLogger(SvgPath::class.java.name)
        private val CONVERTER_INST: PathConverter? = PathConverter()

        /**
         * Create an `SvgPath` from a `PathIterator` object.
         * @param pi path iterator
         * @return svg path
         */
        fun create(pi: PathIterator?): SvgPath? {
            val cur = FloatArray(6)
            var curSegmentType = -1
            val pathString = StringBuilder()
            while (!pi.isDone()) {
                curSegmentType = pi.currentSegment(cur)
                when (curSegmentType) {
                    PathIterator.SEG_MOVETO -> pathString.append("M ").append(numStr(" ", 6, cur[0], cur[1])).append(" ")
                    PathIterator.SEG_LINETO -> pathString.append("L ").append(numStr(" ", 6, cur[0], cur[1])).append(" ")
                    PathIterator.SEG_QUADTO -> pathString.append("Q ").append(numStr(" ", 6, cur[0], cur[1], cur[2], cur[3])).append(" ")
                    PathIterator.SEG_CUBICTO -> pathString.append("C ").append(numStr(" ", 6, cur[0], cur[1], cur[2], cur[3], cur[4], cur[5])).append(" ")
                    PathIterator.SEG_CLOSE -> pathString.append("Z")
                    else -> {
                    }
                }
                pi.next()
            }
            return SvgPath(pathString.toString().trim { it <= ' ' })
        }

        //</editor-fold>
        fun shapeConverter(): Converter<SvgPath?, Path2D?>? {
            return CONVERTER_INST
        }
        //<editor-fold defaultstate="collapsed" desc="STATIC UTILITIES">
        /** Prints numbers w/ up to n digits of precision, removing trailing zeros  */
        fun numStr(prec: Int, `val`: Double): String? {
            val res = String.format("%." + prec + "f", `val`)
            return if (res.indexOf('.') < 0) res else res.replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
        }

        /** Prints a sequence of numbers with the specified joiner and precision  */
        fun numStr(join: String?, prec: Int, vararg vals: Double): String? {
            if (vals.size == 0) {
                return ""
            }
            val res = StringBuilder()
            res.append(numStr(prec, vals[0]))
            for (i in 1 until vals.size) {
                res.append(join).append(numStr(prec, vals[i]))
            }
            return res.toString()
        }

        /** Checks that the given string is a valid Svg path string.  */
        fun checkSvgPathStr(svg: String?): String? {
            toPath(svg)
            return svg
        }

        /** Converts Svg path string to a Java path  */
        fun toPath(svg: String?): GeneralPath? {
            if (Strings.isNullOrEmpty(svg)) {
                return GeneralPath()
            }
            val tokens = tokenizeSvgPath(svg)
            val start = floatArrayOf(0f, 0f)
            val loc = floatArrayOf(0f, 0f)
            val nextAnchor = floatArrayOf(0f, 0f)
            val gp = GeneralPath()
            var pos = 0
            while (pos < tokens.size) {
                val op = operatorOf(tokens.get(pos).toLowerCase())
                val coords = nextFloats(tokens, pos + 1)
                val relative = Character.isLowerCase(tokens.get(pos).get(0))
                op.apply(gp, coords, start, loc, nextAnchor, relative)
                pos += coords.size + 1
            }
            return gp
        }

        /** Tokenize the path string, first making sure we have spaces in front of all numbers  */
        private fun tokenizeSvgPath(path: String?): Array<String?>? {
            return path.replace("[A-Za-z]".toRegex(), " $0 ").trim { it <= ' ' }
                    .replace("[\\-]".toRegex(), " -")
                    .split("[\\s,]+".toRegex()).toTypedArray()
        }

        /** Get operator for given token  */
        private fun operatorOf(token: String?): SvgPathOperator? {
            checkArgument(token.length == 1, "Invalid operator: $token")
            for (op in SvgPathOperator.values()) {
                if (op.cmd == token.get(0)) {
                    return op
                }
            }
            checkArgument(false, "Invalid operator: $token")
            return null
        }

        /**
         * Convert strings to floats, starting at given index, stopping at first non-float value
         * @param tokens array of strings
         * @param start starting index
         * @return coordinates extracted from string
         */
        private fun nextFloats(tokens: Array<String?>?, start: Int): FloatArray? {
            val vals: MutableList<Float?> = Lists.newArrayList()
            for (i in start until tokens.size) {
                try {
                    vals.add(java.lang.Float.valueOf(tokens.get(i)))
                } catch (ex: NumberFormatException) {
                    LOG.log(Level.FINEST, "Not a float: " + tokens.get(i), ex)
                    break
                }
            }
            return Floats.toArray(vals)
        }

        /** See http://stackoverflow.com/questions/1805101/svg-elliptical-arcs-with-java.  */
        private fun arcTo(path: GeneralPath?, rx: Float, ry: Float, theta: Float, largeArcFlag: Boolean, sweepFlag: Boolean, x: Float, y: Float) {
            // Ensure radii are valid
            var rx = rx
            var ry = ry
            var theta = theta
            if (rx == 0f || ry == 0f) {
                path.lineTo(x, y)
                return
            }
            // Get the current (x, y) coordinates of the path
            val p2d = path.getCurrentPoint()
            val x0 = p2d.x as Float
            val y0 = p2d.y as Float
            // Compute the half distance between the current and the final point
            val dx2 = (x0 - x) / 2.0f
            val dy2 = (y0 - y) / 2.0f
            // Convert theta from degrees to radians
            theta = Math.toRadians(theta % 360f.toDouble()) as Float

            //
            // Step 1 : Compute (x1, y1)
            //
            val x1 = (Math.cos(theta.toDouble()) * dx2 as Double + Math.sin(theta.toDouble())
                    * dy2 as Double) as Float
            val y1 = (-Math.sin(theta.toDouble()) * dx2 as Double + Math.cos(theta.toDouble())
                    * dy2 as Double) as Float
            // Ensure radii are large enough
            rx = Math.abs(rx)
            ry = Math.abs(ry)
            var prx = rx * rx
            var pry = ry * ry
            val px1 = x1 * x1
            val py1 = y1 * y1
            val d = px1 / prx + py1 / pry.toDouble()
            if (d > 1) {
                rx = Math.abs((Math.sqrt(d) * rx as Double) as Float)
                ry = Math.abs((Math.sqrt(d) * ry as Double) as Float)
                prx = rx * rx
                pry = ry * ry
            }

            //
            // Step 2 : Compute (cx1, cy1)
            //
            var sign = if (largeArcFlag == sweepFlag) -1.0 else 1.0
            val coef = (sign * Math.sqrt(((prx * pry - prx * py1 - pry * px1)
                    / (prx * py1 + pry * px1)).toDouble())) as Float
            val cx1 = coef * (rx * y1 / ry)
            val cy1 = coef * -(ry * x1 / rx)

            //
            // Step 3 : Compute (cx, cy) from (cx1, cy1)
            //
            val sx2 = (x0 + x) / 2.0f
            val sy2 = (y0 + y) / 2.0f
            val cx = sx2 + (Math.cos(theta.toDouble()) * cx1 as Double - Math.sin(theta.toDouble()) * cy1 as Double) as Float
            val cy = sy2 + (Math.sin(theta.toDouble()) * cx1 as Double + Math.cos(theta.toDouble()) * cy1 as Double) as Float

            //
            // Step 4 : Compute the angleStart (theta1) and the angleExtent (dtheta)
            //
            val ux = (x1 - cx1) / rx
            val uy = (y1 - cy1) / ry
            val vx = (-x1 - cx1) / rx
            val vy = (-y1 - cy1) / ry
            var p: Float
            var n: Float
            // Compute the angle start
            n = Math.sqrt(ux * ux + uy * uy) as Float
            p = ux // (1 * ux) + (0 * uy)
            sign = if (uy < 0) -1.0 else 1.0
            var angleStart = Math.toDegrees(sign * Math.acos(p / n.toDouble())) as Float
            // Compute the angle extent
            n = Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy)) as Float
            p = ux * vx + uy * vy
            sign = if (ux * vy - uy * vx < 0) -1.0 else 1.0
            var angleExtent = Math.toDegrees(sign * Math.acos(p / n.toDouble())) as Float
            if (!sweepFlag && angleExtent > 0) {
                angleExtent -= 360f
            } else if (sweepFlag && angleExtent < 0) {
                angleExtent += 360f
            }
            angleExtent %= 360f
            angleStart %= 360f
            val arc = Arc2D.Float()
            arc.x = cx - rx
            arc.y = cy - ry
            arc.width = rx * 2.0f
            arc.height = ry * 2.0f
            arc.start = -angleStart
            arc.extent = -angleExtent
            val rotation = AffineTransform.getRotateInstance(theta.toDouble(), cx.toDouble(), cy.toDouble())
            path.append(rotation.createTransformedShape(arc), true)
        }
    }
}