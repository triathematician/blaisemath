/**
 * GraphicSupport.java
 * Created Jan 22, 2011
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

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.util.ContextMenuInitializer;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * <p>
 * An object along with style and renderer information allowing it to be drawn
 * on a graphics canvas.
 * </p>
 * <p>
 * Key additional features are:
 * </p>
 * <ul>
 * <li>A <em>parent</em> (via get and set methods), which is a
 * {@link GraphicComposite} and provides access to default styles of various
 * types.</li>
 * <li>Visibility settings (via get and set methods). See {@link VisibilityHint}
 * for the parameters.</li>
 * <li>Three methods based on a point on the canvas:
 * <ul>
 * <li> {@link Graphic#contains(java.awt.Point)}, testing whether the entry
 * contains a point</li>
 * <li> {@link Graphic#getTooltip(java.awt.Point)}, returning the tooltip for a
 * point (or null)</li>
 * <li> {@link Graphic#getMouseListener(java.awt.Point)}, returning an object
 * that can handle mouse actions</li>
 * </ul>
 * </li>
 * </ul>
 * <p>
 *    Implementations must provide the object to be rendered, as well as the
 *    render functionality, and they must implement their own drag functionality.
 * </p>
 * 
 * @param <G> type of graphics canvas to render to
 *
 * @author Elisha
 */
public abstract class Graphic<G> implements ContextMenuInitializer<Graphic<G>> {

    public static final String SELECTION_ENABLED = "selection-enabled";
    public static final String TOOLTIP_ENABLED = "tooltip-enabled";
    public static final String MOUSE_ENABLED = "mouse-enabled";
    public static final String POPUP_ENABLED = "popupmenu-enabled";
    
    /** Stores the parent of this entry */
    protected GraphicComposite parent;
    /** Modifiers that are applied to the style before drawing. */
    protected AttributeSet styleHints = new AttributeSet();
    /** Default text of tooltip */
    protected String defaultTooltip = null;
    /** Context initializers */
    protected final List<ContextMenuInitializer<Graphic<G>>> contextMenuInitializers = Lists.newArrayList();
    /** Adds highlights to the graphic on mouseover. */
    protected final HighlightOnMouseoverHandler highlighter = new HighlightOnMouseoverHandler();
    
    /** Handles property listening */
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    /** Stores event eventHandlers for the entry */
    protected final EventListenerList eventHandlers = new EventListenerList();

    /**
     * Initialize graphic
     */
    public Graphic() {
        addMouseListener(highlighter);
        styleHints.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent ce) {
                fireGraphicChanged();
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="COMPOSITION API">
    //
    // COMPOSITION
    //

    /**
     * Return parent of the entry
     * @return parent, possibly null
     */
    @Nullable 
    public GraphicComposite getParent() {
        return parent;
    }

    /**
     * Set parent of the entry
     * @param p the new parent, possibly null
     */
    @Nullable
    public void setParent(@Nullable GraphicComposite p) {
        this.parent = p;
    }

    /** Notify interested listeners of a change in the plottable. */
    protected void fireGraphicChanged() {
        if (parent != null) {
            parent.graphicChanged(this);
        }
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="STYLE API">
    //
    // STYLE & DRAWING
    //

    /**
     * Return style attributes of the graphic to be used for rendering.
     * The result will have all style hints automatically applied.
     * @return style
     */
    public AttributeSet renderStyle() {
        AttributeSet rawStyle = getStyle();
        if (rawStyle == null) {
            rawStyle = new AttributeSet();
        }
        AttributeSet rawHints = getStyleHints();
        if (parent != null) {
            rawStyle.setParent(parent.getStyle());
            rawHints.setParent(parent.getStyleHints());
            rawStyle = parent.getStyleContext().applyHints(rawStyle, rawHints);
        }
        return rawStyle;
    }
    
    /**
     * Return style set of this graphic
     * @return graphic style
     */
    public abstract AttributeSet getStyle();
    
    /**
     * Return set of style hints for the graphic.
     * @return style hints
     */
    public AttributeSet getStyleHints() {
        return styleHints;
    }

    /**
     * Sets style hints of graphic
     * @param hints new style hints
     */
    public void setStyleHints(AttributeSet hints) {
        this.styleHints = hints;
        fireGraphicChanged();
    }
    
    /**
     * Set status of a particular visibility hint.
     * @param hint hint
     * @param status status of hint
     */
    public void setStyleHint(String hint, boolean status) {
        styleHints.put(hint, status);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="RENDER API">
    //
    // RENDER API
    //
    
    /**
     * Draws the primitive on the specified graphics canvas, using current
     * style.
     * @param canvas graphics canvas
     */
    public abstract void renderTo(G canvas);

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="LOCATOR API">
    //
    // LOCATOR API
    //
    
    /**
     * Method used to determine whether the graphic receives {@link MouseEvent}s
     * and will be asked to provide a tooltip at the given point. The graphic's
     * {@link MouseListener}s and {@link MouseMotionListener}s will have the
     * opportunity to receive events if the graphic is the topmost element
     * containing the event's point.
     *
     * @param point the window point
     * @return true if the entry contains the point, else false
     */
    public abstract boolean contains(Point2D point);

    /**
     * Checks to see if the graphic intersects the area within specified
     * rectangle.
     *
     * @param box rectangle to check against
     * @return true if it intersects, false otherwise
     */
    public abstract boolean intersects(Rectangle2D box);

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CONTEXT MENU API">
    //
    // CONTEXT MENU
    //

    /**
     * Whether graphic supports context menu building
     * @return true if yes
     */
    public boolean isContextMenuEnabled() {
        return styleHints.getBoolean(POPUP_ENABLED, false);
    }

    public void setContextMenuEnabled(boolean val) {
        styleHints.put(POPUP_ENABLED, val);
    }

    public void addContextMenuInitializer(ContextMenuInitializer<Graphic<G>> init) {
        if (!contextMenuInitializers.contains(init)) {
            contextMenuInitializers.add(init);
            setContextMenuEnabled(true);
        }
    }

    public void removeContextMenuInitializer(ContextMenuInitializer<Graphic<G>> init) {
        contextMenuInitializers.remove(init);
        if (contextMenuInitializers.isEmpty()) {
            setContextMenuEnabled(false);
        }
    }

    @Override
    public void initContextMenu(JPopupMenu menu, Graphic<G> src, Point2D point, Object focus, Set selection) {
        for (ContextMenuInitializer<Graphic<G>> cmi : contextMenuInitializers) {
            cmi.initContextMenu(menu, src, point, focus, selection);
        }
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="SELECTION API">
    //
    // SELECTION
    //

    /**
     * Return true if graphic can be selected. If this flag is set to true, the
     * locator API will be used to map selection gestures (e.g. click to select,
     * or select graphics in box).
     * @return selection flag
     */
    public boolean isSelectionEnabled() {
        return styleHints.getBoolean(SELECTION_ENABLED, false);
    }

    public void setSelectionEnabled(boolean val) {
        styleHints.put(SELECTION_ENABLED, val);
    }

    public boolean isHighlightEnabled() {
        return Arrays.asList(eventHandlers.getListenerList()).contains(highlighter);
    }

    public void setHighlightEnabled(boolean val) {
        if (val != isHighlightEnabled()) {
            if (val) {
                addMouseListener(highlighter);
            } else {
                removeMouseListener(highlighter);
            }
        }
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TOOLTIP API">
    //
    // TOOLTIP API
    //
    
    /**
     * Return true if tips are enabled/supported
     * @return true if yes
     */
    public boolean isTooltipEnabled() {
        return styleHints.getBoolean(TOOLTIP_ENABLED, true);
    }

    public final void setTooltipEnabled(boolean val) {
        styleHints.put(TOOLTIP_ENABLED, val);
    }

    /**
     * Return tooltip for the specified point
     * @param p the point
     * @return the tooltip at the specified location (may be null)
     */
    public String getTooltip(Point2D p) {
        return isTooltipEnabled() ? defaultTooltip : null;
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
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY CHANGE LISTENING">
    //
    // PROPERTY CHANGE LISTENING
    //
    
    public void addPropertyChangeListener(PropertyChangeListener pl) {
        pcs.addPropertyChangeListener(pl);
    }
    
    public void addPropertyChangeListener(String string, PropertyChangeListener pl) {
        pcs.addPropertyChangeListener(string, pl);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener pl) {
        pcs.removePropertyChangeListener(pl);
    }
    
    public void removePropertyChangeListener(String string, PropertyChangeListener pl) {
        pcs.removePropertyChangeListener(string, pl);
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="MOUSE HANDLING">
    //
    // MOUSE HANDLING
    //
    
    /**
     * Whether the object should receive mouse events.
     * @return true if yes, false otherwise
     */
    public boolean isMouseEnabled() {
        return styleHints.getBoolean(MOUSE_ENABLED, true);
    }

    public void setMouseEnabled(boolean val) {
        styleHints.put(MOUSE_ENABLED, val);
    }

    public void removeMouseListeners() {
        for (MouseListener m : getMouseListeners()) {
            eventHandlers.remove(MouseListener.class, m);
        }
    }

    public void removeMouseMotionListeners() {
        for (MouseMotionListener m : getMouseMotionListeners()) {
            eventHandlers.remove(MouseMotionListener.class, m);
        }
    }

    /**
     * Adds a mouse listener to the graphic
     * @param handler listener
     */
    public final void addMouseListener(MouseListener handler) {
        checkNotNull(handler);
        eventHandlers.add(MouseListener.class, handler);
    }

    /**
     * Removes a mouse listener from the graphic
     * @param handler listener
     */
    public void removeMouseListener(MouseListener handler) {
        eventHandlers.remove(MouseListener.class, handler);
    }

    /**
     * Return list of mouse listeners registered with the graphic
     * @return listeners
     */
    public MouseListener[] getMouseListeners() {
        return eventHandlers.getListeners(MouseListener.class);
    }

    /**
     * Adds a mouse motion listener to the graphic
     * @param handler listener
     */
    public void addMouseMotionListener(MouseMotionListener handler) {
        checkNotNull(handler);
        eventHandlers.add(MouseMotionListener.class, handler);
    }

    /**
     * Removes a mouse motion listener from the graphic
     * @param handler listener
     */
    public void removeMouseMotionListener(MouseMotionListener handler) {
        eventHandlers.remove(MouseMotionListener.class, handler);
    }

    /**
     * Return list of mouse motion listeners registered with the graphic
     * @return listeners
     */
    public MouseMotionListener[] getMouseMotionListeners() {
        return eventHandlers.getListeners(MouseMotionListener.class);
    }

    //</editor-fold>

}
