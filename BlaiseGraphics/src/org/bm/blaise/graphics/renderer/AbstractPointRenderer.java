/*
 * AbstractPointRenderer.java
 * Created Feb 5, 2011
 */

package org.bm.blaise.graphics.renderer;

import org.bm.blaise.graphics.GraphicVisibility;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 * Draws a point on the graphics canvas.
 * @author Elisha
 */
public abstract class AbstractPointRenderer implements PointRenderer {

    /** Return object used to create the shape */
    abstract public ShapeLibrary getShape();
    /** Return radius of the point */
    abstract public float getRadius();
    /** Return object used to draw the shape */
    abstract public ShapeRenderer getShapeRenderer();

    public void draw(Point2D p, Graphics2D canvas, GraphicVisibility visibility) {
        getShapeRenderer().draw(shape(p), canvas, visibility);
    }

    public void draw(Point2D p, double angle, Graphics2D canvas, GraphicVisibility visibility) {
        getShapeRenderer().draw(shape(p, angle), canvas, visibility);
    }

    public void drawAll(Iterable<Point2D> pts, Graphics2D canvas, GraphicVisibility visibility) {
        for (Point2D p : pts)
            getShapeRenderer().draw(shape(p), canvas, visibility);
    }
    
    public Shape shape(Point2D p) {
        return getShape().create(p, 0, getRadius());
    }

    public Shape shape(Point2D p, double angle) {
        return getShape().create(p, angle, getRadius());
    }

}
