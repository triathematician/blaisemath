/*
 * AbstractPlottable.java
 * Created on Sep 14, 2007, 7:49:09 AM
 */
package org.bm.blaise.specto.visometry;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * <p>
 *   Provides an abstract implementation of a <code>Plottable</code>. Implements
 *   visibility properties, as well as code for handling and firing <code>ChangeEvent</code>s.
 *   Sub-classes need only implement the <code>paintComponent</code> method.
 * </p>
 *
 * @param <C> the coordinate type of the plottable
 *
 * @author Elisha Peterson
 */
public abstract class AbstractPlottable<C> implements Plottable<C> {

    //
    // PROPERTIES
    //

    /** Determines visibility of the plottable. */
    protected boolean visible = true;

    /** Selected state of the plottable. */
    protected boolean selected = false;

    //
    // CONSTRUCTORS
    //

    /** 
     * Default constructor does nothing.
     */
    public AbstractPlottable() {
    }    

    //
    // BEAN PATTERNS
    //


    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean newValue) {
        if (visible != newValue) {
            visible = newValue;
            fireStateChanged();
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean newValue) {
        if (selected != newValue) {
            selected = newValue;
            fireStateChanged();
        }
    }




    //
    //
    // COMPUTATIONAL/VISUAL
    //
    //

    /**
     * Method that paints the plottable. Subclasses have full control over this method.
     * They DO NOT need to check whether the plottable as visible, as this will be
     * done by the parent classes.
     * @param vg the graphics object for painting
     */
    public abstract void paintComponent(VisometryGraphics<C> vg);

    //
    // EVENT HANDLING
    //

    /**
     * Handles a change event. By default, passes the ChangeEvent along
     * to interested listeners (particularly the parent class), provided this class
     * itself did not originate the event.
     * @param e the change event
     */
    public void stateChanged(ChangeEvent e) {
        if (!e.getSource().equals(this)) {
            changeEvent = e;
            fireStateChanged();
        }
    }
    
    protected transient ChangeEvent changeEvent = new ChangeEvent(this);
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Removes a listener from the list of classes receiving <code>ChangeEvent</code>s
     *
     * @param l the listener
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    /**
     * Adds a listener to receive <code>ChangeEvent</code>s
     * 
     * @param l the listener
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    /**
     * Fires the change event to listeners.
     */
    protected void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    return;
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }
}
