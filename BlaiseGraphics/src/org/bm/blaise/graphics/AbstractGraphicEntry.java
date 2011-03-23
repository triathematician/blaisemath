/**
 * AbstractGraphicEntry.java
 * Created Jan 22, 2011
 */
package org.bm.blaise.graphics;

import org.bm.blaise.graphics.renderer.GraphicRendererProvider;
import java.awt.Point;

/**
 *
 * @author Elisha
 */
public abstract class AbstractGraphicEntry implements GraphicEntry {

    /** Stores visibility status */
    protected GraphicVisibility visibility = GraphicVisibility.Regular;
    /** Stores the parent of this entry */
    protected GraphicEntry parent;
    /** Stores a mouse handler for the entry */
    protected GraphicMouseListener mouseHandler;
    /** Stores a tooltip for the entry (may be null) */
    protected String tooltip;

    
// <editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">

    public GraphicEntry getParent() { return parent; }
    public void setParent(GraphicEntry p) { this.parent = p; }

    public GraphicVisibility getVisibility() { return visibility; }
    public void setVisibility(GraphicVisibility visibility) {
        if (this.visibility != visibility) {
            this.visibility = visibility;
            fireStateChanged();
        }
    }

    public GraphicMouseListener getMouseListener(Point p) { return mouseHandler; }
    /** Sets a default mouse handler that will be used for this entry */
    public void setMouseListener(GraphicMouseListener handler) { mouseHandler = handler; }

    public String getTooltip(Point p, GraphicRendererProvider provider) { return tooltip; }
    /** Sets the tooltip for this entry */
    public void setTooltip(String tooltip) { this.tooltip = tooltip; }
    
// </editor-fold>


    /** Change received from child entry. By default, passes along the change. */
    public void stateChanged(GraphicEntry en) { fireStateChanged(); }

    /** Notify interested listeners of an (unspecified) change in the plottable. */
    protected void fireStateChanged() {
        if (parent != null)
            parent.stateChanged(this);
    }

}
