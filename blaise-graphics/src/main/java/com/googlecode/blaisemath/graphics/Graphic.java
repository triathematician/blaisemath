package com.googlecode.blaisemath.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2020 Elisha Peterson
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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.StyleHints;
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * An object along with style and renderer information allowing it to be drawn
 * on a graphics canvas. Key additional features are:
 * <ul>
 * <li>A <em>parent</em> (via get and set methods), which is a {@link GraphicComposite} and provides access to
 * default styles of various types.</li>
 * <li>Visibility settings (via get and set methods). See {@link StyleHints} for the parameters.</li>
 * <li>Three methods based on a point on the canvas:
 * <ul>
 * <li> {@link #boundingBox(Object)}, providing a box that encloses the graphic</li>
 * <li> {@link #contains(Point2D, Object)}, testing whether the entry contains a point</li>
 * <li> {@link #getTooltip(Point2D, Object)}, returning the tooltip for a point (or null)</li>
 * </ul>
 * </li>
 * </ul>
 * Implementations must provide the object to be rendered, as well as the
 * render functionality, and they must implement their own drag functionality.
 *
 * @param <G> type of graphics canvas to render to
 *
 * @author Elisha Peterson
 */
public abstract class Graphic<G> {

    public static final String HINT_SELECTION_ENABLED = "selection-enabled";
    public static final String HINT_TOOLTIP_ENABLED = "tooltip-enabled";
    public static final String HINT_POPUP_ENABLED = "popupmenu-enabled";
    public static final String HINT_MOUSE_DISABLED = "mouse-disabled";
    
    /** Stores the parent of this entry */
    protected GraphicComposite<G> parent;
    /** Modifiers (ordered) that are applied to the style before drawing. */
    protected final Set<String> styleHints = Sets.newLinkedHashSet();
    /** Default text of tooltip */
    protected String defaultTooltip = null;
    /** Context initializers */
    protected final List<ContextMenuInitializer<Graphic<G>>> contextMenuInitializers = Lists.newArrayList();
    /** Adds highlights to the graphic on mouseover. */
    protected final GraphicMouseHighlightHandler highlighter = new GraphicMouseHighlightHandler();
    
    /** Handles property listening */
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    /** Stores event eventHandlers for the entry */
    protected final EventListenerList eventHandlers = new EventListenerList();

    /**
     * Initialize graphic
     */
    public Graphic() {
        addMouseListener(highlighter);
    }

    //region PROPERTIES

    /**
     * Return parent of the entry
     * @return parent, possibly null
     */
    public final @Nullable GraphicComposite getParent() {
        return parent;
    }

    /**
     * Set parent of the entry
     * @param p the new parent, possibly null
     */
    public void setParent(@Nullable GraphicComposite p) {
        this.parent = p;
    }

    /**
     * Return set of style hints for the graphic.
     * @return style hints
     */
    public final Set<String> getStyleHints() {
        return Collections.unmodifiableSet(styleHints);
    }

    /**
     * Sets style hints of graphic
     * @param hints new style hints
     */
    public final void setStyleHints(String... hints) {
        setStyleHints(asList(hints));
    }

    /**
     * Sets style hints of graphic
     * @param hints new style hints
     */
    public final void setStyleHints(Iterable<String> hints) {
        styleHints.clear();
        Iterables.addAll(styleHints, hints);
        fireGraphicChanged();
    }

    /**
     * Set status of a particular visibility hint.
     * @param hint hint
     * @param status status of hint
     */
    public final void setStyleHint(String hint, boolean status) {
        boolean change = status ? styleHints.add(hint) : styleHints.remove(hint);
        if (change) {
            fireGraphicChanged();
        }
    }

    /**
     * Whether graphic supports context menu building
     * @return true if yes
     */
    public final boolean isContextMenuEnabled() {
        return styleHints.contains(HINT_POPUP_ENABLED);
    }

    public final void setContextMenuEnabled(boolean val) {
        setStyleHint(HINT_POPUP_ENABLED, val);
    }

    public final void addContextMenuInitializer(ContextMenuInitializer<Graphic<G>> init) {
        if (!contextMenuInitializers.contains(init)) {
            contextMenuInitializers.add(init);
            setContextMenuEnabled(true);
        }
    }

    public final void removeContextMenuInitializer(ContextMenuInitializer<Graphic<G>> init) {
        contextMenuInitializers.remove(init);
        if (contextMenuInitializers.isEmpty()) {
            setContextMenuEnabled(false);
        }
    }

    public final void clearContextMenuInitializers() {
        contextMenuInitializers.clear();
        setContextMenuEnabled(false);
    }

    /**
     * Return true if graphic can be selected. If this flag is set to true, the
     * locator API will be used to map selection gestures (e.g. click to select,
     * or select graphics in box).
     * @return selection flag
     */
    public final boolean isSelectionEnabled() {
        return styleHints.contains(HINT_SELECTION_ENABLED);
    }

    public final void setSelectionEnabled(boolean val) {
        setStyleHint(HINT_SELECTION_ENABLED, val);
    }

    public final boolean isHighlightEnabled() {
        return asList(eventHandlers.getListenerList()).contains(highlighter);
    }

    public final void setHighlightEnabled(boolean val) {
        if (val != isHighlightEnabled()) {
            if (val) {
                addMouseListener(highlighter);
            } else {
                removeMouseListener(highlighter);
            }
        }
    }

    /**
     * Return true if tips are enabled/supported
     * @return true if yes
     */
    public final boolean isTooltipEnabled() {
        return styleHints.contains(HINT_TOOLTIP_ENABLED);
    }

    public final void setTooltipEnabled(boolean val) {
        setStyleHint(HINT_TOOLTIP_ENABLED, val);
    }

    /**
     * Return the default tooltip for this object
     * @return tip
     */
    public String getDefaultTooltip() {
        return defaultTooltip;
    }

    /**
     * Sets the tooltip for this entry. Also updates the enabled tip flag to true.
     * @param tooltip the tooltip
     */
    public final void setDefaultTooltip(String tooltip) {
        setTooltipEnabled(true);
        this.defaultTooltip = tooltip;
    }

    /**
     * Whether the object should receive mouse events.
     * @return true if yes, false otherwise
     */
    public boolean isMouseDisabled() {
        return styleHints.contains(HINT_MOUSE_DISABLED);
    }

    public void setMouseDisabled(boolean val) {
        setStyleHint(HINT_MOUSE_DISABLED, val);
    }

    //endregion

    //region COMPUTED PROPERTIES and LOOKUPS

    /**
     * Return style attributes of the graphic to be used for rendering.
     * The result will have all style hints automatically applied. Any attributes
     * of the parent style are inherited.
     *
     * @return style
     */
    public final AttributeSet renderStyle() {
        AttributeSet renderStyle = getStyle();
        if (renderStyle == null) {
            renderStyle = new AttributeSet();
        }
        Set<String> renderHints = getStyleHints();

        if (parent != null) {
            AttributeSet parStyle = parent.getStyle();
            if (parStyle != null && parStyle != renderStyle.getParent().orElse(null)) {
                renderStyle = renderStyle.flatCopy().immutableWithParent(parStyle);
            }
            Set<String> parStyleHints = parent.getStyleHints();
            Set<String> useHints = parStyleHints == null ? renderHints : Sets.union(renderHints, parStyleHints);
            renderStyle = parent.getStyleContext().applyModifiers(renderStyle, useHints);
        }
        return renderStyle;
    }

    /**
     * Initialize the context menu by adding any actions appropriate for the given parameters.
     * @param menu context menu
     * @param src source graphic displaying the context menu
     * @param point mouse location
     * @param focus focus graphic
     * @param selection selected graphics
     * @param canvas graphics canvas
     */
    public void initContextMenu(JPopupMenu menu, Graphic<G> src, Point2D point, Object focus, Set<Graphic<G>> selection, G canvas) {
        for (ContextMenuInitializer<Graphic<G>> cmi : contextMenuInitializers) {
            cmi.initContextMenu(menu, src, point, focus, selection);
        }
    }

    /**
     * Return tooltip for the specified point
     * @param p the point
     * @param canvas canvas
     * @return the tooltip at the specified location (may be null)
     */
    public String getTooltip(Point2D p, G canvas) {
        return isTooltipEnabled() ? defaultTooltip : null;
    }

    //endregion

    //region ABSTRACT METHODS - STYLE, RENDER, POSITION

    /**
     * Return style set of this graphic
     * @return graphic style
     */
    public abstract AttributeSet getStyle();

    /**
     * Draws the primitive on the specified graphics canvas, using current style.
     * @param canvas graphics canvas
     */
    public abstract void renderTo(G canvas);
    
    /**
     * Method that provides the bounding box enclosing the graphic.
     * @return bounding box
     * @param canvas where graphic is rendered, or null if not rendered
     */
    public abstract @Nullable Rectangle2D boundingBox(@Nullable G canvas);
    
    /**
     * Method used to determine whether the graphic receives mouse events
     * and will be asked to provide a tooltip at the given point. The graphic's
     * {@link MouseListener}s and {@link MouseMotionListener}s will have the
     * opportunity to receive events if the graphic is the topmost element
     * containing the event's point.
     *
     * @param point the window point
     * @param canvas where graphic is rendered
     * @return true if the entry contains the point, else false
     */
    public abstract boolean contains(Point2D point, @Nullable G canvas);

    /**
     * Checks to see if the graphic intersects the area within specified
     * rectangle.
     *
     * @param box rectangle to check against
     * @param canvas where graphic is rendered
     * @return true if it intersects, false otherwise
     */
    public abstract boolean intersects(Rectangle2D box, @Nullable G canvas);

    //endregion

    //region EVENTS

    /** Notify interested listeners of a change. */
    protected void fireGraphicChanged() {
        if (parent != null) {
            parent.graphicChanged(this);
        }
    }
    
    public final void addPropertyChangeListener(PropertyChangeListener pl) {
        pcs.addPropertyChangeListener(pl);
    }
    
    public final void addPropertyChangeListener(String string, PropertyChangeListener pl) {
        pcs.addPropertyChangeListener(string, pl);
    }
    
    public final void removePropertyChangeListener(PropertyChangeListener pl) {
        pcs.removePropertyChangeListener(pl);
    }
    
    public final void removePropertyChangeListener(String string, PropertyChangeListener pl) {
        pcs.removePropertyChangeListener(string, pl);
    }

    /**
     * Adds a mouse listener to the graphic
     * @param handler listener
     */
    public final void addMouseListener(MouseListener handler) {
        requireNonNull(handler);
        eventHandlers.add(MouseListener.class, handler);
    }

    /**
     * Removes a mouse listener from the graphic
     * @param handler listener
     */
    public final void removeMouseListener(MouseListener handler) {
        eventHandlers.remove(MouseListener.class, handler);
    }

    /**
     * Return list of mouse listeners registered with the graphic
     * @return listeners
     */
    public final MouseListener[] getMouseListeners() {
        return eventHandlers.getListeners(MouseListener.class);
    }

    public final void removeMouseListeners() {
        for (MouseListener m : getMouseListeners()) {
            eventHandlers.remove(MouseListener.class, m);
        }
    }

    /**
     * Adds a mouse motion listener to the graphic
     * @param handler listener
     */
    public final void addMouseMotionListener(MouseMotionListener handler) {
        requireNonNull(handler);
        eventHandlers.add(MouseMotionListener.class, handler);
    }

    /**
     * Removes a mouse motion listener from the graphic
     * @param handler listener
     */
    public final void removeMouseMotionListener(MouseMotionListener handler) {
        eventHandlers.remove(MouseMotionListener.class, handler);
    }

    /**
     * Return list of mouse motion listeners registered with the graphic
     * @return listeners
     */
    public final MouseMotionListener[] getMouseMotionListeners() {
        return eventHandlers.getListeners(MouseMotionListener.class);
    }

    public final void removeMouseMotionListeners() {
        for (MouseMotionListener m : getMouseMotionListeners()) {
            eventHandlers.remove(MouseMotionListener.class, m);
        }
    }

    //endregion

}
