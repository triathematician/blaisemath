/*
 * EuclideanPoint3D.java
 * Created on Sep 14, 2007, 8:15:59 AM
 */
package scio.coordinate;

/**
 * <p>
 *   This class wraps up the simpler <code>P3D</code> into a <code>EuclideanElement</code> class.
 * </p>
 * @author Elisha Peterson
 */
public class EuclideanPoint3D extends P3D implements EuclideanElement<P3D> {

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
    public double distanceTo(P3D p2) {
        return super.distance(p2);
    }

    //
    //
    // VectorSpaceElement METHODS
    //
    //
    public P3D zero() {
        return ZERO;
    }

    public double magnitude() {
        return super.distance(0, 0, 0);
    }

    public P3D normalized() {
        double m = magnitude();
        return m == 0 ? this : this.timesScalar(1 / magnitude());
    }

    public P3D plus(P3D pt) {
        return new P3D(x + pt.x, y + pt.y, z + pt.z);
    }

    public P3D minus(P3D pt) {
        return new P3D(x - pt.x, y - pt.y, z - pt.z);
    }

    public P3D translateBy(P3D pt) {
        x += pt.x;
        y += pt.y;
        z += pt.z;
        return this;
    }

    public P3D timesScalar(double scalar) {
        return new P3D(scalar * x, scalar * y, scalar * z);
    }

    public P3D multiplyBy(double scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }
}