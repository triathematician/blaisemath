package com.googlecode.blaisemath.palette

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Color
import java.io.IOException
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
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
*/ /**
 * Provides a few basic static palettes, as well as some static color definitions.
 *
 * @author Elisha Peterson
 */
object Palettes {
    private val LOG = Logger.getLogger(Palettes::class.java.name)
    private val SYSLAF_PALETTE: String? = "System"
    private val DEFAULT_PALETTE: String? = "Simple"
    private val PALETTES: MutableMap<String?, Palette?>? = Maps.newLinkedHashMap()
    val DEF_PALETTE: MutableMap<String?, Color?>? = ImmutableMap.builder<String?, Color?>()
            .put(Palette.Companion.FOREGROUND, Color.black)
            .put(Palette.Companion.BACKGROUND, Color.white)
            .put(Palette.Companion.SUBTLE_FOREGROUND, Color(225, 230, 220))
            .put(Palette.Companion.BRIGHT_FOREGROUND, Color(16, 16, 16))
            .put(Palette.Companion.SELECTION, Color(128, 0, 0, 128))
            .put(Palette.Companion.ANNOTATION, Color(0, 0, 15, 192))
            .build()

    /**
     * Get a statically-defined default palette.
     * @return palette
     */
    fun defaultPalette(): Palette? {
        return ImmutableMapPalette(DEF_PALETTE)
    }

    /**
     * Construct and return a palette based on the current look-and-feel.
     * @return palette
     */
    fun lafPalette(): Palette? {
        val fg = UIManager.getColor("Label.foreground")
        val bg = UIManager.getColor("Label.background")
        return ImmutableMapPalette(ImmutableMap.of(
                Palette.Companion.FOREGROUND, fg,
                Palette.Companion.BACKGROUND, bg,
                Palette.Companion.BRIGHT_FOREGROUND, Colors.interpolate(fg, 1.2f, bg, -0.2f),
                Palette.Companion.SUBTLE_FOREGROUND, Colors.interpolate(fg, 0.4f, bg, 0.6f)
        ))
    }

    /**
     * Get set of static palettes by name.
     * @return palettes
     */
    fun palettes(): MutableSet<String?>? {
        loadPalettes()
        val res: MutableSet<String?>? = Sets.newLinkedHashSet(Arrays.asList(SYSLAF_PALETTE, DEFAULT_PALETTE))
        res.addAll(PALETTES.keys)
        return Collections.unmodifiableSet(res)
    }

    /**
     * Load palette by id, returning palette constructed from default LAF if not present.
     * @param paletteId id of palette
     * @return palette
     */
    fun paletteOrDefault(paletteId: String?): Palette? {
        loadPalettes()
        return if (SYSLAF_PALETTE == paletteId) {
            lafPalette()
        } else PALETTES.getOrDefault(paletteId, lafPalette())
    }

    /**
     * Return palette colors as key-value map.
     * @param palette palette
     * @return map
     */
    fun colorMap(palette: Palette?): MutableMap<String?, Color?>? {
        val res = Maps.newLinkedHashMap<String?, Color?>()
        if (palette != null) {
            for (k in palette.colors()) {
                res[k] = palette.color(k)
            }
        }
        return res
    }

    //<editor-fold defaultstate="collapsed" desc="PRIVATE UTILITIES">
    private fun loadPalettes() {
        if (!PALETTES.isEmpty()) {
            return
        }
        PALETTES[DEFAULT_PALETTE] = defaultPalette()
        try {
            val p = Properties()
            p.load(Palettes::class.java.getResource("resources/Palettes.properties").openStream())
            PALETTES.putAll(PaletteIo.loadPalettes(p))
        } catch (ex: IOException) {
            LOG.log(Level.SEVERE, null, ex)
        }
    } //</editor-fold>
}