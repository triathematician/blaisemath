package com.googlecode.blaisemath.util.geom

/*
 * #%L
 * BlaiseGraphTheory
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

import java.awt.geom.*
import kotlin.math.*

typealias Point2 = Point2D.Double
typealias Line2 = Line2D.Double
typealias Ellipse2 = Ellipse2D.Double
typealias Rectangle2 = Rectangle2D.Double

//region POINT XF

fun point2(x: Number, y: Number) = Point2(x.toDouble(), y.toDouble())
fun pointPolar(r: Number, theta: Number) = point2(r.toDouble() * cos(theta.toDouble()), r.toDouble() * sin(theta.toDouble()))

operator fun Point2D.plus(p: Point2D) = Point2(x + p.x, y + p.y)
operator fun Point2D.minus(p: Point2D) = Point2(x - p.x, y - p.y)

fun Point2D.format(n: Int) = String.format("(%.${n}f, %.${n}f)", x, y)

/**
 * Create and return bounding box around a given set of pounds. Returns null if there is 0 points, and a box with side length `margin`
 * around the point if there is just 1 point.
 */
fun List<Point2D>.boundingBox(inset: Double = 0.5): Rectangle2? {
    if (isEmpty()) return null

    var minx = Double.MAX_VALUE
    var miny = Double.MAX_VALUE
    var maxx = -Double.MAX_VALUE
    var maxy = -Double.MAX_VALUE
    var count = 0
    for (p in this) {
        minx = min(minx, p.x)
        miny = min(miny, p.y)
        maxx = max(maxx, p.x)
        maxy = max(maxy, p.y)
        count++
    }
    return when (count) {
        1 -> Rectangle2(minx - inset, miny - inset, 2 * inset, 2 * inset)
        else -> Rectangle2(minx - inset, miny - inset, maxx - minx + 2 * inset, maxy - miny + 2 * inset)
    }
}

/** Compute the average location of a set of points. */
fun List<Point2D>.average(): Point2D {
    require(size > 0)
    var sumx = 0.0
    var sumy = 0.0
    var count = 0
    for (p in this) {
        sumx += p.x
        sumy += p.y
        count++
    }
    return Point2(sumx / count, sumy / count)
}

//endregion

//region LINE XF

//endregion

//region ELLIPSE XF

fun circle2(center: Point2D, radius: Number) = radius.toDouble().let { Ellipse2(center.x - it, center.y - it, 2 * it, 2 * it) }
fun ellipse2FromDiagonal(corner1: Point2D, corner2: Point2D) = Ellipse2().apply { setFrameFromDiagonal(corner1, corner2) }

//endregion

//region RECTANGLE XF

fun rectangle2(x: Number, y: Number, wid: Number, ht: Number) = Rectangle2(x.toDouble(), y.toDouble(), wid.toDouble(), ht.toDouble())
fun rectangle2FromCenter(x: Number, y: Number, wid: Number, ht: Number) = rectangle2(x, y, wid, ht) - Point2(.5*wid.toDouble(), .5*ht.toDouble())
fun rectangle2FromCenter(center: Point2D, radius: Number) = radius.toDouble().let { Rectangle2(center.x - it, center.y - it, 2 * it, 2 * it) }
fun rectangle2FromDiagonal(corner1: Point2D, corner2: Point2D) = Rectangle2().apply { setFrameFromDiagonal(corner1, corner2) }

val Rectangle2D.center
    get() = Point2(centerX, centerY)

operator fun Rectangle2D.plus(p: Point2D) = Rectangle2(x + p.x, y + p.y, width, height)
operator fun Rectangle2D.minus(p: Point2D) = Rectangle2(x - p.x, y - p.y, width, height)

/** Compute rectangle that is smallest containing all rectangles in provided list, or null if the list is empty. */
fun boundingBox(list: List<Rectangle2D>): Rectangle2D? {
    if (list.isEmpty()) return null
    var res: Rectangle2D? = null
    for (r in list) res = res?.createUnion(r) ?: r
    return res
}

//endregion