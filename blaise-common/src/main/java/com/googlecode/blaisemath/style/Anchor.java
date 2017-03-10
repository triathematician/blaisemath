/**
 * Anchor.java
 * Created Sep 2009
 */

package com.googlecode.blaisemath.style;

/*
 * #%L
 * blaise-common
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

/**
 * Anchor points for a string. Provides 8 compass directions, as well as a
 * central anchor point.
 * 
 * @author Elisha Peterson
 */
public enum Anchor {
    
    CENTER(0, -.5, .5) {
        @Override
        public Point2D getOffset(double r) {
            return new Point2D.Double();
        }
    }, 
    WEST(0, 0, .5),
    NORTHWEST(0.25 * Math.PI, 0, 1),
    NORTH(0.5 * Math.PI, -.5, 1),
    NORTHEAST(0.75 * Math.PI, -1, 1),
    EAST(Math.PI, -1, .5),
    SOUTHEAST(1.25 * Math.PI, -1, 0),
    SOUTH(1.5 * Math.PI, -.5, 0),
    SOUTHWEST(1.75 * Math.PI, 0, 0);

    private final double angle;
    private final double xOff;
    private final double yOff;
    
    Anchor(double angle, double xOff, double yOff) {
        this.angle = angle; 
        this.xOff = xOff;
        this.yOff = yOff;
    }

    /** 
     * Represents the relative angle for the specified anchor.
     * @return angle of anchor 
     */
    public double getAngle() { 
        return angle; 
    }
    
    /**
     * Returns an offset anchor point for a circle of given radius.
     * @param r radius
     * @return offset of anchor point
     */
    public Point2D getOffset(double r) {
        return new Point2D.Double(r*Math.cos(angle), r*Math.sin(angle));
    }
    
    /**
     * Returns an offset that allows a rectangle to be drawn from a given
     * anchor point rather than from the bottom left.
     * @param wid width of rectangle
     * @param ht height of rectangle
     * @return offset position
     */
    public Point2D getRectOffset(double wid, double ht) {
        return new Point2D.Double(xOff*wid, yOff*ht);
    }
    
}
