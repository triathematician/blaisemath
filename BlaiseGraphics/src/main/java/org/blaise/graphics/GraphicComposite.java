/*
 * GraphicComposite.java
 * Created Jan 16, 2011
 */

package org.blaise.graphics;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPopupMenu;
import org.blaise.style.StyleContext;
import org.blaise.style.VisibilityHint;

/**
 * <p>
 * An ordered collection of {@link Graphic}s, where the ordering indicates draw order.
 * May also have a {@link StyleContext} that graphics can reference when rendering.
 * </p>
 * <p>
 * It is intended that this class be thread-safe, so that graphics can be added to or
 * removed from the composite from any thread.
 * </p>
 * 
 * @author Elisha
 */
public class GraphicComposite extends GraphicSupport {

    /** Stores the shapes and their styles */
    private final List<Graphic> entries = Lists.newArrayList();
    /** The associated style provider; overrides the default style for the components in the composite (may be null). */
    private StyleContext styleContext;

    
    //
    // CONSTRUCTOR
    //
    
    /** Constructs with default settings */
    public GraphicComposite() {
        setTooltipEnabled(true);
    }

    @Override
    public String toString() {
        return "Group";
    }

    @Override
    public void initialize(JPopupMenu menu, Point point, Object focus, Set<Graphic> selection) {
        // add children menu options
        for (Graphic en : visibleEntriesInReverse()) {
            if ((en instanceof GraphicComposite || en.isContextMenuEnabled()) 
                    && en.contains(point)) {
                en.initialize(menu, point, focus, selection);
            }
        }

        // behavior adds inits registered with this class after children
        if (isContextMenuEnabled()) {
            super.initialize(menu, point, focus, selection);
        }
    }
    
    /** 
     * Called when a graphic has changed.
     * @param source the entry changed
     */
    public void graphicChanged(Graphic source) {
        if (parent != null) {
            parent.graphicChanged(source);
        }
    }


    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /** 
     * Get the entries in draw order
     * @return iterator over the entries, in draw order 
     */
    public synchronized Iterable<Graphic> getGraphics() { 
        return Iterables.unmodifiableIterable(entries);
    }
    
    /**
     * Explicit set entries
     * @param graphics graphics in the composite
     */
    public synchronized void setGraphics(List<Graphic> graphics) {
        clearGraphics();
        addGraphics(graphics);
    }

    /** 
     * Return style provider with default styles
     * @return style provider with default styles
     * @throws IllegalStateException if the object returned would be null
     */
    public StyleContext getStyleContext() {
        Preconditions.checkState(styleContext != null || (parent != null && parent.getStyleContext() != null));
        return styleContext == null ? parent.getStyleContext() : styleContext;
    }
    
    /** 
     * Sets default style provider for all child entries (may be null) 
     * @param styler the style provider (may be null)
     */
    public void setStyleContext(StyleContext styler) { 
        if (styleContext != styler) { 
            styleContext = styler; 
            fireGraphicChanged(); 
        } 
    }
    
    //</editor-fold>
    

    //<editor-fold defaultstate="collapsed" desc="COMPOSITE METHODS">
    //
    // COMPOSITE METHODS
    //

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
            if (en.getParent() == this) {
                en.setParent(null);
            }
            return true;
        }
        return false;
    }

    /** 
     * Add an entry to the composite. 
     * @param gfc the entry
     */
    public synchronized final void addGraphic(Graphic gfc) {
        if (addHelp(gfc)) {
            fireGraphicChanged();
        }
    }

    /** 
     * Remove an entry from the composite 
     * @param gfc the entry to remove
     */
    public synchronized void removeGraphic(Graphic gfc) {
        if (removeHelp(gfc)) {
            fireGraphicChanged();
        }
    }
    
    /** 
     * Adds several entries to the composite 
     * @param add the entries to add
     */
    public synchronized final void addGraphics(Iterable<? extends Graphic> add) {
        boolean change = false;
        for (Graphic en : add) {
            change = addHelp(en) || change;
        }
        if (change) {
            fireGraphicChanged();
        }
    }

    /** 
     * Removes several entries from the composite 
     * @param remove the entries to remove
     */
    public synchronized final void removeGraphics(Iterable<? extends Graphic> remove) {
        boolean change = false;
        for (Graphic en : remove) {
            change = removeHelp(en) || change;
        }
        if (change) {
            fireGraphicChanged();
        }
    }

    /**
     * Replaces entries
     * @param remove entries to remove
     * @param add entries to add
     */
    public synchronized void replaceGraphics(Iterable<? extends Graphic> remove, Iterable<? extends Graphic> add) {
        boolean change = false;
        synchronized (entries) {
            for (Graphic en : remove) {
                change = removeHelp(en) || change;
            }
            for (Graphic en : add) {
                change = addHelp(en) || change;
            }
        }
        if (change) {
            fireGraphicChanged();
        }
    }

    /**
     * Removes all entries, clearing their parents
     */
    public synchronized void clearGraphics() {
        boolean change = entries.size() > 0;
        for (Graphic en : entries) {
            if (en.getParent() == this) {
                en.setParent(null);
            }
        }
        entries.clear();
        if (change) {
            fireGraphicChanged();
        }
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Graphic METHODS">
    //
    // Graphic METHODS
    //

    public synchronized boolean contains(Point point) {
        return graphicAt(point) != null;
    }

    public synchronized boolean intersects(Rectangle box) {
        for (Graphic en : entries) {
            if (en.intersects(box)) {
                return true;
            }
        }
        return false;
    }
    
    public synchronized void draw(Graphics2D canvas) {
        for (Graphic en : entries) {
            if (!en.getVisibilityHints().contains(VisibilityHint.Hidden)) {
                en.draw(canvas);
            }
        }
    }
    
    // </editor-fold>  

    
    //<editor-fold defaultstate="collapsed" desc="METHODS that iterate through entries">
    //
    // METHODS that iterate through entries
    //
    
    private static Predicate<Graphic> VISIBLE = new Predicate<Graphic>(){
        public boolean apply(Graphic input) { return !input.getVisibilityHints().contains(VisibilityHint.Hidden); }
    };
    
    /**
     * Iterable over visible entries
     * @return iterable
     */
    public Iterable<Graphic> visibleEntries() {
        return Iterables.filter(entries, VISIBLE);
    }
    
    /**
     * Iterable over visible entries, in reverse order
     * @return iterable
     */
    public Iterable<Graphic> visibleEntriesInReverse() {
        return Iterables.filter(Lists.reverse(entries), VISIBLE);
    }
    
    /** 
     * Return the topmost graphic at specified point, or null if there is none.
     * @param point the window point
     * @return topmost graphic within the composite, or null if there is none
     */
    public synchronized Graphic graphicAt(Point point) {
        for (Graphic en : visibleEntriesInReverse()) {
            if (en instanceof GraphicComposite) {
                Graphic s = ((GraphicComposite)en).graphicAt(point);
                if (s != null) {
                    return s;
                }
            } else if (en.contains(point)) {
                return en;
            }
        }
        return null;
    }

    @Override
    public synchronized String getTooltip(Point p) {
        // return the first non-null tooltip, in draw order
        String l = null;
        for (Graphic en : visibleEntriesInReverse()) {
            if (en.isTooltipEnabled() && en.contains(p) && (l = en.getTooltip(p)) != null) {
                return l;
            }
        }
        return tipText;
    }

    /** 
     * Return the topmost graphic at specified point that is interested in mouse events, or null if there is none.
     * @param point the window point
     * @return topmost graphic within the composite
     */
    protected synchronized Graphic mouseGraphicAt(Point point) {
        // return the first graphic containing the point, in draw order
        for (Graphic en : visibleEntriesInReverse()) {
            if (en.isMouseEnabled()) {
                if (en instanceof GraphicComposite) {
                    Graphic s = ((GraphicComposite)en).mouseGraphicAt(point);
                    if (s != null) {
                        return s;
                    }
                } else if (en.contains(point)) {
                    return en;
                }
            }
        }
        return null;
    }

    /**
     * Return selectable graphic at given point
     * @param point point of interest
     * @return graphic at point that can be selected
     */
    public synchronized Graphic selectableGraphicAt(Point point) {
        for (Graphic en : visibleEntriesInReverse()) {
            if (en instanceof GraphicComposite) {
                Graphic s = ((GraphicComposite)en).selectableGraphicAt(point);
                if (s != null) {
                    return s;
                }
            } else if (en.isSelectionEnabled() && en.contains(point)) {
                return en;
            }
        }
        return isSelectionEnabled() && contains(point) ? this : null;
    }
    
    /**
     * Return collection of graphics in the composite in specified bounding box
     * @param box bounding box
     * @return graphics within bounds
     */
    public synchronized Set<Graphic> selectableGraphicsIn(Rectangle box) {
        Set<Graphic> result = new HashSet<Graphic>();
        for (Graphic g : visibleEntries()) {
            if (g instanceof GraphicComposite) {
                result.addAll(((GraphicComposite)g).selectableGraphicsIn(box));
            }
            // no else belongs here
            if (g.intersects(box)) {
                result.add(g);
            }
        }
        return result;
    }  
    
    // </editor-fold>
    
}
