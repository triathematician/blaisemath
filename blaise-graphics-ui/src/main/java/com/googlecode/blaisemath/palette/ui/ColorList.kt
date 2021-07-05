package com.googlecode.blaisemath.palette.ui

import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.firestarter.property.PropertySheetDialog
import com.googlecode.blaisemath.graphics.testui.AnchorTestFrame
import com.googlecode.blaisemath.palette.ui.PaletteEditorTestUi
import com.googlecode.blaisemath.palette.ui.PaletteIconsTestUi
import com.googlecode.blaisemath.style.ui.AttributeSetPropertyModelTestFrame
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.beans.PropertyChangeEvent
import java.util.*
import javax.swing.*
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

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
 * UI with an editable list of colors, each of which may be associated with a string. This presents the colors
 * in a list, and may allow users to add/edit/remove/rearrange the colors. The allowed behavior can be configured
 * within [ColorListEditConstraints]. Changes are propagated through the "colors" properties when either
 * the list of colors changes, or individual items within the list change.
 *
 * @author Elisha Peterson
 */
class ColorList : JList<Any?>() {
    private var model: ColorListModel? = null
    private var editConstraints: ColorListEditConstraints? = ColorListEditConstraints()
    private val modelListener: ListDataListener? = ListModelListener()

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    fun getColors(): MutableList<KeyColorBean?>? {
        return model.getColors()
    }

    fun setColors(colors: MutableList<KeyColorBean?>?) {
        model.setColors(colors)
    }

    fun getColorListModel(): ColorListModel? {
        return model
    }

    fun setColorListModel(model: ColorListModel?) {
        if (this.model !== model) {
            val old: Any? = this.model
            setModel(model.also { this.model = it })
            firePropertyChange(MODEL, old, model)
        }
    }

    fun getEditConstraints(): ColorListEditConstraints? {
        return editConstraints
    }

    fun setEditConstraints(editConstraints: ColorListEditConstraints?) {
        this.editConstraints = Objects.requireNonNull(editConstraints)
    }

    //</editor-fold>
    private fun listChanged(e: ListDataEvent?) {
        firePropertyChange(COLORS, null, getColors())
    }
    //<editor-fold defaultstate="collapsed" desc="INNER CLASSES">
    /** Propagates changes from list model.  */
    internal inner class ListModelListener : ListDataListener {
        override fun intervalAdded(e: ListDataEvent?) {
            listChanged(e)
        }

        override fun intervalRemoved(e: ListDataEvent?) {
            listChanged(e)
        }

        override fun contentsChanged(e: ListDataEvent?) {
            listChanged(e)
        }
    }

    /** Shows popup menu  */
    private inner class PopupListener : MouseAdapter() {
        override fun mousePressed(e: MouseEvent?) {
            maybeShowPopup(e)
        }

        override fun mouseReleased(e: MouseEvent?) {
            maybeShowPopup(e)
        }

        private fun maybeShowPopup(e: MouseEvent?) {
            if (e.isPopupTrigger()) {
                val idx = locationToIndex(e.getPoint())
                selectedIndex = idx
                if (idx != -1) {
                    val item = model.getElementAt(idx)
                    val add: AddAction = AddAction()
                    add.isEnabled = editConstraints.isAddSupported()
                    val edit: EditAction = EditAction(item)
                    edit.isEnabled = editConstraints.isKeyEditable(item.name)
                    val remove: RemoveAction = RemoveAction(item)
                    remove.isEnabled = editConstraints.isRemovable(item.name)
                    val cp = ColorPicker()
                    cp.color = item.color
                    cp.addPropertyChangeListener("color") { evt: PropertyChangeEvent? ->
                        item.color = cp.color
                        this@ColorList.repaint()
                        listChanged(null)
                    }
                    val popup = JPopupMenu()
                    popup.add(cp)
                    popup.addSeparator()
                    popup.add(edit)
                    popup.add(remove)
                    popup.addSeparator()
                    popup.add(add)
                    popup.show(this@ColorList, e.getX(), e.getY())
                } else if (editConstraints.isAddSupported()) {
                    val popup = JPopupMenu()
                    popup.add(AddAction())
                    popup.show(this@ColorList, e.getX(), e.getY())
                }
            }
        }
    }

    private inner class AddAction private constructor() : AbstractAction("Add...") {
        override fun actionPerformed(e: ActionEvent?) {
            val item = KeyColorBean()
            item.color = UIManager.getColor("List.foreground")
            val frm = SwingUtilities.getAncestorOfClass(JFrame::class.java, this@ColorList) as JFrame
            if (editConstraints.isKeysEditable()) {
                PropertySheetDialog.show(frm, true, item)
            }
            model.addElement(item)
            listChanged(null)
        }
    }

    private inner class EditAction private constructor(private val item: KeyColorBean?) : AbstractAction("Edit...") {
        override fun actionPerformed(e: ActionEvent?) {
            val frm = SwingUtilities.getAncestorOfClass(JFrame::class.java, this@ColorList) as JFrame
            PropertySheetDialog.show(frm, true, item)
            listChanged(null)
            revalidate()
        }
    }

    private inner class RemoveAction private constructor(private val item: KeyColorBean?) : AbstractAction("Remove") {
        override fun actionPerformed(e: ActionEvent?) {
            model.removeElement(item)
            revalidate()
            listChanged(null)
        }
    }

    private inner class BasicShapeStyleRenderer : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component? {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            val lc = value as KeyColorBean?
            text = lc.getName()
            icon = BasicColorIcon(lc.getColor(), 18, null)
            return this
        }
    } //</editor-fold>

    companion object {
        private val MODEL: String? = "colorListModel"
        val COLORS: String? = "colors"
    }

    init {
        setColorListModel(ColorListModel())
        cellRenderer = BasicShapeStyleRenderer()
        layoutOrientation = VERTICAL_WRAP
        visibleRowCount = 0
        addMouseListener(PopupListener())
    }
}