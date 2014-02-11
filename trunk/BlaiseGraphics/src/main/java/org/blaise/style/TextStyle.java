/*
 * TextStyle.java
 * Created Jan 22, 2011
 */

package org.blaise.style;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.annotation.Nullable;

/**
 * Draws a string on a graphics canvas at a given location. Also provides a method
 * to compute the boundaries of that string.
 * 
 * @author Elisha
 */
public interface TextStyle {

    /** 
     * Compute and return the bounding box for a string drawn on the canvas.
     * @param anchor anchor point for string
     * @param text text of string
     * @param canvas graphics element to draw on
     * @return boundaries of the string for the current settings, or null indicating there are none
     */
    @Nullable Rectangle2D bounds(Point2D anchor, @Nullable String text, Graphics2D canvas);

    /**
     * Draws specified string on the graphics canvas with visibility options.
     * @param anchor anchor point for string
     * @param text text of string
     * @param canvas graphics element to draw on
     * @param visibility visibility & highlight settings
     */
    void draw(Point2D anchor, @Nullable String text, Graphics2D canvas, VisibilityHintSet visibility);
    
}
