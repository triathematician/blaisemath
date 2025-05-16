package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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

import com.googlecode.blaisemath.graphics.swing.render.ShapeRenderer;
import com.googlecode.blaisemath.util.swing.CanvasPainter;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.StyleHints;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.SetSelectionModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Set;

/**
 * Mouse handler that enables selection on a composite graphic object.
 * Control must be down for any selection capability.
 * @param <G> type of render canvas
 * @author Elisha Peterson
 */
public final class JGraphicSelectionHandler<G> extends MouseAdapter implements CanvasPainter<Graphics2D> {

    /** Whether selector is enabled */
    private boolean enabled = true;
    /** Determines which objects can be selected */
    private final JGraphicComponent owner;
    /** Model of selected items */
    private final SetSelectionModel<Graphic<Graphics2D>> selection = new SetSelectionModel<>();
    
    /** Style for drawing selection box */
    private AttributeSet selectionBoxStyle = Styles.fillStroke(
            new Color(128,128,255,32), new Color(0,0,128,64));

    private Point pressPt;
    private Point dragPt;
    private Rectangle2D.Double selectionBox = null;
    
    private static boolean MAC;

    /** 
     * Initialize for specified component
     * @param owner the component for handling
     */
    public JGraphicSelectionHandler(JGraphicComponent owner) {
        this.owner = owner;

        // highlight updates
        selection.addPropertyChangeListener(evt -> {
            Set<Graphic> old = (Set<Graphic>) evt.getOldValue();
            Set<Graphic> nue = (Set<Graphic>) evt.getNewValue();
            Sets.difference(old, nue).forEach(g -> g.setStyleHint(StyleHints.SELECTED_HINT, false));
            Sets.difference(nue, old).forEach(g -> g.setStyleHint(StyleHints.SELECTED_HINT, true));
        });
        
        detectMac();
    }

    //region PROPERTIES

    public SetSelectionModel<Graphic<Graphics2D>> getSelectionModel() {
        return selection;
    }

    public boolean isSelectionEnabled() {
        return enabled;
    }

    public void setSelectionEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (!enabled) {
                selection.setSelection(Collections.emptySet());
            }
        }
    }

    public AttributeSet getStyle() {
        return selectionBoxStyle;
    }

    public void setStyle(AttributeSet style) {
        this.selectionBoxStyle = checkNotNull(style);
    }

    //endregion

    @Override
    public void paint(Component component, Graphics2D canvas) {
        if (enabled && selectionBox != null && selectionBox.width > 0 && selectionBox.height > 0) {
            ShapeRenderer.getInstance().render(selectionBox, selectionBoxStyle, canvas);
        }
    }

    //region EVENTS

    @Override
    public void mouseMoved(MouseEvent e) {
        if (e.isConsumed()) {
            return;
        }
        Graphic g = owner.selectableGraphicAt(e.getPoint());
        Graphic gAll = owner.functionalGraphicAt(e.getPoint());
        if (gAll == null) {
            // reset to default if there is no active mouse graphic
            owner.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else if (g != null) {
            // identify selectable graphics when you mouse over them
            owner.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!enabled || !(e.getButton() == MouseEvent.BUTTON1) || e.isConsumed()) {
            return;
        }
        if (!isSelectionEvent(e)) {
            selection.setSelection(Collections.emptySet());
            return;
        }
        Graphic<Graphics2D> g = owner.selectableGraphicAt(e.getPoint());
        if (g == null) {
            selection.setSelection(Collections.emptySet());
        } else if (e.isShiftDown()) {
            selection.deselect(g);
        } else if (e.isAltDown()) {
            selection.select(g);
        } else {
            selection.toggleSelection(g);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!enabled || e.isConsumed() || !(e.getButton()==MouseEvent.BUTTON1) || !isSelectionEvent(e)) {
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
        if (!enabled || e.isConsumed() || selectionBox == null || pressPt == null) {
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
        if (!enabled || e.isConsumed() || selectionBox == null || pressPt == null) {
            return;
        }
        Point releasePt = e.getPoint();
        if (owner.getInverseTransform() == null) {
            selectionBox.setFrameFromDiagonal(pressPt, releasePt);
        } else {
            selectionBox.setFrameFromDiagonal(
                    owner.toGraphicCoordinate(pressPt),
                    owner.toGraphicCoordinate(releasePt));
        }
        if (selectionBox.getWidth() > 0 && selectionBox.getHeight() > 0) {
            Set<Graphic<Graphics2D>> gg = owner.getGraphicRoot().selectableGraphicsIn(selectionBox, owner.canvas());
            if (e.isShiftDown()) {
                Set<Graphic<Graphics2D>> res = Sets.newHashSet(selection.getSelection());
                res.removeAll(gg);
                gg = res;
            } else if (e.isAltDown()) {
                gg.addAll(selection.getSelection());
            }
            selection.setSelection(gg);
        }
        selectionBox = null;
        pressPt = null;
        dragPt = null;
        owner.repaint();
        e.consume();
    }
    
    //endregion

    private static void detectMac() {
        String os = System.getProperty("os.name").toLowerCase();
        MAC = os.contains("mac");
    }
    
    private static boolean isSelectionEvent(InputEvent e) {
        return MAC ? e.isMetaDown() : e.isControlDown();
    }
}
