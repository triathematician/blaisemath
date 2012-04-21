/*
 * GraphicComposite.java
 * Created Jan 16, 2011
 */

package org.bm.blaise.graphics;

import org.bm.blaise.style.VisibilityKey;
import org.bm.blaise.style.StyleProvider;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Maintains a collection of {@link Graphic}s in iterable draw order.
 * May also control the default styling for the graphics contained in the composite
 * via a {@link StyleProvider}.
 * 
 * @author Elisha
 */
public class GraphicComposite extends GraphicSupport {

    /** Stores the shapes and their styles */
    private final List<Graphic> entries = Collections.synchronizedList(new ArrayList<Graphic>());
    
    /** The associated style provider; overrides the default style for the components in the composite (may be null). */
    private StyleProvider styleProvider;

    
    //
    // CONSTRUCTOR
    //
    
    /** Constructs with default settings */
    public GraphicComposite() {
    }

    
    //
    // PROPERTIES
    //

    /** 
     * Get the entries in draw order
     * @return iterator over the entries, in draw order 
     */
    public Iterable<Graphic> getGraphics() { 
        return Collections.unmodifiableList(entries); 
    }
    
    /**
     * Get entries in reverse draw order
     * @return iterator over the entries, in reverse of draw order 
     */
    public Iterable<Graphic> getGraphicsReversed() { 
        return reverseGraphics(); 
    }

    /** 
     * Return style provider with default styles
     * @return style provider with default styles
     * @throws IllegalStateException if the object returned would be null
     */
    public StyleProvider getStyleProvider() { 
        if (styleProvider != null)
            return styleProvider;
        if (parent == null || parent.getStyleProvider() == null)
            throw new IllegalStateException("getStyleProvider() in GraphicComposite should never return a null value!");
        return parent.getStyleProvider();
    }
    
    /** 
     * Sets default style provider for all child entries (may be null) 
     * @param styler the style provider (may be null)
     */
    public void setStyleProvider(StyleProvider styler) { 
        if (styleProvider != styler) { 
            styleProvider = styler; 
            fireAppearanceChanged(); 
        } 
    }
    

    //<editor-fold defaultstate="collapsed" desc="COMPOSITE METHODS">
    //
    // COMPOSITE METHODS
    //

    /** 
     * Add an entry to the composite. 
     * @param gfc the entry
     */
    public final void addGraphic(Graphic gfc) {
        if (addHelp(gfc))
            fireGraphicChanged();
    }

    /** 
     * Remove an entry from the composite 
     * @param gfc the entry to remove
     */
    public void removeGraphic(Graphic gfc) {
        if (removeHelp(gfc))
            fireGraphicChanged();
    }
    
    /** 
     * Adds several entries to the composite 
     * @param add the entries to add
     */
    public final void addGraphics(Iterable<? extends Graphic> add) {
        boolean change = false;
        for (Graphic en : add)
            change = addHelp(en) || change;
        if (change)
            fireGraphicChanged();
    }

    /** 
     * Removes several entries from the composite 
     * @param remove the entries to remove
     */
    public final void removeGraphics(Iterable<? extends Graphic> remove) {
        boolean change = false;
        for (Graphic en : remove)
            change = removeHelp(en) || change;
        if (change)
            fireGraphicChanged();
    }

    /**
     * Replaces entries
     * @param remove entries to remove
     * @param add entries to add
     */
    public void replaceGraphics(Iterable<? extends Graphic> remove, Iterable<? extends Graphic> add) {
        boolean change = false;
        synchronized (entries) {
            for (Graphic en : remove)
                change = removeHelp(en) || change;
            for (Graphic en : add)
                change = addHelp(en) || change;
        }
        if (change)
            fireGraphicChanged();
    }

    /**
     * Removes all entries, clearing their parents
     */
    public void clearGraphics() {
        boolean change = entries.size() > 0;
        for (Graphic en : entries)
            if (en.getParent() == this)
                en.setParent(null);
        entries.clear();
        if (change)
            fireGraphicChanged();
    }
    
    //</editor-fold>
    

    //<editor-fold defaultstate="collapsed" desc="Graphic METHODS">
    //
    // Graphic METHODS
    //

    /** 
     * Return the first "hit" on a shape entry, or null if none can be found
     * @param point the window point
     * @return first "hit" on a shape entry, or null if none can be found 
     */
    public Graphic graphicAt(Point point) {
        // return the first graphic containing the point, in draw order
        for (Graphic en : getGraphicsReversed()) {
            if (en instanceof GraphicComposite) {
                Graphic s = ((GraphicComposite)en).graphicAt(point);
                if (s != null)
                    return s;
            } else
                if (en.contains(point))
                    return en;
        }
        return null;
    }

    /**
     * Return the first "hit" on a graphic that supports mouse listening
     * @param point the window point
     * @return first "hit" on a shape entry, or null if none can be found 
     */
    protected Graphic mouseListenerGraphicAt(Point point) {
        if (!contains(point))
            return null;
        if (super.getMouseListener(point) != null)
            return this;
        
        for (Graphic en : getGraphicsReversed()) {
            if (en instanceof GraphicComposite) {
                Graphic s = ((GraphicComposite)en).mouseListenerGraphicAt(point);
                if (s != null)
                    return s;
            } else
                if (en.contains(point) && en.getMouseListener(point) != null)
                    return en;
        }
        return null;
    }

    public boolean contains(Point point) {
        return graphicAt(point) != null;
    }

    @Override
    public String getTooltip(Point p) {
        // return the first non-null tooltip, in draw order
        String l = null;
        for (Graphic en : getGraphicsReversed()) {
            if (en.contains(p)) {
                l = en.getTooltip(p);
                if (l != null)
                    return l;
            }
        }
        return tooltip;
    }

    @Override
    public GraphicMouseListener getMouseListener(Point point) {
        if (!contains(point))
            return null;
        GraphicMouseListener handler = super.getMouseListener(point);
        if (handler != null)
            return handler;
        
        GraphicMouseListener l;
        for (Graphic en : getGraphicsReversed()) {
            l = en.getMouseListener(point);
            if (l != null)
                return l;
        }
        return null;
    }

    public void draw(Graphics2D canvas) {
        for (Graphic en : entries)
            if (en.getVisibility() != VisibilityKey.Invisible)
                en.draw(canvas);
    }
    // </editor-fold>    


    // <editor-fold desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //
    
    /** 
     * Called when an entry has changed
     * @param source the entry changed
     */
    public void graphicChanged(Graphic source) {
        fireGraphicChanged();
    }
    
    /**
     * Called when an entry's appearance changes
     * @param source the entry whose appearance changed
     */
    public void appearanceChanged(Graphic source) {
        fireAppearanceChanged();
    }

    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="PRIVATE UTILITIES">
    //
    // PRIVATE UTILITIES
    //

    /** @return reverse list iterator */
    private Iterable<Graphic> reverseGraphics() {
        return new Iterable<Graphic>() {
            public Iterator<Graphic> iterator() {
                final ListIterator<Graphic> li = entries.listIterator(entries.size());
                return new Iterator<Graphic>() {
                    public boolean hasNext() { return li.hasPrevious(); }
                    public Graphic next() { return li.previous(); }
                    public void remove() { li.remove(); }
                };
            }
        };
    }

    /** Add w/o events */
    private boolean addHelp(Graphic en) {
        if (entries.add(en)) {
            en.setParent(this);
            return true;
        }
        return false;
    }

    /** Remove w/o events */
    private boolean removeHelp(Graphic en) {
        if (entries.remove(en)) {
            if (en.getParent() == this)
                en.setParent(null);
            return true;
        }
        return false;
    }

    // </editor-fold>
    
}
