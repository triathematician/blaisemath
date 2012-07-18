/*
 * GMouseEvent.java
 * Created Jan 12, 2011
 */

package org.bm.blaise.graphics;

import java.awt.Component;
import java.awt.event.MouseEvent;

/**
 * A mouse event that captures both the point of the event and the source
 * {@link Graphic} for the event.
 *
 * @author Elisha
 */
public class GMouseEvent extends MouseEvent {
    
    /** The graphic associated with the event. */
    protected final Graphic graphic;

    /**
     * Construct with specified graphic and point.
     * @param gfc the graphic
     * @param point the location of the event
     */
    public GMouseEvent(MouseEvent evt, Graphic gfc) {
        super((Component) evt.getSource(), evt.getID(), evt.getWhen(), evt.getModifiers(), evt.getX(), evt.getY(), evt.getClickCount(), evt.isPopupTrigger(), evt.getButton());
        this.graphic = gfc;
    }

    /**
     * Return the graphic source of the event.
     * @return graphic associated with the event
     */
    public Graphic getEntry() { return graphic; }


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
         * @param event base event
         * @param gfc associated graphic
         * @return generic event
         */
        public GMouseEvent createEvent(MouseEvent event, Graphic gfc) {
            return new GMouseEvent(event, gfc);
        }

    } // INNER CLASS GMouseEvent.Factory
}
