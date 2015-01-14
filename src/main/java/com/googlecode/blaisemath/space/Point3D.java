/**
 * P3D.java
 * Created on Jul 29, 2009
 */
package com.googlecode.blaisemath.space;

/*
 * #%L
 * BlaiseMath
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * <p>
 *   <code>P3D</code> is a VERY BASIC class for 3D vectors
 * </p>
 *
 * @author Elisha Peterson
 */
public class Point3D implements Cloneable {

    public static final Point3D ZERO = new Point3D();
    public static final Point3D PLUS_I = new Point3D(1,0,0);
    public static final Point3D MINUS_I = new Point3D(-1,0,0);
    public static final Point3D PLUS_J = new Point3D(0,1,0);
    public static final Point3D MINUS_J = new Point3D(0,-1,0);
    public static final Point3D PLUS_K = new Point3D(0,0,1);
    public static final Point3D MINUS_K = new Point3D(0,0,-1);


    public double x = 0.0;
    public double y = 0.0;
    public double z = 0.0;

    public Point3D() {
    }

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D(Point3D screenCenter) {
        this(screenCenter.x, screenCenter.y, screenCenter.z);
    }

    public Point3D(double[] value) {
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
     * @param x
     * @param y
     * @param z
     */
    public void setLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * @param p2
     * @return distance to second point 
     */
    public double distance(Point3D p2) {
        return Math.sqrt(distanceSq(p2.x, p2.y, p2.z));
    }

    /**
     * @param x2
     * @param y2
     * @param z2
     * @return distance to second point
     */
    public double distance(double x2, double y2, double z2) {
        return Math.sqrt(distanceSq(x2, y2, z2));
    }

    /**
     * @param p2
     * @return distance to second point squared 
     */
    public double distanceSq(Point3D p2) {
        return distanceSq(p2.x, p2.y, p2.z);
    }

    /**
     * @param x2
     * @param y2
     * @param z2
     * @return distance to second point squared
     */
    public double distanceSq(double x2, double y2, double z2) {
        double dx = x - x2;
        double dy = y - y2;
        double dz = z - z2;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * @return magnitude of this vector
     */
    public double magnitude() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    /**
     * @return square of the magnitude of this vector
     */
    public double magnitudeSq() {
        return x*x + y*y + z*z;
    }

    /**
     * @return new point that is a unit vector in the same direction as this
     * vector
     */
    public Point3D normalized() {
        double l = Math.sqrt(x*x + y*y + z*z);
        return new Point3D(x/l, y/l, z/l);
    }

    /**
     * Returns the dotProduct product of two 3-vectors
     * @param p2
     * @return 
     */
    public double dotProduct(Point3D p2) {
        return x * p2.x + y * p2.y + z * p2.z;
    }

    /**
     * Returns the crossProduct product of two 3-vectors
     * @param p2
     * @return 
     */
    public Point3D crossProduct(Point3D p2) {
        return new Point3D(y * p2.z - z * p2.y, z * p2.x - x * p2.z, x * p2.y - y * p2.x);
    }

    /**
     * Subtracts this vector from a second vector, and returns the result.
     * @param p2
     * @return 
     */
    public Point3D minus(Point3D p2) {
        return new Point3D(x - p2.x, y - p2.y, z - p2.z);
    }

    /**
     * Subtracts a triple of coordinates, returns the result.
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Point3D minus(double x, double y, double z) {
        return new Point3D(this.x - x, this.y - y, this.z - z);
    }

    /**
     * Adds this vector to a second vector, and returns the result.
     * @param p2
     * @return 
     */
    public Point3D plus(Point3D p2) {
        return new Point3D(x + p2.x, y + p2.y, z + p2.z);
    }

    /**
     * Adds to a triple of coordinates, returns the result.
     * @param x
     * @param y
     * @param z
     * @return 
     */
    public Point3D plus(double x, double y, double z) {
        return new Point3D(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Multiplies this vector by a scalar, and returns the result.
     * @param s
     * @return 
     */
    public Point3D times(double s) {
        return new Point3D(s * x, s * y, s * z);
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
