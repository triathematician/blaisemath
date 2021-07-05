package com.googlecode.blaisemath.graphics.swing

import com.googlecode.blaisemath.graphics.PrimitiveGraphic
import com.googlecode.blaisemath.style.Styles
import java.awt.BorderLayout
import java.awt.Color
import java.awt.EventQueue
import java.awt.geom.Ellipse2D
import java.awt.geom.Rectangle2D
import java.beans.PropertyChangeEvent
import javax.swing.JFrame
import javax.swing.JLabel

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
*/   class PanAndZoomTestFrame : JFrame() {
    private var gc: JGraphicComponent? = null
    private var jLabel1: JLabel? = null
    private fun initComponents() {
        gc = JGraphicComponent()
        jLabel1 = JLabel()
        defaultCloseOperation = EXIT_ON_CLOSE
        contentPane.add(gc, BorderLayout.CENTER)
        jLabel1.setText("<html>Test the mouse wheel, drag to pan, and alt+drag to create a zoom box. Should be restricted to mouse button 1. Shift+drag should restrict movement to x or y direction.")
        contentPane.add(jLabel1, BorderLayout.PAGE_START)
        pack()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            EventQueue.invokeLater { PanAndZoomTestFrame().isVisible = true }
        }
    }

    init {
        initComponents()
        val g1: PrimitiveGraphic<*, *>? = JGraphics.shape(Ellipse2D.Double(50, 50, 100, 100),
                Styles.fillStroke(Color.blue, Color.red))
        g1.setSelectionEnabled(true)
        gc.addGraphic(g1)
        val g2: PrimitiveGraphic<*, *>? = JGraphics.shape(Rectangle2D.Double(60, 90, 100, 100))
        gc.addGraphic(g2)

        // all it takes to add selection capability!
        gc.setSelectionEnabled(true)
        gc.getSelectionModel().addPropertyChangeListener { evt: PropertyChangeEvent? -> println(evt.getPropertyName() + " : " + evt.getNewValue()) }

        // init pan and zoom
        PanAndZoomHandler.Companion.install(gc)
    }
}