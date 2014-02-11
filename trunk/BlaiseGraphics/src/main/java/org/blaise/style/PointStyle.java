/*
 * PointStyle.java
 * Created Jan 22, 2011
 */

package org.blaise.style;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Draws a point on a graphics canvas. The point may be oriented, and has a specified radius.
 * 
 * @author Elisha Peterson
 */
public interface PointStyle {
    
    /**
     * Returns the (approximate) window radius of the associated point
     * @return radius in window pixels
     */
    float getMarkerRadius();

    /**
     * Returns the shape corresponding to the given point, for the style's current settings.
     * @param point the point to draw
     * @return the shape corresponding to the specified point
     */
    Shape markerShape(Point2D point);

    /**
     * Returns the shape corresponding to the given point, for the style's current settings.
     * @param p the point to draw
     * @param angle angle of rotation for the point
     * @return the shape corresponding to the specified point
     */
    Shape markerShape(Point2D p, double angle);
    
    /**
     * Draws specified point on the graphics canvas with visibility options
     * @param point the point to draw
     * @param canvas graphics element to draw on
     * @param hints visibility visibility & highlight settings
     */
    void draw(Point2D point, Graphics2D canvas, VisibilityHintSet hints);

    /**
     * Draws a (rotated) point on the graphics canvas with visibility options
     * @param point the point to draw
     * @param angle angle of rotation
     * @param canvas graphics element to draw on
     * @param hints visibility & highlight settings
     */
    void draw(Point2D point, double angle, Graphics2D canvas, VisibilityHintSet hints);
    
}
