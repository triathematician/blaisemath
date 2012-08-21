/*
 * Graphic.java
 * Created Jan 16, 2011
 */

package org.blaise.graphics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import org.blaise.style.VisibilityHint;

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
 *          See {@link VisibilityHint} for the parameters.</li>
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
     
     //
     // BASIC STATE
     //
     
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
    public VisibilityHint getVisibility();
    /** 
     * Sets the visibility status of the shape 
     * @param vis visiblity
     */
    public void setVisibility(VisibilityHint vis);
    
    
    //
    // DRAW
    //

    /**
     * Draws the primitive on the specified graphics canvas, using current style.
     * @param canvas graphics canvas
     */
    public void draw(Graphics2D canvas);
    
    
    //
    // BOUNDS
    //
    
    /**
     * Method used to determine whether the graphic receives {@link MouseEvent}s
     * and will be asked to provide a tooltip at the given point.
     * The graphic's {@link MouseListener}s and {@link MouseMotionListener}s will
     * have the opportunity to receive events if the graphic is the topmost element
     * containing the event's point.
     * 
     * 
     * @param point the window point
     * @return true if the entry contains the point, else false
     */
    public boolean contains(Point point);
    
    /**
     * Checks to see if the graphic intersects the area within specified rectangle
     * @param box rectangle to check against
     * @return true if it intersects, false otherwise
     */
    public boolean intersects(Rectangle box);

    /**
     * Return tooltip for the specified point
     * @param point the point
     * @return the tooltip at the specified location (may be null)
     */
    public String getTooltip(Point point);

    /**
     * Whether the object should receive mouse events.
     * @return true if yes, false otherwise
     */
    public boolean isMouseSupported();
    
    
    //
    // EVENT HANDLING
    //

    /**
     * Adds a mouse listener to the graphic
     * @param handler listener
     */
    public void addMouseListener(MouseListener handler);
    
    /**
     * Removes a mouse listener from the graphic
     * @param handler listener
     */
    public void removeMouseListener(MouseListener handler);
    
    /**
     * Return list of mouse listeners registered with the graphic
     * @return listeners
     */
    public MouseListener[] getMouseListeners();

    /**
     * Adds a mouse motion listener to the graphic
     * @param handler listener
     */
    public void addMouseMotionListener(MouseMotionListener handler);
    
    /**
     * Removes a mouse motion listener from the graphic
     * @param handler listener
     */
    public void removeMouseMotionListener(MouseMotionListener handler);
    
    /**
     * Return list of mouse motion listeners registered with the graphic
     * @return listeners
     */
    public MouseMotionListener[] getMouseMotionListeners();
    
}
