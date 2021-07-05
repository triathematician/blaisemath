package com.googlecode.blaisemath.style.ui

import com.google.common.collect.ImmutableMap
import com.googlecode.blaisemath.firestarter.editor.EditorRegistration
import com.googlecode.blaisemath.firestarter.property.PropertySheet
import com.googlecode.blaisemath.firestarter.swing.RollupPanel
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent
import com.googlecode.blaisemath.graphics.swing.JGraphics
import com.googlecode.blaisemath.primitive.Marker
import com.googlecode.blaisemath.primitive.ui.MarkerEditor
import com.googlecode.blaisemath.style.Styles
import java.awt.BorderLayout
import java.awt.Color
import java.awt.EventQueue
import java.awt.Point
import java.beans.PropertyChangeEvent
import java.beans.PropertyEditorManager
import javax.swing.JFrame

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
*/   class AttributeSetPropertyModelTestFrame : JFrame() {
    private val canvas: JGraphicComponent?
    private val rollups: RollupPanel?

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // expect to see property sheet with editable styles, although they may not trigger immediate updates
            EventQueue.invokeLater { AttributeSetPropertyModelTestFrame().isVisible = true }
        }
    }

    init {
        rollups = RollupPanel()
        canvas = JGraphicComponent()
        defaultCloseOperation = EXIT_ON_CLOSE
        contentPane.add(rollups, BorderLayout.WEST)
        contentPane.add(canvas, BorderLayout.CENTER)
        EditorRegistration.registerEditors()
        PropertyEditorManager.registerEditor(Marker::class.java, MarkerEditor::class.java)
        val `as` = Styles.fillStroke(Color.white, Color.red)
        val m = AttributeSetPropertyModel(`as`,
                ImmutableMap.of(Styles.FILL, Color::class.java, Styles.STROKE, Color::class.java, Styles.MARKER, Marker::class.java))
        val ps = PropertySheet.forModel(m)
        rollups.add("AttributeSet Property Sheet Test", ps)
        ps.addPropertyChangeListener { e: PropertyChangeEvent? -> canvas.repaint() }
        canvas.addGraphic(JGraphics.point(Point(20, 20), `as`))
        pack()
    }
}