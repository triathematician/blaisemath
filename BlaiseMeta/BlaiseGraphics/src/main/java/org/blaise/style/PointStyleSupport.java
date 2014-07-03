/*
 * PointStyleSupport.java
 * Created Feb 5, 2011
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
 * Provides most of the functionality to draw a point on a graphics canvas.
 * Sub-classes must provide a {@link Marker} to describe the shapes associated
 * with points, and a {@link ShapeStyle} to render those shapes on the canvas.
 *
 * @author Elisha
 */
public abstract class PointStyleSupport implements PointStyle {

    /**
     * Return object used to create the shape.
     * @return object that will create the point's shape
     */
    public abstract Marker getMarker();

    /**
     * Return object used to draw the shape.
     * @return style, used to draw the shape on the canvas
     */
    protected abstract ShapeStyle getShapeStyle();


    @Override
    public Shape markerShape(Point2D p) {
        return getMarker().create(p, 0, getMarkerRadius());
    }

    @Override
    public Shape markerShape(Point2D p, double angle) {
        return getMarker().create(p, angle, getMarkerRadius());
    }

    @Override
    public void draw(Point2D p, Graphics2D canvas) {
        getShapeStyle().draw(markerShape(p), canvas);
    }

    @Override
    public void draw(Point2D p, double angle, Graphics2D canvas) {
        getShapeStyle().draw(markerShape(p, angle), canvas);
    }

}
