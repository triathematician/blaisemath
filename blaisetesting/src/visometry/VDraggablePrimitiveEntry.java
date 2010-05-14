/*
 * VDraggablePrimitiveEntry.java
 * Created Apr 12, 2010
 */

package visometry;

import primitive.style.PrimitiveStyle;

/**
 * A primitive entry that has local and window primitives, and that can handle
 * mouse listening, which typically involves checking to see if there is interest
 * in a particular mouse event, and then passing on appropriate mouse events to
 * a registered listener, after converting to local coordinates.
 *
 * @author Elisha Peterson
 */
public class VDraggablePrimitiveEntry extends VPrimitiveEntry {

    /** Object that will handle dragging events that pass through this entry. */
    public PointDragListener listener;

    /** Construct with specified primitive, style, and drag */
    public VDraggablePrimitiveEntry(Object localPrimitive, PrimitiveStyle style, PointDragListener pdl) {
        super(localPrimitive, style);
        listener = pdl;
    }

    /** Passes enter event on to listener. */
    public void fireEntered(Object coord) {
        listener.mouseEntered(this, coord);
    }

    /** Passes move event on to listener. */
    public void fireMoved(Object coord) {
        listener.mouseMoved(this, coord);
    }

    /** Passes exit event on to listener. */
    public void fireExited(Object coord) {
        listener.mouseExited(this, coord);
    }

    /** Passes drag-start event on to listener. */
    public void fireDragInitiated(Object coord) {
        listener.mouseDragInitiated(this, coord);
    }

    /** Passes drag event on to listener. */
    public void fireDragged(Object coord) {
        listener.mouseDragged(this, coord);
    }

    /** Passes drag event on to listener. */
    public void fireDragCompleted(Object coord) {
        listener.mouseDragCompleted(this, coord);
    }

}
