/*
 * Visometry.java
 * Created on Sep 14, 2007, 7:42:38 AM
 */
package org.bm.blaise.specto.visometry;

import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import javax.swing.event.ChangeListener;

/**
 * <p>
 *   The purpose of a visometry is to provide the essential methods for translating
 *   between a visual rectangle on the computer screen and a particular coordinate system.
 * </p>
 * 
 * @param <C> the underlying coordinate system in use
 *
 * @author Elisha Peterson
 */
public interface Visometry<C> {

    /**
     * Returns bounds of the screen viewable part of the visometry.
     * @return bounds for the visometry
     */
    public RectangularShape getWindowBounds();

    /**
     * Sets bounds of the visometry.
     * @param newBounds new bounds for the screen
     */
    public void setWindowBounds(RectangularShape newBounds);

    /**
     * Returns the minimum visible coordinate.
     */
    public C getMinPointVisible();

    /**
     * Returns the maximum visible coordinate.
     */
    public C getMaxPointVisible();


    /**
     * Computes a transformation for passing between the window and the local
     * geometry. Should be called whenever the window resizes or the parameters
     * of the visometry change.
     *
     * @throws IllegalStateException if the transformation cannot be computed because of window size, etc.
     */
    public void computeTransformation() throws IllegalStateException;

    /**
     * Converts a local coordinate to a window coordinate.
     * @param coordinate the local coordinate
     * @return a point within the window
     */
    public Point2D.Double getWindowPointOf(C coordinate);

    /**
     * Converts a window point to a local coordinate.
     * @param point the window point
     * @return an underlying coordinate
     */
    public C getCoordinateOf(Point2D point);




    //
    //
    // EVENT HANDLING
    //
    //

    /**
     * Removes a listener from the list of classes receiving <code>ChangeEvent</code>s
     * @param l the listener
     */
    public void addChangeListener(ChangeListener l);

    /**
     * Adds a listener to receive <code>ChangeEvent</code>s
     * @param l the listener
     */
    public void removeChangeListener(ChangeListener l);
}
