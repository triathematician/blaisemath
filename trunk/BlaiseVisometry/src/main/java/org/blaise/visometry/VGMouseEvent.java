/**
 * VGMouseEvent.java
 * Created Jan 28, 2011
 */
package org.blaise.visometry;

import java.awt.event.MouseEvent;
import org.blaise.graphics.Graphic;
import org.blaise.graphics.GraphicMouseEvent;

/**
 * Encapsulates a graphic mouse event with a visometry entry and a local coordinate.
 *
 * @param <C> underlying coordinate system
 * @author Elisha
 */
public class VGMouseEvent<C> extends GraphicMouseEvent {
    
    final C local;

    public VGMouseEvent(MouseEvent event, Graphic gfc, C local) {
        super(event, gfc);
        this.local = local;
    }
    
    
    /** Factory class to associate visometry with mouse events. */
    public static class Factory<C> extends GraphicMouseEvent.Factory {
        final Visometry<C> vis;

        public Factory(Visometry<C> vis) {
            if (vis == null)
                throw new IllegalArgumentException("Visometry cannot be null!");
            this.vis = vis;
        }

        @Override
        public GraphicMouseEvent createEvent(MouseEvent event, Graphic gfc) {
            return new VGMouseEvent(event, gfc, vis.toLocal(event.getPoint()));
        }
    }
}
