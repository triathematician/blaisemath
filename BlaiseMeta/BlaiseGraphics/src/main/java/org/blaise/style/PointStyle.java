/*
 * PointStyle.java
 * Created Jan 22, 2011
 */

package org.blaise.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Draws a point on a graphics canvas. The point may be oriented, and has a specified radius.
 * 
 * @author Elisha Peterson
 */
public interface PointStyle extends Style {
    
    /**
     * Returns the (approximate) window radius of the associated point
     * @return radius in window pixels
     */
    float getMarkerRadius();

    /**
     * Returns the shape corresponding to the given point, for the style's current settings.
     * @param point the point to draw
     * @return the shape corresponding to the specified point
     */
    Shape markerShape(Point2D point);

    /**
     * Returns the shape corresponding to the given point, for the style's current settings.
     * @param p the point to draw
     * @param angle angle of rotation for the point
     * @return the shape corresponding to the specified point
     */
    Shape markerShape(Point2D p, double angle);
    
    /**
     * Draws specified point on the graphics canvas with visibility options
     * @param point the point to draw
     * @param canvas graphics element to draw on
     */
    void draw(Point2D point, Graphics2D canvas);

    /**
     * Draws a (rotated) point on the graphics canvas with visibility options
     * @param point the point to draw
     * @param angle angle of rotation
     * @param canvas graphics element to draw on
     */
    void draw(Point2D point, double angle, Graphics2D canvas);
    
}
