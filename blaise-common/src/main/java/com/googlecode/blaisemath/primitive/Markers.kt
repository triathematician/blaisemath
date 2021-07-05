package com.googlecode.blaisemath.primitive

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.BasicStroke
import java.awt.Shape
import java.awt.geom.*
import java.util.*

/*-
* #%L
* blaise-common
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
*/ /**
 * Provides several custom shapes that can be used to draw points.
 *
 * @author Elisha Peterson
 */
object Markers {
    /** Caches markers loaded from resources file.  */
    private val MARKER_CACHE: MutableList<Marker?>? = Lists.newArrayList()

    /** Singleton for empty path.  */
    private val EMPTY_PATH: GeneralPath? = GeneralPath()

    //region STATIC INSTANCES
    val BLANK: BlankMarker? = BlankMarker()
    val CIRCLE: CircleMarker? = CircleMarker()
    val SQUARE: SquareMarker? = SquareMarker()
    val DIAMOND: DiamondMarker? = DiamondMarker()
    val TRIANGLE: TriangleMarker? = TriangleMarker()
    val STAR: StarMarker5? = StarMarker5()
    val STAR7: StarMarker7? = StarMarker7()
    val STAR11: StarMarker11? = StarMarker11()
    val PLUS: PlusMarker? = PlusMarker()
    val CROSS: CrossMarker? = CrossMarker()
    val TARGET: TargetMarker? = TargetMarker()
    val ARROW: ArrowMarker? = ArrowMarker()
    val GAP_ARROW: GapArrowMarker? = GapArrowMarker()
    val THICK_ARROW: ThickArrowMarker? = ThickArrowMarker()
    val CHEVRON_MARKER: ChevronMarker? = ChevronMarker()
    val TRIANGLE_ARROW: TriangleMarkerForward? = TriangleMarkerForward()
    val ARROWHEAD: ArrowheadMarker? = ArrowheadMarker()
    val TEARDROP: TeardropMarker? = TeardropMarker()
    val HAPPYFACE: HappyFaceMarker? = HappyFaceMarker()
    val HOUSE: HouseMarker? = HouseMarker()

    /**
     * Retrieve list of available shapes.
     * @return list of marker constants
     */
    fun getAvailableMarkers(): MutableList<Marker?>? {
        if (MARKER_CACHE.isEmpty()) {
            val loader = ServiceLoader.load(Marker::class.java)
            Iterables.addAll(MARKER_CACHE, loader)
        }
        return Collections.unmodifiableList(MARKER_CACHE)
    }

    /**
     * Blank marker.
     */
    class BlankMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            return EMPTY_PATH
        }
    }

    /**
     * Circle marker.
     */
    class CircleMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            return Ellipse2D.Double(p.getX() - radius, p.getY() - radius, 2 * radius, 2 * radius)
        }
    }

    /**
     * Square marker.
     */
    class SquareMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            return Rectangle2D.Double(
                    p.getX() - radius / Math.sqrt(2.0),
                    p.getY() - radius / Math.sqrt(2.0),
                    2 * radius / Math.sqrt(2.0),
                    2 * radius / Math.sqrt(2.0))
        }
    }

    /**
     * Diamond marker.
     */
    class DiamondMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val path = GeneralPath()
            path.moveTo(x as Float, (y - radius) as Float)
            path.lineTo((x - radius) as Float, y as Float)
            path.lineTo(x as Float, (y + radius) as Float)
            path.lineTo((x + radius) as Float, y as Float)
            path.closePath()
            return path
        }
    }

    /**
     * Triangle marker, pointing up.
     */
    class TriangleMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val path = GeneralPath()
            path.moveTo(x as Float, (y - radius) as Float)
            path.lineTo((x + radius * Math.cos(Math.PI * 1.16667)) as Float,
                    (y - radius * Math.sin(Math.PI * 1.16667)) as Float)
            path.lineTo((x + radius * Math.cos(Math.PI * 1.83333)) as Float,
                    (y - radius * Math.sin(Math.PI * 1.83333)) as Float)
            path.closePath()
            return path
        }
    }

    /**
     * Five point star marker.
     */
    class StarMarker5 : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val path = GeneralPath()
            path.moveTo(x as Float, (y - radius) as Float)
            for (i in 0..4) {
                var theta = Math.PI / 2 + 2 * Math.PI * i / 5
                path.lineTo((x + radius * Math.cos(theta)) as Float,
                        (y - radius * Math.sin(theta)) as Float)
                theta += Math.PI / 5
                path.lineTo((x + radius / Math.sqrt(8.0) * Math.cos(theta)) as Float,
                        (y - radius / Math.sqrt(8.0) * Math.sin(theta)) as Float)
            }
            path.closePath()
            return path
        }
    }

    /**
     * Seven point star marker.
     */
    class StarMarker7 : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val path = GeneralPath()
            path.moveTo(x as Float, (y - radius) as Float)
            for (i in 0..6) {
                var theta = Math.PI / 2 + 2 * Math.PI * i / 7
                path.lineTo((x + radius * Math.cos(theta)) as Float,
                        (y - radius * Math.sin(theta)) as Float)
                theta += Math.PI / 7
                path.lineTo((x + radius / 2 * Math.cos(theta)) as Float,
                        (y - radius / 2 * Math.sin(theta)) as Float)
            }
            path.closePath()
            return path
        }
    }

    /**
     * Eleven point star marker.
     */
    class StarMarker11 : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val path = GeneralPath()
            path.moveTo(x as Float, (y - radius) as Float)
            for (i in 0..10) {
                var theta = Math.PI / 2 + 2 * Math.PI * i / 11
                path.lineTo((x + radius * Math.cos(theta)) as Float,
                        (y - radius * Math.sin(theta)) as Float)
                theta += Math.PI / 11
                path.lineTo((x + radius / 1.5 * Math.cos(theta)) as Float,
                        (y - radius / 1.5 * Math.sin(theta)) as Float)
            }
            path.closePath()
            return path
        }
    }

    /**
     * Plus marker.
     */
    class PlusMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val path = GeneralPath()
            path.moveTo(x as Float, (y - radius) as Float)
            path.lineTo(x as Float, (y + radius) as Float)
            path.moveTo((x - radius) as Float, y as Float)
            path.lineTo((x + radius) as Float, y as Float)
            return Area(BasicStroke(radius / 3).createStrokedShape(path))
        }
    }

    /**
     * Cross marker.
     */
    class CrossMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val path = GeneralPath()
            val r2 = 0.7 * radius
            path.moveTo((x - r2) as Float, (y - r2) as Float)
            path.lineTo((x + r2) as Float, (y + r2) as Float)
            path.moveTo((x - r2) as Float, (y + r2) as Float)
            path.lineTo((x + r2) as Float, (y - r2) as Float)
            return Area(BasicStroke(radius / 3).createStrokedShape(path))
        }
    }

    /**
     * Target marker (with circle and crosshairs).
     */
    class TargetMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val path = GeneralPath()
            path.moveTo(x as Float, (y - radius) as Float)
            path.lineTo(x as Float, (y + radius) as Float)
            path.moveTo((x - radius) as Float, y as Float)
            path.lineTo((x + radius) as Float, y as Float)
            path.append(Ellipse2D.Double(x - .6 * radius, y - .6 * radius, 1.2 * radius, 1.2 * radius), false)
            return Area(BasicStroke(radius / 6).createStrokedShape(path))
        }
    }

    /**
     * Arrow marker, pointing forward.
     */
    class GapArrowMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val path = GeneralPath()
            path.moveTo((x + .5 * radius) as Float, (y - .5 * radius) as Float)
            path.lineTo((x + radius) as Float, y as Float)
            path.lineTo((x + .5 * radius) as Float, (y + .5 * radius) as Float)
            path.moveTo((x + .4 * radius) as Float, y as Float)
            path.lineTo((x - radius) as Float, y as Float)
            val wideShape: Shape = Area(BasicStroke(radius / 4).createStrokedShape(path))
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(wideShape)
        }
    }

    /**
     * Arrow marker, pointing forward.
     */
    class ArrowMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val path = GeneralPath()
            path.moveTo((x + .5 * radius) as Float, (y - .5 * radius) as Float)
            path.lineTo((x + radius) as Float, y as Float)
            path.lineTo((x + .5 * radius) as Float, (y + .5 * radius) as Float)
            path.moveTo((x + .8 * radius) as Float, y as Float)
            path.lineTo((x - radius) as Float, y as Float)
            val wideShape: Shape = Area(BasicStroke(radius / 4).createStrokedShape(path))
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(wideShape)
        }
    }

    /**
     * Thicker arrow marker, pointing forward.
     */
    class ThickArrowMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val path = GeneralPath()
            path.moveTo((x + .5 * radius) as Float, (y - .5 * radius) as Float)
            path.lineTo((x + radius) as Float, y as Float)
            path.lineTo((x + .5 * radius) as Float, (y + .5 * radius) as Float)
            path.moveTo((x + .6 * radius) as Float, y as Float)
            path.lineTo((x - radius) as Float, y as Float)
            val wideShape: Shape = Area(BasicStroke(radius / 2).createStrokedShape(path))
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(wideShape)
        }
    }

    /**
     * Chevron marker, pointing forward.
     */
    class ChevronMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val path = GeneralPath()
            path.moveTo((x + .3 * radius) as Float, (y - .5 * radius) as Float)
            path.lineTo((x + .8 * radius) as Float, y as Float)
            path.lineTo((x + .3 * radius) as Float, (y + .5 * radius) as Float)
            path.moveTo((x - .7 * radius) as Float, (y - .5 * radius) as Float)
            path.lineTo((x - .2 * radius) as Float, y as Float)
            path.lineTo((x - .7 * radius) as Float, (y + .5 * radius) as Float)
            val wideShape: Shape = Area(BasicStroke(radius / 4).createStrokedShape(path))
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(wideShape)
        }
    }

    /**
     * Triangle marker, pointing forward.
     */
    class TriangleMarkerForward : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val path = GeneralPath()
            path.moveTo((x + radius) as Float, y as Float)
            path.lineTo((x + radius * Math.cos(Math.PI * 0.6667)) as Float,
                    (y - radius * Math.sin(Math.PI * 0.6667)) as Float)
            path.lineTo((x + radius * Math.cos(Math.PI * 1.3333)) as Float,
                    (y - radius * Math.sin(Math.PI * 1.3333)) as Float)
            path.closePath()
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(path)
        }
    }

    /**
     * Arrowhead marker, pointing forward.
     */
    class ArrowheadMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val gp10 = GeneralPath()
            gp10.moveTo((x + radius) as Float, y as Float)
            gp10.lineTo((x - radius) as Float, (y + radius) as Float)
            gp10.lineTo((x - .5 * radius) as Float, y as Float)
            gp10.lineTo((x - radius) as Float, (y - radius) as Float)
            gp10.closePath()
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(gp10)
        }
    }

    /**
     * Teardrop marker, pointing forward.
     */
    class TeardropMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val gp11 = GeneralPath()
            gp11.moveTo(-.25f, -.5f)
            gp11.curveTo(-1f, -.5f, -1f, .5f, -.25f, .5f)
            gp11.curveTo(.5f, .5f, .5f, 0f, 1f, 0f)
            gp11.curveTo(.5f, 0f, .5f, -.5f, -.2f, -.5f)
            gp11.closePath()
            gp11.transform(AffineTransform(radius, 0, 0, radius, x, y))
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(gp11)
        }
    }

    /**
     * Happy face marker.
     */
    class HappyFaceMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val a = Area(Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius))
            a.subtract(Area(Ellipse2D.Double(x - radius / 3 - radius / 6, y - radius / 2, radius / 3, radius / 3)))
            a.subtract(Area(Ellipse2D.Double(x + radius / 3 - radius / 6, y - radius / 2, radius / 3, radius / 3)))
            a.subtract(Area(Arc2D.Double(x - radius / 2, y - radius / 2, radius, radius, 200, 140, Arc2D.CHORD)))
            return a
        }
    }

    /**
     * House-shaped marker.
     */
    class HouseMarker : Marker {
        override fun create(p: Point2D?, angle: Double, radius: Float): Shape? {
            val x = p.getX()
            val y = p.getY()
            val gp13 = GeneralPath()
            gp13.moveTo(-.9f, -.9f)
            gp13.lineTo(.9f, -.9f)
            gp13.lineTo(.9f, .4f)
            gp13.lineTo(1f, .4f)
            gp13.lineTo(.75f, .625f)
            gp13.lineTo(.75f, 1f)
            gp13.lineTo(.5f, 1f)
            gp13.lineTo(.5f, .75f)
            gp13.lineTo(0f, 1f)
            gp13.lineTo(-1f, .4f)
            gp13.lineTo(-.9f, .4f)
            gp13.lineTo(-.9f, -.9f)
            gp13.closePath()
            gp13.transform(AffineTransform(radius, 0, 0, -radius, x, y))
            return gp13
        }
    }
}