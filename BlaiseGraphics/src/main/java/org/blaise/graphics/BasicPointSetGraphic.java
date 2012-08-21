/**
 * BasicPointSetGraphic.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import org.blaise.style.PointStyle;
import org.blaise.style.VisibilityHint;
import org.blaise.util.Delegator;
import org.blaise.util.PointFormatters;

/**
 * A collection of points, all rendered with the same style. Tooltips and
 * {@link VisibilityHint}s may depend on the point.
 *
 * @see PointStyle
 *
 * @author Elisha Peterson
 */
public class BasicPointSetGraphic extends GraphicSupport implements IndexedVisibilityGraphic<Point2D> {

    /** The points that will be drawn. */
    protected Point2D[] points;

    /** The associated style (may be null). */
    protected PointStyle style;
    /** Optional delegate for tooltips */
    protected Delegator<Point2D, String> pointTipper;
    /** Optional delegate for visibility keys */
    protected Delegator<Point2D, VisibilityHint> visibilityDelegate;

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
        IndexedPointBeanDragger dragger = new IndexedPointBeanDragger(this);
        addMouseListener(dragger);
        addMouseMotionListener(dragger);
        addMouseListener(new IndexedGraphicHighlighter());
    }

    @Override
    public String toString() {
        return "Point Set";
    }


    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
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

    public void setVisibility(final int i, final VisibilityHint key) {
        if (i == -1) {
            visibilityDelegate = null;
            visibility = key;
        } else {
            setVisibilityDelegate(new Delegator<Point2D, VisibilityHint>(){
                public VisibilityHint of(Point2D src) { return i < points.length && src == points[i] ? key : visibility; }
                });
        }
        fireGraphicChanged();
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
            fireGraphicChanged();
        }
    }

    public Delegator<Point2D, String> getPointTipDelegate() {
        return pointTipper;
    }

    public void setPointTipDelegate(Delegator<Point2D, String> pointTipper) {
        this.pointTipper = pointTipper;
    }

    public Delegator<Point2D, VisibilityHint> getVisibilityDelegate() {
        return visibilityDelegate;
    }

    public void setVisibilityDelegate(Delegator<Point2D, VisibilityHint> visibilityDelegate) {
        this.visibilityDelegate = visibilityDelegate;
    }

    //</editor-fold>

    
    //
    // GRAPHIC METHODS
    //

    public boolean contains(Point p) {
        return indexOf(p, p) != -1;
    }

    public boolean intersects(Rectangle box) {
        PointStyle drawer = drawStyle();
        for (Point2D p : points)
            if (drawer.shape(p).intersects(box))
                return true;
        return false;
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
    

    //
    // DRAW METHODS
    //

    /** Return the actual style used for drawing */
    private PointStyle drawStyle() {
        return style == null ? parent.getStyleProvider().getPointStyle(this) : style;
    }

    public void draw(Graphics2D canvas) {
        PointStyle drawer = drawStyle();
        for (Point2D p : points)
            drawer.draw(p, canvas, visibilityDelegate == null ? visibility : visibilityDelegate.of(p));
    }

}
