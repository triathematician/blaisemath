/**
 * VAbstractGraphicEntry.java
 * Created Jan 29, 2011
 */
package org.bm.blaise.specto.graphics;

/**
 * Implements much of the basic functionality of {@link VGraphic}.
 *
 * @param <C> the object coordinate type
 * 
 * @see org.bm.blaise.graphics.GraphicSupport
 * @see org.bm.blaise.specto.PlottableSupport
 * 
 * @author Elisha Peterson
 */
public abstract class VGraphicSupport<C> implements VGraphic<C> {

    /** Stores the parent of this entry */
    protected VGraphicComposite parent;
    
    /** Flag indicating whether needs conversion */
    private boolean notConverted = true;
    
    /** Stores a mouse handler for the entry (may be null) */
    protected VGraphicMouseListener mouseHandler;


    
    public VGraphicComposite getParent() { 
        return parent; 
    }
    
    public void setParent(VGraphicComposite parent) {
        this.parent = parent;
    }

    public boolean isUnconverted() { 
        return notConverted; 
    }
    
    public void setUnconverted(boolean flag) {
        notConverted = flag;
        if (flag)
            fireConversionNeeded();
    }

    /** 
     * Notify listeners of a need for conversion.
     */
    protected void fireConversionNeeded() {
        if (parent != null)
            parent.conversionNeeded(this);
    }
}
