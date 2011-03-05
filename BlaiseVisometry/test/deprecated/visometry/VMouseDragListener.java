/*
 * VMouseDragListener.java
 * Created Apr 12, 2010
 */

package deprecated.visometry;

/**
 * A <code>VMouseDragListener</code> is capable of handling mouseDragged events, which are
 * specified by a "start" location and an "end" location. These are given in the
 * coordinates specified by the parameter.
 *
 * @param <C> the coordinate system for the visometry
 *
 * @author Elisha Peterson
 */
public interface VMouseDragListener<C> {

    /**
     * Called when the mouse enters the domain
     * @param src the source object of the drag
     * @param current the current coordinate of the mouse (local coords)
     */
    public void mouseEntered(Object src, C current);

    /**
     * Called when the mouse exits the domain
     * @param src the source object of the drag
     * @param current the current coordinate of the mouse (local coords)
     */
    public void mouseExited(Object src, C current);

    /**
     * Called when the mouse is being moved, apart from being dragged
     * @param src the source object of the drag
     * @param current the current coordinate of the mouse (local coords)
     */
    public void mouseMoved(Object src, C current);

    /**
     * Called when the drag is started, i.e. the mouse is first pressed
     * @param src the source object of the drag
     * @param start the starting coordinate of the drag operation
     */
    public void mouseDragInitiated(Object src, C start);

    /**
     * Called while the drag is happening.
     * @param src the source object of the drag
     * @param current the current coordinate of the drag operation
     */
    public void mouseDragged(Object src, C current);

    /**
     * Called when the drag is completed.
     * @param src the source object of the drag
     * @param end the ending coordinate of the drag operation
     */
    public void mouseDragCompleted(Object src, C end);

}