/*
 * ShapeFactory.java
 * Created Jan 22, 2011
 */

package org.blaise.style;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Generates shapes at a given point. Angle and radius parameters allow the shape
 * to be sized and oriented as well. See http://www.w3.org/TR/SVG/painting.html#Markers.
 * 
 * @author Elisha
 */
public interface Marker {

    /**
     * Generates a shape at specified point with specified radius.
     * @param point the center of the resulting shape
     * @param orientation specifies orientation of the resulting shape
     * @param markerRadius specifies the radius of the resulting shape
     */
    Shape create(Point2D point, double orientation, float markerRadius);

}
