/*
 * GraphicCache.java
 * Created Jan 12, 2011
 */

package org.bm.blaise.graphics;

import org.bm.blaise.graphics.renderer.GraphicRendererProvider;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Caches shapes that are ready to be plotted. Responsible for managing the draw
 * order of the shapes. Also directs mouse events to interested listeners.
 * 
 * @author Elisha
 */
public class GraphicCache extends CompositeGraphicEntry
        implements MouseListener, MouseMotionListener {

    /** Parent component */
    GraphicComponent c;
    
    /** Default mouse listener */
    MouseListener defaultListener;
    /** Provides a pluggable way to generate mouse events */
    protected GraphicMouseEvent.Factory mouseFactory = new GraphicMouseEvent.Factory();

    //
    // CONSTRUCTOR
    //

    /** Construct a default instance */
    public GraphicCache() {
        rProvider = GraphicRendererProvider.DEFAULT;
    }

    //
    // PROPERTIES
    //

    /** Returns default mouse listener */
    public MouseListener getDefaultMouseListener() { return defaultListener; }
    /** Sets default mouse listener */
    public void setDefaultMouseListener(MouseListener l) { defaultListener = l; }

    //
    // MOUSE EVENT HANDLING
    //

    /** Stores the current shape */
    transient GraphicEntry cur = null;
    /** Stores the current mouse handler */
    transient GraphicMouseListener handler = null;

    /**
     * Sets current shape under mouse cursor
     * @param s the shape entry
     * @param p the point for the entry
     */
    private void setCur(GraphicEntry s, Point p) {
        if (cur != s) {
            if (handler != null)
                handler.mouseExited(mouseFactory.createEvent(cur, p));
            cur = s;
            handler = cur == null ? null : cur.getMouseListener(p);
            if (handler != null)
                handler.mouseEntered(mouseFactory.createEvent(cur, p));
        }
    }

    /**
     * Updates selected shape for current mouse event
     * @param e the new mouse event
     */
    private void updateCur(MouseEvent e) {
        Point p = e.getPoint();
        GraphicEntry s = entryAt(p, rProvider);
        setCur(s, p);
    }

    public void mouseClicked(MouseEvent e) {
        updateCur(e);
        if (handler != null) {
            handler.mouseClicked(mouseFactory.createEvent(cur, e.getPoint()));
            e.consume();
        } else if (defaultListener != null)
            defaultListener.mouseClicked(e);
    }

    public void mouseMoved(MouseEvent e) {
        updateCur(e);
        if (handler != null)
            c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        else if (defaultListener == null || !(defaultListener instanceof MouseMotionListener))
            c.setCursor(Cursor.getDefaultCursor());
        else
            ((MouseMotionListener)defaultListener).mouseMoved(e);
    }

    public void mousePressed(MouseEvent e) {
        updateCur(e);
        if (handler != null) {
            handler.mousePressed(mouseFactory.createEvent(cur, e.getPoint()));
            e.consume();
        } else if (defaultListener != null)
            defaultListener.mousePressed(e);
    }

    public void mouseDragged(MouseEvent e) {
        if (handler != null) {
            handler.mouseDragged(mouseFactory.createEvent(cur, e.getPoint()));
            e.consume();
        } else if (defaultListener != null && defaultListener instanceof MouseMotionListener)
            ((MouseMotionListener)defaultListener).mouseDragged(e);
    }

    public void mouseReleased(MouseEvent e) {
        if (handler != null) {
            handler.mouseReleased(mouseFactory.createEvent(cur, e.getPoint()));
            e.consume();
        } else if (defaultListener != null)
            defaultListener.mouseReleased(e);
    }

    public void mouseEntered(MouseEvent e) {
        if (defaultListener != null)
            defaultListener.mouseEntered(e);
    }
    
    public void mouseExited(MouseEvent e) {
        if (handler != null)
            handler.mouseExited(mouseFactory.createEvent(cur, e.getPoint()));
        if (defaultListener != null)
            defaultListener.mouseExited(e);
    }
}
