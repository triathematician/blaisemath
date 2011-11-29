/*
 * PlottableSupport.java
 * Created on Sep 14, 2007, 7:49:09 AM
 */
package org.bm.blaise.specto;

/**
 * <p>
 *      Provides much of the basic functionality associated with a plottable.
 * </p>
 *
 * @param <C> the coordinate type of the plottable
 * 
 * @see org.bm.blaise.graphics.GraphicSupport
 * @see org.bm.blaise.specto.graphics.VGraphicSupport
 *
 * @author Elisha Peterson
 */
public abstract class PlottableSupport<C> implements Plottable<C> {

    /** Parent group of the plottable, possibly null */
    protected PlottableComposite parent = null;
    
    /** Flag indicating whether the plottable needs to be recomputed. The parent object
     * will request that this object "recomputes" prior to grabbing its current set of primitives.
     * If false, the parent will assume that no computation is necessary and may used
     * a cached version of the primitives. */
    transient protected boolean uncomputed = true;


    public PlottableComposite getParent() { 
        return parent; 
    }
    
    public void setParent(PlottableComposite par) { 
        if (parent != par) 
            parent = par; 
    }
    
    public boolean isUncomputed() {
        return uncomputed;
    }
    
    /**
     * By default, no recomputation takes place, but this method resets the
     * {@code uncomputed} flag, and sets the graphic entry's {@code unconverted}
     * flag to true.
     */
    public void recompute() {
        uncomputed = false;
        getGraphicEntry().setUnconverted(true);
    }
    
    /**
     * Notify listeners of a need for computation
     */
    protected void fireComputeNeeded() {
        if (parent != null)
            parent.computeNeeded(this);
    }

}
