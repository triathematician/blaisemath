package com.googlecode.blaisemath.graphics.editor

import com.googlecode.blaisemath.editor.MPanelEditorSupport
import com.googlecode.blaisemath.graphics.swing.render.ShapeRenderer
import com.googlecode.blaisemath.style.Marker
import com.googlecode.blaisemath.style.Markers
import com.googlecode.blaisemath.style.Styles
import java.awt.*
import javax.swing.*

/*
* #%L
* BlaiseGraphics
* --
* Copyright (C) 2009 - 2021 Elisha Peterson
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

/** Provides combo box for selection of a preset marker shape. */
class MarkerEditor : MPanelEditorSupport() {

    /** Box used to select marker.  */
    private val combo = JComboBox<Marker>().apply {
        model = DefaultComboBoxModel(Markers.availableMarkers.toTypedArray())
        setRenderer(MarkerCellRenderer(16))
    }

    override fun initCustomizer() {
        panel = JPanel(BorderLayout())
        panel.add(combo)
        combo.selectedItem = getNewValue()
        combo.addActionListener { setNewValue(combo.selectedItem) }
    }

    override fun initEditorValue() {
        combo.selectedItem = getNewValue()
    }

    //region INNER CLASSES

    /** Renders markers as icons in a list. */
    class MarkerCellRenderer(private val size: Int) : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(list: JList<*>, value: Any, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
            text = value.javaClass.simpleName
            icon = MarkerIcon(value as Marker, size)
            return this
        }
    }

    /** Renders marker as an icon. */
    private class MarkerIcon(private val marker: Marker, private val size: Int) : Icon {
        private val style = Styles.fillStroke(Color.white, Color.black, 1f)
        override fun getIconWidth() = size
        override fun getIconHeight() = size

        override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
            val sh = marker.create(Point(size / 2, size / 2), 0.0, size / 2f - 1)
            ShapeRenderer().render(sh, style, g as Graphics2D)
        }
    }

    //endregion
}