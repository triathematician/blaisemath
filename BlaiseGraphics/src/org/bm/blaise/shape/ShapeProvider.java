/*
 * ShapeProvider.java
 * Created Jan 22, 2011
 */

package org.bm.blaise.shape;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Generates shapes at a given point. Angle and radius parameters allow the shape
 * to be sized and oriented as well.
 * 
 * @author Elisha
 */
public interface ShapeProvider {

    /**
     * Generates a shape at specified point with specified radius.
     * @param p the center of the resulting shape
     * @param angle specifies orientation of the resulting shape
     * @param radius specifies the radius of the resulting shape
     */
    public Shape create(Point2D p, double angle, float radius);

}
