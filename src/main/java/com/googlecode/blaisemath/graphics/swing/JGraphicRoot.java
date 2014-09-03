/*
 * GraphicRoot.java
 * Created Jan 12, 2011
 */

package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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

import com.google.common.base.Objects;
import com.googlecode.blaisemath.graphics.core.GMouseEvent;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.core.GraphicUtils;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.googlecode.blaisemath.style.StyleContext;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * <p>
 *      Manages the entries on a {@link JGraphicComponent}.
 *      The primary additional behavior implemented by {@code GraphicRoot}, beyond that of its parent
 *      {@code GraphicComposite}, is listening to mouse events on the component and
 *      generating {@link GMouseEvent}s from them.
 * </p>
 * <p>
 *      Subclasses might provide additional behavior such as (i) caching the shapes to be drawn
 *      to avoid expensive recomputation, or (ii) sorting the shapes into an alternate draw order
 *      (e.g. for projections from 3D to 2D).
 * </p>
 * 
 * @author Elisha
 */
public final class JGraphicRoot extends GraphicComposite<Graphics2D> implements MouseListener, MouseMotionListener {

    /** Parent component upon which the graphics are drawn. */
    protected final JGraphicComponent owner;
    /** Context menu for actions on the graphics */
    protected final JPopupMenu popup = new JPopupMenu();
    /** Provides a pluggable way to generate mouse events */
    @Nonnull 
    protected GMouseEvent.Factory mouseFactory = new GMouseEvent.Factory();

    /** Current owner of mouse events. Gets first priority for mouse events that occur. */
    private transient Graphic mouseGraphic = null;
    /** Tracks current mouse location */
    private transient Point2D mouseLoc = null;

    /** 
     * Construct a default instance
     * @param component the graphic root's component
     */
    public JGraphicRoot(JGraphicComponent component) {
        this.owner = checkNotNull(component);
        this.owner.addMouseListener(this);
        this.owner.addMouseMotionListener(this);
        this.owner.setComponentPopupMenu(popup);
        
        // set up style
        setStyleContext(Styles.defaultStyleContext());
        style.put(Styles.FILL, Color.lightGray);
        style.put(Styles.STROKE, Color.black);
        
        // set up popup menu
        popup.addPopupMenuListener(new PopupMenuListener(){
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if (mouseLoc != null) {
                    popup.removeAll();
                    Set<Graphic> selected = owner.isSelectionEnabled() ? owner.getSelectionModel().getSelection() : null;
                    initContextMenu(popup, null, mouseLoc, null, selected);
                    if (popup.getComponentCount() == 0) {
                        // cancel popup
                    }
                }
            }
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                popup.removeAll();
            }
            public void popupMenuCanceled(PopupMenuEvent e) {
                popup.removeAll();
            }
        });
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    @Override
    public void setParent(GraphicComposite p) {
        checkArgument(p == null, "GraphicRoot cannot be added to another GraphicComposite");
    }

    @Override
    public void setStyleContext(StyleContext rend) {
        checkArgument(rend != null, "GraphicRoot must have a non-null StyleProvider!");
        super.setStyleContext(rend);
    }

    /**
     * Return current object used to generate mouse events.
     * @return mouse event factory
     */
    public GMouseEvent.Factory getMouseEventFactory() {
        return mouseFactory;
    }

    /**
     * Modifies how mouse events are created.
     * @param factory responsible for generating mouse events
     */
    public void setMouseEventFactory(GMouseEvent.Factory factory) {
        if (this.mouseFactory != factory) {
            this.mouseFactory = checkNotNull(factory);
        }
    }
    
    //</editor-fold>


    //
    // EVENT HANDLING
    //

    @Override
    protected void fireGraphicChanged() {
        graphicChanged(this);
    }

    @Override
    public void graphicChanged(Graphic source) {
        if (owner != null) {
            owner.repaint();
        }
    }


    //<editor-fold defaultstate="collapsed" desc="MOUSE HANDLING">
    //
    // MOUSE HANDLING
    // this code handles translation of mouse events on the component
    //   to mouse events on the graphics
    //

    /**
     * Create GraphicMouseEvent from given event.
     * @param mouseEvent mouse event
     * @return associated graphic mouse event
     */
    private GMouseEvent graphicMouseEvent(MouseEvent e) {
        Point2D localPoint = e.getPoint();
        if (owner.getInverseTransform() != null) {
            localPoint = owner.getInverseTransform().transform(localPoint, null);
        }
        return mouseFactory.createEvent(e, localPoint, this);
    }
    
    /**
     * Change current owner of mouse events.
     * @param gme graphic mouse event
     * @param keepCurrent whether to maintain current selection even if it's behind another graphic
     */
    private void updateMouseGraphic(GMouseEvent gme, boolean keepCurrent) {
        if (keepCurrent && mouseGraphic != null
                && GraphicUtils.isFunctional(mouseGraphic)
                && mouseGraphic.contains(gme.getGraphicLocation())) {
            return;
        }
        Graphic nue = mouseGraphicAt(gme.getGraphicLocation());
        if (!Objects.equal(mouseGraphic, nue)) {
            mouseExit(mouseGraphic, gme);
            mouseGraphic = nue;
            mouseEnter(mouseGraphic, gme);
        }
    }
    
    private void mouseEnter(Graphic mouseGraphic, GMouseEvent event) {
        if (mouseGraphic != null) {
            event.setGraphicSource(mouseGraphic);
            for (MouseListener l : mouseGraphic.getMouseListeners()) {
                l.mouseEntered(event);
                if (event.isConsumed()) {
                    return;
                }
            }
        }
    }
    
    private void mouseExit(Graphic mouseGraphic, GMouseEvent event) {
        if (mouseGraphic != null) {
            event.setGraphicSource(mouseGraphic);
            for (MouseListener l : mouseGraphic.getMouseListeners()) {
                l.mouseExited(event);
                if (event.isConsumed()) {
                    return;
                }
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        GMouseEvent gme = graphicMouseEvent(e);
        updateMouseGraphic(gme, false);
        if (mouseGraphic != null) {
            for (MouseListener l : mouseGraphic.getMouseListeners()) {
                l.mouseClicked(gme);
                if (gme.isConsumed()) {
                    return;
                }
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
        GMouseEvent gme = graphicMouseEvent(e);
        mouseLoc = gme.getGraphicLocation();
        updateMouseGraphic(gme, false);
        if (mouseGraphic != null) {
            gme.setGraphicSource(mouseGraphic);
            for (MouseMotionListener l : mouseGraphic.getMouseMotionListeners()) {
                l.mouseMoved(gme);
                if (gme.isConsumed()) {
                    return;
                }
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        GMouseEvent gme = graphicMouseEvent(e);
        updateMouseGraphic(gme, false);
        if (mouseGraphic != null) {
            gme.setGraphicSource(mouseGraphic);
            for (MouseListener l : mouseGraphic.getMouseListeners()) {
                l.mousePressed(gme);
                if (gme.isConsumed()) {
                    return;
                }
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (mouseGraphic != null) {
            GMouseEvent gme = graphicMouseEvent(e);
            gme.setGraphicSource(mouseGraphic);
            for (MouseMotionListener l : mouseGraphic.getMouseMotionListeners()) {
                l.mouseDragged(gme);
                if (gme.isConsumed()) {
                    return;
                }
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (mouseGraphic != null) {
            GMouseEvent gme = graphicMouseEvent(e);
            gme.setGraphicSource(mouseGraphic);
            for (MouseListener l : mouseGraphic.getMouseListeners()) {
                l.mouseReleased(gme);
                if (gme.isConsumed()) {
                    return;
                }
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
        // no behavior desired
    }

    public void mouseExited(MouseEvent e) {
        if (mouseGraphic != null) {
            GMouseEvent gme = graphicMouseEvent(e);
            mouseExit(mouseGraphic, gme);
        }
    }

    //</editor-fold>

}
