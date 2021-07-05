package com.googlecode.blaisemath.palette

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Color

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
*/ /**
 * Maintains a list of colors suitable for use as categories or color scales within charts.
 * Each scheme is defined by a list of colors, and has a flag that indicates whether its a
 * discrete or continuous (i.e. gradient) scheme.
 *
 * @author Elisha Peterson
 */
class ColorScheme {
    // </editor-fold>
    private var name: String? = null
    private var discrete = true
    private var colors: Array<Color?>? = arrayOfNulls<Color?>(0)
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    /**
     * Get name of scheme.
     * @return name
     */
    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    /**
     * Return true if a discrete scheme.
     * @return discrete
     */
    fun isDiscrete(): Boolean {
        return discrete
    }

    fun setDiscrete(discrete: Boolean) {
        this.discrete = discrete
    }

    /**
     * Get array of colors in scheme.
     * @return colors
     */
    fun getColors(): Array<Color?>? {
        return colors
    }

    fun setColors(colors: Array<Color?>?) {
        this.colors = colors
    } //</editor-fold>

    companion object {
        // <editor-fold defaultstate="collapsed" desc="STATIC COLORS">
        val BRIGHT_ORANGE: Color? = Color(255, 150, 0)
        val LIGHT_BLUE: Color? = Color(128, 128, 255)
        val DARK_BLUE: Color? = Color(0, 0, 128)
        val LIGHT_RED: Color? = Color(255, 100, 100)
        val DARK_GREEN: Color? = Color(0, 164, 0)
        val DARKISH_GREEN: Color? = Color(0, 216, 0)
        val BROWN: Color? = Color(140, 80, 0)
        val DARK_BROWN: Color? = Color(80, 40, 0)
        val HOT_PINK: Color? = Color(200, 40, 140)
        val PURPLE: Color? = Color(130, 10, 150)

        // Green-Armitage colors
        val GA_AMETHYST: Color? = Color(240, 163, 255)
        val GA_BLUE: Color? = Color(0, 117, 220)
        val GA_CARAMEL: Color? = Color(153, 63, 0)
        val GA_DAMSON: Color? = Color(76, 0, 92)
        val GA_EBONY: Color? = Color(25, 25, 25)
        val GA_FOREST: Color? = Color(0, 92, 49)
        val GA_GREEN: Color? = Color(43, 206, 72)
        val GA_HONEYDEW: Color? = Color(255, 204, 153)
        val GA_IRON: Color? = Color(128, 128, 128)
        val GA_JADE: Color? = Color(148, 255, 181)
        val GA_KHAKI: Color? = Color(153, 124, 0)
        val GA_LIME: Color? = Color(157, 204, 0)
        val GA_MALLOW: Color? = Color(194, 0, 136)
        val GA_NAVY: Color? = Color(0, 51, 128)
        val GA_ORPIMENT: Color? = Color(255, 164, 5)
        val GA_PINK: Color? = Color(255, 168, 187)
        val GA_QUAGMIRE: Color? = Color(66, 102, 0)
        val GA_RED: Color? = Color(255, 0, 16)
        val GA_SKY: Color? = Color(94, 241, 242)
        val GA_TURQUOISE: Color? = Color(0, 153, 143)
        val GA_URANIUM: Color? = Color(224, 255, 102)
        val GA_VIOLET: Color? = Color(116, 10, 255)
        val GA_WINE: Color? = Color(153, 0, 0)
        val GA_XANTHIN: Color? = Color(255, 255, 128)
        val GA_YELLOW: Color? = Color(255, 225, 0)
        val GA_ZINNIA: Color? = Color(255, 80, 5)

        /**
         * Construct discrete color scheme.
         * @param name scheme name
         * @param colors scheme colors
         * @return scheme
         */
        fun create(name: String?, vararg colors: Color?): ColorScheme? {
            val res = ColorScheme()
            res.name = name
            res.discrete = true
            res.colors = colors
            return res
        }

        /**
         * Construct gradient color scheme.
         * @param name scheme name
         * @param colors colors to use for gradient
         * @return scheme
         */
        fun createGradient(name: String?, vararg colors: Color?): ColorScheme? {
            val res = ColorScheme()
            res.name = name
            res.discrete = false
            res.colors = colors
            return res
        }
    }
}