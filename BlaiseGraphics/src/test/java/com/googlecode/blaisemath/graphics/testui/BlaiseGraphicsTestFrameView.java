/*
 * BlaiseGraphicsTestFrameView.java
 */

package com.googlecode.blaisemath.graphics.testui;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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

import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.app.MenuConfig;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;

/**
 * The application's main frame.
 */
public class BlaiseGraphicsTestFrameView extends FrameView {

    public BlaiseGraphicsTestFrameView(SingleFrameApplication app) {
        super(app);

        initComponents();
    }
    
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        canvas1 = new JGraphicComponent();
        PanAndZoomHandler.install(canvas1);

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.GridLayout(1, 2));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(BlaiseGraphicsTestApp.class).getContext().getResourceMap(BlaiseGraphicsTestFrameView.class);
        
        canvas1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("canvas1.border.title"))); // NOI18N
        canvas1.setName("canvas1"); // NOI18N

        mainPanel.add(canvas1);
        setComponent(mainPanel);
        
        try {
            setMenuBar(MenuConfig.readMenuBar(BlaiseGraphicsTestApp.class, this));
            setToolBar(MenuConfig.readToolBar(BlaiseGraphicsTestApp.class, this));
        } catch (IOException ex) {
            Logger.getLogger(BlaiseGraphicsTestFrameView.class.getName()).log(Level.SEVERE, 
                    "Menu config failure.", ex);
        }
    }

    JGraphicComponent canvas1;
    private javax.swing.JPanel mainPanel;
}
