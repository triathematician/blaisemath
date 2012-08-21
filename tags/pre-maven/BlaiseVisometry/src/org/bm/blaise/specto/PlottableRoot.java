/*
 * PlottableRoot.java
 * Created Sep 19, 2011
 */
package org.bm.blaise.specto;

import java.awt.Component;

/**
 * Root node for a collection of plottables.
 * 
 * @author Elisha Peterson
 */
public class PlottableRoot<C> extends PlottableComposite<C> {
    
    /** Parent component upon which the graphics are drawn. */
    protected Component component;
    
    /**
     * Sets the component associated with the graphic tree.
     * @param c the component
     */
    void initComponent(Component c) {
        this.component = c;
    }

    //
    // EVENT HANDLING
    //
    
    /**
     * Repaints when a computation is needed.
     * @param p the requestor
     */
    @Override
    public void computeNeeded(Plottable p) {
        if (component != null)
            component.repaint();
    }

}
