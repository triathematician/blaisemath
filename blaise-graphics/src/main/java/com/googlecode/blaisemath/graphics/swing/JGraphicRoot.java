package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2024 Elisha Peterson
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
import com.googlecode.blaisemath.graphics.GraphicMouseEvent;
import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.GraphicComposite;
import com.googlecode.blaisemath.graphics.GraphicUtils;
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
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Manages the entries on a {@link JGraphicComponent}.
 * The primary additional behavior implemented by {@code GraphicRoot}, beyond that of its parent
 * {@code GraphicComposite}, is listening to mouse events on the component and
 * generating {@link GraphicMouseEvent}s from them.
 * 
 * Subclasses might provide additional behavior such as (i) caching the shapes to be drawn
 * to avoid expensive recomputation, or (ii) sorting the shapes into an alternate draw order
 * (e.g. for projections from 3D to 2D).
 *
 * @author Elisha Peterson
 */
public final class JGraphicRoot extends GraphicComposite<Graphics2D> {

    /** Parent component upon which the graphics are drawn. */
    protected final JGraphicComponent owner;
    /** Context menu for actions on the graphics */
    protected final JPopupMenu popup = new JPopupMenu();
    
    /** Provides a pluggable way to generate mouse events */
    protected GraphicMouseEvent.Factory mouseFactory = new GraphicMouseEvent.Factory();
    /** Current owner of mouse events. Gets first priority for mouse events that occur. */
    private Graphic mouseGraphic = null;
    /** Tracks current mouse location */
    private Point2D mouseLoc = null;

    /** 
     * Construct a default instance
     * @param component the graphic root's component
     */
    public JGraphicRoot(JGraphicComponent component) {
        this.owner = checkNotNull(component);
        MouseHandler mh = new MouseHandler();
        this.owner.addMouseListener(mh);
        this.owner.addMouseMotionListener(mh);
        this.owner.setComponentPopupMenu(popup);
        
        // set up style
        setStyleContext(Styles.defaultStyleContext());
        style.put(Styles.FILL, Color.lightGray);
        style.put(Styles.STROKE, Color.black);
        
        // set up popup menu
        popup.addPopupMenuListener(new PopupMenuListener(){
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if (mouseLoc != null) {
                    popup.removeAll();
                    Set<Graphic<Graphics2D>> selected = owner.isSelectionEnabled() ? owner.getSelectionModel().getSelection() : null;
                    initContextMenu(popup, null, mouseLoc, null, selected, owner.canvas());
                }
            }
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                popup.removeAll();
            }
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                popup.removeAll();
            }
        });
    }
    
    //region PROPERTIES

    @Override
    public void setParent(GraphicComposite p) {
        checkArgument(p == null, "GraphicRoot cannot be added to another GraphicComposite");
    }

    @Override
    public void setStyleContext(StyleContext styleContext) {
        checkArgument(styleContext != null, "GraphicRoot must have a non-null StyleProvider!");
        super.setStyleContext(styleContext);
    }

    /**
     * Return current object used to generate mouse events.
     * @return mouse event factory
     */
    public GraphicMouseEvent.Factory getMouseEventFactory() {
        return mouseFactory;
    }

    /**
     * Modifies how mouse events are created.
     * @param factory responsible for generating mouse events
     */
    public void setMouseEventFactory(GraphicMouseEvent.Factory factory) {
        if (this.mouseFactory != factory) {
            this.mouseFactory = checkNotNull(factory);
        }
    }
    
    //endregion

    //region EVENTS

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

    /**
     * Create GraphicMouseEvent from given event.
     * @param e mouse event
     * @return associated graphic mouse event
     */
    private GraphicMouseEvent graphicMouseEvent(MouseEvent e) {
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
     * @param canvas target canvas
     */
    private void updateMouseGraphic(GraphicMouseEvent gme, boolean keepCurrent, Graphics2D canvas) {
        if (keepCurrent && mouseGraphic != null
                && GraphicUtils.isFunctional(mouseGraphic)
                && mouseGraphic.contains(gme.getGraphicLocation(), canvas)) {
            return;
        }
        Graphic nue = mouseGraphicAt(gme.getGraphicLocation(), canvas);
        if (!Objects.equal(mouseGraphic, nue)) {
            mouseExit(mouseGraphic, gme);
            mouseGraphic = nue;
            mouseEnter(mouseGraphic, gme);
        }
    }
    
    private void mouseEnter(Graphic mouseGraphic, GraphicMouseEvent event) {
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
    
    private void mouseExit(Graphic mouseGraphic, GraphicMouseEvent event) {
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

    /** Delegate for mouse events */
    private class MouseHandler implements MouseListener, MouseMotionListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            GraphicMouseEvent gme = graphicMouseEvent(e);
            updateMouseGraphic(gme, false, owner.canvas());
            if (mouseGraphic != null) {
                for (MouseListener l : mouseGraphic.getMouseListeners()) {
                    l.mouseClicked(gme);
                    if (gme.isConsumed()) {
                        return;
                    }
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            GraphicMouseEvent gme = graphicMouseEvent(e);
            mouseLoc = gme.getGraphicLocation();
            updateMouseGraphic(gme, false, owner.canvas());
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

        @Override
        public void mousePressed(MouseEvent e) {
            GraphicMouseEvent gme = graphicMouseEvent(e);
            updateMouseGraphic(gme, false, owner.canvas());
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

        @Override
        public void mouseDragged(MouseEvent e) {
            if (mouseGraphic != null) {
                GraphicMouseEvent gme = graphicMouseEvent(e);
                gme.setGraphicSource(mouseGraphic);
                for (MouseMotionListener l : mouseGraphic.getMouseMotionListeners()) {
                    l.mouseDragged(gme);
                    if (gme.isConsumed()) {
                        return;
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (mouseGraphic != null) {
                GraphicMouseEvent gme = graphicMouseEvent(e);
                gme.setGraphicSource(mouseGraphic);
                for (MouseListener l : mouseGraphic.getMouseListeners()) {
                    l.mouseReleased(gme);
                    if (gme.isConsumed()) {
                        return;
                    }
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // no behavior desired
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (mouseGraphic != null) {
                GraphicMouseEvent gme = graphicMouseEvent(e);
                mouseExit(mouseGraphic, gme);
            }
        }
    }

    //endregion

}
