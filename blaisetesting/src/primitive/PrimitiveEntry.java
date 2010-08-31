/*
 * PrimitiveEntry.java
 * Created Apr 2010
 */

package primitive;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import primitive.style.PrimitiveStyle;

/**
 * Stores information required for a renderer to drawArray an object, including both the
 * object and an associated style.
 *
 * @author Elisha Peterson
 */
public class PrimitiveEntry {
    /** The object to drawArray. */
    public Object primitive;
    /** The associated style. */
    public PrimitiveStyle style;
    /** Flag determining whether plottable will be shown in the eventual render. */
    public boolean visible = true;
    /** Flag determining whether plottable should be "highlighted". */
    public boolean highlight = false;

    /** construct with given primitive and style. */
    public <T> PrimitiveEntry(T primitive, PrimitiveStyle<T> style) {
        this.primitive = primitive;
        this.style = style;
    }

    /** Stores active index of a primitive, e.g. when one point among many is selected. */
    int activeIndex = -1;

    /** 
     * Used to retrieve the index of the last primitive object that was the target result of a call
     * to the <code>handles</code> method, e.g. the index of the object that the mouse moved over.
     *
     * @return index of currently "active" primitive if this entry represents an array of primitives,
     * or -1 if none is currently active
     */
    public int getActiveIndex() {
        return activeIndex;
    }

    /**
     * <p>
     * Determines if the primitive can "handle" the mouse event, i.e. if when it is drawn
     * on screen the mouse coordinate location is over what is drawn. This is primarily where
     * the style's <i>contained</i> and <i>containedInArray</i> methods are used.
     * </p>
     * <p>
     * If the primitive is an array, and one of the array's objects corresponds to the event,
     * then this class stores the index of that object for later retrieval by <code>getActiveIndex()</code>.
     * </p>
     *
     * @return true if the entry can handle the specified mouse event, otherwise return false
     */
    public boolean handles(Graphics2D canvas, MouseEvent e) {
        if (style == null || primitive == null)
            return false;
        if (style.getTargetType().isAssignableFrom( primitive.getClass() ))
            return style.contained(primitive, canvas, e.getPoint());
        else if (primitive.getClass().isArray() && style.getTargetType().isAssignableFrom( primitive.getClass().getComponentType() )) {
            activeIndex = style.containedInArray((Object[]) primitive, canvas, e.getPoint());
            return activeIndex != -1;
        }
        return false;
    }
}
