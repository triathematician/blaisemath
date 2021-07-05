package com.googlecode.blaisemath.graphics.swing

import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.util.SetSelectionModel
import java.awt.Color
import java.awt.EventQueue
import java.awt.Point
import java.awt.Rectangle
import java.beans.PropertyChangeEvent
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
*/   class HelloWorldTestFrame : JFrame() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // expect two colored dots inside a blue rectangle
            EventQueue.invokeLater { HelloWorldTestFrame().isVisible = true }
        }
    }

    init {
        val canvas = JGraphicComponent()
        canvas.background = Color.black
        PanAndZoomHandler.Companion.install(canvas)
        canvas.isSelectionEnabled = true
        val pg1 = JGraphics.point(Point(50, 50))
        pg1.isSelectionEnabled = true
        canvas.addGraphic(pg1)
        canvas.addGraphic(JGraphics.point(Point(100, 50), Styles.fillStroke(Color.yellow, Color.red)))
        canvas.addGraphic(JGraphics.path(Rectangle(25, 25, 100, 50), Styles.strokeWidth(Color.blue, 2f)))
        contentPane = canvas
        canvas.selectionModel.addPropertyChangeListener(
                SetSelectionModel.SELECTION_PROPERTY) { evt: PropertyChangeEvent? -> println(evt.getNewValue()) }
        defaultCloseOperation = EXIT_ON_CLOSE
        pack()
    }
}