/*
 * GMouseListener.java
 * Created Jan 12, 2011
 */
package org.bm.blaise.graphics;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import org.bm.blaise.style.VisibilityKey;

/**
 * Allows a graphic to interact with a mouse, via the six standard mouse events
 * (enter, exit, press, drag, release, click).
 * Also provides an inner adapter {@link Adapter} to simplify implementations.
 *
 * @author Elisha Peterson
 */
public interface GMouseListener extends MouseListener, MouseMotionListener {

    /**
     * Whether listener is interested in a particular mouse location.
     * @param e the source event (stores the point and graphic for the event)
     * @return true if interested, false otherwise
     */
    public boolean interestedIn(MouseEvent e);

    
    /** Adapter class with empty methods */
    public static class Adapter extends MouseAdapter implements GMouseListener {
        public boolean interestedIn(MouseEvent p) { return true; }
        public void mouseMoved(MouseEvent e) {}
        public void mouseDragged(MouseEvent e) {}
    } // INNER CLASS Adapter
    
    
    /** Static instance of highlighter */
    public final static Highlighter HIGHLIGHTER = new Highlighter();

    /**
     * Class that turns on highlight key whenever the mouse is over the graphic.
     */
    public static class Highlighter extends Adapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            Graphic g = ((GMouseEvent)e).graphic;
            if (g.getVisibility() != VisibilityKey.Invisible)
                g.setVisibility(VisibilityKey.Highlight);
        }
        @Override
        public void mouseExited(MouseEvent e) {
            Graphic g = ((GMouseEvent)e).graphic;
            if (g.getVisibility() != VisibilityKey.Invisible)
                g.setVisibility(VisibilityKey.Regular);
        }
    }

    
    /**
     * Class that also captures starting point of a drag event,
     * making it easier to implement drag behavior.
     * Instead of working with all six mouse methods, subclasses can work with
     * two or three (dragInitiated, dragInProgress, and optionally dragCompleted).
     */
    public abstract static class Dragger extends Adapter {
        /** Stores the starting point of the drag */
        protected Point start;

        /**
         * Called when the mouse is pressed, starting the drag
         * @param e the source event
         * @param start the initial drag point
         */
        public abstract void mouseDragInitiated(GMouseEvent e, Point start);

        /**
         * Called as mouse drag is in progress
         * @param e the source event (stores the point and graphic for the event)
         * @param start the initial drag point
         */
        public abstract void mouseDragInProgress(GMouseEvent e, Point start);

        /**
         * Called when the mouse is released, finishing the drag. Does nothing by default.
         * @param e the source event (stores the point and graphic for the event)
         * @param start the initial drag point
         */
        public void mouseDragCompleted(GMouseEvent e, Point start) {
        }

        @Override
        public final void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            if (start != null) {
                mouseReleased(e);
            }
        }

        @Override
        public final void mousePressed(MouseEvent e) {
            start = e.getPoint();
            mouseDragInitiated((GMouseEvent) e, start);
        }

        @Override
        public final void mouseDragged(MouseEvent e) {
            if (start == null) {
                mousePressed(e);
            }
            mouseDragInProgress((GMouseEvent) e, start);
        }

        @Override
        public final void mouseReleased(MouseEvent e) {
            if (start != null) {
                mouseDragCompleted((GMouseEvent) e, start);
                start = null;
            }
        }
    } // INNER CLASS Dragger
}
