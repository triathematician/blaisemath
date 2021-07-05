package com.googlecode.blaisemath.graphics.swing

import com.googlecode.blaisemath.graphics.Graphic
import com.googlecode.blaisemath.graphics.PrimitiveGraphic
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer
import java.awt.BorderLayout
import java.awt.EventQueue
import java.awt.Graphics2D
import java.awt.event.ActionEvent
import java.awt.geom.Ellipse2D
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import javax.swing.AbstractAction
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPopupMenu

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
*/   class ContextMenuTestFrame : JFrame() {
    private var gc: JGraphicComponent? = null
    private var jLabel1: JLabel? = null
    private fun initComponents() {
        gc = JGraphicComponent()
        jLabel1 = JLabel()
        defaultCloseOperation = EXIT_ON_CLOSE
        contentPane.add(gc, BorderLayout.CENTER)
        jLabel1.setText("<html>Should be up to 3 parts of the context menu: one for the circle, one for the selection, and one for the root graphics.")
        contentPane.add(jLabel1, BorderLayout.PAGE_START)
        pack()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            EventQueue.invokeLater { ContextMenuTestFrame().isVisible = true }
        }
    }

    init {
        initComponents()
        val g1: PrimitiveGraphic<*, *>? = JGraphics.shape(Ellipse2D.Double(50, 50, 100, 100))
        g1.addContextMenuInitializer(ContextMenuInitializer { menu: JPopupMenu?, src: Any?, point: Point2D?, focus: Any?, selection: MutableSet<*>? ->
            menu.add("" + point)
            menu.add(object : AbstractAction("press me") {
                override fun actionPerformed(e: ActionEvent?) {
                    println("pressed")
                }
            })
        })
        g1.setSelectionEnabled(true)
        gc.addGraphic(g1)
        val g2: PrimitiveGraphic<*, *>? = JGraphics.shape(Rectangle2D.Double(60, 90, 100, 100))
        g2.setSelectionEnabled(true)
        gc.addGraphic(g2)
        gc.setSelectionEnabled(true)
        gc.getGraphicRoot().addContextMenuInitializer { menu: JPopupMenu?, src: Graphic<Graphics2D?>?, point: Point2D?, focus: Any?, selection: MutableSet<*>? ->
            menu.setLabel("root label")
            menu.add((selection?.size ?: 0).toString() + " graphics selected")
            if (menu.getComponentCount() > 0) menu.addSeparator()
            menu.add("Root menu option")
        }
    }
}