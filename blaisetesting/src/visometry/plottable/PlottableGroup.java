/**
 * PlottableGroup.java
 * Created on Feb 25, 2008
 */
package visometry.plottable;

import coordinate.DomainContext;
import coordinate.DomainHint;
import coordinate.ScreenSampleDomainProvider;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import scio.coordinate.Domain;
import scio.coordinate.sample.SampleSet;
import visometry.VPrimitiveEntry;

/**
 * <p>
 *  Represents a group of plottables.
 * </p>
 *
 * @param <C> class representing the underlying coordinate
 *
 * @author Elisha Peterson
 */
public class PlottableGroup<C> extends DynamicPlottable<C>
        implements Iterable<VPrimitiveEntry> {

    /** Stores the elements in the group. */
    protected ArrayList<Plottable<C>> plottables;

    /**
     * Constructs, initializing list of plottables.
     */
    public PlottableGroup() {
        plottables = new ArrayList<Plottable<C>>();
    }

    @Override
    public String toString() {
        return "Plottable Group [" + plottables.size() + " elements]";
    }

    //
    // INDEXED BEAN PROPERTIES
    //

    public Plottable[] getPlottableArray() {
        if (plottables == null) {
            return (Plottable[]) Array.newInstance(Plottable.class, 0);
        }
        return plottables.toArray( (Plottable[]) Array.newInstance(Plottable.class, 0) );
    }

    public void setPlottableArray(Plottable[] pl) {
        plottables.clear();
        for (int i = 0; i < pl.length; i++) {
            add(pl[i]);
        }
    }

    public Plottable getPlottableArray(int i) {
        return plottables.get(i);
    }

    public void setPlottableArray(int i, Plottable p) {
        plottables.set(i, p);
    }

    //
    // COMPOSITIONAL
    //

    /** 
     * Remove all plottables from the group. Also de-registers each as a listener.
     */
    public void clear() {
        for (Plottable p : plottables)
            p.parent = null;
        plottables.clear();
        fireStateChanged();
    }

    /**
     * Adds specified plottable to the group. Also sets up change listening.
     * @param plottable the plottable to add.
     */
    public void add(Plottable<C> plottable) {
        plottables.add(plottable);
        plottable.parent = this;
        fireStateChanged();
    }

    /**
     * Adds specified plottables to the group. Also sets up change listening.
     * @param plottables the plottables to add.
     */
    public void addAll(Collection<? extends Plottable<C>> pp) {
        plottables.addAll(pp);
        for (Plottable p : pp)
            p.parent = this;
        fireStateChanged();
    }

    /**
     * Removes specified plottable from the group, and removes this class as a listener.
     * @param plottable the plottable to remove.
     * @return value of remove operation
     */
    public boolean remove(Plottable<C> plottable) {
        if (plottables.remove(plottable)) {
            plottable.parent = null;
            fireStateChanged();
            return true;
        }
        return false;
    }

    //
    // COMPUTATIONAL/VISUAL
    //

    @Override
    public void recompute() {
        for (Plottable p : plottables)
            if (p.needsComputation)
                p.recompute();
        needsComputation = false;
    }

    @Override
    protected List<VPrimitiveEntry> getPrimitives() {
        throw new UnsupportedOperationException("getPrimitives not supported for PlottableGroup... iterate over entries instead");
    }

    //
    // ITERATION METHODS: PlottableGroup AS AN ITERATION OVER VPrimitiveEntry's
    //

    transient int curIndex; // currently iterating plottable
    transient Plottable curPlottable; // currently iterating plottable
    transient Iterator<VPrimitiveEntry> pgi; // current iterator

    /** @return an iterator over the entries in the group, returning only those plottables that are visible */
    public Iterator<VPrimitiveEntry> iterator() {
        curIndex = -1;
        return new Iterator<VPrimitiveEntry>() {
            public boolean hasNext() {
                if (curIndex >= plottables.size())
                    return false;
                // loop advances to the next VISIBLE plottable with an entry
                while (pgi == null || !pgi.hasNext()) {
                    do {
                        curIndex++;
                        if (curIndex == plottables.size())
                            return false;
                        curPlottable = plottables.get(curIndex);
                    } while (!curPlottable.isVisible());
                    pgi = curPlottable instanceof PlottableGroup
                            ? ((PlottableGroup) curPlottable).iterator()
                            : curPlottable.getPrimitives().iterator();
                }
                return true;
            }
            public VPrimitiveEntry next() {
                return pgi.next();
            }
            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
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
        // the DynamicPlottable superclass notifies the parent and also fires a change event to interested listeners
        firePlottableChanged();
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
