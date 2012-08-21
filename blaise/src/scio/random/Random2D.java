package scio.random;

import java.util.Vector;
import scio.coordinate.R2;
import scio.function.Function;
import scio.function.FunctionValueException;

/**
 * <b>Random2D.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>February 17, 2007, 11:27 AM</i><br><br>
 * 
 * This file contains mostly static methods for computing random numbers for given
 *   distributions. All numbers are considered to be in double precision.<br><br>
 * 
 * Methods for both one-dimensional and two-dimensional distributions.
 */
public class Random2D {
    
    /**
     * Creates a new instance of Random2D
     */
    public Random2D() {
    }
    
    // TWO-DIMENSIONAL DISTRIBUTIONS
    
    /**
     * Returns a (uniform)  random point in the rectangle [-x,-x]->[x,x]
     **/
    public static R2 rectangle(double x){
        return new R2(RandomGenerator.Uniform.getValue(-x,x),RandomGenerator.Uniform.getValue(-x,x));
    }
    
    /**
     * Returns a (uniform)  random point in the rectangle [-x,-y]->[x,y]
     **/
    public static R2 rectangle(double x,double y){
        return new R2(RandomGenerator.Uniform.getValue(-x,x),RandomGenerator.Uniform.getValue(-y, y));
    }
    
    /**
     * Returns a (uniform)  random point in the rectangle [x1,y1]->[x2,y2]
     **/
    public static R2 rectangle(double x1,double y1,double x2,double y2){
        return new R2(RandomGenerator.Uniform.getValue(x1,x2),RandomGenerator.Uniform.getValue(y1, y2));
    }
    
    /**
     * Returns a (uniform)  random point in the rectangle point1->point2
     **/
    public static R2 rectangle(R2 point1,R2 point2){
        return rectangle(point1.x,point1.y,point2.x,point2.y);
    }
    
    /**
     * Returns a (uniform) random point in the diskNormal radius 0->r
     **/
    public static R2 disk(double r){
        double randRadius=RandomGenerator.Uniform.getValue(0,r);
        double randTheta=RandomGenerator.Uniform.getValue(0,2*Math.PI);
        return new R2(randRadius*Math.cos(randTheta),randRadius*Math.sin(randTheta));
    }    
    /** Returns a (uniform) random point in the diskNormal radius 0->r centered at (x,y) */
    public static R2 disk(double x,double y,double r){return disk(r).plus(new R2(x,y));}
    /** Returns uniform random point in diskNormal radius 0->r centered at point. */
    public static R2 disk(R2 point,double r){return disk(r).plus(point);}
    
    /**
     * Returns a normally distributed (with respect to radius) random point
     * centered at (x,y).
     **/
    public static R2 diskNormal(double std){
        double randRadius=RandomGenerator.GAUSSIAN.getRValue()*std;
        double randTheta=RandomGenerator.Uniform.getValue(0,2*Math.PI);
        return new R2(randRadius*Math.cos(randTheta),randRadius*Math.sin(randTheta));
    }
    /** Returns a (Gaussian) random point in the diskNormal radius 0->r centered at (x,y) */
    public static R2 diskNormal(double x,double y,double std){return diskNormal(std).plus(new R2(x,y));}
    /** Returns Gaussian random point in diskNormal radius 0->r centered at point. */
    public static R2 diskNormal(R2 point,double std){return diskNormal(std).plus(point);}
    
    /** Separate x and y normal distributions. */
    public static R2 rectNormal(double stdx,double stdy){return new R2(RandomGenerator.GAUSSIAN.getRValue()*stdx,RandomGenerator.GAUSSIAN.getRValue()*stdy);}
    
    /**
     * Returns a uniformly distributed direction
     */
    public static R2 direction(){
        double randTheta=RandomGenerator.Uniform.getValue(0,2*Math.PI);
        return new R2(Math.cos(randTheta),Math.sin(randTheta));
    }
}
