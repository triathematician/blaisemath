/*
 * StringRenderer.java
 * Created Jan 22, 2011
 */

package org.bm.blaise.graphics.renderer;

import org.bm.blaise.graphics.GraphicVisibility;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Elisha
 */
public interface StringRenderer {

    /**
     * Draws specified string on the graphics canvas with visibility options.
     * @param p anchor point for string
     * @param s text of string
     * @param canvas graphics element to draw on
     * @param visibility visibility & highlight settings
     */
    public void draw(Point2D p, String s, Graphics2D canvas, GraphicVisibility visibility);

    /** 
     * Compute and return the bounding box for a string drawn on the canvas.
     * @param p anchor point for string
     * @param s text of string
     * @param canvas graphics element to draw on
     * @return boundaries of the string for the current settings
     */
    public Rectangle2D bounds(Point2D p, String s, Graphics2D canvas);
}
