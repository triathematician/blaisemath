package com.googlecode.blaisemath.graphics.testui

import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler
import org.jdesktop.application.Application
import org.jdesktop.application.FrameView
import org.jdesktop.application.SingleFrameApplication
import java.awt.GridLayout
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.BorderFactory
import javax.swing.JPanel

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
 * The application's main frame.
 */
class BlaiseGraphicsTestFrameView(app: SingleFrameApplication?) : FrameView(app) {
    private fun initComponents() {
        mainPanel = JPanel()
        canvas1 = JGraphicComponent()
        PanAndZoomHandler.install(canvas1)
        mainPanel.setName("mainPanel") // NOI18N
        mainPanel.setLayout(GridLayout(1, 2))
        val resourceMap = Application.getInstance(BlaiseGraphicsTestApp::class.java).context.getResourceMap(BlaiseGraphicsTestFrameView::class.java)
        canvas1.setBorder(BorderFactory.createTitledBorder(resourceMap.getString("canvas1.border.title"))) // NOI18N
        canvas1.setName("canvas1") // NOI18N
        mainPanel.add(canvas1)
        component = mainPanel
        try {
            menuBar = ApplicationMenuConfig.readMenuBar(BlaiseGraphicsTestApp::class.java, this)
            toolBar = ApplicationMenuConfig.readToolBar(BlaiseGraphicsTestApp::class.java, this)
        } catch (ex: IOException) {
            Logger.getLogger(BlaiseGraphicsTestFrameView::class.java.name).log(Level.SEVERE,
                    "Menu config failure.", ex)
        }
    }

    var canvas1: JGraphicComponent? = null
    private var mainPanel: JPanel? = null

    init {
        initComponents()
    }
}