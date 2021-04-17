package com.googlecode.blaisemath.util
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

import java.awt.Color
import kotlin.math.min

/**
 * Provides a number of utilities for working with colors, e.g. creating lighter/darker colors,
 * adjusting the alpha of a color, converting to/from hex strings.
 */
object Colors {

    /**
     * Convert color to string. Results in #RRGGBB or #RRGGBBAA, depending on
     * whether or not the color has an alpha channel.
     * @param c color
     * @return string
     * @throws NullPointerException if c is null
     */
    fun encode(c: Color): String = when (c.alpha) {
        255 -> String.format("#%02x%02x%02x", c.red, c.green, c.blue)
        else -> String.format("#%02x%02x%02x%02x", c.red, c.green, c.blue, c.alpha)
    }

    /**
     * Flexible color decoder. Uses [javafx.scene.paint.Color.web] to decode.
     * @throws IllegalArgumentException if v is an invalid string
     */
    fun decode(v: String): Color {
        val fx = javafx.scene.paint.Color.web(v)
        return Color(fx.red.toFloat(), fx.green.toFloat(), fx.blue.toFloat(), fx.opacity.toFloat())
    }

    //region EXTENSION FUNCTIONS

    /** Transform the alpha component of a color. */
    @JvmStatic
    fun Color.alpha(alpha: Int): Color {
        require(alpha in 0..255)
        return Color(red, green, blue, alpha)
    }

    /** Create a color that is slightly lighter. */
    @kotlin.jvm.JvmStatic
    fun Color.lightened() = Color(lighten(red), lighten(green), lighten(blue), alpha)

    private fun lighten(i: Int) = i + min(64, (255 - i) / 2)

    /** Create a color with saturation reduced by 50% */
    @kotlin.jvm.JvmStatic
    fun Color.withReducedSaturation(level: Float = .5f): Color {
        require(level in 0f..1f)
        val hsb = FloatArray(3).also { Color.RGBtoHSB(red, green, blue, it) }
        val c2 = Color.getHSBColor(hsb[0], level * hsb[1], hsb[2])
        return Color(c2.red, c2.green, c2.blue, alpha)
    }

    //endregion

    /**
     * Interpolates between two colors, e.g. r = r1*wt + r2*(1-wt).
     * @param c1 first color
     * @param wt weight of first color (between 0 and 1)
     * @param c2 second color
     * @return interpolated color
     */
    fun interpolate(c1: Color, wt: Float, c2: Color): Color {
        require(wt in 0f..1f)
        val awt = 1 - wt
        return Color((c1.red * wt + c2.red * awt) / 255, (c1.green * wt + c2.green * awt) / 255,
                (c1.blue * wt + c2.blue * awt) / 255, (c1.alpha * wt + c2.alpha * awt) / 255)
    }
}