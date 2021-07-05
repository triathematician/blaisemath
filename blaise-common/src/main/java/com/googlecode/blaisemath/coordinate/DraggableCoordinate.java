package com.googlecode.blaisemath.coordinate;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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
 * Marks object methods that are used to get/set coordinates.
 * A third method allows the point to be set based on an initial point, and
 * coordinates for the start and end of a drag gesture.
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
    
    /**
     * Wraps a point as a {@link DraggableCoordinate} object.
     * @param pt the point
     * @return wrapped instance of point as a point bean
     */
    static DraggableCoordinate<Point2D> create(final Point2D pt) {
        return new DraggableCoordinate<Point2D>() {
            @Override
            public Point2D getPoint() {
                return pt;
            }
            @Override
            public void setPoint(Point2D p) {
                pt.setLocation(p);
            }
            @Override
            public void setPoint(Point2D initial, Point2D dragStart, Point2D dragFinish) {
                pt.setLocation(initial.getX() + dragFinish.getX() - dragStart.getX(),
                        initial.getY() + dragFinish.getY() - dragStart.getY());
            }
        };
    }
}
