/*
 * VGraphic.java
 * Created Apr 2010
 * Substantial Revision Jan 28, 2011
 */

package com.googlecode.blaisemath.visometry;

/*
 * #%L
 * BlaiseVisometry
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import com.googlecode.blaisemath.graphics.core.Graphic;

/**
 * <p>
 *      Stores a graphic primitive that exists in a local coordinate system. 
 *      Looks a lot like {@link Graphic}, except that the corresponding methods are in terms of local coordinates instead of window coordinates. 
 *      This class also adds in support for the conversion steps;
 *      once the conversion is done, the corresponding window graphic may be retrieved.
 * </p>
 *
 * @param <C> the object coordinate type
 * @param <G> graphics canvas type
 * 
 * @author Elisha Peterson
 *
 * @see Graphic
 */
public interface VGraphic<C,G> {

    /**
     * Returns the graphic entry. Should never be called unless {@link #isUnconverted()} returns true.
     * The return value should never be null.
     * @return the regular (window coordinates) graphic entry computed after conversion
     */
    Graphic<G> getWindowGraphic();
     
    /** 
      * Return parent of the graphic
      * @return parent, possibly null 
      */
    VGraphicComposite<C,G> getParentGraphic();
    
    /** 
     * Sets parent of the graphic 
     * @param parent the parent
     */
    void setParentGraphic(VGraphicComposite<C,G> parent);
    
    /** 
     * Return true if graphic needs conversion to local coords
     * @return true if graphic needs to be converted to window coordinates 
     */
    boolean isUnconverted();
    
    /** 
     * Used to set the converted flag, determining whether the entry to be reconverted before drawing again 
     * @param flag if true, graphic needs conversion before display
     */
    void setUnconverted(boolean flag);

    /**
     * Performs the conversion from local coordinates to window coordinates
     * @param vis the underlying visometry
     * @param processor provides some basic translation utilities
     */
    void convert(Visometry<C> vis, VisometryProcessor<C> processor);

}
