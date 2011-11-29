/*
 * GraphicRoot.java
 * Created Jan 12, 2011
 */

package org.bm.blaise.graphics;

import org.bm.blaise.style.StyleProvider;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import org.bm.blaise.style.StyleUtils.DefaultStyleProvider;

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
public class GraphicRoot extends GraphicComposite
        implements MouseListener, MouseMotionListener {

    /** Parent component upon which the graphics are drawn. */
    protected Component component;
    
    /** Default mouse listener */
    protected MouseListener defaultListener;
    /** Provides a pluggable way to generate mouse events */
    protected GraphicMouseEvent.Factory mouseFactory = new GraphicMouseEvent.Factory();

    /** Construct a default instance */
    public GraphicRoot() {
        setStyleProvider(new DefaultStyleProvider());
    }
    
    /**
     * Sets the component associated with the graphic tree.
     * Sets up mouse handling, and allows repainting to occur when the graphics change.
     * @param c the component
     */
    void initComponent(Component c) {
        if (this.component != c) {
            if (this.component != null) {
                this.component.removeMouseListener(this);
                this.component.removeMouseMotionListener(this);
            }
            this.component = c;
            if (this.component != null) {
                c.addMouseListener(this);
                c.addMouseMotionListener(this);
            }
        }
    }
    
    //
    // PROPERTIES
    //
    
    @Override
    public final void setStyleProvider(StyleProvider rend) { 
        if (rend == null)
            throw new IllegalArgumentException("GraphicRoot must have a non-null StyleProvider!");
        super.setStyleProvider(rend);
    }

    /** 
     * Returns default mouse listener 
     * @return default listener, or null if there is none
     */
    public MouseListener getDefaultMouseListener() { 
        return defaultListener; 
    }
    
    /** 
     * Sets default mouse listener 
     * @param l the default listener
     */
    public void setDefaultMouseListener(MouseListener l) { 
        defaultListener = l; 
    }

    /** 
     * Modifies how mouse events are created. 
     * @param factory responsible for generating mouse events
     */
    public void setMouseEventFactory(GraphicMouseEvent.Factory factory) {
        this.mouseFactory = factory;
    }
    
    
    //
    // EVENT HANDLING
    //

    @Override
    protected void fireGraphicChanged() {
        if (component != null)
            component.repaint();
    }
    
    @Override
    public void fireAppearanceChanged() {
        if (component != null)
            component.repaint();
    }


    //<editor-fold defaultstate="collapsed" desc="MOUSE HANDLING">
    //
    // MOUSE HANDLING
    //
    
    /** Stores the current shape */
    transient Graphic cur = null;
    /** Stores the current mouse handler */
    transient GraphicMouseListener handler = null;

    /**
     * Updates selected shape for current mouse event
     * @param e the new mouse event
     */
    private void updateCur(MouseEvent e) {
        Point p = e.getPoint();
        Graphic s = mouseListenerGraphicAt(p);
        GraphicMouseListener sHandler = s == null ? null : s.getMouseListener(p);
        if (cur != s || handler != sHandler) {
            if (handler != null)
                handler.mouseExited(mouseFactory.createEvent(cur, p));
            cur = s;
            handler = cur == null ? null : cur.getMouseListener(p);
            if (handler != null)
                handler.mouseEntered(mouseFactory.createEvent(cur, p));
        }
    }

    /**
     * Forwards the click event to a graphic if possible; otherwise uses a default handler.
     * @param e the mouse event
     */
    public void mouseClicked(MouseEvent e) {
        updateCur(e);
        if (handler != null) {
            handler.mouseClicked(mouseFactory.createEvent(cur, e.getPoint()));
            e.consume();
        } else if (defaultListener != null)
            defaultListener.mouseClicked(e);
    }


    /**
     * Forwards the move event to a graphic if possible (possibly generating an enter
     * or exit event on that graphic); otherwise uses a default handler.
     * @param e the mouse event
     */
    public void mouseMoved(MouseEvent e) {
        updateCur(e);
        if (handler != null)
            component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        else if (defaultListener == null || !(defaultListener instanceof MouseMotionListener))
            component.setCursor(Cursor.getDefaultCursor());
        else
            ((MouseMotionListener)defaultListener).mouseMoved(e);
    }


    /**
     * Forwards the press event to a graphic if possible; otherwise uses a default handler.
     * @param e the mouse event
     */
    public void mousePressed(MouseEvent e) {
        updateCur(e);
        if (handler != null) {
            handler.mousePressed(mouseFactory.createEvent(cur, e.getPoint()));
            e.consume();
        } else if (defaultListener != null)
            defaultListener.mousePressed(e);
    }


    /**
     * Forwards the drag event to a graphic if possible; otherwise uses a default handler.
     * @param e the mouse event
     */
    public void mouseDragged(MouseEvent e) {
        if (handler != null) {
            handler.mouseDragged(mouseFactory.createEvent(cur, e.getPoint()));
            e.consume();
        } else if (defaultListener != null && defaultListener instanceof MouseMotionListener)
            ((MouseMotionListener)defaultListener).mouseDragged(e);
    }


    /**
     * Forwards the release event to a graphic if possible; otherwise uses a default handler.
     * @param e the mouse event
     */
    public void mouseReleased(MouseEvent e) {
        if (handler != null) {
            handler.mouseReleased(mouseFactory.createEvent(cur, e.getPoint()));
            e.consume();
        } else if (defaultListener != null)
            defaultListener.mouseReleased(e);
    }

    /**
     * Forwards the enter event to a default handler.
     * @param e the mouse event
     */
    public void mouseEntered(MouseEvent e) {
        if (defaultListener != null)
            defaultListener.mouseEntered(e);
    }

    /**
     * Forwards the exit event to a default handler.
     * @param e the mouse event
     */
    public void mouseExited(MouseEvent e) {
        if (handler != null)
            handler.mouseExited(mouseFactory.createEvent(cur, e.getPoint()));
        if (defaultListener != null)
            defaultListener.mouseExited(e);
    }
    //</editor-fold>
    
}
