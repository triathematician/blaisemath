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

    /** @return index of currently "active" primitive if this entry represents an array of primitives, or -1 if none is currently active */
    public int getActiveIndex() {
        return activeIndex;
    }

    /** @return true if the entry can handle the specified mouse event, otherwise return false */
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
