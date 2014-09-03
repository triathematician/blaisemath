/**
 * DraggablePoint2D.java
 * Created Aug 1, 2014
 */
package com.googlecode.blaisemath.util.geom;

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


import com.googlecode.blaisemath.util.coordinate.CoordinateBean;
import java.awt.geom.Point2D;

/**
 * An instance of {@link Point2D} that is also a {@link CoordinateBean}.
 * @author Elisha
 */
public class Point2DBean extends Point2D.Double implements CoordinateBean<Point2D> {

    public Point2DBean() {
    }

    public Point2DBean(double x, double y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "DraggablePoint{" + getX() + ',' + getY() + '}';
    }
    
    public Point2D getPoint() {
        return this;
    }
    
    public void setPoint(Point2D p) {
        setLocation(p);
    }
    
}
