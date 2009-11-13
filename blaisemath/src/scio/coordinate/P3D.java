/**
 * P3D.java
 * Created on Jul 29, 2009
 */
package scio.coordinate;

/**
 * <p>
 *   <code>P3D</code> is a VERY BASIC class for 3D vectors
 * </p>
 *
 * @author Elisha Peterson
 */
public class P3D implements Cloneable {

    public static final P3D ZERO = new P3D();
    public static final P3D PLUS_I = new P3D(1,0,0);
    public static final P3D MINUS_I = new P3D(-1,0,0);
    public static final P3D PLUS_J = new P3D(0,1,0);
    public static final P3D MINUS_J = new P3D(0,-1,0);
    public static final P3D PLUS_K = new P3D(0,0,1);
    public static final P3D MINUS_K = new P3D(0,0,-1);


    public double x = 0.0;
    public double y = 0.0;
    public double z = 0.0;

    public P3D() {
    }

    public P3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public P3D(P3D screenCenter) {
        this(screenCenter.x, screenCenter.y, screenCenter.z);
    }

    public P3D(double[] value) {
        this(value[0], value[1], value[2]);
    }



    /**
     * Get the value of x
     * @return the value of x
     */
    public double getX() {
        return x;
    }

    /**
     * Set the value of x
     * @param x new value of x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Get the value of y
     * @return the value of y
     */
    public double getY() {
        return y;
    }

    /**
     * Set the value of y
     * @param y new value of y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Get the value of z
     * @return the value of z
     */
    public double getZ() {
        return z;
    }

    /**
     * Set the value of z
     * @param z new value of z
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Sets the location of this <code>P3D</code> to the
     * specified <code>double</code> coordinates.
     */
    public void setLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** @return distance to second point */
    public double distance(P3D p2) {
        return Math.sqrt(distanceSq(p2.x, p2.y, p2.z));
    }

    /** @return distance to second point */
    public double distance(double x2, double y2, double z2) {
        return Math.sqrt(distanceSq(x2, y2, z2));
    }

    /** @return distance to second point squared */
    public double distanceSq(P3D p2) {
        return distanceSq(p2.x, p2.y, p2.z);
    }

    /** @return distance to second point squared */
    public double distanceSq(double x2, double y2, double z2) {
        double dx = x - x2;
        double dy = y - y2;
        double dz = z - z2;
        return dx * dx + dy * dy + dz * dz;
    }

    /** @return magnitude of this vector */
    public double magnitude() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    /** @return square of the magnitude of this vector */
    public double magnitudeSq() {
        return x*x + y*y + z*z;
    }

    /** @return new point that is a unit vector in the same direction as this vector */
    public P3D normalized() {
        double l = Math.sqrt(x*x + y*y + z*z);
        return new P3D(x/l, y/l, z/l);
    }

    /** Returns the dotProduct product of two 3-vectors */
    public double dotProduct(P3D p2) {
        return x * p2.x + y * p2.y + z * p2.z;
    }

    /** Returns the crossProduct product of two 3-vectors */
    public P3D crossProduct(P3D p2) {
        return new P3D(y * p2.z - z * p2.y, z * p2.x - x * p2.z, x * p2.y - y * p2.x);
    }

    /** Subtracts this vector from a second vector, and returns the result. */
    public P3D minus(P3D p2) {
        return new P3D(x - p2.x, y - p2.y, z - p2.z);
    }
    /** Subtracts a triple of coordinates, returns the result. */
    public P3D minus(double x, double y, double z) {
        return new P3D(this.x - x, this.y - y, this.z - z);
    }
    /** Adds this vector to a second vector, and returns the result. */
    public P3D plus(P3D p2) {
        return new P3D(x + p2.x, y + p2.y, z + p2.z);
    }
    /** Adds to a triple of coordinates, returns the result. */
    public P3D plus(double x, double y, double z) {
        return new P3D(this.x + x, this.y + y, this.z + z);
    }
    /** Multiplies this vector by a scalar, and returns the result. */
    public P3D times(double s) {
        return new P3D(s * x, s * y, s * z);
    }


    /**
     * Returns a <code>String</code> that represents the value
     */
    @Override
    public String toString() {
        return "P3D[" + x + ", " + y + ", " + z + "]";
    }

    @Override
    public Object clone() {
	try {
            return super.clone();
	} catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

}
