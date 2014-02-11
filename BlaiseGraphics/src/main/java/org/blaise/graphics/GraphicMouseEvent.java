/*
 * GraphicMouseEvent.java
 * Created Jan 12, 2011
 */

package org.blaise.graphics;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * A mouse event that captures both the point of the event and the source
 * {@link Graphic} for the event.
 *
 * @author Elisha
 */
public final class GraphicMouseEvent extends MouseEvent {
    
    /** Source event */
    protected final MouseEvent baseEvent;
    /** The graphic associated with the event. */
    protected Graphic graphic;
    /** Location of event, in graphic coordinates. */
    protected final Point2D loc;

    /**
     * Construct with specified graphic and point.
     * @param gfc the graphic
     * @param point the location of the event
     */
    public GraphicMouseEvent(MouseEvent evt, Point2D loc, Graphic gfc) {
        super((Component) evt.getSource(), 
                evt.getID(), evt.getWhen(), evt.getModifiers(),
                evt.getX(), evt.getY(),
                evt.getClickCount(), evt.isPopupTrigger(), evt.getButton());
        this.baseEvent = evt;
        if (evt.isConsumed()) {
            consume();
        }
        this.graphic = gfc;
        this.loc = loc;
    }
    
    /**
     * Return the graphic source of the event.
     * @return graphic associated with the event
     */
    public Graphic getGraphicSource() { 
        return graphic; 
    }
    
    /**
     * Change graphic source of event.
     * @param graphic new source
     */
    void setGraphicSource(Graphic gr) {
        this.graphic = gr;
    }

    /**
     * Return graphic coordinate location of event
     * @return lcoation
     */
    public Point2D getGraphicLocation() {
        return loc;
    }

    @Override
    public boolean isConsumed() {
        return super.isConsumed() || baseEvent.isConsumed();
    }

    @Override
    public void consume() {
        super.consume();
        baseEvent.consume();
    }


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
         * @param loc location to use
         * @param gfc associated graphic
         * @return generic event
         */
        public GraphicMouseEvent createEvent(MouseEvent event, Point2D loc, Graphic gfc) {
            return new GraphicMouseEvent(event, loc, gfc);
        }

    } // INNER CLASS GraphicMouseEvent.Factory
}
