/*
 * VGraphicPlottable.java
 * Created Sep 19, 2011
 */
package org.blaise.visometry;


/**
 * Wraps a {@link VGraphic} in a plottable object, allowing it to be added to a
 * {@link PlottableComposite} object.
 * 
 * @param <C> the coordinate type of the plottable
 *
 * @author Elisha Peterson
 */
public final class VGraphicPlottable<C> extends PlottableSupport<C> {
    
    /** The window entry */
    private final VGraphic<C> entry;

    /** 
     * Construct with the specified entry.
     * @param entry local graphics primitive
     */
    public VGraphicPlottable(VGraphic<C> entry) {
        this.entry = entry;
    }

    public VGraphic getGraphicEntry() {
        return entry;
    }
    
}
