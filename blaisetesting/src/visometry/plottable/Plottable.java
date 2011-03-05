/*
 * Plottable.java
 * Created Jan 18, 2011
 */

package visometry.plottable;

import visometry.graphics.VGraphicEntry;

/**
 * Common interface for plottable objects. This class has the minimal elements
 * necessary to create a tree of plottables.
 * 
 * @author Elisha
 */
public interface Plottable<C> {

    /**
     * Generate or provide a graphic entry that can be drawn. This may be either
     * a single entry or a composite entry with lots of elements.
     * @return graphic entry associated with the plottable
     */
    public VGraphicEntry getGraphicEntry();

    /** @return parent object of the plottable */
    public PlottableGroup<C> getParent();
    /** Set parent object of the plottable */
    public void setParent(PlottableGroup<C> parent);

//    /** @return the visibility status of the shape(s) */
//    public GraphicVisibility getVisibility();
//    /** Sets the visibility status of the shape */
//    public void setVisibility(GraphicVisibility vis);

    /** @return true if the plottable needs to be recomputed */
    public boolean isUncomputed();
    /**
     * <p>
     * This method is called during the drawing process before any "local" primitive elements are converted into "window" primitive elements.
     * It will be called whenever the <code>needsComputation</code> flag is set. Note that this flag is automatically set by the
     * <code>firePlottableChanged</code> method, and so any computation that needs to be done in response to user changes can occur
     * here and will automatically be done before the plottable is redisplayed. By default, it just sets the <code>needsComputation</code>
     * flag to false.
     * </p>
     * <p>
     * <b>Overriding this method is the preferred way to "recompute" an object that depends on a user's input or some other visual elements.</b>
     * For example, the convex hull of a set of points depends on the positions of the points. When the user moves the points, the hull
     * needs to be recomputed, and this is where that computation should be done.
     * </p>
     */
    public void recompute();
    
}
