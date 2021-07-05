package com.googlecode.blaisemath.palette.ui

import com.google.common.collect.Sets
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graphics.testui.AnchorTestFrame
import com.googlecode.blaisemath.palette.MutablePalette
import com.googlecode.blaisemath.palette.Palette
import com.googlecode.blaisemath.palette.Palettes
import com.googlecode.blaisemath.palette.ui.PaletteEditorTestUi
import com.googlecode.blaisemath.palette.ui.PaletteIconsTestUi
import com.googlecode.blaisemath.style.ui.AttributeSetPropertyModelTestFrame
import java.util.*

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
 * Used to edit the keyed colors associated with a [Palette]. Disables removal of fg/bg colors.
 * @author Elisha Peterson
 */
class PaletteEditor : ColorListEditorSupport() {
    private var palette = Palettes.defaultPalette().mutableCopy()
    override fun updateModelStyles(styles: MutableList<KeyColorBean?>?) {
        val removeKeys: MutableSet<String?>? = Sets.newHashSet(palette.colors())
        for (b in styles) {
            palette[b.getName()] = b.getColor()
            removeKeys.remove(b.getName())
        }
        for (c in removeKeys) {
            palette.remove(c)
        }
        firePropertyChange("palette", null, palette)
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    fun getPalette(): MutablePalette? {
        return palette
    }

    fun setPalette(palette: MutablePalette?) {
        if (this.palette !== palette) {
            val old: Any? = this.palette
            this.palette = palette
            list.colorListModel.setColors(palette)
            firePropertyChange("palette", old, palette)
        }
    } //</editor-fold>

    init {

        // don't allow removal of fg/bg colors
        list.editConstraints = object : ColorListEditConstraints() {
            override fun isRemovable(item: String?): Boolean {
                return !Arrays.asList(Palette.BACKGROUND, Palette.FOREGROUND).contains(item)
            }

            override fun isKeyEditable(item: String?): Boolean {
                return isRemovable(item)
            }
        }
    }
}