/**
 * BasicPointGraphic.java
 * Created Jan 22, 2011
 */
package org.bm.blaise.graphics;

import org.bm.blaise.style.PointStyle;
import org.bm.util.PointBean;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * A point with position, orientation, and an associated style.
 * Implements {@code GraphicMouseListener.PointBean}, allowing the point to be dragged around.
 * 
 * @see PointStyle
 * 
 * @author Elisha Peterson
 */
public class BasicPointGraphic extends GraphicSupport
        implements PointBean<Point2D> {

    /** The object that will be drawn. */
    Point2D point;
    /** Angle specifying point orientation */
    private double angle = 0;
    
    /** The associated style (may be null). */
    PointStyle style;

    //
    // CONSTRUCTORS
    //
    
    /**
     * Construct with no point (defaults to origin)
     */
    public BasicPointGraphic() {
        this(new Point2D.Double(), null);
    }

    /** 
     * Construct with no style (will use the default) 
     * @param p initial point
     */
    public BasicPointGraphic(Point2D p) { 
        this(p, null); 
    }

    /** 
     * Construct with given primitive and style.
     * @param p initial point
     * @param style the style
     */
    public BasicPointGraphic(Point2D p, PointStyle style) {
        this.point = p;
        this.style = style;
        setMouseListener(new GraphicPointDragger(this));
    }

    //
    // PROPERTIES
    //

    public Point2D getPoint() { return point; }
    public void setPoint(Point2D p) {
        if (point != p) {
            point = new Point2D.Double(p.getX(), p.getY());
            fireGraphicChanged();
        }
    }

    /**
     * Return orientation/angle of the point
     * @return angle
     */
    public double getAngle() { return angle; }
    /**
     * Set orientation/angle of the point
     * @param d angle
     */
    public void setAngle(double d) {
        if (angle != d) {
            angle = d;
            fireGraphicChanged();
        }
    }

    /**
     * Return the style for the point
     * @return style, or null if there is none
     */
    public PointStyle getStyle() { 
        return style; 
    }
    
    /**
     * Set the style for the point
     * @param style the style; may be null
     */
    public void setStyle(PointStyle style) {
        if (this.style != style) {
            this.style = style;
            fireAppearanceChanged();
        }
    }

    //
    // DRAW METHODS
    //
    
    /** Return the actual style used for drawing */
    private PointStyle drawStyle() {
        return style == null ? parent.getStyleProvider().getPointStyle() : style;
    }

    public void draw(Graphics2D canvas) {
        drawStyle().draw(point, angle, canvas, visibility);
    }

    public boolean contains(Point p) {
        return drawStyle().shape(point).contains(p);
    }
}
