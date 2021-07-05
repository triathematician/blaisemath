package com.googlecode.blaisemath.palette.ui

import com.google.common.base.Objects
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.firestarter.editor.ColorEditor
import com.googlecode.blaisemath.graphics.testui.AnchorTestFrame
import com.googlecode.blaisemath.palette.ui.PaletteEditorTestUi
import com.googlecode.blaisemath.palette.ui.PaletteIconsTestUi
import com.googlecode.blaisemath.style.ui.AttributeSetPropertyModelTestFrame
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.JPanel

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
 * Displays editable color.
 * @author Elisha Peterson
 */
class ColorPicker : JPanel() {
    val ed: ColorEditor?
    fun getColor(): Color? {
        return ed.getNewValue() as Color
    }

    fun setColor(c: Color?) {
        val old: Any? = getColor()
        if (!Objects.equal(old, c)) {
            ed.setValue(c)
            firePropertyChange("color", old, c)
        }
    }

    init {
        ed = ColorEditor()
        layout = BorderLayout()
        val customEditor = ed.getCustomEditor()
        add(customEditor)
        preferredSize = Dimension(150, 30)
        ed.addPropertyChangeListener(PropertyChangeListener { evt: PropertyChangeEvent? -> firePropertyChange("color", evt.getOldValue(), evt.getNewValue()) })
    }
}