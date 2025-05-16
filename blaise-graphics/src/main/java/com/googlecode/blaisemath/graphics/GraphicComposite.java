package com.googlecode.blaisemath.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import static com.googlecode.blaisemath.graphics.PrimitiveGraphic.P_STYLE;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.StyleContext;
import com.googlecode.blaisemath.style.StyleHints;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import javax.swing.JPopupMenu;

/**
 * An ordered collection of {@link Graphic}s, where the ordering indicates draw order.
 * May also have a {@link StyleContext} that graphics can reference when rendering.
 * The composite is NOT thread safe. Any access and changes should be done from a single
 * thread.
 *
 * @param <G> type of graphics canvas to render to
 * 
 * @author Elisha Peterson
 */
public class GraphicComposite<G> extends Graphic<G> {
    
    public static final String P_BOUNDING_BOX_VISIBLE = "boundingBoxVisible";
    public static final String P_BOUNDING_BOX_STYLE = "boundingBoxStyle";

    public PrimitiveGraphic<Shape, G> getBoundingBoxGraphic() {
        return boundingBoxGraphic;
    }

    /** Stores the shapes and their styles (in order) */
    protected final Set<Graphic<G>> entries = Sets.newLinkedHashSet();
    /** The attributes associated with the composite. These will be inherited by child graphics. */
    protected AttributeSet style = new AttributeSet();
    /** The associated style provider; overrides the default style for the components in the composite (may be null). */
    protected @Nullable StyleContext styleContext;
    
    /** Delegate graphic used for drawing the bounding box */
    private final PrimitiveGraphic<Shape,G> boundingBoxGraphic = new PrimitiveGraphic<>();
    
    /** Constructs with default settings */
    public GraphicComposite() {
        setTooltipEnabled(true);
        boundingBoxGraphic.setStyleHint(StyleHints.HIDDEN_HINT, true);
    }

    //region PROPERTIES

    /** 
     * Get graphic entries in the order they are drawn.
     * @return iterator over the entries, in draw order 
     */
    public Set<Graphic<G>> getGraphics() {
        return Collections.unmodifiableSet(entries);
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
     */
    public StyleContext getStyleContext() {
        if (styleContext != null) {
            return styleContext;
        } else if (parent != null) {
            return parent.getStyleContext();
        } else {
            return new StyleContext();
        }
    }
    
    /** 
     * Sets default style provider for all child entries (may be null) 
     * @param styleContext the style provider (may be null)
     */
    public void setStyleContext(@Nullable StyleContext styleContext) {
        if (this.styleContext != styleContext) {
            this.styleContext = styleContext;
            fireGraphicChanged(); 
        } 
    }

    @Override
    public @Nullable AttributeSet getStyle() {
        return style;
    }

    public final void setStyle(@Nullable AttributeSet sty) {
        if (this.style != sty) {
            Object old = this.style;
            this.style = sty;
            fireGraphicChanged();
            pcs.firePropertyChange(P_STYLE, old, style);
        }
    }

    public boolean isBoundingBoxVisible() {
        return !GraphicUtils.isInvisible(boundingBoxGraphic);
    }

    public void setBoundingBoxVisible(boolean show) {
        if (isBoundingBoxVisible() != show) {
            boundingBoxGraphic.setStyleHint(StyleHints.HIDDEN_HINT, !show);
            fireGraphicChanged();
            pcs.firePropertyChange(P_BOUNDING_BOX_VISIBLE, !show, show);
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
            pcs.firePropertyChange(P_BOUNDING_BOX_STYLE, old, style);
        }
    }

    public Renderer<Shape, G> getBoundingBoxRenderer() {
        return boundingBoxGraphic.getRenderer();
    }

    public void setBoundingBoxRenderer(Renderer<Shape, G> renderer) {
        boundingBoxGraphic.setRenderer(renderer);
        fireGraphicChanged();
    }
    
    //endregion

    //region COMPOSITE METHODS

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
    public final boolean removeGraphic(Graphic<G> gfc) {
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
    public boolean replaceGraphics(Iterable<? extends Graphic<G>> remove, Iterable<? extends Graphic<G>> add) {
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
        entries.stream().filter(en -> en.getParent() == this).forEach(en -> en.setParent(null));
        entries.clear();
        if (change) {
            fireGraphicChanged();
            return true;
        }
        return false;
    }

    /** Add w/o events */
    private boolean addHelp(Graphic<G> en) {
        if (entries.add(en)) {
            GraphicComposite par = en.getParent();
            if (par != null) {
                par.removeGraphic(en);
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
    
    //endregion
    
    //region GRAPHIC IMPLEMENTATIONS

    @Override
    public Rectangle2D boundingBox(@Nullable G canvas) {
        return GraphicUtils.boundingBox(entries, canvas);
    }

    @Override
    public boolean contains(Point2D point, @Nullable G canvas) {
        return graphicAt(point, canvas) != null;
    }

    @Override
    public boolean intersects(Rectangle2D box, @Nullable G canvas) {
        return entries.stream().anyMatch(en -> en.intersects(box, canvas));
    }
    
    @Override
    public void renderTo(G canvas) {
        entries.stream().filter(en -> !StyleHints.isInvisible(en.getStyleHints()))
                .forEach(en -> en.renderTo(canvas));
        if (!GraphicUtils.isInvisible(boundingBoxGraphic)) {
            AttributeSet baseStyle = boundingBoxGraphic.getStyle();
            AttributeSet modStyle = getStyleContext().applyModifiers(baseStyle, styleHints);
            boundingBoxGraphic.setStyle(modStyle);
            boundingBoxGraphic.setPrimitive(boundingBox(canvas));
            boundingBoxGraphic.renderTo(canvas);
            boundingBoxGraphic.setStyle(baseStyle);
        }
    }
    
    //endregion

    //region QUERIES
    
    /**
     * Iterable over visible entries
     * @return iterable
     */
    public Iterable<Graphic<G>> visibleEntries() {
        return Sets.filter(entries, GraphicUtils::isVisible);
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
        return Sets.filter(entries, GraphicUtils::isFunctional);
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
     * @param canvas canvas
     * @return topmost graphic within the composite, or null if there is none
     */
    public Graphic<G> graphicAt(Point2D point, G canvas) {
        for (Graphic<G> en : visibleEntriesInReverse()) {
            if (en instanceof GraphicComposite) {
                Graphic<G> s = ((GraphicComposite<G>)en).graphicAt(point, canvas);
                if (s != null) {
                    return s;
                }
            } else if (en.contains(point, canvas)) {
                return en;
            }
        }
        if (GraphicUtils.isFunctional(boundingBoxGraphic) && boundingBox(canvas).contains(point)) {
            return this;
        }
        return null;
    }

    @Override
    public String getTooltip(Point2D p, G canvas) {
        for (Graphic<G> en : visibleEntriesInReverse()) {
            if (en.isTooltipEnabled() && en.contains(p, canvas)) {
                String l = en.getTooltip(p, canvas);
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
     * @param canvas graphics canvas
     * @return topmost graphic within the composite
     */
    public Graphic<G> mouseGraphicAt(Point2D point, G canvas) {
        // return the first graphic containing the point, in draw order
        for (Graphic<G> en : functionalEntriesInReverse()) {
            if (en.isMouseDisabled()) {
                // do nothing
            } else if (en instanceof GraphicComposite) {
                Graphic<G> s = ((GraphicComposite<G>) en).mouseGraphicAt(point, canvas);
                if (s != null) {
                    return s;
                }
            } else if (en.contains(point, canvas)) {
                return en;
            }
        }
        Rectangle2D rect = boundingBox(canvas);
        if (GraphicUtils.isFunctional(boundingBoxGraphic) && rect != null && rect.contains(point)) {
            return this;
        }
        return null;
    }

    /**
     * Return selectable graphic at given point
     * @param point point of interest
     * @param canvas canvas
     * @return graphic at point that can be selected
     */
    public Graphic<G> selectableGraphicAt(Point2D point, G canvas) {
        for (Graphic<G> en : visibleEntriesInReverse()) {
            if (en instanceof GraphicComposite) {
                Graphic<G> s = ((GraphicComposite<G>)en).selectableGraphicAt(point, canvas);
                if (s != null) {
                    return s;
                }
            } else if (en.isSelectionEnabled() && en.contains(point, canvas)) {
                return en;
            }
        }
        return isSelectionEnabled() && contains(point, canvas) ? this : null;
    }
    
    /**
     * Return collection of graphics in the composite in specified bounding box
     * @param box bounding box
     * @param canvas canvas
     * @return graphics within bounds
     */
    public Set<Graphic<G>> selectableGraphicsIn(Rectangle2D box, G canvas) {
        Set<Graphic<G>> result = new HashSet<>();
        for (Graphic<G> g : visibleEntries()) {
            if (g instanceof GraphicComposite) {
                result.addAll(((GraphicComposite<G>)g).selectableGraphicsIn(box, canvas));
            }
            // no else belongs here
            if (g.intersects(box, canvas) && g.isSelectionEnabled()) {
                result.add(g);
            }
        }
        return result;
    }

    @Override
    public void initContextMenu(JPopupMenu menu, Graphic<G> src, Point2D point, Object focus, Set<Graphic<G>> selection, G canvas) {
        for (Graphic<G> en : visibleEntriesInReverse()) {
            if ((en instanceof GraphicComposite || en.isContextMenuEnabled()) && en.contains(point, canvas)) {
                en.initContextMenu(menu, en, point, focus, selection, canvas);
            }
        }

        if (isContextMenuEnabled()) {
            super.initContextMenu(menu, this, point, focus, selection, canvas);
        }
    }
    
    //endregion

    //region EVENTS

    /**
     * Called when a graphic has changed.
     * @param source the entry changed
     */
    public void graphicChanged(Graphic<G> source) {
        if (parent != null) {
            parent.graphicChanged(source);
        }
    }

    //endregion
    
}
