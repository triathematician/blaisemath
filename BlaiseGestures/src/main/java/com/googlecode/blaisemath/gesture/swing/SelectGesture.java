/*
 * Copyright 2015 elisha.
 *
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
 */
package com.googlecode.blaisemath.gesture.swing;

/*
 * #%L
 * BlaiseGestures
 * --
 * Copyright (C) 2015 Elisha Peterson
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


import static com.google.common.base.Preconditions.checkArgument;
import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.googlecode.blaisemath.gesture.MouseGestureSupport;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.JGraphicSelectionHandler;
import com.googlecode.blaisemath.util.SetSelectionModel;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

/**
 * Gesture that allows for selection of graphics.
 * @see JGraphicSelectionHandler
 * @author elisha
 */
public class SelectGesture extends MouseGestureSupport {
    
    private final JGraphicComponent view;
    private final SetSelectionModel<Graphic<Graphics2D>> selectionModel;

    public SelectGesture(GestureOrchestrator orchestrator) {
        super(orchestrator, "Select", "Select graphics");
        
        checkArgument(orchestrator.getComponent() instanceof JGraphicComponent, 
                "Orchestrator must use a JGraphicComponent");
        view = (JGraphicComponent) orchestrator.getComponent();
        selectionModel = view.getSelectionModel();
        setConsuming(false);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        Graphic gfc = view.graphicAt(e.getPoint());
        if (gfc != null && gfc.isSelectionEnabled()) {
            orchestrator.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            orchestrator.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Graphic gfc = view.graphicAt(e.getPoint());
        if (gfc != null && gfc.isSelectionEnabled()) {
            selectionModel.toggleSelection(gfc);
        } else {
            selectionModel.clearSelection();
        }
    }
    
}
