/*
 * StringStyle.java
 * Created Jan 22, 2011
 */

package org.blaise.style;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Draws a string on a graphics canvas. Contains methods for determining the bounding
 * box of a string that is drawn.
 * 
 * @author Elisha
 */
public interface StringStyle {

    /**
     * Draws specified string on the graphics canvas with visibility options.
     * @param point anchor point for string
     * @param text text of string
     * @param canvas graphics element to draw on
     * @param visibility visibility & highlight settings
     */
    public void draw(Point2D point, String text, Graphics2D canvas, VisibilityHint visibility);

    /** 
     * Compute and return the bounding box for a string drawn on the canvas.
     * @param point anchor point for string
     * @param text text of string
     * @param canvas graphics element to draw on
     * @return boundaries of the string for the current settings
     */
    public Rectangle2D bounds(Point2D point, String text, Graphics2D canvas);
}
