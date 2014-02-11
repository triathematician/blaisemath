/**
 * Anchor.java
 * Created Sep 2009
 */

package org.blaise.style;

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
