/*
 * PointStyleSupport.java
 * Created Feb 5, 2011
 */

package org.bm.blaise.style;

import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 * Draws a point on the graphics canvas.
 * @author Elisha
 */
public abstract class PointStyleSupport implements PointStyle {

    /** Return object used to create the shape */
    abstract public ShapeLibrary getShape();
    /** Return radius of the point */
    abstract public float getRadius();
    /** Return object used to draw the shape */
    abstract protected ShapeStyle getShapeStyle();

    public void draw(Point2D p, Graphics2D canvas, VisibilityKey visibility) {
        getShapeStyle().draw(shape(p), canvas, visibility);
    }

    public void draw(Point2D p, double angle, Graphics2D canvas, VisibilityKey visibility) {
        getShapeStyle().draw(shape(p, angle), canvas, visibility);
    }

    public void drawAll(Iterable<Point2D> pts, Graphics2D canvas, VisibilityKey visibility) {
        for (Point2D p : pts)
            getShapeStyle().draw(shape(p), canvas, visibility);
    }

    public void drawAll(Iterable<Point2D> pts, double angle, Graphics2D canvas, VisibilityKey visibility) {
        for (Point2D p : pts)
            getShapeStyle().draw(shape(p, angle), canvas, visibility);
    }
    
    public Shape shape(Point2D p) {
        return getShape().create(p, 0, getRadius());
    }

    public Shape shape(Point2D p, double angle) {
        return getShape().create(p, angle, getRadius());
    }

}
