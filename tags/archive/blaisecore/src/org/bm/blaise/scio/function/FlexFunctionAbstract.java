/*
 * FlexSpaceCurve.java
 * Created on Jan 9, 2010
 */

package org.bm.blaise.scio.function;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import scio.function.utils.DemoCurve3D;
import util.ChangeEventHandler;

/**
 * This class is a class that wraps an actual function together with demo functions,
 * allowing for automated generation of "demo"s.
 * 
 * @author ae3263
 * @see scio.function.utils.DemoCurve3D
 */
public abstract class FlexFunctionAbstract<F> implements ChangeEventHandler {
    
    protected F baseFunc;

    public FlexFunctionAbstract() {
    }

    public FlexFunctionAbstract(F baseFunc) {
        this.baseFunc = baseFunc;
    }

    //
    // GETTERS & SETTERS
    //

    abstract protected boolean isDemo();

    public F getBaseFunc() {
        return baseFunc;
    }

    public void setBaseFunc(F baseFunc) {
        if (this.baseFunc != baseFunc) {
            this.baseFunc = baseFunc;
            fireStateChanged();
        }
    }

    //
    // EVENT HANDLING
    //

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
