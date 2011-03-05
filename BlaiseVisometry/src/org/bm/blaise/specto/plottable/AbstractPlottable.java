/*
 * AbstractPlottable.java
 * Created on Sep 14, 2007, 7:49:09 AM
 */
package visometry.plottable;

import org.bm.blaise.graphics.GraphicVisibility;
import javax.swing.event.ChangeListener;
import util.DefaultChangeBroadcaster;

/**
 * <p>
 *      Provides much of the basic functionality associated with a plottable.
 *
 * </p>
 *
 * @param <C> the coordinate type of the plottable
 *
 * @author Elisha Peterson
 */
public abstract class AbstractPlottable<C> implements Plottable<C> {

    /** Parent group of the plottable, possibly null */
    protected PlottableGroup parent = null;
    /** Determines visibility of the plottable. */
    protected GraphicVisibility visible = GraphicVisibility.Regular;
    /** Flag indicating whether the plottable needs to be recomputed. The parent object
     * will request that this object "recomputes" prior to grabbing its current set of primitives.
     * If false, the parent will assume that no computation is necessary and may used
     * a cached version of the primitives. */
    transient protected boolean needsComputation = true;

    /**
     * Default constructor
     */
    public AbstractPlottable() {
    }

    /** 
     * Default constructor uses a parent group for construction.
     * @parent the parent group of the plottable
     */
    public AbstractPlottable(PlottableGroup parent) {
        this.parent = parent;
    }

    //
    // PLOTTABLE INTERFACE METHODS
    //

    public PlottableGroup getParent() { return parent; }
    public void setParent(PlottableGroup par) { if (parent != par) parent = par; }

    public GraphicVisibility getVisibility() { return visible; }
    public void setVisibility(GraphicVisibility vis) { 
        if (visible != vis) {
            visible = vis;
            firePlottableChanged(false);
        }
    }
    
    public boolean isUncomputed() {
        return needsComputation;
    }
    public void recompute() {
        needsComputation = false;
        getGraphicEntry().setUnconverted(true);
    }

    //
    // CHANGE HANDLING
    //

    /** Used to keep track of change listeners. */
    protected DefaultChangeBroadcaster changer = new DefaultChangeBroadcaster(this);

    public void addChangeListener(ChangeListener l) { changer.addChangeListener(l); }
    public void removeChangeListener(ChangeListener l) { changer.removeChangeListener(l); }

    /** Notify interested listeners of an (unspecified) change in the plottable. */
    public void fireStateChanged() { changer.fireStateChanged(); }

    /** Notifies listeners that the plottable has changed in some way. Also notifies the parent group. */
    protected void firePlottableChanged(boolean recompute) {
        needsComputation = recompute;
        fireStateChanged();
        if (parent != null)
            parent.plottableChanged(this);
    }

    /** Fires a notification to the parent object that the visual style of the plottable has changed in some way. */
    public void firePlottableStyleChanged() {
        if (parent != null)
            parent.plottableStyleChanged(this);
    }
}
