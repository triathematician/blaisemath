/*
 * BlaiseGraphicsTestView.java
 */

package org.blaise.visometry;

/*
 * #%L
 * BlaiseVisometry
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
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.blaise.graphics.GraphicComponent;
import org.blaise.graphics.GraphicSelectionHandler;
import org.blaise.visometry.plane.PlanePlotComponent;
import org.jdesktop.application.Application;

/**
 * The application's main frame.
 */
public class BlaiseGraphicsTestView extends FrameView {

    public BlaiseGraphicsTestView(SingleFrameApplication app) {
        super(app);
        initComponents(app);
    }
    
    private void initComponents(Application app) {
        mainPanel = new javax.swing.JPanel();
        canvas1 = new GraphicComponent(); canvas1.setSelectionEnabled(true);
        canvas2 = new PlanePlotComponent(); canvas2.setSelectionEnabled(true);
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu("File");
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem19 = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.GridLayout(1, 2));

        org.jdesktop.application.ResourceMap resourceMap = app.getContext().getResourceMap(BlaiseGraphicsTest.class);
        canvas1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("canvas1.border.title"))); // NOI18N
        canvas1.setName("canvas1"); // NOI18N

        javax.swing.GroupLayout canvas1Layout = new javax.swing.GroupLayout(canvas1);
        canvas1.setLayout(canvas1Layout);
        canvas1Layout.setHorizontalGroup(
            canvas1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 376, Short.MAX_VALUE)
        );
        canvas1Layout.setVerticalGroup(
            canvas1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 266, Short.MAX_VALUE)
        );

        mainPanel.add(canvas1);

        canvas2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("canvas2.border.title"))); // NOI18N
        canvas2.setName("canvas2"); // NOI18N

        javax.swing.GroupLayout canvas2Layout = new javax.swing.GroupLayout(canvas2);
        canvas2.setLayout(canvas2Layout);
        canvas2Layout.setHorizontalGroup(
            canvas2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 376, Short.MAX_VALUE)
        );
        canvas2Layout.setVerticalGroup(
            canvas2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 266, Short.MAX_VALUE)
        );

        mainPanel.add(canvas2);

        menuBar.setName("menuBar"); // NOI18N

        javax.swing.ActionMap actionMap = app.getContext().getActionMap(BlaiseGraphicsTest.class, app);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        JMenu jMenu1 = new JMenu("BlaiseGraphics");

        jMenuItem7.setAction(actionMap.get("addPoint")); // NOI18N
        jMenuItem7.setName("jMenuItem7"); // NOI18N
        jMenu1.add(jMenuItem7);

        jMenuItem8.setAction(actionMap.get("addPointSet")); // NOI18N
        jMenuItem8.setName("jMenuItem8"); // NOI18N
        jMenu1.add(jMenuItem8);

        jMenuItem14.setAction(actionMap.get("editPointSetStyle")); // NOI18N
        jMenuItem14.setName("jMenuItem14"); // NOI18N
        jMenu1.add(jMenuItem14);

        jMenuItem9.setAction(actionMap.get("addSegment")); // NOI18N
        jMenuItem9.setName("jMenuItem9"); // NOI18N
        jMenu1.add(jMenuItem9);

        jMenuItem10.setAction(actionMap.get("addRectangle")); // NOI18N
        jMenuItem10.setName("jMenuItem10"); // NOI18N
        jMenu1.add(jMenuItem10);

        jMenuItem11.setAction(actionMap.get("addString")); // NOI18N
        jMenuItem11.setName("jMenuItem11"); // NOI18N
        jMenu1.add(jMenuItem11);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jMenu1.add(jSeparator1);

        jMenuItem5.setAction(actionMap.get("addDelegatingPointSet")); // NOI18N
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        jMenu1.add(jMenuItem5);

        jMenuItem22.setAction(actionMap.get("addDelegatingPointSet2")); // NOI18N
        jMenuItem22.setName("jMenuItem22"); // NOI18N
        jMenu1.add(jMenuItem22);

        jMenuItem6.setAction(actionMap.get("addDelegatingGraph")); // NOI18N
        jMenuItem6.setName("jMenuItem6"); // NOI18N
        jMenu1.add(jMenuItem6);

        menuBar.add(jMenu1);

        JMenu jMenu2 = new JMenu("PlanePlotComponent");

        jMenuItem15.setAction(actionMap.get("addPlanePoint")); // NOI18N
        jMenuItem15.setName("jMenuItem15"); // NOI18N
        jMenu2.add(jMenuItem15);

        jMenuItem16.setAction(actionMap.get("addPlanePointSet")); // NOI18N
        jMenuItem16.setName("jMenuItem16"); // NOI18N
        jMenu2.add(jMenuItem16);

        jMenuItem17.setAction(actionMap.get("addPlanePolygonalPath")); // NOI18N
        jMenuItem17.setName("jMenuItem17"); // NOI18N
        jMenu2.add(jMenuItem17);

        jMenuItem18.setAction(actionMap.get("addPlaneGraph")); // NOI18N
        jMenuItem18.setName("jMenuItem18"); // NOI18N
        jMenu2.add(jMenuItem18);

        jSeparator4.setName("jSeparator4"); // NOI18N
        jMenu2.add(jSeparator4);

        jMenuItem19.setAction(actionMap.get("addCustomPlanePointSet")); // NOI18N
        jMenuItem19.setName("jMenuItem19"); // NOI18N
        jMenu2.add(jMenuItem19);

        jMenuItem20.setAction(actionMap.get("addCustomPlaneGraph")); // NOI18N
        jMenuItem20.setName("jMenuItem20"); // NOI18N
        jMenu2.add(jMenuItem20);

        menuBar.add(jMenu2);

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
    PlanePlotComponent canvas2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
}
