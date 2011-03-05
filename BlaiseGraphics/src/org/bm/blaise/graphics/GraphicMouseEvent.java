/*
 * GraphicMouseEvent.java
 * Created Jan 12, 2011
 */

package org.bm.blaise.graphics;

import java.awt.Point;

/**
 * An event corresponding to a particular shape.
 * @author Elisha
 */
public class GraphicMouseEvent {
    final Point pt;
    final GraphicEntry en;

    public GraphicMouseEvent(GraphicEntry s, Point point) {
        this.en = s;
        this.pt = point;
    }

    public Point getPoint() { return pt; }
    public int getX() { return pt.x; }
    public int getY() { return pt.y; }
    public GraphicEntry getEntry() { return en; }

    /**
     * Provides a simple way to generate "graphics" mouse events, e.g. those tied to a particular graphics entry.
     */
    public static class Factory {
        /** @return generic event */
        public GraphicMouseEvent createEvent(GraphicEntry s, Point p) {
            return new GraphicMouseEvent(s, p);
        }
    }
}
