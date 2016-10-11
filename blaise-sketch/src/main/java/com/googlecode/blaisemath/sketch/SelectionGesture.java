/**
 * SelectGesture.java
 * Created Jan 2015
 */
package com.googlecode.blaisemath.sketch;

/*
 * #%L
 * BlaiseGestures
 * --
 * Copyright (C) 2014 - 2016 Elisha Peterson
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


import com.google.common.collect.Sets;
import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.googlecode.blaisemath.gesture.MouseGestureSupport;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.JGraphicSelectionHandler;
import com.googlecode.blaisemath.graphics.swing.ShapeRenderer;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.SetSelectionModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Set;

/**
 * <p>
 *   Supports selection of graphics, including clicks to select/de-select graphics,
 *   and press/drag/release to draw a selection box around graphics.
 * </p>
 * @see JGraphicSelectionHandler
 * @author elisha
 */
public final class SelectionGesture extends MouseGestureSupport<JGraphicComponent> {
    
    private static final Boolean MAC = System.getProperty("os.name").toLowerCase().contains("mac");
    
    private final SetSelectionModel<Graphic<Graphics2D>> selectionModel;
    
    /** Style for drawing selection box */
    private static final AttributeSet SELECTION_BOX_STYLE = Styles.fillStroke(
            new Color(128,128,255,32), new Color(0,0,128,64)).immutable();

    private transient Point pressPt;
    private transient Point dragPt;
    private transient Rectangle2D.Double selectionBox = null;
    
    public SelectionGesture(GestureOrchestrator<JGraphicComponent> orchestrator) {
        super(orchestrator, "Select", "Select graphics");
        selectionModel = view.getSelectionModel();
    }
    
    @Override
    public String toString() {
        return "select";
    }

    @Override
    public void paint(Graphics2D g) {
        if (selectionBox != null && selectionBox.width > 0 && selectionBox.height > 0) {
            ShapeRenderer.getInstance()
                    .render(selectionBox, SELECTION_BOX_STYLE, g);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (e.isConsumed()) {
            return;
        }
        super.mouseMoved(e);
        Graphic gfc = view.selectableGraphicAt(e.getPoint());
        if (gfc != null) {
            orchestrator.setComponentCursor(Cursor.HAND_CURSOR);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isConsumed()) {
            return;
        }
        Graphic<Graphics2D> gfc = view.selectableGraphicAt(e.getPoint());
        if (gfc == null) {
            selectionModel.clearSelection();
        } else if (isAddEvent(e)) {
            selectionModel.select(gfc);
        } else if (isRemoveEvent(e)) {
            selectionModel.deselect(gfc);
        } else {
            selectionModel.setSelection(Collections.singleton(gfc));
        }
        e.consume();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isConsumed()) {
            return;
        }
        pressPt = e.getPoint();
        if (selectionBox == null) {
            selectionBox = new Rectangle2D.Double();
        }
        selectionBox.setFrameFromDiagonal(pressPt, pressPt);
        e.consume();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.isConsumed() || selectionBox == null || pressPt == null) {
            return;
        }
        dragPt = e.getPoint();
        selectionBox.setFrameFromDiagonal(pressPt, dragPt);
        if (e.getSource() instanceof Component) {
            ((Component)e.getSource()).repaint();
        }
        e.consume();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isConsumed() || selectionBox == null || pressPt == null) {
            return;
        }
        Point releasePt = e.getPoint();
        if (view.getInverseTransform() == null) {
            selectionBox.setFrameFromDiagonal(pressPt, releasePt);
        } else {
            selectionBox.setFrameFromDiagonal(
                    view.toGraphicCoordinate(pressPt),
                    view.toGraphicCoordinate(releasePt));
        }
        if (selectionBox.getWidth() > 0 && selectionBox.getHeight() > 0) {
            Set<Graphic<Graphics2D>> gfcs = view.getGraphicRoot().selectableGraphicsIn(selectionBox);
            if (isRemoveEvent(e)) {
                Set<Graphic<Graphics2D>> res = Sets.newHashSet(selectionModel.getSelection());
                res.removeAll(gfcs);
                gfcs = res;
            } else if (isAddEvent(e)) {
                gfcs.addAll(selectionModel.getSelection());
            }
            selectionModel.setSelection(gfcs);
        }
        selectionBox = null;
        pressPt = null;
        dragPt = null;
        view.repaint();
        e.consume();
    }
    
    private boolean isAddEvent(MouseEvent e) {
        return e.isShiftDown() && !(MAC ? e.isMetaDown() : e.isControlDown());
    }
    
    private boolean isRemoveEvent(MouseEvent e) {
        return e.isShiftDown() && (MAC ? e.isMetaDown() : e.isControlDown());
    }
    
}
