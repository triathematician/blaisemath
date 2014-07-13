/*
 * IndexedPointBean.java
 * Created Jan 2011
 */
package com.googlecode.blaisemath.coordinate;

/*
 * #%L
 * BlaiseGraphics
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

import java.awt.geom.Point2D;

/**
 * An object that can get and set an array of points (used to simplify point dragging).
 *
 * @param <C> the type of point
 * 
 * @author Elisha Peterson
 */
public interface IndexedPointBean<C> {
    
    /**
     * Return number of points
     * @return number of points
     */
    int getPointCount();

    /**
     * Return the point.
     * @param i the index
     * @return the point
     */
    C getPoint(int i);

    /**
     * Set the point.
     * @param i the index
     * @param p the new point
     */
    void setPoint(int i, C p);

    /**
     * Return the index of the specified point
     * @param point window location
     * @param testPoint point to look up
     * @return index at the specified location, or -1 if there is no point at that location
     */
    int indexOf(Point2D point, C testPoint);
    
}
