/**
 * VGraphicMouseEventFactory.java
 * Created Jan 28, 2011
 */
package org.bm.blaise.specto.graphics;

import org.bm.blaise.graphics.Graphic;
import org.bm.blaise.graphics.GraphicMouseEvent;
import java.awt.Point;

/**
 * @param <C> local coordinate type
 * @author Elisha
 */
public class VGraphicMouseEventFactory<C> extends GraphicMouseEvent.Factory {
    final Visometry<C> vis;

    public VGraphicMouseEventFactory(Visometry<C> vis) {
        if (vis == null)
            throw new IllegalArgumentException("Visometry cannot be null!");
        this.vis = vis;
    }

    @Override
    public GraphicMouseEvent createEvent(Graphic s, Point p) {
        return new VGraphicMouseEvent(s, p, vis.toLocal(p));
    }
}
