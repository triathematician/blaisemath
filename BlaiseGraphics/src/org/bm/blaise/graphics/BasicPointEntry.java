/**
 * BasicPointEntry.java
 * Created Jan 22, 2011
 */
package org.bm.blaise.graphics;

import org.bm.blaise.graphics.renderer.GraphicRendererProvider;
import org.bm.blaise.graphics.renderer.PointRenderer;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 *
 * @author Elisha
 */
public class BasicPointEntry extends AbstractGraphicEntry
        implements GraphicMouseListener.PointBean {

    /** The object that will be drawn. */
    private Point2D point;
    /** Angle specifying point orientation */
    private double angle = 0;
    /** The associated renderer (may be null). */
    private PointRenderer renderer;

    //
    // CONSTRUCTORS
    //

    /** Construct with no renderer (will use the default) */
    public BasicPointEntry(Point2D p) { this(p, null); }

    /** Construct with given primitive and renderer. */
    public BasicPointEntry(Point2D p, PointRenderer renderer) {
        this.point = p;
        this.renderer = renderer;
        setMouseListener(new GraphicMouseListener.PointDragger(this));
    }

    //
    // PROPERTY PATTERNS
    //

    public Point2D getPoint() { return point; }
    public void setPoint(Point2D p) {
        if (point != p) {
            point = new Point2D.Double(p.getX(), p.getY());
            fireStateChanged();
        }
    }

    public double getAngle() { return angle; }
    public void setAngle(double d) {
        if (angle != d) {
            angle = d;
            fireStateChanged();
        }
    }

    public PointRenderer getRenderer() { return renderer; }
    public void setRenderer(PointRenderer renderer) {
        if (this.renderer != renderer) {
            this.renderer = renderer;
            fireStateChanged();
        }
    }

    //
    // DRAW METHODS
    //

    public void draw(Graphics2D canvas, GraphicRendererProvider rend) {
        if (renderer == null)
            rend.getPointRenderer().draw(point, angle, canvas, visibility);
        else
            renderer.draw(point, angle, canvas, visibility);
    }

    public boolean contains(Point p, GraphicRendererProvider factory) {
        try {
        return renderer != null ? renderer.shape(point).contains(p)
                : factory.getPointRenderer().shape(point).contains(p);
        } catch (Exception ex) {
            System.out.println("here");
            return false;
        }
    }
}
