/*
 * VisometryMouseEvent.java
 * Created on Mar 2, 2008
 */
package org.bm.blaise.specto.visometry;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * <p>
 *   <code>VisometryMouseEvent</code> converts the coordinate in a <code>MouseEvent</code> to a local
 *   event using the underlying visometry. This allows listeners to obtain the natural coordinate without
 *   doing any extra work.
 * </p>
 *
 * @param <C> the coordinate type of the plottable
 *
 * @author Elisha Peterson
 */
public class VisometryMouseEvent<C> {

    Visometry<C> visometry;
    MouseEvent event;
    C coordinate;

    /**
     * Constructs with a <code>MouseEvent</code>.
     * @param mouseEvent a mouse event
     * @param visometry a visometry
     */
    public VisometryMouseEvent(MouseEvent mouseEvent, Visometry<C> visometry) {
        this.visometry = visometry;
        setMouseEvent(mouseEvent);
    }

    /**
     * Gets the local coordinate of the point of the mouse event.
     * @return local coordinate of the mouse event.
     */
    public C getCoordinate() {
        return coordinate;
    }

    /**
     * Sets the underlying event, and computes the coordinate.
     * @param event the mouse event
     */
    public void setMouseEvent(MouseEvent event) {
        this.event = event;
        coordinate = visometry.getCoordinateOf(event.getPoint());
    }

    
// TODO - may need to reimplement this later. Not sure.
//    /**
//     * Gets the visometry underlying this event.
//     * @return source of the event, as a visometry.
//     */
//    public V getSourceVisometry() {
//        return (V) source;
//    }


    /**
     * Determines if the event is close enough to the specified coordinate to be "clickable"
     * @param coordinate local coordinate
     * @param pixelRange the range (in pixels) required for clicking
     * @return true or false depending on whether the mouse is close enough to click
     */
    public boolean withinRangeOf(C coordinate, int pixelRange) {
        // TODO - might need to find a better home for this method
        Point2D point = visometry.getWindowPointOf(coordinate);
        return event.getPoint().distance(point) < pixelRange;
    }

    //
    // DELEGATES
    //

    public Point getWindowPoint() {
        return event.getPoint();
    }

    public int getModifiersEx() {
        return event.getModifiersEx();
    }

    
}
