/**
 * GraphicSupport.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPopupMenu;
import javax.swing.event.EventListenerList;
import org.blaise.style.VisibilityHint;

/**
 * <p>
 *      Provides much of the functionality required in a {@link Graphic}, including tooltips, default mouse handlers,
 *      a parent object, and a visibility property.
 *      Adds {@link GraphicSupport#fireGraphicChanged()} to notify the parent when it changes.
 * </p>
 *
 * @author Elisha
 */
public abstract class GraphicSupport implements Graphic {

    /** Stores the parent of this entry */
    protected GraphicComposite parent;
    /** Stores visibility status */
    protected final Set<VisibilityHint> visibility = new HashSet<VisibilityHint>();

    /** Flag indicating whether tips are enabled */
    protected boolean tipEnabled = false;
    /** Flag indicating whether popups are enabled */
    protected boolean popupEnabled = false;
    /** Flag indicating whether generic mouse listening is enabled */
    protected boolean mouseEnabled = true;
    /** Flag indicating whether the object can be selected */
    protected boolean selectEnabled = true;
    
    /** Default text of tooltip */
    protected String tipText = null;

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
    
    public Set<VisibilityHint> getVisibilityHints() {
        return visibility;
    }

    public void setVisibilityHints(Set<VisibilityHint> visibility) {
        if (this.visibility != visibility) {
            this.visibility.clear();
            this.visibility.addAll(visibility);
            fireGraphicChanged();
        }
    }

    public void setVisibilityHint(VisibilityHint hint, boolean status) {
        if (status && visibility.add(hint)) {
            fireGraphicChanged();
        } else if (!status && visibility.remove(hint)) {
            fireGraphicChanged();
        }
    }
    
    //
    // CONTEXT MENU
    //

    /** Context initializers */
    protected final List<ContextMenuInitializer> cInits = new ArrayList<ContextMenuInitializer>();
    
    public boolean isContextMenuEnabled() {
        return popupEnabled;
    }
    
    public void setContextMenuEnabled(boolean val) {
        popupEnabled = val;
    }

    public void addContextMenuInitializer(ContextMenuInitializer init) {
        if (!cInits.contains(init)) {
            cInits.add(init);
            setContextMenuEnabled(true);
        }
    }

    public void removeContextMenuInitializer(ContextMenuInitializer init) {
        cInits.remove(init);
        if (cInits.isEmpty()) {
            setContextMenuEnabled(false);
        }
    }

    public void initialize(JPopupMenu menu, Point point) {
        for (ContextMenuInitializer cmi : cInits) {
            cmi.initialize(menu, point);
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

    //
    // TOOLTIPS
    //

    public boolean isTooltipEnabled() {
        return tipEnabled;
    }
    
    public void setTooltipEnabled(boolean val) {
        tipEnabled = val;
    }
    
    public String getTooltip(Point p) {
        return tipEnabled ? tipText : null;
    }
    
    /**
     * Return the default tooltip for this object
     * @return tip
     */
    public String getDefaultTooltip() {
        return tipText;
    }

    /**
     * Sets the tooltip for this entry. Also updates the enabled tip flag to true.
     * @param tooltip the tooltip
     */
    public void setDefaultTooltip(String tooltip) {
        setTooltipEnabled(true);
        this.tipText = tooltip;
    }

    //
    // GENERAL EVENT HANDLING
    //

    /** Stores event handlers for the entry */
    protected EventListenerList handlers = new EventListenerList();

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
        for (MouseListener m : getMouseListeners())
            handlers.remove(MouseListener.class, m);
    }
    
    public void removeMouseMotionListeners() {
        for (MouseMotionListener m : getMouseMotionListeners())
            handlers.remove(MouseMotionListener.class, m);
    }
    
    public void addMouseListener(MouseListener handler) {
        if (handler == null)
            throw new IllegalArgumentException();
        handlers.add(MouseListener.class, handler);
    }

    public void removeMouseListener(MouseListener handler) {
        handlers.remove(MouseListener.class, handler);
    }
    
    public MouseListener[] getMouseListeners() {
        return handlers.getListeners(MouseListener.class);
    }
    
    public void addMouseMotionListener(MouseMotionListener handler) {
        if (handler == null)
            throw new IllegalArgumentException();
        handlers.add(MouseMotionListener.class, handler);
    }

    public void removeMouseMotionListener(MouseMotionListener handler) {
        handlers.remove(MouseMotionListener.class, handler);
    }
    
    public MouseMotionListener[] getMouseMotionListeners() {
        return handlers.getListeners(MouseMotionListener.class);
    }
    
    //</editor-fold>

}
