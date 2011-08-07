/*
 * PointRenderer.java
 * Created Jan 22, 2011
 */

package org.bm.blaise.graphics.renderer;

import org.bm.blaise.graphics.GraphicVisibility;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 *
 * @author Elisha
 */
public interface PointRenderer {

    /**
     * Draws specified point on the graphics canvas with visibility options
     * @param point the point to draw
     * @param canvas graphics element to draw on
     * @param visibility visibility & highlight settings
     */
    public void draw(Point2D point, Graphics2D canvas, GraphicVisibility visibility);

    /**
     * Draws a (rotated) point on the graphics canvas with visibility options
     * @param point the point to draw
     * @param angle angle of rotation
     * @param canvas graphics element to draw on
     * @param visibility visibility & highlight settings
     */
    public void draw(Point2D point, double angle, Graphics2D canvas, GraphicVisibility visibility);

    /**
     * Draws specified points on the graphics canvas with visibility options
     * @param points the points to draw
     * @param canvas graphics element to draw on
     * @param visibility visibility & highlight settings
     */
    public void drawAll(Iterable<Point2D> points, Graphics2D canvas, GraphicVisibility visibility);

    /**
     * Returns the (approximate) window radius of the associated point
     * @return radius in window pixels
     */
    public float getRadius();
    
    /**
     * Returns the shape corresponding to the given point, for the renderer's current settings.
     * @param point the point to draw
     * @return the shape corresponding to the specified point
     */
    public Shape shape(Point2D point);

    /**
     * Returns the shape corresponding to the given point, for the renderer's current settings.
     * @param p the point to draw
     * @param angle angle of rotation for the point
     * @return the shape corresponding to the specified point
     */
    public Shape shape(Point2D p, double angle);

}
