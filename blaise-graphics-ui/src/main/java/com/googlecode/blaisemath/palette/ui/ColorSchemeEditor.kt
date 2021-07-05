package com.googlecode.blaisemath.palette.ui

import com.google.common.collect.Maps
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graphics.testui.AnchorTestFrame
import com.googlecode.blaisemath.palette.ColorScheme
import com.googlecode.blaisemath.palette.ui.PaletteEditorTestUi
import com.googlecode.blaisemath.palette.ui.PaletteIconsTestUi
import com.googlecode.blaisemath.style.ui.AttributeSetPropertyModelTestFrame
import com.googlecode.blaisemath.util.Colors
import java.awt.Color

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
 * Used to edit colors in a color scheme. If the scheme is a gradient scheme, the number of colors is fixed.
 *
 * @author Elisha Peterson
 */
class ColorSchemeEditor : ColorListEditorSupport() {
    private var scheme: ColorScheme? = ColorScheme()
    override fun updateModelStyles(items: MutableList<KeyColorBean?>?) {
        // update names since the user should not be updating them
        for (i in items.indices) {
            val b = items.get(i)
            b.setName(name(i, b.getColor()))
        }

        // update the scheme
        val colors = arrayOfNulls<Color?>(items.size)
        for (i in colors.indices) {
            colors[i] = items.get(i).getColor()
        }
        scheme.setColors(colors)
        firePropertyChange("scheme", null, scheme)
    }

    private fun updateListColors() {
        val colors: MutableMap<String?, Color?>? = Maps.newLinkedHashMap()
        for (i in scheme.getColors().indices) {
            val color = scheme.getColors()[i]
            colors[name(i, color)] = color
        }
        list.colorListModel.colorMap = colors
        list.editConstraints = ColorListEditConstraints().keysEditable(false)
    }

    private fun name(i: Int, c: Color?): String? {
        return if (scheme.isDiscrete()) Colors.encode(c) else "Stop " + (i + 1)
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    fun getScheme(): ColorScheme? {
        return scheme
    }

    fun setScheme(scheme: ColorScheme?) {
        if (this.scheme !== scheme) {
            val old: Any? = this.scheme
            this.scheme = scheme
            updateListColors()
            firePropertyChange("scheme", old, scheme)
        }
    } //</editor-fold>

    init {
        list.editConstraints = ColorListEditConstraints().keysEditable(false)
    }
}