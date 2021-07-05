package com.googlecode.blaisemath.graphics.editor

import com.googlecode.blaisemath.editor.EditorRegistration
import com.googlecode.blaisemath.firestarter.PropertySheet
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent
import com.googlecode.blaisemath.graphics.swing.JGraphics
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.util.RollupPanel
import java.awt.BorderLayout
import java.awt.Color
import java.awt.EventQueue
import java.awt.Point
import javax.swing.JFrame

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

object AttributeSetPropertyModelTestFrame : JFrame() {

    private var jGraphicComponent1 = JGraphicComponent()
    private var rollupPanel1 = RollupPanel()

    init {
        EditorRegistration.registerEditors()
        val attr = Styles.fillStroke(Color.white, Color.red)
        val m = AttributeSetPropertyModel(attr, mapOf(Styles.FILL to Color::class.java, Styles.STROKE to Color::class.java))
        rollupPanel1.add("AS Test", PropertySheet.forModel(m))
        jGraphicComponent1.addGraphic(JGraphics.point(Point(20, 20), attr))

        defaultCloseOperation = EXIT_ON_CLOSE
        contentPane.add(rollupPanel1, BorderLayout.WEST)
        contentPane.add(jGraphicComponent1, BorderLayout.CENTER)
        pack()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        EventQueue.invokeLater { AttributeSetPropertyModelTestFrame.isVisible = true }
    }
}