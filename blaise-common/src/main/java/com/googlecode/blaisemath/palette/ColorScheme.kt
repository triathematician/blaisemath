package com.googlecode.blaisemath.palette

import com.google.common.collect.Maps
import com.googlecode.blaisemath.util.kotlin.severe
import java.awt.Color
import java.io.IOException
import java.util.*
import java.util.logging.Level

/*
* #%L
* blaise-graphics
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

/**
 * Maintains a list of colors suitable for use as categories or color scales within charts.
 * Each scheme is defined by a list of colors, and has a flag that indicates whether its a
 * discrete or continuous (i.e. gradient) scheme.
 */
class ColorScheme(
    var name: String? = null,
    var discrete: Boolean = true,
    var colors: List<Color> = listOf()) {

    companion object {

        /** Construct gradient color scheme. */
        fun gradient(name: String?, vararg colors: Color) = ColorScheme(name, false, listOf(*colors))
        /** Construct gradient color scheme. */
        fun gradient(name: String?, colors: List<Color>) = ColorScheme(name, false, colors)

        const val BASIC = "BASIC"
        const val GREEN_ARMITAGE = "GREEN_ARMITAGE"
        const val GLASBEY = "GLASBEY"
        const val CATEGORY10 = "CATEGORY10"
        const val CATEGORY20 = "CATEGORY20"
        const val CATEGORY20B = "CATEGORY20B"
        const val CATEGORY20C = "CATEGORY20C"
        const val BLUES = "BLUES"

        val SCHEMES: Map<String, ColorScheme> by lazy {
            try {
                val p = Properties()
                p.load(Palettes::class.java.getResource("resources/ColorSchemes.properties").openStream())
                PaletteIo.loadSchemes(p)
            } catch (ex: IOException) {
                severe<ColorScheme>("Failed to load schemes from resources file", ex)
                mapOf()
            }
        }

        val BRIGHT_ORANGE = Color(255, 150, 0)
        val LIGHT_BLUE = Color(128, 128, 255)
        val DARK_BLUE = Color(0, 0, 128)
        val LIGHT_RED = Color(255, 100, 100)
        val DARK_GREEN = Color(0, 164, 0)
        val DARKISH_GREEN = Color(0, 216, 0)
        val BROWN = Color(140, 80, 0)
        val DARK_BROWN = Color(80, 40, 0)
        val HOT_PINK = Color(200, 40, 140)
        val PURPLE = Color(130, 10, 150)

        // Green-Armitage colors
        val GA_AMETHYST = Color(240, 163, 255)
        val GA_BLUE = Color(0, 117, 220)
        val GA_CARAMEL = Color(153, 63, 0)
        val GA_DAMSON = Color(76, 0, 92)
        val GA_EBONY = Color(25, 25, 25)
        val GA_FOREST = Color(0, 92, 49)
        val GA_GREEN = Color(43, 206, 72)
        val GA_HONEYDEW = Color(255, 204, 153)
        val GA_IRON = Color(128, 128, 128)
        val GA_JADE = Color(148, 255, 181)
        val GA_KHAKI = Color(153, 124, 0)
        val GA_LIME = Color(157, 204, 0)
        val GA_MALLOW = Color(194, 0, 136)
        val GA_NAVY = Color(0, 51, 128)
        val GA_ORPIMENT = Color(255, 164, 5)
        val GA_PINK = Color(255, 168, 187)
        val GA_QUAGMIRE = Color(66, 102, 0)
        val GA_RED = Color(255, 0, 16)
        val GA_SKY = Color(94, 241, 242)
        val GA_TURQUOISE = Color(0, 153, 143)
        val GA_URANIUM = Color(224, 255, 102)
        val GA_VIOLET = Color(116, 10, 255)
        val GA_WINE = Color(153, 0, 0)
        val GA_XANTHIN = Color(255, 255, 128)
        val GA_YELLOW = Color(255, 225, 0)
        val GA_ZINNIA = Color(255, 80, 5)
    }

}