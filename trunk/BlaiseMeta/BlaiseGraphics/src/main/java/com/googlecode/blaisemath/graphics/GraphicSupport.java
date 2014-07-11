/**
 * GraphicSupport.java
 * Created Jan 22, 2011
 */
package com.googlecode.blaisemath.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import com.googlecode.blaisemath.style.context.StyleHintSet;
import com.googlecode.blaisemath.util.ContextMenuInitializer;

/**
 * <p>
 *      Provides much of the functionality required in a {@link Graphic}, including tooltips, default mouse eventHandlers,
      a parent object, and a visibility property.
      Adds {@link GraphicSupport#fireGraphicChanged()} to notify the parent when it changes.
 * </p>
 *
 * @author Elisha
 */
public abstract class GraphicSupport implements Graphic {

    /** Stores the parent of this entry */
    protected GraphicComposite parent;
    /** Modifiers that are applied to the style before drawing. */
    protected final StyleHintSet styleHints = new StyleHintSet();
    /** Default text of tooltip */
    protected String defaultTooltip = null;

    /** Flag indicating whether tips are enabled */
    protected boolean tipEnabled = false;
    /** Flag indicating whether popups are enabled */
    protected boolean popupEnabled = false;
    /** Flag indicating whether generic mouse listening is enabled */
    protected boolean mouseEnabled = true;
    /** Flag indicating whether the object can be selected */
    protected boolean selectEnabled = true;
    
    /** Adds highlights to the graphic on mouseover. */
    protected final GraphicHighlighter highlighter = new GraphicHighlighter();
    /** Context initializers */
    protected final List<ContextMenuInitializer<Graphic>> contextMenuInitializers = Lists.newArrayList();
    /** Stores event eventHandlers for the entry */
    protected final EventListenerList eventHandlers = new EventListenerList();

    /**
     * Initialize graphic
     */
    public GraphicSupport() {
        addMouseListener(highlighter);
        styleHints.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent ce) {
                fireGraphicChanged();
            }
        });
    }


    //
    // COMPOSITION
    //

    @Override
    public GraphicComposite getParent() {
        return parent;
    }

    @Override
    public void setParent(GraphicComposite p) {
        this.parent = p;
    }

    //
    // STYLE & DRAWING
    //

    @Override
    public StyleHintSet getStyleHints() {
        return styleHints;
    }

    /**
     * Set status of a particular visibility hint.
     * @param hint hint
     * @param status status of hint
     */
    public void setStyleHint(String hint, boolean status) {
        if (status) {
            styleHints.add(hint);
        } else {
            styleHints.remove(hint);
        }
    }

    //
    // CONTEXT MENU
    //

    @Override
    public boolean isContextMenuEnabled() {
        return popupEnabled;
    }

    public void setContextMenuEnabled(boolean val) {
        popupEnabled = val;
    }

    public void addContextMenuInitializer(ContextMenuInitializer<Graphic> init) {
        if (!contextMenuInitializers.contains(init)) {
            contextMenuInitializers.add(init);
            setContextMenuEnabled(true);
        }
    }

    public void removeContextMenuInitializer(ContextMenuInitializer<Graphic> init) {
        contextMenuInitializers.remove(init);
        if (contextMenuInitializers.isEmpty()) {
            setContextMenuEnabled(false);
        }
    }

    @Override
    public void initContextMenu(JPopupMenu menu, Graphic src, Point2D point, Object focus, Set selection) {
        for (ContextMenuInitializer<Graphic> cmi : contextMenuInitializers) {
            cmi.initContextMenu(menu, src, point, focus, selection);
        }
    }


    //
    // SELECTION
    //

    @Override
    public boolean isSelectionEnabled() {
        return selectEnabled;
    }

    public void setSelectionEnabled(boolean val) {
        selectEnabled = val;
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

    //
    // TOOLTIPS
    //

    @Override
    public boolean isTooltipEnabled() {
        return tipEnabled;
    }

    public void setTooltipEnabled(boolean val) {
        tipEnabled = val;
    }

    @Override
    public String getTooltip(Point2D p) {
        return tipEnabled ? defaultTooltip : null;
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
    public void setDefaultTooltip(String tooltip) {
        setTooltipEnabled(true);
        this.defaultTooltip = tooltip;
    }

    //
    // GENERAL EVENT HANDLING
    //

    /** Notify interested listeners of a change in the plottable. */
    protected void fireGraphicChanged() {
        if (parent != null) {
            parent.graphicChanged(this);
        }
    }


    //<editor-fold defaultstate="collapsed" desc="MOUSE HANDLING">
    //
    // MOUSE HANDLING
    //

    @Override
    public boolean isMouseEnabled() {
        return mouseEnabled;
    }

    public void setMouseEnabled(boolean val) {
        this.mouseEnabled = val;
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

    @Override
    public final void addMouseListener(MouseListener handler) {
        checkNotNull(handler);
        eventHandlers.add(MouseListener.class, handler);
    }

    @Override
    public void removeMouseListener(MouseListener handler) {
        eventHandlers.remove(MouseListener.class, handler);
    }

    @Override
    public MouseListener[] getMouseListeners() {
        return eventHandlers.getListeners(MouseListener.class);
    }

    @Override
    public void addMouseMotionListener(MouseMotionListener handler) {
        checkNotNull(handler);
        eventHandlers.add(MouseMotionListener.class, handler);
    }

    @Override
    public void removeMouseMotionListener(MouseMotionListener handler) {
        eventHandlers.remove(MouseMotionListener.class, handler);
    }

    @Override
    public MouseMotionListener[] getMouseMotionListeners() {
        return eventHandlers.getListeners(MouseMotionListener.class);
    }

    //</editor-fold>

}
