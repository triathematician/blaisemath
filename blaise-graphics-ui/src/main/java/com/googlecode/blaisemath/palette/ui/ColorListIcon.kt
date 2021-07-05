package com.googlecode.blaisemath.palette.ui

import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graphics.testui.AnchorTestFrame
import com.googlecode.blaisemath.palette.Palette
import com.googlecode.blaisemath.palette.Palettes
import com.googlecode.blaisemath.palette.ui.PaletteEditorTestUi
import com.googlecode.blaisemath.palette.ui.PaletteIconsTestUi
import com.googlecode.blaisemath.style.ui.AttributeSetPropertyModelTestFrame
import java.awt.Component
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
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
 * Paints a preview icon showing all the colors in a color list. Uses a [Palette] for
 * foreground and background colors, and a [ColorListModel] for the actual colors.
 *
 * @author Elisha Peterson
 */
class ColorListIcon : Icon {
    private var palette = Palettes.lafPalette()
    private var colors: ColorListModel? = ColorListModel()
    private var showNames = true

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    fun getPalette(): Palette? {
        return palette
    }

    fun setPalette(palette: Palette?) {
        this.palette = palette
    }

    fun getColors(): ColorListModel? {
        return colors
    }

    fun setColors(colors: ColorListModel?) {
        this.colors = colors
    }

    fun isShowNames(): Boolean {
        return showNames
    }

    fun setShowNames(showNames: Boolean) {
        this.showNames = showNames
    }

    //</editor-fold>
    override fun getIconWidth(): Int {
        return 150
    }

    override fun getIconHeight(): Int {
        return 150
    }

    override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
        val gr = g as Graphics2D?
        gr.setColor(palette.background())
        gr.fillRect(x, y, iconWidth, iconHeight)
        if (showNames) {
            paintColorsWithNames(gr, x, y)
        } else {
            paintColorsWithoutNames(gr, x, y)
        }
    }

    private fun paintColorsWithNames(gr: Graphics2D?, x: Int, y: Int) {
        val xx = x + 5
        var yy = y + 5
        gr.setFont(Font("Dialog", Font.PLAIN, 15))
        for (sty in colors.getColors()) {
            BasicColorIcon(sty.color, 15, palette.foreground()).paintIcon(null, gr, xx, yy)
            gr.setColor(if (Palette.BACKGROUND == sty.name) palette.foreground() else sty.color)
            if (sty.name != null) {
                gr.drawString(sty.name, 23, yy + 13)
            }
            yy += 20
        }
    }

    private fun paintColorsWithoutNames(gr: Graphics2D?, x: Int, y: Int) {
        var xx = x + 6
        var yy = y + 5
        for (sty in colors.getColors()) {
            BasicColorIcon(sty.color, 15, palette.foreground()).paintIcon(null, gr, xx, yy)
            xx += 20
            if (xx > iconWidth - 20) {
                xx = x + 6
                yy += 20
            }
        }
    }
}