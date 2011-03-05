/**
 * VAbstractGraphicEntry.java
 * Created Jan 29, 2011
 */
package visometry.graphics;

import org.bm.blaise.graphics.AbstractGraphicEntry;

/**
 * Provides common methods for <code>VGraphicEntry</code> classes.
 * @author Elisha
 */
public abstract class AbstractVGraphicEntry<C> implements VGraphicEntry<C> {

    /** Stores the parent of this entry */
    protected VCompositeGraphicEntry parent;
    /** Flag indicating whether needs conversion */
    private boolean notConverted = true;
    /** Stores a mouse handler for the entry (may be null) */
    protected VGraphicMouseListener mouseHandler;

    //
    // METHOD IMPLEMENTATIONS
    //

    /** @return parent associated with the entry */
    public VCompositeGraphicEntry getParent() { return parent; }

    public boolean isUnconverted() { return notConverted; }
    public void setUnconverted(boolean flag) {
        notConverted = flag;
        if (flag)
            fireStateChanged();
    }

    public VGraphicMouseListener getMouseListener() { return mouseHandler; }
    public void setMouseListener(VGraphicMouseListener l) {
        mouseHandler = l;
        if (l != null && getWindowEntry() != null && getWindowEntry() instanceof AbstractGraphicEntry)
            ((AbstractGraphicEntry)getWindowEntry()).setMouseListener(l.adapter());
    }

    /** Notify interested listeners of a need for conversion */
    protected void fireStateChanged() {
        if (parent != null)
            parent.fireConversionRequest(this);
    }
}
