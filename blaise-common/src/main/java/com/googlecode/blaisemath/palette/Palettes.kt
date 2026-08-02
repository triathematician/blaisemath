package com.googlecode.blaisemath.palette

import com.googlecode.blaisemath.palette.Palette.Companion.ANNOTATION
import com.googlecode.blaisemath.palette.Palette.Companion.BACKGROUND
import com.googlecode.blaisemath.palette.Palette.Companion.BRIGHT_FOREGROUND
import com.googlecode.blaisemath.palette.Palette.Companion.FOREGROUND
import com.googlecode.blaisemath.palette.Palette.Companion.SELECTION
import com.googlecode.blaisemath.palette.Palette.Companion.SUBTLE_FOREGROUND
import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.kotlin.severe
import java.awt.Color
import java.io.IOException
import java.util.*
import javax.swing.UIManager

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

/** Provides a few basic static palettes, as well as some static color definitions. */
object Palettes {

    private const val SYSLAF_PALETTE = "System"
    private const val DEFAULT_PALETTE = "Simple"

    internal val PALETTES: Map<String, Palette> by lazy {
        val res = mutableMapOf<String, Palette>()
        res[DEFAULT_PALETTE] = DEF_PALETTE
        res[SYSLAF_PALETTE] = lafPalette()
        try {
            val p = Properties()
            p.load(Palettes::class.java.getResource("resources/Palettes.properties").openStream())
            res.putAll(PaletteIo.loadPalettes(p))
        } catch (ex: IOException) {
            severe<ColorScheme>("Failed to load palettes from resources file", ex)
        }
        res
    }

    /** Load palette by id, returning palette constructed from default LAF if not present. */
    fun paletteOrDefault(paletteId: String) = if (SYSLAF_PALETTE == paletteId) lafPalette() else PALETTES[paletteId] ?: DEF_PALETTE

    /** A general purpose default palette. */
    val DEF_PALETTE = ImmutableMapPalette(mapOf<String, Color>(
            FOREGROUND to Color.black,
            BACKGROUND to Color.white,
            SUBTLE_FOREGROUND to Color(225, 230, 220),
            BRIGHT_FOREGROUND to Color(16, 16, 16),
            SELECTION to Color(128, 0, 0, 128),
            ANNOTATION to Color(0, 0, 15, 192)
    ))

    /** Construct and return a palette based on the current look-and-feel. */
    fun lafPalette(): Palette {
        val fg = UIManager.getColor("Label.foreground")
        val bg = UIManager.getColor("Label.background")
        return ImmutableMapPalette(mapOf(
                FOREGROUND to fg,
                BACKGROUND to bg,
                BRIGHT_FOREGROUND to Colors.interpolate(fg, 1.2f, bg),
                SUBTLE_FOREGROUND to Colors.interpolate(fg, 0.4f, bg)
        ))
    }

}