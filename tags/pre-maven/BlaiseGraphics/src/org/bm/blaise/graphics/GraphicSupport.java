/**
 * GraphicSupport.java
 * Created Jan 22, 2011
 */
package org.bm.blaise.graphics;

import java.awt.event.MouseEvent;
import javax.swing.Action;
import org.bm.blaise.style.VisibilityKey;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
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
    /** Actions associated with the graphic */
    protected final List<Action> actions = new ArrayList<Action>();

    
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
    
    //</editor-fold>
    

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

    
    //<editor-fold defaultstate="collapsed" desc="MOUSE HANDLING">
    //
    // MOUSE HANDLING
    //

    /** Stores a mouse handler for the entry */
    protected final List<GMouseListener> mouseHandlers = new ArrayList<GMouseListener>();

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
    public void addMouseListener(GMouseListener handler) {
        if (handler == null)
            throw new IllegalArgumentException();
        mouseHandlers.add(handler);
    }

    /**
     * Removes a mouse listener.
     * @param handler mouse listener
     */
    public void removeMouseListener(GMouseListener handler) {
        mouseHandlers.remove(handler);
    }
    
    public boolean interestedIn(MouseEvent e) {
        if (!contains(e.getPoint()))
            return false;
        for (GMouseListener g : mouseHandlers)
            if (g.interestedIn(e))
                return true;
        return false;
    }

    public void mouseClicked(MouseEvent e) {
        GMouseEvent gme = (GMouseEvent) e;
        for (GMouseListener g : mouseHandlers)
            if (g.interestedIn(gme)) {
                g.mouseClicked(gme);
                if (gme.isConsumed())
                    return;
            }
    }

    public void mouseEntered(MouseEvent e) {
        GMouseEvent gme = (GMouseEvent) e;
        for (GMouseListener g : mouseHandlers)
            if (g.interestedIn(gme)) {
                g.mouseEntered(gme);
                if (gme.isConsumed())
                    return;
            }
    }

    public void mouseExited(MouseEvent e) {
        GMouseEvent gme = (GMouseEvent) e;
        for (GMouseListener g : mouseHandlers)
            if (g.interestedIn(gme)) {
                g.mouseExited(gme);
                if (gme.isConsumed())
                    return;
            }
    }

    public void mousePressed(MouseEvent e) {
        GMouseEvent gme = (GMouseEvent) e;
        for (GMouseListener g : mouseHandlers)
            if (g.interestedIn(gme)) {
                g.mousePressed(gme);
                if (gme.isConsumed())
                    return;
            }
    }

    public void mouseReleased(MouseEvent e) {
        GMouseEvent gme = (GMouseEvent) e;
        for (GMouseListener g : mouseHandlers)
            if (g.interestedIn(gme)) {
                g.mouseReleased(gme);
                if (gme.isConsumed())
                    return;
            }
    }

    public void mouseDragged(MouseEvent e) {
        GMouseEvent gme = (GMouseEvent) e;
        for (GMouseListener g : mouseHandlers)
            if (g.interestedIn(gme)) {
                g.mouseDragged(gme);
                if (gme.isConsumed())
                    return;
            }
    }

    public void mouseMoved(MouseEvent e) {
        GMouseEvent gme = (GMouseEvent) e;
        for (GMouseListener g : mouseHandlers)
            if (g.interestedIn(gme)) {
                g.mouseMoved(gme);
                if (gme.isConsumed())
                    return;
            }
    }
    
    //</editor-fold>

    public List<Action> getActions() {
        return actions;
    }

}
