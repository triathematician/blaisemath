/*
 * PointBean.java
 * Created Jan 2011
 */
package com.googlecode.blaisemath.util.coordinate;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2015 Elisha Peterson
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
 * An object that can get and set a point (used to simplify point dragging).
 *
 * @param <C> the type of point
 * 
 * @author Elisha Peterson
 */
public interface CoordinateBean<C> {

    /**
     * Return the point.
     * @return the point
     */
    C getPoint();

    /**
     * Set the point.
     * @param p the new point
     */
    void setPoint(C p);
    
}
