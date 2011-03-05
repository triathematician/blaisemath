/**
 * VCompositeGraphicEntry.java
 * Created Jan 29, 2011
 */
package org.bm.blaise.specto.graphics;

import org.bm.blaise.graphics.CompositeGraphicEntry;
import org.bm.blaise.graphics.GraphicEntry;
import org.bm.blaise.graphics.renderer.GraphicRendererProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bm.blaise.specto.Visometry;
import org.bm.blaise.specto.VisometryProcessor;
import util.DefaultChangeBroadcaster;

/**
 * Groups together several <code>VGraphicEntry</code>'s.
 *
 * @param <C> local coordinate type
 * @see graphics.GraphicEntry, graphics.CompositeGraphicEntry
 * @author Elisha
 */
public class VCompositeGraphicEntry<C> extends AbstractVGraphicEntry<C>
        implements ChangeListener {

    /** Stores the local entries */
    protected final List<VGraphicEntry<C>> entries = Collections.synchronizedList(new ArrayList<VGraphicEntry<C>>());
    /** Stores the window entry */
    protected final CompositeGraphicEntry windowEntry = new CompositeGraphicEntry();

    public VCompositeGraphicEntry() {}

    //
    // PROPERTIES (DELEGATES)
    //

    public CompositeGraphicEntry getWindowEntry() { return windowEntry; }

    public GraphicRendererProvider getRenderer() { return windowEntry.getRendererProvider(); }
    public void setRenderer(GraphicRendererProvider rend) { windowEntry.setRendererProvider(rend); }

    //
    // COMPOSITE METHODS
    //

    /** @return list of entries (unmodifiable) */
    public List<VGraphicEntry<C>> getEntries() { return Collections.unmodifiableList(entries); }

    /** Addition helper */
    private boolean addHelp(VGraphicEntry en) {
        if (entries.add(en)) {
            if (en instanceof VCompositeGraphicEntry)
                ((VCompositeGraphicEntry)en).addChangeListener(this);
            else if (en instanceof AbstractVGraphicEntry)
                ((AbstractVGraphicEntry)en).parent = this;
            return true;
        }
        return false;
    }

    /** Add an entry to the composite. */
    public void addEntry(VGraphicEntry en) {
        if (addHelp(en))
            fireStateChanged();
    }

    /** Add an entry to the composite. */
    public void addAllEntries(Iterable<? extends VGraphicEntry> add) {
        boolean change = false;
        for (VGraphicEntry en : add)
            change = addHelp(en) || change;
        if (change)
            fireStateChanged();
    }

    /** Replaces an entire set of entries. */
    public void replaceEntries(Iterable<? extends VGraphicEntry> add) {
        synchronized(entries) {
            for (VGraphicEntry<C> en : entries) {
                if (en instanceof VCompositeGraphicEntry)
                    ((VCompositeGraphicEntry)en).removeChangeListener(this);
                else if (en instanceof AbstractVGraphicEntry) {
                    if (((AbstractVGraphicEntry)en).parent == this)
                        ((AbstractVGraphicEntry)en).parent = null;
                }
            }
            entries.clear();
            boolean change = false;
            for (VGraphicEntry en : add)
                change = addHelp(en) || change;
            if (change)
                fireStateChanged();
        }
    }

    /** Remove w/o events */
    private boolean removeHelp(VGraphicEntry en) {
        if (entries.remove(en)) {
            if (en instanceof CompositeGraphicEntry)
                ((CompositeGraphicEntry)en).removeChangeListener(this);
            else if (en instanceof AbstractVGraphicEntry) {
                if (((AbstractVGraphicEntry)en).parent == this)
                    ((AbstractVGraphicEntry)en).parent = null;
            }
            return true;
        }
        return false;
    }

    /** Remove an entry from the composite */
    public void removeEntry(VGraphicEntry en) {
        if (removeHelp(en))
            fireStateChanged();
    }

    /** Removes several entries from the composite */
    public final void removeAllEntries(Iterable<? extends VGraphicEntry> remove) {
        boolean change = false;
        for (VGraphicEntry en : remove)
            change = removeHelp(en) || change;
        if (change)
            fireStateChanged();
    }

    public void clearEntries() {
        for (VGraphicEntry<C> en : entries) {
            if (en instanceof VCompositeGraphicEntry)
                ((VCompositeGraphicEntry)en).removeChangeListener(this);
            else if (en instanceof AbstractVGraphicEntry) {
                if (((AbstractVGraphicEntry)en).parent == this)
                    ((AbstractVGraphicEntry)en).parent = null;
            }
        }
        entries.clear();
        fireStateChanged();
    }

    //
    // CONVERSIONS
    //

    @Override
    public boolean isUnconverted() {
        synchronized(entries) {
            for (VGraphicEntry<C> en : entries)
                if (en.isUnconverted())
                    return true;
        }
        return false;
    }

    /** Flag to prevent events from firing while updating convert */
    transient boolean settingConvertFlag = false;

    @Override
    public void setUnconverted(boolean flag) {
        settingConvertFlag = true;
//        synchronized(entries) {
        try {
            for (VGraphicEntry<C> en : entries)
                en.setUnconverted(flag);
        } catch (Exception ex) {
            System.err.println(ex);
        }
//        }
        settingConvertFlag = false;
    }

    public void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        synchronized(entries) {
            for (VGraphicEntry<C> e : entries)
                if (e.isUnconverted())
                    e.convert(vis, processor);
            updateEntries();
        }
    }

    protected void updateEntries() {
        ArrayList<GraphicEntry> oldEntries = new ArrayList<GraphicEntry>();
        for (GraphicEntry e : windowEntry.getEntries()) oldEntries.add(e);
        ArrayList<GraphicEntry> toAdd = new ArrayList<GraphicEntry>(),
                toRemove = new ArrayList<GraphicEntry>(oldEntries);

        for (VGraphicEntry<C> e : entries) {
            GraphicEntry en = e.getWindowEntry();
            if (oldEntries.contains(en))
                toRemove.remove(en);
            else
                toAdd.add(en);
        }

        windowEntry.replaceEntries(toRemove, toAdd);
    }

    //
    // CHANGE HANDLING
    //

    /** Used to keep track of change listeners. */
    protected DefaultChangeBroadcaster changer = new DefaultChangeBroadcaster(this);

    public void addChangeListener(ChangeListener l) { changer.addChangeListener(l); }
    public void removeChangeListener(ChangeListener l) { changer.removeChangeListener(l); }

    /** Notify interested listeners of an (unspecified) change in the plottable. */
    @Override
    protected void fireStateChanged() { changer.fireStateChanged(); }
    public void stateChanged(ChangeEvent e) { fireStateChanged(); }
    /** Called by child elements to request conversion */
    public void fireConversionRequest(VGraphicEntry en) {
        if (!settingConvertFlag)
            fireStateChanged();
    }

}
