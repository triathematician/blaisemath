package com.googlecode.blaisemath.palette.ui;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2019 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



/**
 * Object describing ways in which a particular list of colors may be edited.
 * @author Elisha Peterson
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
