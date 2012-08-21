/*
 * Plottable.java
 * Created Jan 18, 2011
 */

package org.blaise.visometry;


/**
 * <p>
 *      Common interface for plottable objects. This class has the minimal elements
 *      necessary to create a tree of plottables.
 * </p>
 * <p>
 *      The API almost exactly parallels {@link VGraphic}.
 * </p>
 * 
 * @author Elisha Peterson
 */
public interface Plottable<C> {

    /** 
     * Return the plottable's parent
     * @return parent object of the plottable 
     */
    public PlottableComposite<C> getParent();
    
    /** 
     * Set parent object of the plottable 
     * @param parent new parent
     */
    public void setParent(PlottableComposite<C> parent);

    /** 
     * Return true if the plottable is uncomputed, i.e. requires recomputation
     * to generate the new graphic entry.
     * @return true if the plottable needs to be recomputed 
     */
    public boolean isUncomputed();
    
    /**
     * <p>
     *      This method is called during the drawing process before any "local" primitive elements are converted into "window" primitive elements,
     *      when {@link Plottable#isUncomputed()} returns true.
     * </p>
     * <p>
     *      <b>Overriding this method is the preferred way to "recompute" an object that depends on a user's input or some other visual elements.</b>
     *      For example, the convex hull of a set of points depends on the positions of the points. When the user moves the points, the hull
     *      needs to be recomputed, and this is where that computation should be done.
     * </p>
     */
    public void recompute();

    /**
     * Generate or provide a graphic entry that can be drawn. This may be either
     * a single entry or a composite entry with lots of elements.
     * @return graphic entry associated with the plottable
     */
    public VGraphic getGraphicEntry();
    
}
