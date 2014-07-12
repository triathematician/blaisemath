/**
 * GraphicSelector.java
 * Created Aug 1, 2012
 */
package com.googlecode.blaisemath.graphics;

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

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.style.ShapeStyleBasic;
import com.googlecode.blaisemath.style.context.StyleHintSet;
import com.googlecode.blaisemath.util.CanvasPainter;
import com.googlecode.blaisemath.util.SetSelectionModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Set;

/**
 * <p>
 *  Mouse handler that enables selection on a composite graphic object.
 *  Control must be down for any selection capability.
 * </p>
 * @author elisha
 */
public final class GraphicSelectionHandler extends MouseAdapter implements CanvasPainter {

    /** Whether selector is enabled */
    private boolean enabled = true;
    /** Determines which objects can be selected */
    private final GraphicComponent component;
    /** Model of selected items */
    private final SetSelectionModel<Graphic> selection = new SetSelectionModel<Graphic>();
    
    /** Style for drawing selection box */
    private ShapeStyleBasic selectionBoxStyle = new ShapeStyleBasic()
            .fill(new Color(128,128,255,32)).stroke(new Color(0,0,128,64));

    private transient Point pressPt;
    private transient Point dragPt;
    private transient Rectangle selectionBox = null;

    /** 
     * Initialize for specified component
     * @param domain 
     */
    public GraphicSelectionHandler(GraphicComponent domain) {
        this.component = domain;

        // highlight updates
        selection.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Set<Graphic> old = (Set<Graphic>) evt.getOldValue();
                Set<Graphic> nue = (Set<Graphic>) evt.getNewValue();
                for (Graphic g : Sets.difference(old, nue)) {
                    g.getStyleHints().remove(StyleHintSet.SELECTED_HINT);
                }
                for (Graphic g : Sets.difference(nue, old)) {
                    g.getStyleHints().add(StyleHintSet.SELECTED_HINT);
                }
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public boolean isSelectionEnabled() {
        return enabled;
    }

    public void setSelectionEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (!enabled) {
                selection.setSelection(Collections.<Graphic>emptySet());
            }
        }
    }

    public ShapeStyleBasic getStyle() {
        return selectionBoxStyle;
    }

    public SetSelectionModel<Graphic> getSelectionModel() {
        return selection;
    }

    public void setStyle(ShapeStyleBasic style) {
        this.selectionBoxStyle = checkNotNull(style);
    }

    //</editor-fold>


    @Override
    public void paint(Component component, Graphics2D canvas) {
        if (enabled && selectionBox != null && selectionBox.width > 0 && selectionBox.height > 0) {
            selectionBoxStyle.draw(selectionBox, canvas);
        }
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="MOUSE GESTURE HANDLERS">

    @Override
    public void mouseMoved(MouseEvent e) {
        if (e.isConsumed()) {
            return;
        }
        Graphic g = component.selectableGraphicAt(e.getPoint());
        component.setCursor(g == null ? Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
                : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!enabled || !(e.getButton()==MouseEvent.BUTTON1) || e.isConsumed()) {
            return;
        }
        if (!e.isControlDown()) {
            selection.setSelection(Collections.<Graphic>emptySet());
            return;
        }
        Graphic g = component.selectableGraphicAt(e.getPoint());
        if (g == null) {
            selection.setSelection(Collections.<Graphic>emptySet());
        } else if (e.isShiftDown()) {
            selection.removeSelection(g);
        } else if (e.isAltDown()) {
            selection.addSelection(g);
        } else {
            selection.toggleSelection(g);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!enabled || e.isConsumed() || !(e.getButton()==MouseEvent.BUTTON1) || !e.isControlDown()) {
            return;
        }
        pressPt = e.getPoint();
        if (selectionBox == null) {
            selectionBox = new Rectangle();
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
        if (component.getInverseTransform() == null) {
            selectionBox.setFrameFromDiagonal(pressPt, releasePt);
        } else {
            selectionBox.setFrameFromDiagonal(
                    component.toGraphicCoordinate(pressPt),
                    component.toGraphicCoordinate(releasePt));
        }
        if (selectionBox.getWidth() > 0 && selectionBox.getHeight() > 0) {
            Set<Graphic> gg = component.getGraphicRoot().selectableGraphicsIn(selectionBox);
            if (e.isShiftDown()) {
                Set<Graphic> res = Sets.newHashSet(selection.getSelection());
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
        releasePt = null;
        e.consume();
    }
    
    //</editor-fold>

}
