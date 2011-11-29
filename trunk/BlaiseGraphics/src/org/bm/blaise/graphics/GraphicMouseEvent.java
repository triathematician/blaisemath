/*
 * GraphicMouseEvent.java
 * Created Jan 12, 2011
 */

package org.bm.blaise.graphics;

import java.awt.Point;

/**
 * A mouse event that captures both the point of the event and the source
 * {@link Graphic} for the event.
 * 
 * @author Elisha
 */
public class GraphicMouseEvent {
    
    /** The point where the event occurred. */
    protected final Point point;
    /** The graphic associated with the event. */
    protected final Graphic graphic;

    /**
     * Construct with specified graphic and point.
     * @param gfc the graphic
     * @param point the location of the event
     */
    public GraphicMouseEvent(Graphic gfc, Point point) {
        this.graphic = gfc;
        this.point = point;
    }

        
    /**
     * Return the graphic source of the event.
     * @return graphic associated with the event
     */
    public Graphic getEntry() { return graphic; }
    
    /**
     * Return the location of the event.
     * @return location
     */
    public Point getPoint() { return point; }
    
    /**
     * Return the x coordinate of the event's location.
     * @return x coordinate
     */
    public int getX() { return point.x; }
    
    /**
     * Return the y coordinate of the event's location.
     * @return y coordinate
     */
    public int getY() { return point.y; }

    
    /**
     * Provides a simple way to generate "graphics" mouse events, 
     * e.g. those tied to a particular graphics entry. Making this a class
     * of its own allows subclasses to create "more interesting" events
     * associated with a {@code Graphic}, in particular enriching it
     * with more information, or associating it with the object responsible
     * for creating the {@code Graphic}.
     */
    public static class Factory {
        
        /** 
         * Create and return an event associated with the specified entry and point.
         * @param entry the entry associated with the event
         * @param point the point where the source mouse event occurred
         * @return generic event 
         */
        public GraphicMouseEvent createEvent(Graphic entry, Point point) {
            return new GraphicMouseEvent(entry, point);
        }
        
    } // INNER CLASS GraphicMouseEvent.Factory
}
