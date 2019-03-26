/**
 * Anchor.java
 * Created Sep 2009
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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Anchor points for a string. Provides 8 compass directions, as well as a
 * central anchor point.
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
     * Get the opposite anchor point.
     * @return opposite
     */
    public Anchor opposite() {
        switch (this) {
            case CENTER:
                return CENTER;
            case WEST:
                return EAST;
            case NORTHWEST:
                return SOUTHEAST;
            case NORTH:
                return SOUTH;
            case NORTHEAST:
                return SOUTHWEST;
            case EAST:
                return WEST;
            case SOUTHEAST:
                return NORTHWEST;
            case SOUTH:
                return NORTH;
            case SOUTHWEST:
                return NORTHEAST;
            default:
                throw new IllegalStateException();
        }
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
     * Returns an offset that allows a rectangle to be drawn from the bottom left
     * rather than a given anchor point. So if you want to draw a rectangle of
     * size (width, height) anchored at the CENTER, for instance, you have
     * to add (-.5*wid, .5*ht) to the anchor (x,y) coordinates to get the bottom left
     * coordinate of the resulting rectangle.
     * 
     * @param wid width of rectangle
     * @param ht height of rectangle
     * @return offset position
     */
    public Point2D getRectOffset(double wid, double ht) {
        return new Point2D.Double(xOff*wid, yOff*ht);
    }
    
    /**
     * Returns an offset that allows a rectangle to be drawn from the bottom left
     * rather than a given anchor point. So if you want to draw a rectangle of
     * size (width, height) anchored at the CENTER, for instance, you have
     * to add (-.5*wid, .5*ht) to the anchor (x,y) coordinates to get the bottom left
     * coordinate of the resulting rectangle.
     * 
     * @param x anchor x position
     * @param y anchor y position
     * @param wid width of rectangle
     * @param ht height of rectangle
     * @return offset position
     */
    public Rectangle2D anchoredRectangle(double x, double y, double wid, double ht) {
        return new Rectangle2D.Double(x+xOff*wid, y+yOff*ht, wid, ht);
    }
    
}
