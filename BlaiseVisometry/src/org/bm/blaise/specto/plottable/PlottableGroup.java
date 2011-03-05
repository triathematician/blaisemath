/**
 * PlottableGroup.java
 * Created on Feb 25, 2008
 */
package org.bm.blaise.specto.plottable;

import java.util.ArrayList;
import java.util.Collection;
import org.bm.blaise.scio.coordinate.Domain;
import org.bm.blaise.scio.coordinate.DomainContext;
import org.bm.blaise.scio.coordinate.DomainHint;
import org.bm.blaise.scio.coordinate.ScreenSampleDomainProvider;
import org.bm.blaise.scio.coordinate.sample.SampleSet;
import org.bm.blaise.specto.graphics.VCompositeGraphicEntry;
import org.bm.blaise.specto.graphics.VGraphicEntry;

/**
 * <p>
 *  Represents a group of plottables. Maintains a reference to the graphical entries
 *  corresponding to the plottables, which can be drawn on a graphics canvas.
 * </p>
 * <p>
 *  Generating the actual graphic entries occurs after the plottables are recomputed.
 * </p>
 *
 * @param <C> class representing the underlying coordinate
 *
 * @author Elisha Peterson
 */
public final class PlottableGroup<C> extends AbstractPlottable<C> {
//        implements Iterable<VGraphicEntry> {

    /** Stores the elements in the group. */
    protected ArrayList<Plottable<C>> plottables;
    /** The graphic entry container for the plottables */
    protected VCompositeGraphicEntry<C> compositeEntry;

    /**
     * Constructs, initializing list of plottables.
     */
    public PlottableGroup() {
        plottables = new ArrayList<Plottable<C>>();
        compositeEntry = new VCompositeGraphicEntry<C>();
    }

    @Override
    public String toString() {
        return "Plottable Group [" + plottables.size() + " elements]";
    }

    //
    // PROPERTIES
    //

    public VCompositeGraphicEntry getGraphicEntry() {
        return compositeEntry;
    }

    //
    // INDEXED BEAN PROPERTIES
    //

    public Plottable[] getPlottable() {
        return plottables.toArray(new Plottable[]{});
    }
    public Plottable getPlottable(int i) {
        return plottables.get(i);
    }

    public void setPlottable(Plottable[] pl) {
        plottables.clear();
        for (int i = 0; i < pl.length; i++) {
            if (plottables.get(i) != null)
                plottables.get(i).setParent(null);
            pl[i].setParent(this);
            add(pl[i]);
        }
        fireStateChanged();
    }
    public void setPlottable(int i, Plottable p) {
        if (plottables.get(i) != null)
            plottables.get(i).setParent(null);
        plottables.set(i, p);
        p.setParent(this);
        fireStateChanged();
    }

    //
    // COMPOSITIONAL
    //

    /** 
     * Remove all plottables from the group. Also de-registers each as a listener.
     */
    public void clear() {
        for (Plottable p : plottables)
            if (p.getParent() == this)
                p.setParent(null);
        plottables.clear();
        fireStateChanged();
    }

    /**
     * Adds specified plottable to the group, provided it is not already there.
     * Also sets up change listening.
     * @param plottable the plottable to add.
     */
    public void add(Plottable<C> plottable) {
        if (!plottables.contains(plottable))
            plottables.add(plottable);
        plottable.setParent(this);
        fireStateChanged();
    }

    public void add(int index, Plottable<C> plottable) {
        if (plottables.contains(plottable) && plottables.indexOf(plottable) != index)
            plottables.remove(plottable);
        plottables.add(index, plottable);
        plottable.setParent(this);
        fireStateChanged();
    }

    /**
     * Adds specified plottables to the group. Also sets up change listening.
     * @param plottables the plottables to add.
     */
    public void addAll(Collection<? extends Plottable<C>> pp) {
        for (Plottable p : pp) {
            if (!plottables.contains(p))
                plottables.add(p);
            p.setParent(this);
        }
        fireStateChanged();
    }

    /**
     * Removes specified plottable from the group, and removes this class as a listener.
     * @param plottable the plottable to remove.
     * @return value of remove operation
     */
    public boolean remove(Plottable<C> plottable) {
        if (plottables.remove(plottable)) {
            if (plottable.getParent() == this)
                plottable.setParent(null);
            fireStateChanged();
            return true;
        }
        return false;
    }

    //
    // COMPUTATIONAL/VISUAL
    //

    @Override
    public boolean isUncomputed() {
        if (needsComputation)
            return true;
        for (Plottable p : plottables)
            if (p.isUncomputed())
                return true;
        return false;
    }

    @Override
    public synchronized void recompute() {
        boolean change = false;
        for (Plottable p : plottables) {
            if (p.isUncomputed()) {
                p.recompute();
                change = true;
            }
        }
        if (change)
            updateEntries();
        needsComputation = false;
    }

    public synchronized void recomputeAll() {
        for (Plottable p : plottables) {
            if (p instanceof PlottableGroup)
                ((PlottableGroup)p).recomputeAll();
            else
                p.recompute();
        }
        updateEntries();
        needsComputation = false;
    }

    protected void updateEntries() {
        ArrayList<VGraphicEntry<C>> oldEntries = new ArrayList<VGraphicEntry<C>>(compositeEntry.getEntries()),
                remove = new ArrayList<VGraphicEntry<C>>(oldEntries);

        for (Plottable p : plottables) {
            VGraphicEntry<C> en = p.getGraphicEntry();
            if (oldEntries.contains(en))
                remove.remove(en);
            else
                compositeEntry.addEntry(en);
        }

        for (VGraphicEntry<C> en : remove)
            compositeEntry.removeEntry(en);
    }

    //
    // PLOTTABLE EVENT HANDLING
    //

    /**
     * Called when a plottable updates its state in some way, e.g. when it
     * has changed and needs to be redrawn. This marks the group as needing
     * computation and redraw, and notifies the parent and any interested
     * <code>ChangeListener</code>'s.
     * @param p the plottable
     */
    public void plottableChanged(Plottable p) {
        firePlottableChanged(p.isUncomputed());
    }

    /**
     * Called when a plottable updates its style, so that it needs to be redrawn. This marks the group as needing
     * redraw, and notifies the parent and any interested <code>ChangeListener</code>'s.
     * @param p the plottable
     */
    public void plottableStyleChanged(Plottable p) {
        firePlottableStyleChanged();
        fireStateChanged();
    }

    //
    // CONTEXTUAL METHODS
    //

    /** Stores any sample generators associated with this group. */
    DomainContext domains;

    /**
     * Registers a domain with the sampling context.
     * @param id identification value for the domain
     * @param domain the domain
     * @param cls the class type of the sampler
     */
    public <T> void registerDomain(String id, Domain<T> domain, Class<? extends T> cls) {
        if (domains == null)
            domains = new DomainContext();
        domains.addEntry(id, domain, cls);
    }

    /**
     * Attempts to find an appropriate sampler for the given class. If unable to
     * find one, returns null.
     *
     * @param cls the type of sample being requested
     * @param id used to describe which object is being requested
     * @return a sampler of specified type
     */
    protected <T> Domain<T> lookupDomain(String id, Class<? extends T> cls) {
        return domains == null ? null : domains.getDomain(id, cls);
    }

    /**
     * This method allows a plottable to request an appropriate sampler from the group.
     * The group is responsible for finding an appropriate sampler and (if supported)
     * adding event support to notify the plottable when the settings within the sampler change.
     *
     * @param id used to describe which object is being requested
     * @param cls the type of sample being requested
     * @return a sampler appropriate for use by the plottable; possibly a null value if an
     *   appropriate sampler cannot be found
     */
    public <T> Domain<T> requestDomain(String id, Class<? extends T> cls) {
        Domain dom = lookupDomain(id, cls);
        if (dom == null && parent != null)
            dom = parent.requestDomain(id, cls);
        return dom;
    }

    /**
     * This method allows a plottable to request a sampler configured for a specified
     * number of pixels per sample.
     *
     * @param id used to describe which object is being requested
     * @param cls the type of sample being requested
     * @param pixels desired number of pixels per sample
     * @param hint a key representing the type of sampling desired
     * @return a sampler appropriate for use by the plottable; possibly a null value if an
     *   appropriate sampler cannot be found
     */
    public <T> SampleSet<T> requestScreenSampleDomain(String id, Class<? extends T> cls, float pixels, DomainHint hint) {
        Domain dom = lookupDomain(id, cls);
        if (dom == null && parent != null)
            dom = parent.requestDomain(id, cls);
        if (dom instanceof ScreenSampleDomainProvider)
            return ((ScreenSampleDomainProvider)dom).samplerWithPixelSpacing(pixels, hint);
        return null;
    }

}
