package com.googlecode.blaisemath.palette.ui

import com.google.common.base.Preconditions
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graphics.testui.AnchorTestFrame
import com.googlecode.blaisemath.palette.ColorScheme
import com.googlecode.blaisemath.palette.Palette
import com.googlecode.blaisemath.palette.Palettes
import com.googlecode.blaisemath.palette.ui.PaletteEditorTestUi
import com.googlecode.blaisemath.palette.ui.PaletteIconsTestUi
import com.googlecode.blaisemath.style.ui.AttributeSetPropertyModelTestFrame
import java.awt.*
import java.util.*
import javax.swing.Icon

/*
* #%L
* blaise-graphics
* --
* Copyright (C) 2019 - 2021 Elisha Peterson
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
 * Icon displaying gradient palette.
 *
 * @author Elisha Peterson
 */
class ColorSchemeGradientIcon : Icon {
    private var palette = Palettes.lafPalette()
    private var scheme = ColorScheme.createGradient("", Color.black, Color.white)
    private var height = 150

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    fun getPalette(): Palette? {
        return palette
    }

    fun setPalette(palette: Palette?) {
        this.palette = Objects.requireNonNull(palette)
    }

    fun getScheme(): ColorScheme? {
        return scheme
    }

    fun setScheme(p: ColorScheme?) {
        Preconditions.checkArgument(!p.isDiscrete(), "Scheme must be a gradient!")
        scheme = p
    }

    override fun getIconHeight(): Int {
        return height
    }

    fun setIconHeight(ht: Int) {
        height = ht
    }

    //</editor-fold>
    override fun getIconWidth(): Int {
        return 30
    }

    override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
        val gr = g as Graphics2D?
        gr.setColor(palette.background())
        gr.fillRect(x, y, iconWidth, iconHeight)
        if (scheme.colors.size > 0) {
            if (scheme.colors.size == 1) {
                gr.setColor(scheme.colors[0])
            } else if (scheme.colors.size == 2) {
                gr.setPaint(GradientPaint(0, 5, scheme.colors[0], 0, iconHeight - 5, scheme.colors[1]))
            } else if (scheme.colors.size > 2) {
                val stops = FloatArray(scheme.colors.size)
                for (i in stops.indices) {
                    stops[i] = i / (stops.size - 1) as Float
                }
                gr.setPaint(LinearGradientPaint(0, 5, 0, iconHeight - 5, stops, scheme.colors))
            }
            gr.fillRect(x + 5, y + 5, iconWidth - 11, iconHeight - 11)
        }
        gr.setColor(palette.foreground())
        gr.drawRect(x + 5, y + 5, iconWidth - 11, iconHeight - 11)
    }
}