/**
 * GraphicSupport.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

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
import org.blaise.style.VisibilityHint;
import org.blaise.style.VisibilityHintSet;
import org.blaise.util.ContextMenuInitializer;

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
    /** Stores visibility status */
    protected final VisibilityHintSet visibility = new VisibilityHintSet();

    /** Flag indicating whether tips are enabled */
    protected boolean tipEnabled = false;
    /** Flag indicating whether popups are enabled */
    protected boolean popupEnabled = false;
    /** Flag indicating whether generic mouse listening is enabled */
    protected boolean mouseEnabled = true;
    /** Flag indicating whether the object can be selected */
    protected boolean selectEnabled = true;

    /** Default text of tooltip */
    protected String defaultTooltip = null;
    
    /** Highlighter */
    protected final GraphicHighlightHandler highlighter = new GraphicHighlightHandler();

    /** Context initializers */
    protected final List<ContextMenuInitializer<Graphic>> contextMenuInitializers = Lists.newArrayList();

    /** Stores event eventHandlers for the entry */
    protected EventListenerList eventHandlers = new EventListenerList();

    /**
     * Initialize graphic
     */
    public GraphicSupport() {
        addMouseListener(highlighter);
        visibility.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent ce) {
                fireGraphicChanged();
            }
        });
    }


    //
    // COMPOSITION
    //

    public GraphicComposite getParent() {
        return parent;
    }

    public void setParent(GraphicComposite p) {
        this.parent = p;
    }

    //
    // STYLE & DRAWING
    //

    public VisibilityHintSet getVisibilityHints() {
        return visibility;
    }

    /**
     * Set status of a particular visibility hint.
     * @param hint hint
     * @param status status of hint
     */
    public void setVisibilityHint(VisibilityHint hint, boolean status) {
        if (status) {
            visibility.add(hint);
        } else {
            visibility.remove(hint);
        }
    }

    //
    // CONTEXT MENU
    //

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

    public void initContextMenu(JPopupMenu menu, Graphic src, Point2D point, Object focus, Set selection) {
        for (ContextMenuInitializer<Graphic> cmi : contextMenuInitializers) {
            cmi.initContextMenu(menu, src, point, focus, selection);
        }
    }


    //
    // SELECTION
    //

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

    public boolean isTooltipEnabled() {
        return tipEnabled;
    }

    public void setTooltipEnabled(boolean val) {
        tipEnabled = val;
    }

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

    public final void addMouseListener(MouseListener handler) {
        checkNotNull(handler);
        eventHandlers.add(MouseListener.class, handler);
    }

    public void removeMouseListener(MouseListener handler) {
        eventHandlers.remove(MouseListener.class, handler);
    }

    public MouseListener[] getMouseListeners() {
        return eventHandlers.getListeners(MouseListener.class);
    }

    public void addMouseMotionListener(MouseMotionListener handler) {
        checkNotNull(handler);
        eventHandlers.add(MouseMotionListener.class, handler);
    }

    public void removeMouseMotionListener(MouseMotionListener handler) {
        eventHandlers.remove(MouseMotionListener.class, handler);
    }

    public MouseMotionListener[] getMouseMotionListeners() {
        return eventHandlers.getListeners(MouseMotionListener.class);
    }

    //</editor-fold>

}
