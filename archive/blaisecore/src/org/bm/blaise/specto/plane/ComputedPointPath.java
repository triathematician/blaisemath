/**
 * VComputedPointPath.java
 * Created on Sep 17, 2009
 */

package org.bm.blaise.specto.plane;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import javax.swing.event.ChangeEvent;
import org.bm.blaise.specto.plottable.VPoint;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.primitive.PointStyle;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;

/**
 * <p>
 *   <code>VComputedPointPath</code> displays a path that is recomputed whenever a point
 *   changes. The point is also shown. The class is essentially incomplete with the
 *   exception of the <code>recompute</code> method, provided as an abstract method
 *   here so that subclasses can override it.
 * </p>
 *
 * @param <C> coordinate type of visometry
 * @author Elisha Peterson
 */
public abstract class ComputedPointPath extends ComputedPath {

    /** Stores the initial point. */
    protected VPoint<Point2D.Double> point;

    /** Stores the point style */
    protected PointStyle pointStyle;

    //
    // CONSTRUCTOR
    //

    /** Constructs with given point. */
    public ComputedPointPath(Point2D.Double point) {
        this.point = new VPoint<Point2D.Double>(point);
        pointStyle = PointStyle.SMALL;
        pointStyle.setStrokeColor(BlaisePalette.STANDARD.func1());
        this.point.addChangeListener(this);
    }

    //
    // STYLE PATTERNS
    //

    public PointStyle getPointStyle() {
        return pointStyle;
    }

    public void setPointStyle(PointStyle pointStyle) {
        this.pointStyle = pointStyle;
    }

    public void setPoint(Point2D.Double value) {
        point.setPoint(value);
    }

    public Point2D.Double getPoint() {
        return point.getPoint();
    }

    //
    // DRAW METHODS
    //
    
    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == point)
            needsComputation = true;
        super.stateChanged(e);
    }

    @Override
    public void draw(VisometryGraphics<Point2D.Double> vg) {
        super.draw(vg);
        point.draw(vg);
    }

    //
    // MOUSE EVENT HANDLING
    //

    boolean pointClick = true;

    @Override
    public boolean isClickablyCloseTo(VisometryMouseEvent<Double> e) {
        if (point.isClickablyCloseTo(e)) {
            pointClick = true;
            return true;
        } else {
            pointClick = false;
            return super.isClickablyCloseTo(e);
        }
    }

    @Override
    public void mousePressed(VisometryMouseEvent<Double> e) {
        if (pointClick)
            point.mousePressed(e);
        else
            super.mousePressed(e);
    }

    @Override
    public void mouseDragged(VisometryMouseEvent<Double> e) {
        if (pointClick)
            point.mouseDragged(e);
        else
            super.mouseDragged(e);
    }

    @Override
    public void mouseReleased(VisometryMouseEvent<Double> e) {
        if (pointClick)
            point.mouseReleased(e);
        else
            super.mouseReleased(e);
    }

}
