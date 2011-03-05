/*
 * AbstractPlottable.java
 * Created on Sep 14, 2007, 7:49:09 AM
 */
package visometry.plottable;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import primitive.style.PrimitiveStyle;
import visometry.VPrimitiveEntry;

/**
 * <p>
 *   Base class for elements that can be drawn on a plot component.
 * </p>
 *
 * @param <C> the coordinate type of the plottable
 *
 * @author Elisha Peterson
 */
public abstract class Plottable<C>
        implements ChangeListener {

    /** Stores the objects that will be redrawn... populated when the plottable "recomputes". */
    protected ArrayList<VPrimitiveEntry> primitives = new ArrayList<VPrimitiveEntry>();
    /** Parent group of the plottable, possibly null */
    protected PlottableGroup parent = null;
    /** Determines visibility of the plottable. */
    protected boolean visible = true;
    /** Flag indicating whether the plottable needs to be recomputed. The parent object
     * will request that this object "recomputes" prior to grabbing its current set of primitives.
     * If false, the parent will assume that no computation is necessary and may used
     * a cached version of the primitives. */
    transient protected boolean needsComputation = true;
    /** Flag indicating whether the plottable needs to be repainted. */
    transient protected boolean needsRepaint = true;

    /**
     * Default constructor
     */
    public Plottable() {
    }

    /** 
     * Default constructor uses a parent group for construction.
     * @parent the parent group of the plottable
     */
    public Plottable(PlottableGroup parent) {
        this.parent = parent;
    }

    /** @return current parent of this plottable */
    public PlottableGroup getParent() { return parent; }
    /** Sets current parent of this plottable */
    public void setParent(PlottableGroup par) { if (parent != par) { parent = par; firePlottableChanged(); } }

    /** @return visibility status of plottable */
    public boolean isVisible() { return visible; }
    /** Sets visibility status of plottable */
    public void setVisible(boolean newValue) { if (visible != newValue) { visible = newValue; firePlottableChanged(); } }

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
    protected void recompute() { needsComputation = false; }

    /**
     * Adds a primitive as an object/style pairing
     * @param object primitive object
     * @param style style associated with the object (null directs renderer to use default style)
     */
    protected void addPrimitive(Object object, PrimitiveStyle style) {
        primitives.add(new VPrimitiveEntry(object, style));
    }

    /**
     * Adds a primitive to the list of primitives.
     * @param primitives one or more primitives
     */
    protected void addPrimitive(VPrimitiveEntry... vpes) {
        for (VPrimitiveEntry vpe : vpes)
            primitives.add(vpe);
    }

    /**
     * Return primitives associated with the plottable.
     * @return list of primitive objects represent the visualization of the plottable (in local coordinates)
     */
    protected List<VPrimitiveEntry> getPrimitives() {
        return primitives;
    }

    //
    // CHANGE EVENT HANDLING
    //

    /**
     * Handles a change event. The event is <b>assumed</b> to change the visualization in some way, and so
     * the net result is to alter the appropriate flag and to notify the parent.
     * @param e the change event
     */
    public void stateChanged(ChangeEvent e) {
        firePlottableChanged();
    }

    /** Fires a notification to the parent object that the plottable has changed in some way. */
    protected void firePlottableChanged() {
        needsComputation = true;
        needsRepaint = true;
        if (parent != null)
            parent.plottableChanged(this);
    }

    /** Fires a notification to the parent object that the visual style of the plottable has changed in some way. */
    protected void firePlottableStyleChanged() {
        needsRepaint = true;
        if (parent != null)
            parent.plottableStyleChanged(this);
    }
}
