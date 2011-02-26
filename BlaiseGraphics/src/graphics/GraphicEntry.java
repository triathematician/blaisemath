/*
 * GraphicEntry.java
 * Created Jan 16, 2011
 */

package graphics;

import graphics.renderer.GraphicRendererProvider;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * Defines an entry that may be drawn on a graphics canvas. Provides visibility
 * settings, a tooltip getter, and potentially a mouse handler.
 *
 * @author Elisha
 */
public interface GraphicEntry {

    /** @return the visibility status of the shape(s) */
    public GraphicVisibility getVisibility();
    /** Sets the visibility status of the shape */
    public void setVisibility(GraphicVisibility vis);

    /**
     * @return the tooltip at the specified location (may be null)
     * @param provider renderer factory to use if no renderer is specified
     */
    public String getTooltip(Point p, GraphicRendererProvider provider);

    /**
     * Checks to see if the provided window point is covered by the primitive, when drawn in this style.
     * @param point the window point
     * @param provider renderer factory to use if no renderer is specified
     */
    public abstract boolean contains(Point point, GraphicRendererProvider provider);

    /**
     * @param point the window point
     * @return the mouse handler that can deal with mouse events for this entry at specified point (may be null)
     */
    public GraphicMouseListener getMouseListener(Point p);

    /**
     * Draws the primitive on the specified graphics canvas, using current style.
     * @param canvas graphics canvas
     * @param factory default renderer to use if the shape entry has none
     */
    public void draw(Graphics2D canvas, GraphicRendererProvider factory);

}
