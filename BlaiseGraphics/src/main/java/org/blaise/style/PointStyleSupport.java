/*
 * PointStyleSupport.java
 * Created Feb 5, 2011
 */

package org.blaise.style;

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


    public Shape markerShape(Point2D p) {
        return getMarker().create(p, 0, getMarkerRadius());
    }

    public Shape markerShape(Point2D p, double angle) {
        return getMarker().create(p, angle, getMarkerRadius());
    }

    public void draw(Point2D p, Graphics2D canvas, VisibilityHintSet visibility) {
        getShapeStyle().draw(markerShape(p), canvas, visibility);
    }

    public void draw(Point2D p, double angle, Graphics2D canvas, VisibilityHintSet visibility) {
        getShapeStyle().draw(markerShape(p, angle), canvas, visibility);
    }

}
