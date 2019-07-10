package com.googlecode.blaisemath.palette.ui;

/*-
 * #%L
 * ******************************* UNCLASSIFIED *******************************
 * ColorListEditConstraints.java
 * edu.jhuapl.vis:conjecture-legacy
 * %%
 * Copyright (C) 2012 - 2017 Johns Hopkins University Applied Physics Laboratory
 * %%
 * (c) Johns Hopkins University Applied Physics Laboratory. All Rights Reserved.
 * JHU-APL Proprietary Information. Do not disseminate without prior approval.
 * #L%
 */

/**
 * Object describing ways in which a particular list of colors may be edited.
 * @author petereb1
 */
public class ColorListEditConstraints {
    
    boolean addSupported = true;
    boolean removable = true;
    boolean keysEditable = true;

    public boolean isKeyEditable(String item) {
        return isKeysEditable();
    }

    public boolean isRemovable(String item) {
        return isRemovable();
    }

    //<editor-fold defaultstate="collapsed" desc="BUILDER PATTERN">
    
    public ColorListEditConstraints addSupported(boolean val) {
        setAddSupported(val);
        return this;
    }
    
    public ColorListEditConstraints removable(boolean val) {
        setRemovable(val);
        return this;
    }
    
    public ColorListEditConstraints keysEditable(boolean val) {
        setKeysEditable(val);
        return this;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    
    public boolean isAddSupported() {
        return addSupported;
    }
    
    public void setAddSupported(boolean addSupported) {
        this.addSupported = addSupported;
    }
    
    public boolean isRemovable() {
        return removable;
    }
    
    public void setRemovable(boolean removable) {
        this.removable = removable;
    }
    
    public boolean isKeysEditable() {
        return keysEditable;
    }
    
    public void setKeysEditable(boolean keysEditable) {
        this.keysEditable = keysEditable;
    }
    
    //</editor-fold>

}
