/*
 * GraphicComposite.java
 * Created Jan 16, 2011
 */

package com.googlecode.blaisemath.graphics.core;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.StyleContext;
import com.googlecode.blaisemath.style.StyleHints;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JPopupMenu;

/**
 * <p>
 *   An ordered collection of {@link Graphic}s, where the ordering indicates draw order.
 *   May also have a {@link StyleContext} that graphics can reference when rendering.
 *   The composite is NOT thread safe. Any access and changes should be done from a single
 *   thread.
 * </p>
 * 
 * @param <G> type of graphics canvas to render to
 * 
 * @author Elisha
 */
@NotThreadSafe
public class GraphicComposite<G> extends Graphic<G> {
    
    public static final String BOUNDING_BOX_VISIBLE_PROP = "boundingBoxVisible";
    public static final String BOUNDING_BOX_STYLE_PROP = "boundingBoxStyle";

    /** Stores the shapes and their styles (in order) */
    protected final Set<Graphic<G>> entries = Sets.newLinkedHashSet();
    /** The attributes associated with the composite. These will be inherited by child graphics. */
    protected AttributeSet style = new AttributeSet();
    /** The associated style provider; overrides the default style for the components in the composite (may be null). */
    @Nullable 
    protected StyleContext styleContext;
    
    /** Delegate graphic used for drawing the bounding box */
    private final PrimitiveGraphic<Shape,G> boundingBoxGraphic = new PrimitiveGraphic<Shape,G>();
    
    /** Constructs with default settings */
    public GraphicComposite() {
        setTooltipEnabled(true);
        setBoundingBoxVisible(false);
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
    public Iterable<Graphic<G>> getGraphics() { 
        return Iterables.unmodifiableIterable(entries);
    }
    
    /**
     * Explicitly set list of entries. The draw order will correspond to the iteration order.
     * @param graphics graphics in the composite
     */
    public void setGraphics(Iterable<? extends Graphic<G>> graphics) {
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

    public boolean isBoundingBoxVisible() {
        return !GraphicUtils.isInvisible(boundingBoxGraphic);
    }

    public void setBoundingBoxVisible(boolean show) {
        if (isBoundingBoxVisible() != show) {
            boundingBoxGraphic.setStyleHint(StyleHints.HIDDEN_HINT, !show);
            fireGraphicChanged();
            pcs.firePropertyChange(BOUNDING_BOX_VISIBLE_PROP, !show, show);
        }
    }

    public AttributeSet getBoundingBoxStyle() {
        return boundingBoxGraphic.getStyle();
    }

    public void setBoundingBoxStyle(AttributeSet style) {
        Object old = getBoundingBoxStyle();
        if (old != style) {
            boundingBoxGraphic.setStyle(style);
            fireGraphicChanged();
            pcs.firePropertyChange(BOUNDING_BOX_STYLE_PROP, old, style);
        }
    }

    public Renderer<Shape, G> getBoundingBoxRenderer() {
        return boundingBoxGraphic.getRenderer();
    }

    public void setBoundingBoxRenderer(Renderer<Shape, G> renderer) {
        boundingBoxGraphic.setRenderer(renderer);
        fireGraphicChanged();
    }
    
    //</editor-fold>
    

    //<editor-fold defaultstate="collapsed" desc="COMPOSITE METHODS">
    //
    // COMPOSITE METHODS
    //

    /** Add w/o events */
    private boolean addHelp(Graphic<G> en) {
        if (entries.add(en)) {
            if (en.getParent() != null) {
                en.getParent().removeGraphic(en);
            }
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
    public final boolean addGraphic(Graphic<G> gfc) {
        if (addHelp(gfc)) {
            fireGraphicChanged();
            return true;
        }
        return false;
    }

    /** 
     * Remove an entry from the composite 
     * @param gfc the entry to remove
     * @return true if composite was changed
     */
    public boolean removeGraphic(Graphic<G> gfc) {
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
    public final boolean addGraphics(Iterable<? extends Graphic<G>> add) {
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
     * @return true if composite was changed
     */
    public final boolean removeGraphics(Iterable<? extends Graphic<G>> remove) {
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
    public boolean replaceGraphics(
            Iterable<? extends Graphic<G>> remove, 
            Iterable<? extends Graphic<G>> add) {
        boolean change = false;
        for (Graphic<G> en : remove) {
            change = removeHelp(en) || change;
        }
        for (Graphic<G> en : add) {
            change = addHelp(en) || change;
        }
        if (change) {
            fireGraphicChanged();
        }
        return change;
    }

    /**
     * Removes all entries, clearing their parents
     * @return true if composite was changed
     */
    public boolean clearGraphics() {
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
    public Rectangle2D boundingBox() {
        return GraphicUtils.boundingBox(entries);
    }

    @Override
    public boolean contains(Point2D point) {
        return graphicAt(point) != null;
    }

    @Override
    public boolean intersects(Rectangle2D box) {
        for (Graphic<G> en : entries) {
            if (en.intersects(box)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void renderTo(G canvas) {
        for (Graphic<G> en : entries) {
            if (!StyleHints.isInvisible(en.getStyleHints())) {
                en.renderTo(canvas);
            }
        }
        if (!GraphicUtils.isInvisible(boundingBoxGraphic)) {
            AttributeSet baseStyle = boundingBoxGraphic.getStyle();
            AttributeSet modStyle = getStyleContext().applyModifiers(baseStyle, styleHints);
            boundingBoxGraphic.setStyle(modStyle);
            boundingBoxGraphic.setPrimitive(boundingBox());
            boundingBoxGraphic.renderTo(canvas);
            boundingBoxGraphic.setStyle(baseStyle);
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
        return Iterables.filter(entries, GraphicUtils.functionalFilter());
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
    public Graphic<G> graphicAt(Point2D point) {
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
        if (GraphicUtils.isFunctional(boundingBoxGraphic) && boundingBox().contains(point)) {
            return this;
        }
        return null;
    }

    @Override
    public String getTooltip(Point2D p) {
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
    public Graphic<G> mouseGraphicAt(Point2D point) {
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
        Rectangle2D rect = boundingBox();
        if (GraphicUtils.isFunctional(boundingBoxGraphic) && rect != null && rect.contains(point)) {
            return this;
        }
        return null;
    }

    /**
     * Return selectable graphic at given point
     * @param point point of interest
     * @return graphic at point that can be selected
     */
    public Graphic<G> selectableGraphicAt(Point2D point) {
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
    public Set<Graphic<G>> selectableGraphicsIn(Rectangle2D box) {
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
