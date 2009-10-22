/**
 * Complex.java
 * Created on Mar 24, 2008
 */
package scio.coordinate;

/**
 * This class represents a complex number.
 * 
 * @author Elisha Peterson
 */
@Deprecated
public class Complex extends EuclideanPoint2D {

    @Deprecated
    public double realPart() {
        return super.x;
    }

    @Deprecated
    public double imaginaryPart() {
        return super.y;
    }

    // LIBRARY OF STATIC FUNCTIONS
    @Deprecated
    public static Complex exp(Complex z) {
        return null;
    }

    @Deprecated
    public static Complex cos(Complex z) {
        return null;
    }

    @Deprecated
    public static Complex sin(Complex z) {
        return null;
    }

    @Deprecated
    public static Complex power(Complex z1, double z2) {
        return null;
    }

    @Deprecated
    public static Complex power(Complex z1, Complex z2) {
        return null;
    }
}
