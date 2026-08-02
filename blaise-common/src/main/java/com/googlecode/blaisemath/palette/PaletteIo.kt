package com.googlecode.blaisemath.palette

import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.kotlin.warning
import java.awt.Color
import java.util.*

/*
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

/**
 * Handles palette read/write operations.
 * @author Elisha Peterson
 */
object PaletteIo {

    /**
     * Loads palettes from properties file.
     * @param p properties file with key-value pairs defining individual palette colors
     * @return decoded palettes
     */
    fun loadPalettes(p: Properties): Map<String, Palette> {
        val palettes = mutableMapOf<String, MutableMap<String, Color>>()
        for (k in p.stringPropertyNames()) {
            if (k.startsWith("palette.")) {
                val dot = k.indexOf('.', 8)
                val palette = k.substring(8, dot)
                palettes.putIfAbsent(palette, mutableMapOf())
                val color = k.substring(dot + 1)
                try {
                    palettes[palette]!![color] = Colors.decode(p.getProperty(k))
                } catch (x: IllegalArgumentException) {
                    warning<PaletteIo>("Invalid color: ${p.getProperty(k)}", x)
                }
            }
        }
        return palettes.mapValues { ImmutableMapPalette(it.value) }
    }

    /**
     * Loads color schemes from properties file.
     * @param p properties file with schemes encoded as contiguous color strings
     * @return decoded schemes
     */
    fun loadSchemes(p: Properties): Map<String, ColorScheme> {
        val res = mutableMapOf<String, ColorScheme>()
        for (k in p.stringPropertyNames()) {
            if (k.startsWith("scheme.")) {
                val scheme = k.substring(7)
                val cs = ColorScheme(scheme, true, colors(p.getProperty(k)))
                res[scheme] = cs
            } else if (k.startsWith("scheme-gradient.")) {
                val scheme = k.substring(16)
                val cs = ColorScheme.gradient(scheme, colors(p.getProperty(k)))
                res[scheme] = cs
            }
        }
        return res
    }

    /** Decode array of colors from string.  */
    fun colors(f: String): List<Color> {
        val res = mutableListOf<Color>()
        for (i in 0 until f.length / 6) {
            res += Color.decode("#" + f.substring(6 * i, 6 * (i + 1)))
        }
        return res
    }

    /** Encode array of colors as a string.  */
    fun colorString(colors: List<Color>): String? {
        val res = StringBuilder()
        for (c in colors) {
            res.append(String.format("%02x%02x%02x", c.red, c.green, c.blue))
        }
        return res.toString()
    }
}