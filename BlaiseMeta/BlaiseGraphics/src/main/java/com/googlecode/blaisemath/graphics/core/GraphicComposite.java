/*
 * GraphicComposite.java
 * Created Jan 16, 2011
 */

package com.googlecode.blaisemath.graphics.core;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static com.google.common.base.Preconditions.checkState;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import static com.googlecode.blaisemath.graphics.core.PrimitiveGraphic.STYLE_PROP;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.StyleContext;
import com.googlecode.blaisemath.style.StyleHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JPopupMenu;

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
 * @param <G> type of graphics canvas to render to
 * 
 * @author Elisha
 */
public class GraphicComposite<G> extends Graphic<G> {

    /** Stores the shapes and their styles (in order) */
    protected final Set<Graphic<G>> entries = Sets.newLinkedHashSet();
    /** The attributes associated with the composite */
    protected AttributeSet style = new AttributeSet();
    /** The associated style provider; overrides the default style for the components in the composite (may be null). */
    @Nullable 
    protected StyleContext styleContext;
    
    /** Constructs with default settings */
    public GraphicComposite() {
        setTooltipEnabled(true);
    }

    @Override
    public void initContextMenu(JPopupMenu menu, Graphic<G> src, Point2D point, Object focus, Set selection) {
        // add children menu options
        for (Graphic<G> en : visibleEntriesInReverse()) {
            if ((en instanceof GraphicComposite || en.isContextMenuEnabled()) 
                    && en.contains(point)) {
                en.initContextMenu(menu, en, point, focus, selection);
            }
        }

        // behavior adds inits registered with this class after children
        if (isContextMenuEnabled()) {
            super.initContextMenu(menu, this, point, focus, selection);
        }
    }
    
    /** 
     * Called when a graphic has changed.
     * @param source the entry changed
     */
    public void graphicChanged(Graphic<G> source) {
        if (parent != null) {
            parent.graphicChanged(source);
        }
    }


    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /** 
     * Get graphic entries in the order they are drawn.
     * @return iterator over the entries, in draw order 
     */
    public synchronized Iterable<Graphic<G>> getGraphics() { 
        return Iterables.unmodifiableIterable(entries);
    }
    
    /**
     * Explicitly set list of entries. The draw order will correspond to the iteration order.
     * @param graphics graphics in the composite
     */
    public synchronized void setGraphics(Iterable<? extends Graphic<G>> graphics) {
        clearGraphics();
        addGraphics(graphics);
    }

    /** 
     * Return style provider with default styles
     * @return style provider with default styles
     * @throws IllegalStateException if the object returned would be null
     */
    @Nonnull
    public StyleContext getStyleContext() {
        if (styleContext != null) {
            return styleContext;
        } else {
            checkState(parent != null);
            StyleContext res = parent.getStyleContext();
            return res;
        }
    }
    
    /** 
     * Sets default style provider for all child entries (may be null) 
     * @param styler the style provider (may be null)
     * @throws IllegalArgumentException if the styler is null, and the composite cannot
     *    get a non-null context from its parent
     */
    public void setStyleContext(@Nullable StyleContext styler) { 
        if (styler == null) {
            checkState(parent != null);
        }
        if (styleContext != styler) { 
            styleContext = styler; 
            fireGraphicChanged(); 
        } 
    }

    @Nullable 
    public AttributeSet getStyle() {
        return style;
    }

    public final void setStyle(@Nullable AttributeSet sty) {
        if (this.style != sty) {
            Object old = this.style;
            this.style = sty;
            fireGraphicChanged();
            pcs.firePropertyChange(STYLE_PROP, old, style);
        }
    }
    
    //</editor-fold>
    

    //<editor-fold defaultstate="collapsed" desc="COMPOSITE METHODS">
    //
    // COMPOSITE METHODS
    //

    /** Add w/o events */
    private boolean addHelp(Graphic<G> en) {
        if (entries.add(en)) {
            en.setParent(this);
            return true;
        }
        return false;
    }

    /** Remove w/o events */
    private boolean removeHelp(Graphic<G> en) {
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
     * @return whether composite was changed by add
     */
    public final synchronized boolean addGraphic(Graphic<G> gfc) {
        if (addHelp(gfc)) {
            fireGraphicChanged();
            return true;
        }
        return false;
    }

    /** 
     * Remove an entry from the composite 
     * @param gfc the entry to remove
     * @return 
     */
    public synchronized boolean removeGraphic(Graphic<G> gfc) {
        if (removeHelp(gfc)) {
            fireGraphicChanged();
            return true;
        }
        return false;
    }
    
    /** 
     * Adds several entries to the composite 
     * @param add the entries to add
     * @return true if composite was changed
     */
    public final synchronized boolean addGraphics(Iterable<? extends Graphic<G>> add) {
        boolean change = false;
        for (Graphic<G> en : add) {
            change = addHelp(en) || change;
        }
        if (change) {
            fireGraphicChanged();
            return true;
        } else {
            return false;
        }
    }

    /** 
     * Removes several entries from the composite 
     * @param remove the entries to remove
     * @return 
     */
    public final synchronized boolean removeGraphics(Iterable<? extends Graphic<G>> remove) {
        boolean change = false;
        for (Graphic<G> en : remove) {
            change = removeHelp(en) || change;
        }
        if (change) {
            fireGraphicChanged();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Replaces entries
     * @param remove entries to remove
     * @param add entries to add
     * @return true if composite changed
     */
    public synchronized boolean replaceGraphics(
            Iterable<? extends Graphic<G>> remove, 
            Iterable<? extends Graphic<G>> add) {
        boolean change = false;
        synchronized (entries) {
            for (Graphic<G> en : remove) {
                change = removeHelp(en) || change;
            }
            for (Graphic<G> en : add) {
                change = addHelp(en) || change;
            }
        }
        if (change) {
            fireGraphicChanged();
        }
        return change;
    }

    /**
     * Removes all entries, clearing their parents
     * @return 
     */
    public synchronized boolean clearGraphics() {
        boolean change = !entries.isEmpty();
        for (Graphic<G> en : entries) {
            if (en.getParent() == this) {
                en.setParent(null);
            }
        }
        entries.clear();
        if (change) {
            fireGraphicChanged();
            return true;
        }
        return false;
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Graphic METHODS">
    //
    // Graphic METHODS
    //

    @Override
    public synchronized boolean contains(Point2D point) {
        return graphicAt(point) != null;
    }

    @Override
    public synchronized boolean intersects(Rectangle2D box) {
        for (Graphic<G> en : entries) {
            if (en.intersects(box)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public synchronized void renderTo(G canvas) {
        for (Graphic<G> en : entries) {
            if (!StyleHints.isInvisible(en.getStyleHints())) {
                en.renderTo(canvas);
            }
        }
    }
    
    // </editor-fold>  

    
    //<editor-fold defaultstate="collapsed" desc="METHODS that iterate through entries">
    //
    // METHODS that iterate through entries
    //
    
    /**
     * Iterable over visible entries
     * @return iterable
     */
    public Iterable<Graphic<G>> visibleEntries() {
        return Iterables.filter(entries, GraphicUtils.visibleFilter());
    }
    
    /**
     * Iterable over visible entries, in reverse order
     * @return iterable
     */
    public Iterable<Graphic<G>> visibleEntriesInReverse() {
        return Lists.reverse(Lists.newArrayList(visibleEntries()));
    }
    
    /**
     * Iterable over functional entries
     * @return iterable
     */
    public Iterable<Graphic<G>> functionalEntries() {
        return Iterables.filter(entries, GraphicUtils.<G>functionalFilter());
    }
    
    /**
     * Iterable over functional entries, in reverse order
     * @return iterable
     */
    public Iterable<Graphic<G>> functionalEntriesInReverse() {
        return Lists.reverse(Lists.newArrayList(functionalEntries()));
    }
    
    /** 
     * Return the topmost graphic at specified point, or null if there is none.
     * @param point the window point
     * @return topmost graphic within the composite, or null if there is none
     */
    public synchronized Graphic<G> graphicAt(Point2D point) {
        for (Graphic<G> en : visibleEntriesInReverse()) {
            if (en instanceof GraphicComposite) {
                Graphic<G> s = ((GraphicComposite<G>)en).graphicAt(point);
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
    public synchronized String getTooltip(Point2D p) {
        // return the first non-null tooltip, in draw order
        for (Graphic<G> en : visibleEntriesInReverse()) {
            if (en.isTooltipEnabled() && en.contains(p)) {
                String l = en.getTooltip(p);
                if (l != null) {
                    return l;
                }
            }
        }
        return defaultTooltip;
    }

    /** 
     * Return the topmost graphic at specified point that is interested in mouse events, or null if there is none.
     * @param point the window point
     * @return topmost graphic within the composite
     */
    public synchronized Graphic<G> mouseGraphicAt(Point2D point) {
        // return the first graphic containing the point, in draw order
        for (Graphic<G> en : functionalEntriesInReverse()) {
            if (en.isMouseEnabled()) {
                if (en instanceof GraphicComposite) {
                    Graphic<G> s = ((GraphicComposite<G>)en).mouseGraphicAt(point);
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
    public synchronized Graphic<G> selectableGraphicAt(Point2D point) {
        for (Graphic<G> en : visibleEntriesInReverse()) {
            if (en instanceof GraphicComposite) {
                Graphic<G> s = ((GraphicComposite<G>)en).selectableGraphicAt(point);
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
    public synchronized Set<Graphic<G>> selectableGraphicsIn(Rectangle2D box) {
        Set<Graphic<G>> result = new HashSet<Graphic<G>>();
        for (Graphic<G> g : visibleEntries()) {
            if (g instanceof GraphicComposite) {
                result.addAll(((GraphicComposite<G>)g).selectableGraphicsIn(box));
            }
            // no else belongs here
            if (g.intersects(box) && g.isSelectionEnabled()) {
                result.add(g);
            }
        }
        return result;
    }  
    
    // </editor-fold>
    
}
