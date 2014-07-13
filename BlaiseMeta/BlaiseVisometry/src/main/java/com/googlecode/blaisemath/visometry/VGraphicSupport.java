/**
 * VAbstractGraphicEntry.java
 * Created Jan 29, 2011
 */
package com.googlecode.blaisemath.visometry;

/*
 * #%L
 * BlaiseVisometry
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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
 * Implements much of the basic functionality of {@link VGraphic}.
 *
 * @param <C> the object coordinate type
 * 
 * @see org.bm.blaise.graphics.GraphicSupport
 * @see org.bm.blaise.specto.PlottableSupport
 * 
 * @author Elisha Peterson
 */
public abstract class VGraphicSupport<C> implements VGraphic<C> {

    /** Stores the parent of this entry */
    protected VGraphicComposite<C> parent;
    
    /** Flag indicating whether needs conversion */
    private boolean notConverted = true;

    
    public VGraphicComposite<C> getParentGraphic() { 
        return parent; 
    }
    
    public void setParentGraphic(VGraphicComposite<C> parent) {
        this.parent = parent;
    }

    public boolean isUnconverted() { 
        return notConverted; 
    }
    
    public void setUnconverted(boolean flag) {
        notConverted = flag;
        if (flag) {
            fireConversionNeeded();
        }
    }

    /** 
     * Notify listeners of a need for conversion.
     */
    protected void fireConversionNeeded() {
        if (parent != null) {
            parent.conversionNeeded(this);
        }
    }
}
