/*
 * ShapeFactory.java
 * Created Jan 22, 2011
 */

package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
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

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Generates shapes at a given point. Angle and radius parameters allow the shape
 * to be sized and oriented as well. See http://www.w3.org/TR/SVG/painting.html#Markers.
 * 
 * @author Elisha
 */
public interface Marker {

    /**
     * Generates a shape at specified point with specified radius.
     * @param point the center of the resulting shape
     * @param orientation specifies orientation of the resulting shape
     * @param markerRadius specifies the radius of the resulting shape
     * @return marker shape
     */
    Shape create(Point2D point, double orientation, float markerRadius);

}
