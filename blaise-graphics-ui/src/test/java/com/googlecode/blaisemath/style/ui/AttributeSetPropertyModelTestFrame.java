package com.googlecode.blaisemath.style.ui;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2019 - 2025 Elisha Peterson
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


import com.google.common.collect.ImmutableMap;
import com.googlecode.blaisemath.firestarter.swing.RollupPanel;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.primitive.Marker;
import com.googlecode.blaisemath.primitive.ui.MarkerEditor;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.firestarter.property.PropertySheet;
import com.googlecode.blaisemath.firestarter.editor.EditorRegistration;

import java.awt.*;
import java.beans.PropertyEditorManager;

public class AttributeSetPropertyModelTestFrame extends javax.swing.JFrame {

    private com.googlecode.blaisemath.graphics.swing.JGraphicComponent canvas;
    private RollupPanel rollups;

    public AttributeSetPropertyModelTestFrame() {

        rollups = new RollupPanel();
        canvas = new com.googlecode.blaisemath.graphics.swing.JGraphicComponent();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(rollups, java.awt.BorderLayout.WEST);
        getContentPane().add(canvas, java.awt.BorderLayout.CENTER);
        
        EditorRegistration.registerEditors();
        PropertyEditorManager.registerEditor(Marker.class, MarkerEditor.class);

        AttributeSet as = Styles.fillStroke(Color.white, Color.red);
        AttributeSetPropertyModel m = new AttributeSetPropertyModel(as,
                ImmutableMap.of(Styles.FILL, Color.class, Styles.STROKE, Color.class, Styles.MARKER, Marker.class));
        PropertySheet ps = PropertySheet.forModel(m);
        rollups.add("AttributeSet Property Sheet Test", ps);
        ps.addPropertyChangeListener(e -> canvas.repaint());

        canvas.addGraphic(JGraphics.point(new Point(20, 20), as));
        
        pack();
    }

    public static void main(String[] args) {
        // expect to see property sheet with editable styles, although they may not trigger immediate updates
        java.awt.EventQueue.invokeLater(() -> new AttributeSetPropertyModelTestFrame().setVisible(true));
    }

}
