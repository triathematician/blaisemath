/*
 * Graphic.java
 * Created Jan 16, 2011
 */

package org.bm.blaise.graphics;

import org.bm.blaise.style.VisibilityKey;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * <p>
 *      Defines an entry that may be drawn on a graphics canvas.
 *      This draw functionality is implemented in the {@link Graphic#draw(java.awt.Graphics2D)} method.
 * </p>
 * <p>
 *      Key additional features are:
 * </p>
 * <ul>
 *      <li>A <em>parent</em> (via get and set methods), 
 *          which is a {@link GraphicComposite} 
 *          and provides access to default styles of various types.</li>
 *      <li>Visibility settings (via get and set methods). 
 *          See {@link VisibilityKey} for the parameters.</li>
 *      <li>Three methods based on a point on the canvas:
 *          <ul>
 *              <li> {@link Graphic#contains(java.awt.Point)}, testing whether the entry contains a point</li>
 *              <li> {@link Graphic#getTooltip(java.awt.Point)}, returning the tooltip for a point (or null)</li>
 *              <li> {@link Graphic#getMouseListener(java.awt.Point)}, returning an object that can handle mouse actions</li>
 *          </ul>
 *      </li>
 *
 * @author Elisha Peterson
 */
 public interface Graphic {
     
    /** 
      * Return parent of the entry
      * @return parent, possibly null 
      */
    public GraphicComposite getParent();
    /** 
     * Sets parent of the entry 
     * @param parent the parent
     */
    public void setParent(GraphicComposite parent);

    /** 
     * Return visibility state of the entry
     * @return the visibility status of the entry
     */
    public VisibilityKey getVisibility();
    /** 
     * Sets the visibility status of the shape 
     * @param vis visiblity
     */
    public void setVisibility(VisibilityKey vis);

    /**
     * Draws the primitive on the specified graphics canvas, using current style.
     * @param canvas graphics canvas
     */
    public void draw(Graphics2D canvas);
    
    

    /**
     * Checks to see if the provided window point is covered by the primitive, when drawn in this style.
     * @param point the window point
     * @return true if the entry contains the point, else false
     */
    public boolean contains(Point point);
    
    /**
     * Return tooltip for the specified point
     * @param point the point
     * @return the tooltip at the specified location (may be null)
     */
    public String getTooltip(Point point);

    /**
     * Return an appropriate mouse handler for the specified point.
     * @param point the window point
     * @return the mouse handler that can deal with mouse events for this entry at specified point (may be null)
     */
    public GraphicMouseListener getMouseListener(Point point);

}
