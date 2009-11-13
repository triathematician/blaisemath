/*
 * AbstractPlottable.java
 * Created on Sep 14, 2007, 7:49:09 AM
 */
package org.bm.blaise.specto.visometry;

import javax.swing.event.ChangeListener;

/**
 * <p>
 *  This interface consists of basic features for an object that can be drawn on
 *  a <code>VisometryGraphics</code> canvas. The idea is to convert an underlying
 *  mathematical object into something that can be seen on the plot. The class has
 *  methods that show/hide the plottable.
 * </p>
 * <p>
 *  Finally, the class extends <code>ChangeListener</code> and adds patterns for
 *  adding and removing other <code>ChangeListener</code>'s to this class. This
 *  allows for other classes to be notified if the plottable is changed visually,
 *  and allows it to be "recomputed" if the underlying mathematical objects change.
 * </p>
 * <p>
 *  The <code>AbstractPlottable</code> class implements basic functionality for
 *  most of these features.
 * </p>
 *
 * @see AbstractPlottable
 * @param <C> the coordinate type of the plottable
 *
 * @author Elisha Peterson
 */
public interface Plottable<C> extends ChangeListener {

    //
    //
    // BEAN PATTERNS
    //
    //

    /** 
     * Sets whether plot is visible.
     */
    public void setVisible(boolean newValue);

    /** 
     * Returns visibility status.
     */
    public boolean isVisible();

    /**
     * Sets selected status.
     */
    public void setSelected(boolean newValue);

    /**
     * Whether the plottable is selected.
     */
    public boolean isSelected();

    //
    //
    // COMPUTATIONAL/VISUAL
    //
    //

    /**
     * Method that paints the plottable. Subclasses have full control over this method.
     * They DO NOT need to check whether the plottable as visible, as this will be
     * done by the parent classes.
     * @param vg the visometry graphics object for painting
     */
    public void paintComponent(VisometryGraphics<C> vg);

    //
    //
    // EVENT HANDLING
    //
    //

    /**
     * Removes a listener from the list of classes receiving <code>ChangeEvent</code>s
     * @param l the listener
     */
    public void addChangeListener(ChangeListener l);

    /**
     * Adds a listener to receive <code>ChangeEvent</code>s
     * @param l the listener
     */
    public void removeChangeListener(ChangeListener l);
}
