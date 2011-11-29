/**
 * BasicPointSetGraphic.java
 * Created Jan 22, 2011
 */
package org.bm.blaise.graphics;

import org.bm.blaise.style.PointStyle;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Arrays;
import org.bm.util.Delegator;
import org.bm.util.IndexedPointBean;
import org.bm.util.PointFormatters;

/**
 * A set of points with position.
 * The point set is not dynamic, and all points have the same style.
 * 
 * @see PointStyle
 * 
 * @author Elisha Peterson
 */
public class BasicPointSetGraphic extends GraphicSupport implements IndexedPointBean<Point2D> {

    /** The object that will be drawn. */
    Point2D[] points;
    /** Angle specifying point orientation */
    private double angle = 0;
    
    /** The associated style (may be null). */
    PointStyle style;
    /** Optional elegate for tooltips */
    protected Delegator<Point2D, String> pointTipper;

    //
    // CONSTRUCTORS
    //
    
    /**
     * Construct with no point (defaults to origin)
     */
    public BasicPointSetGraphic() {
        this(new Point2D[]{}, null);
    }

    /** 
     * Construct with no style (will use the default) 
     * @param p initial point
     */
    public BasicPointSetGraphic(Point2D[] p) { 
        this(p, null); 
    }

    /** 
     * Construct with given primitive and style.
     * @param p initial point
     * @param style the style
     */
    public BasicPointSetGraphic(Point2D[] p, PointStyle style) {
        this.points = p;
        this.style = style;
        setMouseListener(new GraphicIndexedPointDragger(this));
    }

    //
    // PROPERTIES
    //

    public Point2D[] getPoint() { return points; }
    public void setPoint(Point2D[] p) {
        if (points != p) {
            points = p;
            fireGraphicChanged();
        }
    }
    public Point2D getPoint(int i) { 
        return points[i]; 
    }
    public void setPoint(int i, Point2D pt) { 
        points[i] = pt;
        fireGraphicChanged();
    }

    public int getPointCount() {
        return points.length;
    }

    public int indexOf(Point2D nearby, Point2D start2) {
        PointStyle rend = drawStyle();
        synchronized(points) {
            for (int i = points.length-1; i >= 0; i--)
                if (rend.shape(points[i]).contains(nearby))
                    return i;
        }
        return -1;
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

    public Delegator<Point2D, String> getPointTipDelegate() {
        return pointTipper;
    }

    public void setPointTipDelegate(Delegator<Point2D, String> pointTipper) {
        this.pointTipper = pointTipper;
    }

    //
    // DRAW METHODS
    //
    
    /** Return the actual style used for drawing */
    private PointStyle drawStyle() {
        return style == null ? parent.getStyleProvider().getPointStyle() : style;
    }

    public void draw(Graphics2D canvas) {
        drawStyle().drawAll(Arrays.asList(points), angle, canvas, visibility);
    }
    
    public boolean contains(Point p) {
        return indexOf(p, p) != -1;
    }

    @Override
    public String getTooltip(Point p) {
        int i = indexOf(p, p);
        return i == -1 ? getPointTooltip(null) : getPointTooltip(points[i]);
    }
    
    /**
     * Overridable method that generates the default tooltip on a point
     * @param pt the point
     * @return formatted location of the point
     */
    public String getPointTooltip(Point2D pt) {
        if (pt == null)
            return null;
        else if (pointTipper == null)
            return PointFormatters.formatPoint(pt, 1);
        else
            return pointTipper.of(pt);
    }
    
}
