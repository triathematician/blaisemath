package com.googlecode.blaisemath.primitive.ui

import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.firestarter.editor.MPanelEditorSupport
import com.googlecode.blaisemath.graphics.swing.render.ShapeRenderer
import com.googlecode.blaisemath.graphics.testui.AnchorTestFrame
import com.googlecode.blaisemath.palette.ui.PaletteEditorTestUi
import com.googlecode.blaisemath.palette.ui.PaletteIconsTestUi
import com.googlecode.blaisemath.primitive.Marker
import com.googlecode.blaisemath.primitive.Markers
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.style.ui.AttributeSetPropertyModelTestFrame
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

/*
* #%L
* BlaiseGraphics
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
 * Provides combo box for selection of a preset marker shape.
 *
 * @author Elisha Peterson
 */
class MarkerEditor : MPanelEditorSupport() {
    /** Box used to select marker.  */
    private val combo: JComboBox<Marker?>?
    override fun initCustomizer() {
        panel = JPanel(BorderLayout())
        panel.add(combo)
        combo.setSelectedItem(getNewValue())
        combo.addActionListener(ActionListener { e: ActionEvent? -> setNewValue(combo.getSelectedItem()) })
    }

    override fun initEditorValue() {
        combo.setSelectedItem(getNewValue())
    }
    //region INNER CLASSES
    /**
     * Renders markers as icons in a list.
     */
    class MarkerCellRenderer(private val size: Int) : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component? {
            text = if (value == null) "<html><i>no marker selected</i>" else value.javaClass.simpleName
            icon = MarkerIcon(value as Marker?, size)
            return this
        }
    }

    /**
     * Renders marker as an icon.
     */
    private class MarkerIcon(private val marker: Marker?, private val size: Int) : Icon {
        private val style = Styles.fillStroke(Color.white, Color.black, 1f)
        override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
            if (marker != null) {
                val sh = marker.create(Point(size / 2, size / 2), 0.0, size / 2f - 1)
                ShapeRenderer.getInstance().render(sh, style, g as Graphics2D?)
            }
        }

        override fun getIconWidth(): Int {
            return size
        }

        override fun getIconHeight(): Int {
            return size
        }
    } //endregion

    /** Initialize the editor.  */
    init {
        combo = JComboBox()
        combo.setModel(DefaultComboBoxModel<Any?>(Markers.getAvailableMarkers().toTypedArray()))
        combo.setRenderer(MarkerCellRenderer(16))
    }
}