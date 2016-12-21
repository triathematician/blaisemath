/**
 * MouseGestureWrapper.java
 * Created Nov 2016
 */
package com.googlecode.blaisemath.gesture;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Wraps basic mouse listeners as gestures. Cannot be activated.
 * 
 * @author elisha
 */
public abstract class DelegatingMouseGesture extends MouseGestureSupport {

    protected DelegatingMouseGesture(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public Integer getDesiredCursor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean cancelsWhen(InputEvent event) {
        return false;
    }

    @Override
    public boolean activate() {
        return false;
    }

    @Override
    public void complete() {
        throw new IllegalStateException();
    }

    @Override
    public boolean cancel() {
        throw new IllegalStateException();
    }
    
}
