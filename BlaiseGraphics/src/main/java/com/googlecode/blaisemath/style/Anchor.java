/**
 * Anchor.java
 * Created Sep 2009
 */

package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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
 */
public enum Anchor {
    
    CENTER(0) {
        @Override
        public Point2D getOffset(double r) {
            return new Point2D.Double();
        }
    }, 
    WEST(0),
    NORTHWEST(0.25 * Math.PI),
    NORTH(0.5 * Math.PI),
    NORTHEAST(0.75 * Math.PI),
    EAST(Math.PI),
    SOUTHEAST(1.25 * Math.PI),
    SOUTH(1.5 * Math.PI),
    SOUTHWEST(1.75 * Math.PI);

    private final double angle;
    
    Anchor(double angle) {
        this.angle = angle; 
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
    
}
