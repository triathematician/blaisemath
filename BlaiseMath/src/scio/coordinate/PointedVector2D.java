/*
 * PointedVector2D.java
 * Created on Oct 31, 2007, 8:45:24 AM
 */
package scio.coordinate;

import java.awt.geom.Point2D;

/**
 * <p>
 *   Represents a vector in the plane that is based at a particular point.
 * </p>
 * @author ae3263
 */
public class PointedVector2D {

    public Point2D.Double base = new Point2D.Double();
    public Point2D.Double vec = new Point2D.Double();

    public PointedVector2D() {
    }

    public PointedVector2D(Point2D.Double base, Point2D.Double vec) {
        this.base = base;
        this.vec = vec;
    }

    public Point2D.Double getBase() {
        return base;
    }

    public void setBase(Point2D.Double base) {
        this.base = base;
    }

    public Point2D.Double getVec() {
        return vec;
    }

    public void setVec(Point2D.Double vec) {
        this.vec = vec;
    }

    public Point2D.Double getEndpoint() {
        return new Point2D.Double(base.x + vec.x, base.y + vec.y);
    }
}
