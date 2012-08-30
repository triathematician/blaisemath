/*
 * PlotComponent.java
 * Created Sep 19, 2011
 */
package org.blaise.visometry.plottable;

import java.util.Collection;
import org.blaise.visometry.VGraphicComponent;
import org.blaise.visometry.Visometry;

/**
 * Contains a group of plottables that can be recomputed, reconverted, and rendered
 * to a graphics object.
 *
 * @author Elisha Peterson
 */
public class PlotComponent<C> extends VGraphicComponent<C> {

    /** The group of plottables on the component */
    protected final PlottableRoot<C> plottables = new PlottableRoot<C>();

    /**
     * Construct the plot component.
     * @param vis the visometry used for coordinate transformations
     */
    public PlotComponent(Visometry<C> vis) {
        super(vis);
        vRoot.addGraphic(plottables.getGraphicEntry());
    }

    //
    // PROPERTIES
    //

    /**
     * Return root object containing local graphics.
     * @return local graphics object
     */
    public PlottableRoot<C> getPlottableRoot() {
        return plottables;
    }

    //
    // COMPOSITE METHODS (DELEGATES)
    //

    public void addPlottable(Plottable<C> plottable) { plottables.add(plottable); }
    public void addPlottable(int index, Plottable<C> plottable) { plottables.add(index, plottable); }
    public void addPlottables(Collection<? extends Plottable<C>> pp) { plottables.addAll(pp); }
    public boolean removePlottable(Plottable<C> plottable) { return plottables.remove(plottable); }
    public void clearPlottables() { plottables.clear(); }
    public Plottable[] getPlottableArray() { return plottables.getPlottable(); }
    public Plottable getPlottableArray(int i) { return plottables.getPlottable(i); }
    public void setPlottableArray(int i, Plottable p) { plottables.setPlottable(i, p); }
    public void setPlottableArray(Plottable[] pp) { plottables.clear(); for (Plottable p : pp) plottables.add(p); }


    //
    // DRAW METHODS
    //

    @Override
    protected void recompute() {
        plottables.recompute();
    }



}
