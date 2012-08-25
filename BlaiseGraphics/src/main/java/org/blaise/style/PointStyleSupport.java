/*
 * PointStyleSupport.java
 * Created Feb 5, 2011
 */

package org.blaise.style;

import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Set;

/**
 * Provides most of the functionality to draw a point on a graphics canvas.
 * Sub-classes must provide a {@link ShapeProvider} to describe the shapes associated
 * with points, and a {@link ShapeStyle} to render those shapes on the canvas.
 *
 * @author Elisha
 */
public abstract class PointStyleSupport implements PointStyle {

    /**
     * Return object used to create the shape.
     * @return object that will create the point's shape
     */
    public abstract ShapeProvider getShape();

    /**
     * Return object used to draw the shape.
     * @return style, used to draw the shape on the canvas
     */
    protected abstract ShapeStyle getShapeStyle();


    public void draw(Point2D p, Graphics2D canvas, Set<VisibilityHint> visibility) {
        getShapeStyle().draw(shape(p), canvas, visibility);
    }

    public void draw(Point2D p, double angle, Graphics2D canvas, Set<VisibilityHint> visibility) {
        getShapeStyle().draw(shape(p, angle), canvas, visibility);
    }

    public Shape shape(Point2D p) {
        return getShape().create(p, 0, getRadius());
    }

    public Shape shape(Point2D p, double angle) {
        return getShape().create(p, angle, getRadius());
    }

}
