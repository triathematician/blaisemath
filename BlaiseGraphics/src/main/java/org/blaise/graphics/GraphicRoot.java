/*
 * GraphicRoot.java
 * Created Jan 12, 2011
 */

package org.blaise.graphics;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.blaise.style.StyleProvider;
import org.blaise.style.StyleUtils.DefaultStyleProvider;
import org.blaise.style.VisibilityHint;

/**
 * <p>
 *      Manages the entries on a {@link GraphicComponent}.
 *      The primary additional behavior implemented by {@code GraphicRoot}, beyond that of its parent
 *      {@code GraphicComposite}, is listening to mouse events on the component and
 *      generating {@link GraphicMouseEvent}s from them.
 * </p>
 * <p>
 *      Subclasses might provide additional behavior such as (i) caching the shapes to be drawn
 *      to avoid expensive recomputation, or (ii) sorting the shapes into an alternate draw order
 *      (e.g. for projections from 3D to 2D).
 * </p>
 *
 * @author Elisha
 */
public class GraphicRoot extends GraphicComposite implements MouseListener, MouseMotionListener {

    /** Parent component upon which the graphics are drawn. */
    protected JComponent owner;
    /** Context menu for actions on the graphics */
    protected JPopupMenu popup = new JPopupMenu();
    /** Provides a pluggable way to generate mouse events */
    protected GraphicMouseEvent.Factory mouseFactory = new GraphicMouseEvent.Factory();

    /** Construct a default instance */
    public GraphicRoot() {
        setStyleProvider(new DefaultStyleProvider());
        popup.addPopupMenuListener(new PopupMenuListener(){
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if (mouseLoc != null) {
                    popup.removeAll();
                    initialize(popup, mouseLoc);
                }
            }
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });
    }

    @Override
    public String toString() {
        return "Graphics Canvas";
    }

    /**
     * Sets the component associated with the graphic tree.
     * Sets up mouse handling, and allows repainting to occur when the graphics change.
     * @param c the component
     */
    void initComponent(JComponent c) {
        if (this.owner != c) {
            if (this.owner != null) {
                this.owner.removeMouseListener(this);
                this.owner.removeMouseMotionListener(this);
                this.owner.setComponentPopupMenu(null);
            }
            this.owner = c;
            if (this.owner != null) {
                this.owner.addMouseListener(this);
                this.owner.addMouseMotionListener(this);
                this.owner.setComponentPopupMenu(popup);
            }
        }
    }
    
    //
    // PROPERTIES
    //

    @Override
    public final void setStyleProvider(StyleProvider rend) {
        if (rend == null) {
            throw new IllegalArgumentException("GraphicRoot must have a non-null StyleProvider!");
        }
        super.setStyleProvider(rend);
    }


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
        this.mouseFactory = factory;
    }
    
    /** Current owner of mouse events. Gets first priority for mouse events that occur. */
    private transient Graphic mouseGraphic;

    private void updateMouseGraphic(MouseEvent e, boolean keepCurrent) {
        if (keepCurrent && mouseGraphic != null 
                && !mouseGraphic.getVisibilityHints().contains(VisibilityHint.Hidden) 
                && mouseGraphic.contains(e.getPoint()))
            return;
        Graphic nue = mouseGraphicAt(e.getPoint());
        if (mouseGraphic != nue) {
            if (mouseGraphic != null) {
                GraphicMouseEvent gme = mouseFactory.createEvent(e, mouseGraphic);
                for (MouseListener l : mouseGraphic.getMouseListeners()) {
                    l.mouseExited(gme);
                    if (gme.isConsumed())
                        return;
                }
            }
            mouseGraphic = nue;
            if (mouseGraphic != null) {
                GraphicMouseEvent gme = mouseFactory.createEvent(e, mouseGraphic);
                for (MouseListener l : mouseGraphic.getMouseListeners()) {
                    l.mouseEntered(gme);
                    if (gme.isConsumed())
                        return;
                }
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        updateMouseGraphic(e, false);
        if (mouseGraphic != null) {
            GraphicMouseEvent gme = mouseFactory.createEvent(e, mouseGraphic);
            for (MouseListener l : mouseGraphic.getMouseListeners()) {
                l.mouseClicked(gme);
                if (gme.isConsumed())
                    return;
            }
        }
    }

    /** Tracks current mouse location */
    Point mouseLoc;
    
    public void mouseMoved(MouseEvent e) {
        mouseLoc = e.getPoint();
        updateMouseGraphic(e, false);
        if (mouseGraphic != null) {
            owner.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            GraphicMouseEvent gme = mouseFactory.createEvent(e, mouseGraphic);
            for (MouseMotionListener l : mouseGraphic.getMouseMotionListeners()) {
                l.mouseMoved(gme);
                if (gme.isConsumed())
                    return;
            }
        } else
            owner.setCursor(Cursor.getDefaultCursor());
    }

    public void mousePressed(MouseEvent e) {
        updateMouseGraphic(e, false);
        if (mouseGraphic != null) {
            GraphicMouseEvent gme = mouseFactory.createEvent(e, mouseGraphic);
            for (MouseListener l : mouseGraphic.getMouseListeners()) {
                l.mousePressed(gme);
                if (gme.isConsumed())
                    return;
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (mouseGraphic != null) {
            GraphicMouseEvent gme = mouseFactory.createEvent(e, mouseGraphic);
            for (MouseMotionListener l : mouseGraphic.getMouseMotionListeners()) {
                l.mouseDragged(gme);
                if (gme.isConsumed())
                    return;
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (mouseGraphic != null) {
            GraphicMouseEvent gme = mouseFactory.createEvent(e, mouseGraphic);
            for (MouseListener l : mouseGraphic.getMouseListeners()) {
                l.mouseReleased(gme);
                if (gme.isConsumed())
                    return;
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
        if (mouseGraphic != null) {
            GraphicMouseEvent gme = mouseFactory.createEvent(e, mouseGraphic);
            for (MouseListener l : mouseGraphic.getMouseListeners()) {
                l.mouseExited(gme);
                if (gme.isConsumed())
                    return;
            }
        }
    }
    
    //</editor-fold>
    
}
