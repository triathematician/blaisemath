package com.googlecode.blaisemath.style
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
 */

import com.googlecode.blaisemath.util.geom.circle2
import com.googlecode.blaisemath.util.geom.plus
import com.googlecode.blaisemath.util.geom.point2
import com.googlecode.blaisemath.util.geom.rectangle2FromCenter
import java.awt.BasicStroke
import java.awt.Shape
import java.awt.geom.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Provides several custom shapes that can be used to draw points.
 */
object Markers {

    //region STATIC INSTANCES

    val BLANK = BlankMarker
    val CIRCLE = CircleMarker
    val SQUARE = SquareMarker()
    val DIAMOND = DiamondMarker()
    val TRIANGLE = TriangleMarker()
    val STAR = StarMarker5()
    val STAR7 = StarMarker7()
    val STAR11 = StarMarker11()
    val PLUS = PlusMarker()
    val CROSS = CrossMarker()
    val TARGET = TargetMarker()
    val ARROW = ArrowMarker()
    val GAP_ARROW = GapArrowMarker()
    val THICK_ARROW = ThickArrowMarker()
    val CHEVRON_MARKER = ChevronMarker()
    val TRIANGLE_ARROW = TriangleMarkerForward()
    val ARROWHEAD = ArrowheadMarker()
    val TEARDROP = TeardropMarker()
    val HAPPYFACE = HappyFaceMarker()
    val HOUSE = HouseMarker()

    val availableMarkers = listOf(BLANK, CIRCLE, SQUARE, DIAMOND, TRIANGLE, STAR, STAR7, STAR11, PLUS, CROSS, TARGET,
            ARROW, GAP_ARROW, THICK_ARROW, CHEVRON_MARKER, TRIANGLE_ARROW, ARROWHEAD, TEARDROP, HAPPYFACE, HOUSE)

    //endregion

    //region SPECIFIC MARKERS

    object BlankMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = GeneralPath()
    }
    object CircleMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = circle2(point, radius)
    }
    class SquareMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = rectangle2FromCenter(point, radius/sqrt(2.0))
    }
    class DiamondMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = GeneralPath().apply {
            moveTo(point.x, point.y - radius)
            lineTo(point.x - radius, point.y)
            lineTo(point.x, point.y + radius)
            lineTo(point.x + radius, point.y)
            closePath()
        }
    }
    class TriangleMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = GeneralPath().apply {
            moveTo(point.x, point.y - radius)
            lineToPolar(point, 1.166667 * Math.PI, radius)
            lineToPolar(point, 1.833333 * Math.PI, radius)
            closePath()
        }
    }
    class StarMarker5 : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = starShape(5, point, orientation, radius, reducedRadius = sqrt(8.0))
    }
    class StarMarker7 : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = starShape(7, point, orientation, radius, reducedRadius = sqrt(8.0))
    }
    class StarMarker11 : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = starShape(11, point, orientation, radius, reducedRadius = 1.5)
    }

    class PlusMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = GeneralPath().apply {
            moveTo(point.x, point.y - radius)
            lineTo(point.x, point.y + radius)
            moveTo(point.x - radius, point.y)
            lineTo(point.x + radius, point.y)
        }.let {
            Area(BasicStroke(radius / 3).createStrokedShape(it))
        }
    }

    class CrossMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = GeneralPath().apply {
            val r2 = 0.7 * radius
            moveTo(point.x - r2, point.y - r2)
            lineTo(point.x + r2, point.y + r2)
            moveTo(point.x - r2, point.y + r2)
            lineTo(point.x + r2, point.y - r2)
        }.let {
            Area(BasicStroke(radius / 3).createStrokedShape(it))
        }
    }

    class TargetMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = GeneralPath().apply {
            moveTo(point.x, (point.y - radius))
            lineTo(point.x, (point.y + radius))
            moveTo((point.x - radius), point.y)
            lineTo((point.x + radius), point.y)
            append(circle2(point, .6 * radius), false)
        }.let {
            Area(BasicStroke(radius / 6).createStrokedShape(it))
        }
    }

    class GapArrowMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = GeneralPath().apply {
            moveTo(point.x + .5 * radius, point.y - .5 * radius)
            lineTo(point.x + radius, point.y)
            lineTo(point.x + .5 * radius, point.y +  .5 * radius)
            moveTo(point.x + .4 * radius, point.y)
            lineTo(point.x - radius, point.y)
        }.let {
            val wideShape = Area(BasicStroke(radius / 4).createStrokedShape(it))
            AffineTransform.getRotateInstance(orientation, point.x, point.y).createTransformedShape(wideShape)
        }
    }

    class ArrowMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = GeneralPath().apply {
            moveTo(point.x + .5 * radius, point.y - .5 * radius)
            lineTo(point.x + radius, point.y)
            lineTo(point.x + .5 * radius, point.y + .5 * radius)
            moveTo(point.x + .8 * radius, point.y)
            lineTo(point.x - radius, point.y)
        }.let {
            val wideShape = Area(BasicStroke(radius / 4).createStrokedShape(it))
            AffineTransform.getRotateInstance(orientation, point.x, point.y).createTransformedShape(wideShape)
        }
    }

    class ThickArrowMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = GeneralPath().apply {
            moveTo(point.x + .5 * radius, point.y - .5 * radius)
            lineTo(point.x + radius, point.y)
            lineTo(point.x + .5 * radius, point.y +  .5 * radius)
            moveTo(point.x + .6 * radius, point.y)
            lineTo(point.x - radius, point.y)
        }.let {
            val wideShape = Area(BasicStroke(radius / 4).createStrokedShape(it))
            AffineTransform.getRotateInstance(orientation, point.x, point.y).createTransformedShape(wideShape)
        }
    }

    class ChevronMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = GeneralPath().apply {
            moveTo(point.x + .3 * radius, point.y - .5 * radius)
            lineTo(point.x + .8 * radius, point.y)
            lineTo(point.x + .3 * radius, point.y +  .5 * radius)
            moveTo(point.x - .7 * radius, point.y - .5 * radius)
            lineTo(point.x - .2 * radius, point.y)
            lineTo(point.x - .7 * radius, point.y +  .5 * radius)
        }.let {
            val wideShape = Area(BasicStroke(radius / 4).createStrokedShape(it))
            AffineTransform.getRotateInstance(orientation, point.x, point.y).createTransformedShape(wideShape)
        }
    }

    class TriangleMarkerForward : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = GeneralPath().apply {
            moveTo(point.x + radius, point.y)
            lineToPolar(point, Math.PI * 0.666, radius)
            lineToPolar(point, Math.PI * 1.3333, radius)
            closePath()
        }.let {
            AffineTransform.getRotateInstance(orientation, point.x, point.y).createTransformedShape(it)
        }
    }

    class ArrowheadMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = GeneralPath().apply {
            moveTo(point.x + radius, point.y)
            lineTo(point.x - radius, point.y +  radius)
            lineTo(point.x - .5 * radius, point.y)
            lineTo(point.x - radius, point.y - radius)
            closePath()
        }.let {
            AffineTransform.getRotateInstance(orientation, point.x, point.y).createTransformedShape(it)
        }
    }

    class TeardropMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = GeneralPath().apply {
            moveTo(-.25f, -.5f)
            curveTo(-1f, -.5f, -1f, .5f, -.25f, .5f)
            curveTo(.5f, .5f, .5f, 0f, 1f, 0f)
            curveTo(.5f, 0f, .5f, -.5f, -.2f, -.5f)
            closePath()
            transform(AffineTransform(radius.toDouble(), 0.0, 0.0, radius.toDouble(), point.x, point.y))
        }.let {
            AffineTransform.getRotateInstance(orientation, point.x, point.y).createTransformedShape(it)
        }
    }
    
    class HappyFaceMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float): Shape {
            val a = Area(circle2(point, radius))
            a.subtract(Area(circle2(point + point2(-radius/3, -radius/3), radius / 6)))
            a.subtract(Area(circle2(point + point2(radius/3, -radius/3), radius / 6)))
            a.subtract(Area(Arc2D.Double(point.x - radius / 2, point.y - radius / 2, radius.toDouble(), radius.toDouble(), 200.0, 140.0, Arc2D.CHORD)))
            return a
        }
    }
    
    class HouseMarker : Marker {
        override fun create(point: Point2D, orientation: Double, radius: Float) = GeneralPath().apply {
            moveTo(-.9f, -.9f)
            lineTo(.9f, -.9f)
            lineTo(.9f, .4f)
            lineTo(1f, .4f)
            lineTo(.75f, .625f)
            lineTo(.75f, 1f)
            lineTo(.5f, 1f)
            lineTo(.5f, .75f)
            lineTo(0f, 1f)
            lineTo(-1f, .4f)
            lineTo(-.9f, .4f)
            lineTo(-.9f, -.9f)
            closePath()
            transform(AffineTransform(radius.toDouble(), 0.0, 0.0, -radius.toDouble(), point.x, point.y))
        }
    }

    //endregion

    //region SHAPE UTILS

    private fun GeneralPath.lineToPolar(point: Point2D, angle: Double, radius: Number) =
            lineTo(point.x + radius.toDouble() * cos(angle), point.y - radius.toDouble() * sin(angle))

    private fun starShape(n: Int, point: Point2D, orientation: Double, radius: Float, reducedRadius: Double) = GeneralPath().apply {
        moveTo(point.x, point.y - radius)
        for (i in 0 until n) {
            var theta = Math.PI / 2 + 2 * Math.PI * i / n + orientation
            lineToPolar(point, theta, radius)
            theta += Math.PI / n
            lineToPolar(point, theta, radius / reducedRadius)
        }
        closePath()
    }

    //endregion
}