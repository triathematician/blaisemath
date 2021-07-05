package com.googlecode.blaisemath.palette.ui

import com.google.common.collect.Maps
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graphics.testui.AnchorTestFrame
import com.googlecode.blaisemath.palette.Palette
import com.googlecode.blaisemath.palette.Palettes
import com.googlecode.blaisemath.palette.ui.PaletteEditorTestUi
import com.googlecode.blaisemath.palette.ui.PaletteIconsTestUi
import com.googlecode.blaisemath.primitive.Marker
import com.googlecode.blaisemath.style.ui.AttributeSetPropertyModelTestFrame
import java.awt.Color
import java.util.*
import javax.swing.DefaultListModel

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
 * List model for colors associated with strings.
 * @author Elisha Peterson
 */
class ColorListModel : DefaultListModel<KeyColorBean?>() {
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    fun getColors(): MutableList<KeyColorBean?>? {
        return Collections.list(elements())
    }

    fun setColors(colors: MutableList<KeyColorBean?>?) {
        clear()
        for (c in colors) {
            addElement(c)
        }
    }

    fun setColors(pal: Palette?) {
        setColorMap(Palettes.colorMap(pal))
    }

    fun getColorMap(): MutableMap<String?, Color?>? {
        val res = Maps.newLinkedHashMap<String?, Color?>()
        for (en in Collections.list(elements())) {
            res[en.getName()] = en.getColor()
        }
        return res
    }

    fun setColorMap(cols: MutableMap<String?, Color?>?) {
        clear()
        for ((key, value) in cols) {
            addElement(KeyColorBean.Companion.create(key, value))
        }
    }

    //</editor-fold>
    fun name(index: Int): String? {
        return if (index >= 0 && index < size()) get(index).getName() else null
    }

    fun color(index: Int): Color? {
        return if (index >= 0 && index < size()) get(index).getColor() else null
    }

    fun marker(index: Int): Marker? {
        return if (index >= 0 && index < size()) get(index).getMarker() else null
    }

    companion object {
        /**
         * Construct color list model from given palette.
         * @param p palette
         * @return list model
         */
        fun create(p: Palette?): ColorListModel? {
            val res = ColorListModel()
            res.setColors(p)
            return res
        }
    }
}