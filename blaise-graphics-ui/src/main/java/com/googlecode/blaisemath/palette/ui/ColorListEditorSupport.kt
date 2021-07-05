package com.googlecode.blaisemath.palette.ui

import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graphics.testui.AnchorTestFrame
import com.googlecode.blaisemath.palette.ui.PaletteEditorTestUi
import com.googlecode.blaisemath.palette.ui.PaletteIconsTestUi
import com.googlecode.blaisemath.style.ui.AttributeSetPropertyModelTestFrame
import java.awt.BorderLayout
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.JPanel
import javax.swing.JScrollPane

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
 * Used to edit a list of colors. Provides custom hooks to
 *
 * @author Elisha Peterson
 */
abstract class ColorListEditorSupport : JPanel() {
    protected val list: ColorList? = ColorList()

    /**
     * Update the model content based on user edits in the UI.
     * @param styles list of styles from the UI
     */
    protected abstract fun updateModelStyles(styles: MutableList<KeyColorBean?>?)

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    fun getColorListModel(): ColorListModel? {
        return list.getColorListModel()
    }

    //</editor-fold>
    fun addColorListPropertyChangeListener(l: PropertyChangeListener?) {
        list.addPropertyChangeListener(ColorList.Companion.COLORS, l)
    }

    fun removeColorListPropertyChangeListener(l: PropertyChangeListener?) {
        list.removePropertyChangeListener(ColorList.Companion.COLORS, l)
    }

    init {
        layout = BorderLayout()
        add(JScrollPane(list), BorderLayout.CENTER)
        list.addPropertyChangeListener(PropertyChangeListener { evt: PropertyChangeEvent? -> updateModelStyles(list.getColorListModel().colors) })
        list.setEditConstraints(ColorListEditConstraints().keysEditable(false))
    }
}