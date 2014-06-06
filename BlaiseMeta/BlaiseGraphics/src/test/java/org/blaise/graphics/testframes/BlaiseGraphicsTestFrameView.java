/*
 * BlaiseGraphicsTestFrameView.java
 */

package org.blaise.graphics.testframes;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.blaise.graphics.GraphicComponent;
import org.blaise.graphics.PanAndZoomHandler;
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
        canvas1 = new GraphicComponent();
        new PanAndZoomHandler(canvas1);
        menuBar = new javax.swing.JMenuBar();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.GridLayout(1, 2));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(BlaiseGraphicsTestApp.class).getContext().getResourceMap(BlaiseGraphicsTestFrameView.class);
        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(BlaiseGraphicsTestApp.class).getContext().getActionMap(BlaiseGraphicsTestFrameView.class, this);
        
        canvas1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("canvas1.border.title"))); // NOI18N
        canvas1.setName("canvas1"); // NOI18N

        mainPanel.add(canvas1);

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem(actionMap.get("quit")));
        menuBar.add(fileMenu);

        JMenu basicMenu = new JMenu("Basic Objects");
        basicMenu.add(new JMenuItem(actionMap.get("addPoint")));
        basicMenu.add(new JMenuItem(actionMap.get("addSegment")));
        basicMenu.add(new JMenuItem(actionMap.get("addRectangle")));
        basicMenu.add(new JMenuItem(actionMap.get("addString")));
        menuBar.add(basicMenu);
        
        JMenu setsMenu = new JMenu("Object Sets");
        setsMenu.add(new JMenuItem(actionMap.get("addPointSet")));
        setsMenu.add(new JMenuItem(actionMap.get("editPointSetStyle")));
        setsMenu.add(new JMenuItem(actionMap.get("addDelegatingPointSet")));
        setsMenu.add(new JMenuItem(actionMap.get("addDelegatingPointSet2")));
        setsMenu.add(new JMenuItem(actionMap.get("addLabeledPointSet")));
        setsMenu.add(new JMenuItem(actionMap.get("addDelegatingGraph")));
        menuBar.add(setsMenu);
        
        JMenu compMenu = new JMenu("Compound Objects");
        compMenu.add(new JMenuItem(actionMap.get("addLabeledPoint")));
        compMenu.add(new JMenuItem(actionMap.get("addArrow")));
        compMenu.add(new JMenuItem(actionMap.get("addRuler")));
        compMenu.add(new JMenuItem(actionMap.get("add2Point")));
        compMenu.add(new JMenuItem(actionMap.get("addRay")));
        compMenu.add(new JMenuItem(actionMap.get("addLine")));
        menuBar.add(compMenu);

        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        jButton1.setAction(actionMap.get("clear1")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setName("jButton1"); // NOI18N
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton1);

        jButton2.setAction(actionMap.get("clear2")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton2);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setToolBar(jToolBar1);
    }

    GraphicComponent canvas1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
}
