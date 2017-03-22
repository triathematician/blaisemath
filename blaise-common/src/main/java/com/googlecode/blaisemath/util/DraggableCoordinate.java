/*
 * DraggablePointBean.java
 * Created Jan 229, 2011
 */
package com.googlecode.blaisemath.util;

import com.googlecode.blaisemath.util.CoordinateBean;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2017 Elisha Peterson
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
 * Interface that can get and set a point in an arbitrary coordinate system.
 * A third method allows the point to be set based on an initial point, and
 * coordinates for the start and end of a drag gesture.
 * 
 * @see CoordinateBean
 * 
 * @param <C> coordinate of the point
 * 
 * @author Elisha Peterson
 */
public interface DraggableCoordinate<C> extends CoordinateBean<C> {
    
    /** 
     * Sets the point by movement from an initial point 
     * @param initial starting position
     * @param dragStart start of drag
     * @param dragFinish end of drag
     */
    void setPoint(C initial, C dragStart, C dragFinish);
    
}
