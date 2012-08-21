/*
 * GMouseListener.java
 * Created Jan 12, 2011
 */

package org.bm.blaise.specto.graphics;

import java.awt.Point;
import java.awt.event.MouseEvent;
import org.bm.blaise.graphics.GMouseListener;

/**
 * For mouse cursor's interaction with shapes.
 *
 * @param <C> underlying coordinate
 * @author Elisha
 */
public interface VGMouseListener<C> {

    /** Called when mouse enters a shape */
    public void mouseEntered(VGMouseEvent<C> e);
    /** Called when mouse exits a shape */
    public void mouseExited(VGMouseEvent<C> e);
    /** Called when mouse press on a shape */
    public void mousePressed(VGMouseEvent<C> e);
    /** Called when mouse dragged on a shape */
    public void mouseDragged(VGMouseEvent<C> e);
    /** Called when mouse released on a shape */
    public void mouseReleased(VGMouseEvent<C> e);
    /** Called when mouse clicked on a shape */
    public void mouseClicked(VGMouseEvent<C> e);

    /** Provides an adapter that converts regular events to VGME's by simple casting. */
    public GMouseListener adapter();



    /** Adapter class with empty methods */
    public static class Adapter<C> implements VGMouseListener<C> {
        public void mouseEntered(VGMouseEvent<C> e) {}
        public void mouseExited(VGMouseEvent<C> e) {}
        public void mousePressed(VGMouseEvent<C> e) {}
        public void mouseDragged(VGMouseEvent<C> e) {}
        public void mouseReleased(VGMouseEvent<C> e) {}
        public void mouseClicked(VGMouseEvent<C> e) {}

        public GMouseListener adapter() {
            return new GMouseListener(){
                // TODO - check... is this the proper logic ??
                public boolean interestedIn(MouseEvent p) { return true; }
                public void mouseEntered(MouseEvent e) { VGMouseListener.Adapter.this.mouseEntered((VGMouseEvent<C>) e); }
                public void mouseExited(MouseEvent e) { VGMouseListener.Adapter.this.mouseExited((VGMouseEvent<C>) e); }
                public void mousePressed(MouseEvent e) { VGMouseListener.Adapter.this.mousePressed((VGMouseEvent<C>) e); }
                public void mouseDragged(MouseEvent e) { VGMouseListener.Adapter.this.mouseDragged((VGMouseEvent<C>) e); }
                public void mouseReleased(MouseEvent e) { VGMouseListener.Adapter.this.mouseReleased((VGMouseEvent<C>) e); }
                public void mouseClicked(MouseEvent e) { VGMouseListener.Adapter.this.mouseClicked((VGMouseEvent<C>) e); }
                public void mouseMoved(MouseEvent e) { }
            };
        }
    }



    /** Class that also captures starting point of a drag event, making it easier to implement drag behavior */
    public abstract static class Dragger<C> extends Adapter<C> {
        /** Stores the starting point of the drag */
        C start;

        /** Called when the mouse is pressed, starting the drag */
        public abstract void mouseDragInitiated(VGMouseEvent<C> e, C start);
        /** Called as mouse drag is in progress */
        public abstract void mouseDragInProgress(VGMouseEvent<C> e, C start);
        /** Called when the mouse is released, finishing the drag */
        public void mouseDragCompleted(VGMouseEvent<C> e, C start) {}

        @Override public void mousePressed(VGMouseEvent<C> e) {
            start = e.local;
            mouseDragInitiated(e, start);
        }

        @Override public void mouseDragged(VGMouseEvent<C> e) {
            if (start == null)
                mousePressed(e);
            mouseDragInProgress(e, start);
        }

        @Override public void mouseReleased(VGMouseEvent<C> e) {
            if (start != null) {
                mouseDragCompleted(e, start);
                start = null;
            }
        }

        @Override public void mouseExited(VGMouseEvent<C> e) {
            if (start != null)
                mouseReleased(e);
        }
    }

}
