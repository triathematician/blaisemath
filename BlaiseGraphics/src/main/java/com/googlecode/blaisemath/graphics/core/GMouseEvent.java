/*
 * GMouseEvent.java
 * Created Jan 12, 2011
 */
package com.googlecode.blaisemath.graphics.core;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * <p>
 *   A mouse event that captures both the point of the event and the source
 *   {@link Graphic} for the event.
 * </p>
 * <p>
 *   This event is not designed for serialization.
 * </p>
 *
 * @author Elisha
 */
public final class GMouseEvent extends MouseEvent {
    
    /** Source event */
    private final MouseEvent baseEvent;
    /** The graphic associated with the event. */
    private transient Graphic graphic;
    /** Location of event, in graphic coordinates. */
    private final Point2D loc;

    /**
     * Construct with specified graphic and point.
     * @param evt the associated awt event
     * @param loc the location of the event
     * @param gfc the graphic
     */
    public GMouseEvent(MouseEvent evt, Point2D loc, Graphic gfc) {
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
     * @param gr new source
     */
    public void setGraphicSource(Graphic gr) {
        this.graphic = gr;
    }

    /**
     * Return graphic coordinate location of event
     * @return location
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
        public GMouseEvent createEvent(MouseEvent event, Point2D loc, Graphic gfc) {
            return new GMouseEvent(event, loc, gfc);
        }
    }
}
