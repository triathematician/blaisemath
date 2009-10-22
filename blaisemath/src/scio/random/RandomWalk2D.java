/**
 * RandomWalk2D.java
 * Created on February 17, 2007, 11:27 AM
 */

package scio.random;

import java.awt.geom.Point2D;

/**
 * <p>
 *   This class represents a random walk in the plane. Distance is
 *   uniform; angles are chosen uniformly randomly on a range [-a,a].
 * </p>
 * @author ae3263
 */
public class RandomWalk2D {

    //
    //
    // PARAMETERS
    //
    //

    /** Initial point */
    Point2D.Double p0;

    /** Length of each step. */
    protected double stepDist = 1.0;

    /** Maximum change in angle per step. */
    protected double stepAngle = Math.PI/4;

    /** Number of steps to store. */
    protected int nSteps = 100;

    //
    //
    // CONSTRUCTORS
    //
    //

    /** Default constructor. */
    public RandomWalk2D(){
        p0 = new Point2D.Double();
    }

    //
    //
    // BEAN PATTERNS
    //
    //

    /** Return starting point */
    public Point2D.Double getStartPoint() {
        return p0;
    }

    /** Sets starting point */
    public void setStartPoint(Point2D.Double p0) {
        this.p0 = p0;
    }

    /** Return max step angle */
    public double getStepAngle() {
        return stepAngle;
    }

    /** Sets max step angle */
    public void setStepAngle(double stepAngle) {
        this.stepAngle = stepAngle;
    }

    /** Return step distance */
    public double getStepDist() {
        return stepDist;
    }

    /** Sets step distance */
    public void setStepDist(double stepDist) {
        this.stepDist = stepDist;
    }

    /** Return number of steps */
    public int getNSteps() {
        return nSteps;
    }

    /** Sets number of steps */
    public void setNSteps(int nSteps) {
        this.nSteps = nSteps;
    }

    //
    //
    // SOLUTION METHODS
    //
    //
    
    transient double[] dAngles;
    transient Point2D.Double[] lastWalk;

    /**
     * Computes random walk from starting point. Result is locally stored.
     * 
     * @return array of points representing the walk
     */
    public Point2D.Double[] computeWalk() {
        dAngles = computeAngles();
        lastWalk = new Point2D.Double[nSteps];
        lastWalk[0] = (Point2D.Double) p0.clone();
        double angle = 2 * Math.PI * Math.random();
        for (int i = 1; i < dAngles.length; i++) {
            angle += dAngles[i];
            lastWalk[i] = new Point2D.Double(
                    lastWalk[i-1].x + Math.cos(angle) * stepDist,
                    lastWalk[i-1].y + Math.sin(angle) * stepDist
                    );
        }
        return lastWalk;
    }

    /**
     * Computes an array of angles that generate the path. These
     * angles are locally stored.
     * 
     * @return array of angles representing the walk
     */
    double[] computeAngles() {
        double[] result = new double[nSteps];
        for (int i = 1; i < result.length; i++) {
            result[i] = (2.0*Math.random()-1.0) * stepAngle;
        }
        return result;
    }

}
