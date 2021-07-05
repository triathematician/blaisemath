package com.googlecode.blaisemath.graph.view

import com.google.common.base.Function
import com.google.common.collect.Ordering
import com.google.common.graph.EndpointPair
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.app.PresetMenuInitializer
import com.googlecode.blaisemath.app.PropertyActionPanel
import com.googlecode.blaisemath.graph.app.AnimationUtils
import com.googlecode.blaisemath.graph.app.GraphApp
import com.googlecode.blaisemath.graph.app.GraphAppCanvas
import com.googlecode.blaisemath.graph.app.GraphAppFrameView
import com.googlecode.blaisemath.graph.app.MetricScaler
import com.googlecode.blaisemath.graph.test.DynamicGraphTestFrame
import com.googlecode.blaisemath.graph.test.GraphTestFrame
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Styles
import java.awt.Color

/*
* #%L
* BlaiseGraphTheory
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
 * Provides an edge styler for changing the appearance of edges in a weighted graph. Provides unique styles for positive
 * and negative weights.
 *
 * @param <E> edge type
 * @author Elisha Peterson
</E> */
class WeightedEdgeStyler<E : EndpointPair<*>?>(
        /** Parent style  */
        protected val parent: AttributeSet?,
        /** Edge weights  */
        protected var weights: MutableMap<E?, Double?>?
) : Function<E?, AttributeSet?> {
    /** The maximum edge weight  */
    protected var maxWeight: Double
    fun getWeights(): MutableMap<E?, Double?>? {
        return weights
    }

    fun setWeights(weights: MutableMap<E?, Double?>?) {
        if (this.weights !== weights) {
            this.weights = weights
            maxWeight = if (weights.isEmpty()) 1.0 else Ordering.natural<Comparable<*>?>().max(weights.values)
        }
    }

    override fun apply(o: E?): AttributeSet? {
        val wt = weights.get(o)
        maxWeight = Math.max(maxWeight, Math.abs(wt))
        val positive = wt >= 0
        val relativeWeight = Math.abs(wt) / maxWeight
        val stroke = parent.getColor(Styles.STROKE)
        val c = if (positive) positiveColor(stroke, relativeWeight) else negativeColor(stroke, relativeWeight)
        return AttributeSet.withParent(parent)
                .and(Styles.STROKE, c)
                .and(Styles.STROKE_WIDTH, (2 * relativeWeight) as Float)
    }

    companion object {
        private const val HUE_RANGE = 0.1f

        //region UTILS
        private fun positiveColor(c: Color?, weight: Double): Color? {
            val wt = Math.min(1.0, Math.max(0.0, weight))
            val alpha = 100 + (155 * wt) as Int
            return if (c == null) {
                Color(
                        25 - (25 * wt) as Int,
                        205 + (50 * wt) as Int,
                        100 - (50 * wt) as Int,
                        alpha)
            } else {
                val hsb = Color.RGBtoHSB(c.red, c.green, c.blue, null)
                hsb[0] += HUE_RANGE * wt
                hsb[1] *= .5 + .5 * wt
                hsbColor(hsb, alpha)
            }
        }

        private fun negativeColor(c: Color?, weight: Double): Color? {
            val wt = Math.min(1.0, Math.max(0.0, weight))
            val alpha = 100 + (155 * wt) as Int
            return if (c == null) {
                Color(
                        205 + (50 * wt) as Int,
                        0,
                        100 - (50 * wt) as Int,
                        alpha)
            } else {
                val hsb = Color.RGBtoHSB(c.red, c.green, c.blue, null)
                hsb[0] -= 2 * HUE_RANGE * wt
                hsb[1] *= .5 + .5 * wt
                hsbColor(hsb, alpha)
            }
        }

        private fun hsbColor(hsb: FloatArray?, alpha: Int): Color? {
            val col = Color(Color.HSBtoRGB(hsb.get(0), hsb.get(1), hsb.get(2)))
            return Color(col.red, col.green, col.blue, alpha)
        } //endregion
    }

    /**
     * Construct the customizer
     * @param parent the parent style
     * @param weights weightings for edges in graph
     */
    init {
        maxWeight = if (weights.isEmpty()) 1.0 else Ordering.natural<Comparable<*>?>().max(weights.values)
    }
}