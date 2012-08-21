/*
 * VisometryMouseInputListener.java
 * Created on Mar 2, 2008
 */
package org.bm.blaise.specto.visometry;

/**
 * <p>
 *   VisometryMouseInputListener is able to handle <code>MouseVisometryEvent</code>s.
 * </p>
 *
 * @param <C> the coordinate type of the plottable
 *
 * @author Elisha Peterson
 */
public interface VisometryMouseInputListener<C> {

    /**
     * Determines whether the provided coordinate is close enough to the class to
     * enable a click event to pass to it.
     * @param coordinate the coordinate
     * @return true if the object has been clicked, otherwise false
     */
    public abstract boolean isClickablyCloseTo(VisometryMouseEvent<C> e);

    /** Called when mouse is clicked. */
    public void mouseClicked(VisometryMouseEvent<C> e);

    /** Called when mouse is pressed. */
    public void mousePressed(VisometryMouseEvent<C> e);

    /** Called when mouse is released. */
    public void mouseReleased(VisometryMouseEvent<C> e);

    /** Called when mouse enters the area. */
    public void mouseEntered(VisometryMouseEvent<C> e);

    /** Called when mouse exits the area. */
    public void mouseExited(VisometryMouseEvent<C> e);

    /** Called when mouse is dragged. */
    public void mouseDragged(VisometryMouseEvent<C> e);

    /** Called when mouse is moved. */
    public void mouseMoved(VisometryMouseEvent<C> e);
}
