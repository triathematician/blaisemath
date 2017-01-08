/**
 * MouseGestureSupport.java
 * Created Nov 2016
 */
package com.googlecode.blaisemath.gesture;

/*
 * #%L
 * blaise-gestures
 * --
 * Copyright (C) 2015 - 2016 Elisha Peterson
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
