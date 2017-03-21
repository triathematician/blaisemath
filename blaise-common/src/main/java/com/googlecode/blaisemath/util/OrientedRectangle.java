/**
 * OrientedRectangle.java
 * Created on Mar 19, 2015
 */
package com.googlecode.blaisemath.util;

/*
 * #%L
 * BlaiseCommon
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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A rectangle that provides access to "point 1" and "point 2".
 * @author petereb1
 */
public class OrientedRectangle extends Rectangle2D.Double {

    public OrientedRectangle() {
    }

    public OrientedRectangle(double x, double y, double w, double h) {
        super(x, y, w, h);
    }
    
    public Point2D.Double getPoint1() {
        return new Point2D.Double(x, y);
    }
    
    public Point2D.Double getPoint2() {
        return new Point2D.Double(x+width, y+height);
    }

}
