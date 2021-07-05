package com.googlecode.blaisemath.graphics.swing

import com.googlecode.blaisemath.graphics.PrimitiveGraphic
import com.googlecode.blaisemath.graphics.swing.render.ShapeRenderer
import com.googlecode.blaisemath.style.Styles
import java.awt.BorderLayout
import java.awt.EventQueue
import java.awt.geom.Ellipse2D
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
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
*/   class TooltipTestFrame : JFrame() {
    private var gc: JGraphicComponent? = null
    private var jLabel1: JLabel? = null
    private fun initComponents() {
        gc = JGraphicComponent()
        jLabel1 = JLabel()
        defaultCloseOperation = EXIT_ON_CLOSE
        contentPane.add(gc, BorderLayout.CENTER)
        jLabel1.setText("<html>Test that the square gives a tooltip that depends on where you're at, while the circle gives just one tooltip.")
        contentPane.add(jLabel1, BorderLayout.PAGE_START)
        pack()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            EventQueue.invokeLater { TooltipTestFrame().isVisible = true }
        }
    }

    init {
        initComponents()
        val g1: PrimitiveGraphic<*, *>? = JGraphics.shape(Ellipse2D.Double(50, 50, 100, 100))
        g1.setDefaultTooltip("Ellipse")
        gc.addGraphic(g1)
        val g2: PrimitiveGraphic<*, *> = object : PrimitiveGraphic<Any?, Any?>(Rectangle2D.Double(60, 90, 100, 100),
                Styles.DEFAULT_SHAPE_STYLE, ShapeRenderer.Companion.getInstance()) {
            override fun getTooltip(p: Point2D?, canvas: Any?): String? {
                return "" + p
            }
        }
        g2.isTooltipEnabled = true
        gc.addGraphic(g2)
    }
}