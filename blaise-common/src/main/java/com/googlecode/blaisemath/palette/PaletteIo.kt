package com.googlecode.blaisemath.palette

import com.google.common.collect.Maps
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Color
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

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
 * Handles palette read/write operations.
 * @author Elisha Peterson
 */
object PaletteIo {
    private val LOG = Logger.getLogger(PaletteIo::class.java.name)

    /**
     * Loads palettes from properties file.
     * @param p properties file with key-value pairs defining individual palette colors
     * @return decoded palettes
     */
    fun loadPalettes(p: Properties?): MutableMap<String?, Palette?>? {
        val palettes: MutableMap<String?, MutableMap<String?, Color?>?>? = Maps.newTreeMap()
        for (k in p.stringPropertyNames()) {
            if (k.startsWith("palette.")) {
                val dot = k.indexOf('.', 8)
                val palette = k.substring(8, dot)
                palettes.putIfAbsent(palette, Maps.newLinkedHashMap())
                val color = k.substring(dot + 1)
                try {
                    palettes.get(palette)[color] = Colors.decode(p.getProperty(k))
                } catch (x: IllegalArgumentException) {
                    LOG.log(Level.WARNING, "Invalid color: {0}", p.getProperty(k))
                }
            }
        }
        val res: MutableMap<String?, Palette?>? = Maps.newLinkedHashMap()
        for (k in palettes.keys) {
            res[k] = ImmutableMapPalette(palettes.get(k))
        }
        return res
    }

    /**
     * Loads color schemes from properties file.
     * @param p properties file with schemes encoded as contiguous color strings
     * @return decoded schemes
     */
    fun loadSchemes(p: Properties?): MutableMap<String?, ColorScheme?>? {
        val res: MutableMap<String?, ColorScheme?>? = Maps.newTreeMap()
        for (k in p.stringPropertyNames()) {
            if (k.startsWith("scheme.")) {
                val scheme = k.substring(7)
                val cs: ColorScheme = ColorScheme.Companion.create(scheme, *colors(p.getProperty(k)))
                res[scheme] = cs
            } else if (k.startsWith("scheme-gradient.")) {
                val scheme = k.substring(16)
                val cs: ColorScheme = ColorScheme.Companion.createGradient(scheme, *colors(p.getProperty(k)))
                res[scheme] = cs
            }
        }
        return res
    }

    /** Decode array of colors from string.  */
    fun colors(f: String?): Array<Color?>? {
        val res = arrayOfNulls<Color?>(f.length / 6)
        for (i in res.indices) {
            res[i] = Color.decode("#" + f.substring(6 * i, 6 * (i + 1)))
        }
        return res
    }

    /** Encode array of colors as a string.  */
    fun colorString(colors: Array<Color?>?): String? {
        val res = StringBuilder()
        for (c in colors) {
            res.append(String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()))
        }
        return res.toString()
    }
}