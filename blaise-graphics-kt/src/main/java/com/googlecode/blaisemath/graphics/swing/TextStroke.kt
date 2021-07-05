package com.googlecode.blaisemath.graphics.swing

import java.awt.Font
import java.awt.Shape
import java.awt.Stroke
import java.awt.font.FontRenderContext
import java.awt.geom.AffineTransform
import java.awt.geom.FlatteningPathIterator
import java.awt.geom.GeneralPath
import java.awt.geom.PathIterator

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

/**
 * Stroke that renders text along a path.
 * @author Elisha Peterson
 */
internal class TextStroke @JvmOverloads constructor(private val text: String, private val font: Font, private val stretchToFit: Boolean = true, private val repeat: Boolean = false) : Stroke {

    private val flatness = 1.0

    private val transform = AffineTransform()

    override fun createStrokedShape(shape: Shape): Shape {
        val frc = FontRenderContext(null, true, true)
        val glyphVector = font.createGlyphVector(frc, text)
        val result = GeneralPath()
        val it = FlatteningPathIterator(shape.getPathIterator(null), flatness)
        val points = FloatArray(6)
        var moveX = 0f
        var moveY = 0f
        var lastX = 0f
        var lastY = 0f
        var thisX: Float
        var thisY: Float
        var type: Int
        var next = 0f
        var currentChar = 0
        val length = glyphVector.numGlyphs
        if (length == 0) {
            return result
        }
        val factor = if (stretchToFit) measurePathLength(shape) / glyphVector.logicalBounds.width.toFloat() else 1.0f
        var nextAdvance = 0f
        while (currentChar < length && !it.isDone) {
            type = it.currentSegment(points)
            when (type) {
                PathIterator.SEG_MOVETO -> {
                    run {
                        lastX = points[0]
                        moveX = lastX
                    }
                    run {
                        lastY = points[1]
                        moveY = lastY
                    }
                    result.moveTo(moveX, moveY)
                    nextAdvance = glyphVector.getGlyphMetrics(currentChar).advance * 0.5f
                    next = nextAdvance
                }
                PathIterator.SEG_CLOSE -> {
                    points[0] = moveX
                    points[1] = moveY
                    thisX = points[0]
                    thisY = points[1]
                    val dx = thisX - lastX
                    val dy = thisY - lastY
                    val distance = Math.sqrt(dx * dx + dy * dy.toDouble()).toFloat()
                    if (distance >= next) {
                        val r = 1.0f / distance
                        val angle = Math.atan2(dy.toDouble(), dx.toDouble()).toFloat()
                        while (currentChar < length && distance >= next) {
                            val glyph = glyphVector.getGlyphOutline(currentChar)
                            val p = glyphVector.getGlyphPosition(currentChar)
                            val px = p.x.toFloat()
                            val py = p.y.toFloat()
                            val x = lastX + next * dx * r
                            val y = lastY + next * dy * r
                            val advance = nextAdvance
                            nextAdvance = if (currentChar < length - 1) glyphVector.getGlyphMetrics(currentChar + 1).advance * 0.5f else 0f
                            transform.setToTranslation(x.toDouble(), y.toDouble())
                            transform.rotate(angle.toDouble())
                            transform.translate(-px - advance.toDouble(), -py.toDouble())
                            result.append(transform.createTransformedShape(glyph), false)
                            next += (advance + nextAdvance) * factor
                            currentChar++
                            if (repeat) {
                                currentChar %= length
                            }
                        }
                    }
                    next -= distance
                    lastX = thisX
                    lastY = thisY
                }
                PathIterator.SEG_LINETO -> {
                    thisX = points[0]
                    thisY = points[1]
                    val dx = thisX - lastX
                    val dy = thisY - lastY
                    val distance = Math.sqrt(dx * dx + dy * dy.toDouble()).toFloat()
                    if (distance >= next) {
                        val r = 1.0f / distance
                        val angle = Math.atan2(dy.toDouble(), dx.toDouble()).toFloat()
                        while (currentChar < length && distance >= next) {
                            val glyph = glyphVector.getGlyphOutline(currentChar)
                            val p = glyphVector.getGlyphPosition(currentChar)
                            val px = p.x.toFloat()
                            val py = p.y.toFloat()
                            val x = lastX + next * dx * r
                            val y = lastY + next * dy * r
                            val advance = nextAdvance
                            nextAdvance = if (currentChar < length - 1) glyphVector.getGlyphMetrics(currentChar + 1).advance * 0.5f else 0f
                            transform.setToTranslation(x.toDouble(), y.toDouble())
                            transform.rotate(angle.toDouble())
                            transform.translate(-px - advance.toDouble(), -py.toDouble())
                            result.append(transform.createTransformedShape(glyph), false)
                            next += (advance + nextAdvance) * factor
                            currentChar++
                            if (repeat) {
                                currentChar %= length
                            }
                        }
                    }
                    next -= distance
                    lastX = thisX
                    lastY = thisY
                }
                else -> {
                }
            }
            it.next()
        }
        return result
    }

    fun measurePathLength(shape: Shape): Float {
        val it: PathIterator = FlatteningPathIterator(shape.getPathIterator(null), flatness)
        val points = FloatArray(6)
        var moveX = 0f
        var moveY = 0f
        var lastX = 0f
        var lastY = 0f
        var thisX: Float
        var thisY: Float
        var type: Int
        var total = 0f
        while (!it.isDone) {
            type = it.currentSegment(points)
            when (type) {
                PathIterator.SEG_MOVETO -> {
                    run {
                        lastX = points[0]
                        moveX = lastX
                    }
                    run {
                        lastY = points[1]
                        moveY = lastY
                    }
                }
                PathIterator.SEG_CLOSE -> {
                    points[0] = moveX
                    points[1] = moveY
                    thisX = points[0]
                    thisY = points[1]
                    val dx = thisX - lastX
                    val dy = thisY - lastY
                    total += Math.sqrt(dx * dx + dy * dy.toDouble()).toFloat()
                    lastX = thisX
                    lastY = thisY
                }
                PathIterator.SEG_LINETO -> {
                    thisX = points[0]
                    thisY = points[1]
                    val dx = thisX - lastX
                    val dy = thisY - lastY
                    total += Math.sqrt(dx * dx + dy * dy.toDouble()).toFloat()
                    lastX = thisX
                    lastY = thisY
                }
                else -> {
                }
            }
            it.next()
        }
        return total
    }

}