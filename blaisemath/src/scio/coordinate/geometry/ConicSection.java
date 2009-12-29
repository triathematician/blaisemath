/**
 * ConicSection.java
 * Created on Dec 15, 2009
 */

package scio.coordinate.geometry;

/**
 * <p>
 *    This class represents a conic section.
 * </p>
 * @author Elisha Peterson
 */
public class ConicSection {

    /** Array of coefficents, in the form [a,b,c,d,e,f] where the equation of the conic is a*x^2+b*x*y+c*y^2+d*x+e*y+f=0. */
    double[] coeffs;

    /** Construct the conic with an array of coefficients. */
    public ConicSection(double[] coeffs) {
        this.coeffs = coeffs;
    }

    /** Returns the discriminant of the conic section... differentiates between the standard cases. */
    public double discriminant() {
        return coeffs[0]*coeffs[2] - coeffs[1]*coeffs[1]/4;
    }

    /** Returns the determinant of the conic section... determines degeneracy. */
    public double determinant() {
        return coeffs[0]*coeffs[2]*coeffs[5] + coeffs[1]*coeffs[3]*coeffs[4]/4
                - coeffs[0]*coeffs[4]*coeffs[4]/4 - coeffs[1]*coeffs[1]*coeffs[5]/4 - coeffs[2]*coeffs[2]*coeffs[3]/4;
    }

    /** 
     * Returns the center as a double[].
     *
     * @return an array with center [x,y], or null if the center does not exist (degenerate cases)
     */
    public double[] center() {
        double disc = 4*discriminant();
        return disc == 0 ? null : new double[] {
            (coeffs[1]*coeffs[4]-2*coeffs[2]*coeffs[3]) / disc,
            (coeffs[1]*coeffs[3]-2*coeffs[0]*coeffs[4]) / disc
        };
    }

    //
    // DEGENERATE CASES
    //

    public boolean isDegenerate() {
        return determinant()==0;
    }

    /** Returns true if the solutions degenerately includes all of the plane... when all coefficients are zero. */
    public boolean isNotQuadratic() {
        return coeffs[0]==0 && coeffs[1]==0 && coeffs[2]==0;
    }

    /** Returns true when the solution consists of two intersecting lines. */
    public boolean isTwoIntersectingLines() {
        return isDegenerate() && discriminant() < 0;
    }

    /** Returns true when solution consists of two parallel lines. */
    public boolean isTwoParallelLines() {
        return isDegenerate() && discriminant() > 0;
    }

    //
    // NON-DEGENERATE CASES
    //

    /** Returns true when the solution is a parabola */
    public boolean isParabola() {
        return !isDegenerate() && discriminant() == 0;
    }

    /** Returns true when the solution is a hyperbola */
    public boolean isHyperbola() {
        return !isDegenerate() && discriminant() < 0;
    }

    /** Returns true when the solution is an ellipse */
    public boolean isEllipse() {
        return !isDegenerate() && discriminant() > 0;
    }

    //
    // SPECIAL CASES
    //

    /** Returns true when the solution is a circle */
    public boolean isCircle() {
        return isEllipse() && coeffs[0]==coeffs[2];
    }

}
