/*
 * VGraphic.java
 * Created Apr 2010
 * Substantial Revision Jan 28, 2011
 */

package org.bm.blaise.specto.graphics;

import org.bm.blaise.graphics.Graphic;

/**
 * <p>
 *      Stores a graphic primitive that exists in a local coordinate system. 
 *      Looks a lot like {@link Graphic}, except that the corresponding methods are in terms of local coordinates instead of window coordinates. 
 *      This class also adds in support for the conversion steps;
 *      once the conversion is done, the corresponding window graphic may be retrieved.
 * </p>
 *
 * @param <C> the object coordinate type
 * 
 * @author Elisha Peterson
 *
 * @see Graphic
 */
public interface VGraphic<C> {
     
    /** 
      * Return parent of the graphic
      * @return parent, possibly null 
      */
    public VGraphicComposite getParent();
    /** 
     * Sets parent of the graphic 
     * @param parent the parent
     */
    public void setParent(VGraphicComposite parent);
    
    /** 
     * Return true if graphic needs conversion to local coords
     * @return true if graphic needs to be converted to window coordinates 
     */
    public boolean isUnconverted();
    /** 
     * Used to set the converted flag, determining whether the entry to be reconverted before drawing again 
     * @param flag if true, graphic needs conversion before display
     */
    public void setUnconverted(boolean flag);

    /**
     * Performs the conversion from local coordinates to window coordinates
     * @param vis the underlying visometry
     * @param processor provides some basic translation utilities
     */
    public void convert(Visometry<C> vis, VisometryProcessor<C> processor);

    /**
     * Returns the graphic entry. Should never be called unless <code>isNeedsConversion()</code> returns true.
     * The return value should never be null.
     * @return the regular (window coordinates) graphic entry computed after conversion
     */
    public Graphic getWindowEntry();

}
