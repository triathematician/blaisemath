/**
 * ShapeFromPoints.java
 * Created Oct 11, 2014
 */
package com.googlecode.blaisemath.util;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2014 - 2018 Elisha Peterson
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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.annotation.Nullable;

/**
 * Creates a shape from two mouse positions.
 * 
 * @author elisha
 */
public abstract class ShapeFromPoints {
    
    /**
     * Create from nullable points.
     * @param press where mouse was pressed
     * @param release where it was released
     * @return created shape, or null if either point was null
     */
    @Nullable
    public Shape createFromNullable(@Nullable Point2D press, @Nullable Point2D release) {
        return press == null || release == null ? null
                : create(press, release);
    }
    
    /**
     * Create shape from mouse positions. May generate exceptions if either point
     * is null.
     * @param press where mouse was pressed
     * @param release where it was released
     * @return created shape
     */
    public abstract Shape create(Point2D press, Point2D release);
    
    
    public static class Line extends ShapeFromPoints {
        @Override
        public Shape create(Point2D press, Point2D release) {
            return new Line2D.Double(press, release);
        }
    }
    
    public static class Circle extends ShapeFromPoints {
        @Override
        public Shape create(Point2D press, Point2D release) {
            double rad = press.distance(release);
            return new Ellipse2D.Double(press.getX()-rad, press.getY()-rad, 2*rad, 2*rad);
        }
    }
    
    public static class Ellipse extends ShapeFromPoints {
        @Override
        public Shape create(Point2D press, Point2D release) {
            Ellipse2D.Double res = new Ellipse2D.Double();
            res.setFrameFromDiagonal(press, release);
            return res;
        }
    }
    
    public static class Rectangle extends ShapeFromPoints {
        @Override
        public Shape create(Point2D press, Point2D release) {
            Rectangle2D.Double res = new Rectangle2D.Double();
            res.setFrameFromDiagonal(press, release);
            return res;
        }
    }
    
}
