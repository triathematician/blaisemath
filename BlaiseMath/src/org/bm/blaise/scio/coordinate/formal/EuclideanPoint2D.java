/*
 * EuclideanPoint2D.java
 * Created on Sep 14, 2007, 8:10:55 AM
 */
package org.bm.blaise.scio.coordinate.formal;

import org.bm.blaise.scio.coordinate.formal.EuclideanElement;
import org.bm.blaise.scio.coordinate.utils.PlanarMathUtils;
import java.awt.geom.Point2D;

/**
 * <p>
 *   This class wraps a Point2D.Double with "EuclideanElement" methods, allowing it to support
 *   inner products, metrics, and vector space operations in addition to euclidean ones.
 * </p>
 * 
 * @author Elisha Peterson
 */
//@XmlAccessorType(XmlAccessType.NONE)
public class EuclideanPoint2D extends Point2D.Double implements EuclideanElement<Point2D.Double> {

    //
    //
    // CONSTRUCTORS
    //
    //
    /** Default constructs to (0,0) */
    public EuclideanPoint2D() {
        super(0, 0);
    }

    /** Constructs to specified values.
     * @param x x value of point
     * @param y y value of point
     */
    public EuclideanPoint2D(double x, double y) {
        super(x, y);
    }

    /** Constructs to the values of the specified point.
     * @param p the point to use for values
     */
    public EuclideanPoint2D(Point2D.Double p) {
        super(p.x, p.y);
    }

    //
    //
    // BEAN PATTERNS
    //
    //
    public void setX(double newX) {
        x = newX;
    }

    public void setY(double newY) {
        y = newY;
    }

    //
    //
    // EuclideanElement METHODS
    //
    //
    public int getLength() {
        return 2;
    }

    public double getCoordinate(int position) {
        if (position == 0) {
            return x;
        } else if (position == 1) {
            return y;
        } else {
            throw new IllegalArgumentException("Position not 0 or 1: " + position);
        }
    }

    public void setCoordinate(int position, double value) {
        if (position == 0) {
            x = value;
        } else if (position == 1) {
            y = value;
        } else {
            throw new IllegalArgumentException("Position not 0 or 1: " + position);
        }
    }

    public void addToCoordinate(int position, double value) {
        if (position == 0) {
            x += value;
        } else if (position == 1) {
            y += value;
        } else {
            throw new IllegalArgumentException("Position not 0 or 1: " + position);
        }
    }

    public void multiplyCoordinateBy(int position, double value) {
        if (position == 0) {
            x *= value;
        } else if (position == 1) {
            y *= value;
        } else {
            throw new IllegalArgumentException("Position not 0 or 1: " + position);
        }
    }

    //
    //
    // InnerProductSpaceElement METHODS
    //
    //
    public double dotProduct(Point2D.Double p2) {
        return x * p2.x + y * p2.y;
    }

    //
    //
    // MetricSpaceElement METHODS
    //
    //

    public double distanceTo(Point2D.Double p2) {
        return super.distance(p2);
    }

    //
    //
    // VectorSpaceElement METHODS
    //
    //

    public Point2D.Double zero() {
        return PlanarMathUtils.ZERO;
    }

    public double magnitude() {
        return super.distance(0, 0);
    }

    public Point2D.Double normalized() {
        double m = magnitude();
        return m == 0 ? this : this.timesScalar(1/magnitude());
    }

    public Point2D.Double plus(Point2D.Double pt) {
        return new Point2D.Double(x + pt.x, y + pt.y);
    }

    public Point2D.Double minus(Point2D.Double pt) {
        return new Point2D.Double(x - pt.x, y-pt.y);
    }

    public Point2D.Double translateBy(Point2D.Double pt) {
        x += pt.x;
        y += pt.y;
        return this;
    }

    public Point2D.Double timesScalar(double scalar) {
        return new Point2D.Double(scalar *x, scalar*y);
    }

    public Point2D.Double multiplyBy(double scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }
}
