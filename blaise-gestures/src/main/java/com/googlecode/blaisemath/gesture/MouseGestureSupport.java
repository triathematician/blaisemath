/**
 * MouseGestureSupport.java
 * Created Nov 2016
 */
package com.googlecode.blaisemath.gesture;

import java.awt.event.MouseAdapter;

/**
 * Partial implementation of {@link MouseGesture}.
 * @author elisha
 */
public abstract class MouseGestureSupport extends MouseAdapter implements MouseGesture {
    
    /** User-friendly name of the gesture */
    protected final String name;
    /** Description of the gesture */
    protected final String description;
    
    /** Current cursor setting */
    protected Integer cursor = null;

    protected  MouseGestureSupport(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // <editor-fold defaultstate="collapsed" desc="PROPERTIES">
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDesription() {
        return description;
    }
    
    // </editor-fold>
    
}
