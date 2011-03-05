/*
 * EuclideanPoint3D.java
 * Created on Sep 14, 2007, 8:15:59 AM
 */
package org.bm.blaise.scio.coordinate.formal;

import org.bm.blaise.scio.coordinate.*;

/**
 * <p>
 *   This class wraps up the simpler <code>P3D</code> into a <code>EuclideanElement</code> class.
 * </p>
 * @author Elisha Peterson
 */
public class EuclideanPoint3D extends Point3D implements EuclideanElement<Point3D> {

    // CONSTRUCTORS
    /** Default constructor. */
    public EuclideanPoint3D() {
        super();
    }

    /** Construct with coordinates. */
    public EuclideanPoint3D(double x, double y, double z) {
        super(x, y, z);
    }


    // COMPARISONS
    public boolean equals(EuclideanPoint3D c2r) {
        return x == c2r.x && y == c2r.y && z == c2r.z;
    }


    //
    //
    // EuclideanElement METHODS
    //
    //
    public int getLength() {
        return 3;
    }

    public double getCoordinate(int position) {
        switch (position) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
        }
        throw new IllegalArgumentException("Position not 0-2: " + position);
    }

    public void setCoordinate(int position, double value) {
        switch (position) {
            case 0:
                x = value;
            case 1:
                y = value;
            case 2:
                z = value;
        }
        throw new IllegalArgumentException("Position not 0-2: " + position);
    }

    public void addToCoordinate(int position, double value) {
        switch (position) {
            case 0:
                x += value;
            case 1:
                y += value;
            case 2:
                z += value;
        }
        throw new IllegalArgumentException("Position not 0-2: " + position);
    }

    public void multiplyCoordinateBy(int position, double value) {
        switch (position) {
            case 0:
                x *= value;
            case 1:
                y *= value;
            case 2:
                z *= value;
        }
        throw new IllegalArgumentException("Position not 0-2: " + position);
    }

    //
    //
    // MetricSpaceElement METHODS
    //
    //
    public double distanceTo(Point3D p2) {
        return super.distance(p2);
    }

    //
    //
    // VectorSpaceElement METHODS
    //
    //
    public Point3D zero() {
        return ZERO;
    }

    public double magnitude() {
        return super.distance(0, 0, 0);
    }

    public Point3D normalized() {
        double m = magnitude();
        return m == 0 ? this : this.timesScalar(1 / magnitude());
    }

    public Point3D plus(Point3D pt) {
        return new Point3D(x + pt.x, y + pt.y, z + pt.z);
    }

    public Point3D minus(Point3D pt) {
        return new Point3D(x - pt.x, y - pt.y, z - pt.z);
    }

    public Point3D translateBy(Point3D pt) {
        x += pt.x;
        y += pt.y;
        z += pt.z;
        return this;
    }

    public Point3D timesScalar(double scalar) {
        return new Point3D(scalar * x, scalar * y, scalar * z);
    }

    public Point3D multiplyBy(double scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }
}