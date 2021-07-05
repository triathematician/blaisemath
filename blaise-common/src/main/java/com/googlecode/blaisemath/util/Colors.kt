package com.googlecode.blaisemath.util

import com.google.common.base.Preconditions
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Color
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
 * Provides a number of utilities for working with colors, e.g. creating lighter/darker colors,
 * adjusting the alpha of a color, converting to/from hex strings.
 *
 * @author Elisha Peterson
 */
object Colors {
    /**
     * Convert color to string. Results in #RRGGBB or #RRGGBBAA, depending on
     * whether or not the color has an alpha channel.
     * @param c color
     * @return string
     * @throws NullPointerException if c is null
     */
    fun encode(c: Color?): String? {
        Objects.requireNonNull(c)
        return if (c.getAlpha() == 255) {
            String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue())
        } else {
            String.format("#%02x%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha())
        }
    }

    /**
     * Flexible color decoder. Uses [javafx.scene.paint.Color.web] to decode.
     * @param v color string
     * @return color
     * @throws NullPointerException if v is null
     * @throws IllegalArgumentException if v is an invalid string
     */
    fun decode(v: String?): Color? {
        Objects.requireNonNull(v)
        val fx = javafx.scene.paint.Color.web(v)
        return Color(fx.red as Float, fx.green as Float,
                fx.blue as Float, fx.opacity as Float)
    }

    /**
     * Transform the alpha component of a color.
     * @param col the color
     * @param a new alpha value
     * @return transformed color
     */
    fun alpha(col: Color?, a: Int): Color? {
        Preconditions.checkArgument(a >= 0 && a <= 255)
        return Color(col.getRed(), col.getGreen(), col.getBlue(), a)
    }

    /**
     * Interpolates between two colors, e.g. r = r1*wt + r2*(1-wt).
     * @param c1 first color
     * @param wt weight of first color (between 0 and 1)
     * @param c2 second color
     * @return interpolated color
     */
    fun interpolate(c1: Color?, wt: Float, c2: Color?): Color? {
        return interpolate(c1, wt, c2, 1 - wt)
    }

    /**
     * Interpolates between two colors. In most cases, the weights should sum to 1.
     * @param c1 first color
     * @param wt1 first color weight
     * @param c2 second color
     * @param wt2 second color weight
     * @return interpolated colors
     */
    fun interpolate(c1: Color?, wt1: Float, c2: Color?, wt2: Float): Color? {
        return Color(interpolate(c1.getRed(), wt1, c2.getRed(), wt2),
                interpolate(c1.getGreen(), wt1, c2.getGreen(), wt2),
                interpolate(c1.getBlue(), wt1, c2.getBlue(), wt2),
                interpolate(c1.getAlpha(), wt1, c2.getAlpha(), wt2))
    }

    private fun interpolate(a: Int, wt1: Float, b: Int, wt2: Float): Int {
        val res = (a * wt1 + b * wt2) as Int
        return if (res < 0) 0 else if (res > 255) 255 else res
    }

    /**
     * Produces a color that is lighter than the specified color.
     * @param c source color
     * @return new color
     */
    fun lighterThan(c: Color?): Color? {
        return Color(lighten(c.getRed()), lighten(c.getGreen()),
                lighten(c.getBlue()), c.getAlpha())
    }

    private fun lighten(i: Int): Int {
        return i + Math.min(64, (255 - i) / 2)
    }

    /**
     * Produces a color that is "blander" than the specified color (reducing saturation by 50%).
     * @param c source color
     * @return new color
     */
    fun blanderThan(c: Color?): Color? {
        val hsb = FloatArray(3)
        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb)
        val c2 = Color.getHSBColor(hsb[0], .5f * hsb[1], hsb[2])
        return Color(c2.red, c2.green, c2.blue, c.getAlpha())
    }
}