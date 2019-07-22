package com.googlecode.blaisemath.primitive;

/*-
 * #%L
 * blaise-common
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

import static java.lang.Math.PI;

/**
 * Represents anchor locations and utilities for using them in practice. These anchors can be used for finding points
 * around a circle or for shifting rectangles so various corners are anchored at a target point.
 *
 * @author Elisha Peterson
 */
public enum Anchor {
    
    CENTER(0, 0, 0),
    WEST(PI, -.5, 0),
    NORTHWEST(-0.75 * PI, -.5, -.5),
    NORTH(-0.5 * PI, 0, -.5),
    NORTHEAST(-0.25 * PI, .5, -.5),
    EAST(0, .5, 0),
    SOUTHEAST(0.25 * PI, .5, .5),
    SOUTH(0.5 * PI, 0, .5),
    SOUTHWEST(0.75 * PI, -.5, .5);

    private final double angle;
    private final double xOff;
    private final double yOff;

    /**
     * Initialize parameters for the anchor point.
     * @param angle orientation of the anchor relative to the center point (for circles)
     * @param xOff horizontal offset from rectangle center
     * @param yOff vertical offset from rectangle center
     */
    Anchor(double angle, double xOff, double yOff) {
        this.angle = angle; 
        this.xOff = xOff;
        this.yOff = yOff;
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
     * Represents the relative angle for the specified anchor. This is the location of
     * the anchor relative to a central point.
     * @return angle of anchor 
     */
    public double angle() {
        return angle; 
    }
    
    /**
     * Returns the relative offset of an anchor point for a circle of radius r.
     * @param r radius
     * @return offset of anchor point
     */
    public Point2D offsetForCircle(double r) {
        return this == CENTER ? new Point2D.Double() : new Point2D.Double(r * Math.cos(angle), r * Math.sin(angle));
    }

    /**
     * Returns the absolute location of an anchor point on a circle of radius r.
     * @param center center point of circle
     * @param r radius
     * @return anchor point location
     */
    public Point2D onCircle(Point2D center, double r) {
        return this == CENTER ? center : new Point2D.Double(center.getX() + r * Math.cos(angle), center.getY() + r * Math.sin(angle));
    }

    /**
     * Returns the relative offset of an anchor point for a rectangle of given size. This indicates how much the given anchor
     * point on the outside of the rectangle is shifted from the center of the rectangle.
     * @param wid width of rectangle
     * @param ht height of rectangle
     * @return offset of anchor point
     */
    public Point2D offsetForRectangle(double wid, double ht) {
        return this == CENTER ? new Point2D.Double() : new Point2D.Double(xOff * wid, yOff * ht);
    }

    /**
     * Returns the absolute location of an anchor point on a rectangle of given size.
     * @param rectangle the rectangle
     * @return anchor point location
     */
    public Point2D onRectangle(Rectangle2D rectangle) {
        return this == CENTER ? new Point2D.Double(rectangle.getCenterX(), rectangle.getCenterY())
                : new Point2D.Double(rectangle.getCenterX() + xOff * rectangle.getWidth(), rectangle.getCenterY() + yOff * rectangle.getHeight());
    }
    
    /**
     * Get the rectangle of given size whose corner matching this anchor is pt.
     *
     * @param pt the anchor point
     * @param wid width of rectangle
     * @param ht height of rectangle
     * @return offset position
     */
    public Rectangle2D rectangleAnchoredAt(Point2D pt, double wid, double ht) {
        return rectangleAnchoredAt(pt.getX(), pt.getY(), wid, ht);
    }

    /**
     * Get the rectangle of given size whose corner matching this anchor is pt.
     *
     * @param x anchor x
     * @param y anchor y
     * @param wid width of rectangle
     * @param ht height of rectangle
     * @return offset position
     */
    public Rectangle2D rectangleAnchoredAt(double x, double y, double wid, double ht) {
        Point2D offset = offsetForRectangle(wid, ht);
        Point2D center = new Point2D.Double(x - offset.getX(), y - offset.getY());
        Rectangle2D res = new Rectangle2D.Double();
        res.setFrameFromCenter(center.getX(), center.getY(), center.getX() + .5 * wid, center.getY() + .5 * ht);
        return res;
    }
    
}
