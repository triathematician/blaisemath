/**
 * GraphicSupport.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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
    protected VisibilityHint visibility = VisibilityHint.Regular;
    /** Whether the object receives mouse events or not */
    protected boolean mouseSupported = true;
    /** Stores a tooltip for the entry (may be null) */
    protected String tooltip;

    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public GraphicComposite getParent() {
        return parent;
    }

    public void setParent(GraphicComposite p) {
        this.parent = p;
    }

    public VisibilityHint getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityHint visibility) {
        if (this.visibility != visibility) {
            this.visibility = visibility;
            fireGraphicChanged();
        }
    }

    /**
     * Sets the tooltip for this entry
     * @param tooltip the tooltip
     */
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public boolean isMouseSupported() {
        return mouseSupported;
    }

    public void setMouseSupported(boolean val) {
        this.mouseSupported = val;
    }
    
    //</editor-fold>
    

    public String getTooltip(Point p) {
        return tooltip;
    }

    /** Notify interested listeners of a change in the plottable. */
    protected void fireGraphicChanged() {
        if (parent != null)
            parent.graphicChanged(this);
    }

    
    //<editor-fold defaultstate="collapsed" desc="MOUSE HANDLING">
    //
    // MOUSE HANDLING
    //

    /** Stores event handlers for the entry */
    protected EventListenerList handlers = new EventListenerList();

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
