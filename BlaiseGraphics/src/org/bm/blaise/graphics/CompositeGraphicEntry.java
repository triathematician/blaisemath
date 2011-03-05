/*
 * CompositeGraphicEntry.java
 * Created Jan 16, 2011
 */

package org.bm.blaise.graphics;

import org.bm.blaise.graphics.renderer.GraphicRendererProvider;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Maintains a collection of <code>GraphicEntry</code>'s in iterable draw order.
 *
 * Implements <code>ChangeListener</code>. Default behavior is to forward any
 * change messages received to registered listeners.
 * 
 * @author Elisha
 */
public class CompositeGraphicEntry implements GraphicEntry,
        ChangeListener {

    //
    // PROPERTIES
    //

    /** Stores the shapes and their styles */
    List<GraphicEntry> entries = Collections.synchronizedList(new ArrayList<GraphicEntry>());
    /** Flag determining whether plottable will be shown in the eventual render. */
    GraphicVisibility visibility = GraphicVisibility.Regular;
    
    /** The associated renderer; overrides the default renderer for the components in the composite (may be null). */
    GraphicRendererProvider rProvider;

    //
    // CONSTRUCTOR
    //

    /** Constructs with default settings */
    public CompositeGraphicEntry() {}

    //
    // PROPERTY PATTERNS
    //

    /** @return iterator over the entries, in draw order */
    public Iterable<GraphicEntry> getEntries() { return entries; }
    public Iterable<GraphicEntry> getEntriesReversed() { return reverseEntries(); }

    public GraphicVisibility getVisibility() { return visibility; }
    public void setVisibility(GraphicVisibility vis) { if (this.visibility != vis) { visibility = vis; fireStateChanged(); } }

    /** @return default renderer factory for all child entries (may be null) */
    public GraphicRendererProvider getRendererProvider() { return rProvider; }
    /** Sets default renderer factory for all child entries (may be null) */
    public void setRendererProvider(GraphicRendererProvider rend) { if (rProvider != rend) { rProvider = rend; fireStateChanged(); } }

    //
    // COMPOSITE PATTERNS
    //

    /** Add w/o events */
    private boolean addHelp(GraphicEntry en) {
        if (entries.add(en)) {
            if (en instanceof CompositeGraphicEntry)
                ((CompositeGraphicEntry)en).addChangeListener(this);
            else if (en instanceof AbstractGraphicEntry)
                ((AbstractGraphicEntry)en).parent = this;
            return true;
        }
        return false;
    }

    /** Add an entry to the composite. */
    public final void addEntry(GraphicEntry en) {
        if (addHelp(en))
            fireStateChanged();
    }
    
    /** Adds several entries to the composite */
    public final void addAllEntries(Iterable<? extends GraphicEntry> add) {
        boolean change = false;
        for (GraphicEntry en : add)
            change = addHelp(en) || change;
        if (change)
            fireStateChanged();
    }

    /** Remove w/o events */
    private boolean removeHelp(GraphicEntry en) {
        if (entries.remove(en)) {
            if (en instanceof CompositeGraphicEntry)
                ((CompositeGraphicEntry)en).removeChangeListener(this);
            else if (en instanceof AbstractGraphicEntry) {
                if (((AbstractGraphicEntry)en).parent == this)
                    ((AbstractGraphicEntry)en).parent = null;
            }
            return true;
        }
        return false;
    }

    /** Remove an entry from the composite */
    public void removeEntry(GraphicEntry en) {
        if (removeHelp(en))
            fireStateChanged();
    }

    /** Removes several entries from the composite */
    public final void removeAllEntries(Iterable<? extends GraphicEntry> remove) {
        boolean change = false;
        for (GraphicEntry en : remove)
            change = removeHelp(en) || change;
        if (change)
            fireStateChanged();
    }

    public void replaceEntries(Iterable<? extends GraphicEntry> remove, Iterable<? extends GraphicEntry> add) {
        boolean change = false;
        synchronized (entries) {
            for (GraphicEntry en : remove)
                change = removeHelp(en) || change;
            for (GraphicEntry en : add)
                change = addHelp(en) || change;
        }
        if (change)
            fireStateChanged();
    }

    public void clearEntries() {
        for (GraphicEntry en : entries) {
            if (en instanceof CompositeGraphicEntry)
                ((CompositeGraphicEntry)en).removeChangeListener(this);
            else if (en instanceof AbstractGraphicEntry) {
                if (((AbstractGraphicEntry)en).parent == this)
                    ((AbstractGraphicEntry)en).parent = null;
            }
        }
        entries.clear();
        fireStateChanged();
    }

    //
    // POINT TESTING
    //

    /** @return first "hit" on a shape entry, or null if none can be found */
    public GraphicEntry entryAt(Point point, GraphicRendererProvider p) {
        for (GraphicEntry en : getEntriesReversed()) {
            if (en instanceof CompositeGraphicEntry) {
                GraphicEntry s = ((CompositeGraphicEntry)en).entryAt(point, rProvider == null ? p : rProvider);
                if (s != null)
                    return s;
            } else
                if (en.contains(point, rProvider == null ? p : rProvider))
                    return en;
        }
        return null;
    }

    public GraphicMouseListener getMouseListener(Point point) {
        GraphicMouseListener l;
        for (GraphicEntry en : getEntriesReversed()) {
            l = en.getMouseListener(point);
            if (l != null)
                return l;
        }
        return null;
    }

    public boolean contains(Point point, GraphicRendererProvider provider) {
        return entryAt(point, rProvider == null ? provider : rProvider) != null;
    }

    public String getTooltip(Point p, GraphicRendererProvider provider) {
        String l = null;
        for (GraphicEntry en : getEntriesReversed()) {
            if (en.contains(p, rProvider == null ? provider : rProvider)) {
                l = en.getTooltip(p, rProvider == null ? provider : rProvider);
                if (l != null)
                    return l;
            }
        }
        return null;
    }

    public void draw(Graphics2D canvas, GraphicRendererProvider defRenderer) {
        if (rProvider != null)
            defRenderer = rProvider;
        for (GraphicEntry en : entries)
            if (en.getVisibility() != GraphicVisibility.Invisible)
                en.draw(canvas, defRenderer);
    }

    //
    // UTILITIES
    //

    /** @return reverse list iterator */
    private Iterable<GraphicEntry> reverseEntries() {
        return new Iterable<GraphicEntry>() {
            public Iterator<GraphicEntry> iterator() {
                final ListIterator<GraphicEntry> li = entries.listIterator(entries.size());
                return new Iterator<GraphicEntry>() {
                    public boolean hasNext() { return li.hasPrevious(); }
                    public GraphicEntry next() { return li.previous(); }
                    public void remove() { li.remove(); }
                };
            }
        };
    }

    //
    // CHANGE HANDLING
    //

    public void stateChanged(ChangeEvent e) { fireStateChanged(e); }

    protected ChangeEvent changeEvent = new ChangeEvent(this);
    protected EventListenerList listenerList = new EventListenerList();

    public final void addChangeListener(ChangeListener l) { listenerList.add(ChangeListener.class, l); }
    public final void removeChangeListener(ChangeListener l) { listenerList.remove(ChangeListener.class, l); }

    /** Notify listeners of a particular change */
    protected void fireStateChanged(ChangeEvent e) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == ChangeListener.class) {
                if (e == null) return;
                ((ChangeListener) listeners[i + 1]).stateChanged(e);
            }
    }
    
    /** Notify interested listeners of an (unspecified) change in the plottable. */
    protected void fireStateChanged() { fireStateChanged(changeEvent); }

}
