/**
 * VGraphicMouseEvent.java
 * Created Jan 28, 2011
 */
package visometry.graphics;

import org.bm.blaise.graphics.GraphicEntry;
import org.bm.blaise.graphics.GraphicMouseEvent;
import java.awt.Point;

/**
 * Encapsulates a graphic mouse event with a visometry entry and a local coordinate.
 *
 * @param <C> underlying coordinate system
 * @author Elisha
 */
public class VGraphicMouseEvent<C> extends GraphicMouseEvent {
    final C local;

    public VGraphicMouseEvent(GraphicEntry s, Point point, C local) {
        super(s, point);
        this.local = local;
    }
}
