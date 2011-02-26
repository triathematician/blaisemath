/*
 * VMouseInputListener.java
 * Created Aug 21, 2010
 */

package visometry;

/**
 * <p>
 * This interface guarantees support for mouse-input events, in the form of click,
 * press, drag, and release events.
 * </p>
 *
 *
 * @param <C> the coordinate system for the visometry
 * 
 * @author Elisha Peterson
 */
public interface VMouseInputListener<C> {

    /**
     * Returns true if this listener expects drag events as well as click events.
     */
    public boolean handlesDragEvents();

    /**
     * Called when the mouse is clicked
     * @param start the starting coordinate of the drag operation
     */
    public void mouseClicked(C point);

    /**
     * Called when the drag is started, i.e. the mouse is first pressed
     * @param src the source object of the drag
     * @param start the starting coordinate of the drag operation
     */
    public void mouseDragInitiated(C start);

    /**
     * Called while the drag is happening.
     * @param src the source object of the drag
     * @param current the current coordinate of the drag operation
     */
    public void mouseDragged(C current);

    /**
     * Called when the drag is completed.
     * @param src the source object of the drag
     * @param end the ending coordinate of the drag operation
     */
    public void mouseDragCompleted(C end);

}
