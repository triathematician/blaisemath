/**
 * GraphicSupport.java
 * Created Jan 22, 2011
 */
package org.bm.blaise.graphics;

import org.bm.blaise.style.VisibilityKey;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *      Provides much of the functionality required in a {@link Graphic}, including
 *      tooltips and default mouse handlers. Adds {@link GraphicSupport#fireGraphicChanged()}
 *      and {@link GraphicSupport#fireAppearanceChanged()} to notify the parent
 *      when the entry has changed. Does not implement {@link Graphic#draw(java.awt.Graphics2D)}
 *      or {@link Graphic#contains(java.awt.Point)}.
 * </p>
 *
 * @author Elisha
 */
public abstract class GraphicSupport implements Graphic {

    /** Stores the parent of this entry */
    protected GraphicComposite parent;
    /** Stores visibility status */
    protected VisibilityKey visibility = VisibilityKey.Regular;
    /** Stores a tooltip for the entry (may be null) */
    protected String tooltip;


    public GraphicComposite getParent() {
        return parent;
    }

    public void setParent(GraphicComposite p) {
        this.parent = p;
    }

    public VisibilityKey getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityKey visibility) {
        if (this.visibility != visibility) {
            this.visibility = visibility;
            fireGraphicChanged();
        }
    }

    public String getTooltip(Point p) {
        return tooltip;
    }

    /**
     * Sets the tooltip for this entry
     * @param tooltip the tooltip
     */
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    /** Notify interested listeners of a change in the plottable. */
    protected void fireGraphicChanged() {
        if (parent != null)
            parent.graphicChanged(this);
    }

    /** Notify interested listeners of an appearance-only change for the plottable. */
    public void fireAppearanceChanged() {
        if (parent != null)
            parent.appearanceChanged(this);
    }

    //
    // MOUSE HANDLING
    //

    /** Stores a mouse handler for the entry */
    protected final List<GraphicMouseListener> mouseHandlers = new ArrayList<GraphicMouseListener>();

    public GraphicMouseListener getMouseListener(Point p) {
        for (GraphicMouseListener handler : mouseHandlers)
            if (handler.interestedIn(p))
                return handler;
        return null;
    }

    /**
     * Clears out all mouse listeners.
     */
    public void clearMouseListeners() {
        mouseHandlers.clear();
    }

    /**
     * Sets a default mouse handler that will be used for this entry
     * @param handler default mouse handler
     */
    public void addMouseListener(GraphicMouseListener handler) {
        if (handler == null)
            throw new IllegalArgumentException();
        mouseHandlers.add(handler);
    }

    /**
     * Removes a mouse listener.
     * @param handler mouse listener
     */
    public void removeMouseListener(GraphicMouseListener handler) {
        mouseHandlers.remove(handler);
    }

}
