/**
 * AbstractVGraphicDragger.java
 * Created Aug 2, 2012
 */
package org.blaise.visometry.plottable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * <p>
 *  Provides hooks for drag mouse gestures. Instead of working with all six mouse methods, subclasses can work with
 *  two or three (dragInitiated, dragInProgress, and optionally dragCompleted).
 * </p>
 * @param <C> underlying coordinate
 * @author elisha
 */
public abstract class AbstractVGraphicDragger<C> extends MouseAdapter implements MouseMotionListener {
    
    /** Stores the starting point of the drag */
    C start;

    /** Called when the mouse is pressed, starting the drag */
    public abstract void mouseDragInitiated(VGMouseEvent<C> e, C start);
    /** Called as mouse drag is in progress */
    public abstract void mouseDragInProgress(VGMouseEvent<C> e, C start);
    /** Called when the mouse is released, finishing the drag */
    public void mouseDragCompleted(VGMouseEvent<C> e, C start) {}


    @Override
    public final void mousePressed(MouseEvent e) {
        start = ((VGMouseEvent<C>)e).local;
        mouseDragInitiated(((VGMouseEvent<C>)e), start);
    }

    public final void mouseDragged(MouseEvent e) {
        if (start == null)
            mousePressed(((VGMouseEvent<C>)e));
        mouseDragInProgress(((VGMouseEvent<C>)e), start);
    }

    @Override
    public final void mouseReleased(MouseEvent e) {
        if (start != null) {
            mouseDragCompleted(((VGMouseEvent<C>)e), start);
            start = null;
        }
    }

    @Override
    public final void mouseExited(MouseEvent e) {
        if (start != null)
            mouseReleased(((VGMouseEvent<C>)e));
    }

    public void mouseMoved(MouseEvent e) {
    }
    
}
