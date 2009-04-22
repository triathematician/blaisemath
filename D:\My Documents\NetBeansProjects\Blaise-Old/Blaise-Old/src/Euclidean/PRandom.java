package Euclidean;

/**
 * <b>PRandom.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>February 17, 2007, 11:27 AM</i><br><br>
 * 
 * This file contains mostly static methods for computing random numbers for given
 *   distributions. All numbers are considered to be in double precision.<br><br>
 * 
 * Methods for both one-dimensional and two-dimensional distributions.
 */
public class PRandom {
    
    /**
     * Creates a new instance of PRandom
     */
    public PRandom() {
    }
    
    // ONE-DIMENSIONAL DISTRIBUTIONS
    
    /**
     * Returns a random point in the range [low,high]
     **/
    public static double between(double low,double high){
        return ((high-low)*Math.random()+low);
    }
    
    /**
     * Returns number in the standard (Gaussian) normal distribution.
     **/
    public static double normal(){
        return ((new java.util.Random()).nextGaussian());
    }
    
    /**
     * Returns a point in the normal distribution with given mean and standard
     * deviation.
     **/
    public static double normal(double mean,double std){
        return (std*(normal()+mean));
    }
    
    // TWO-DIMENSIONAL DISTRIBUTIONS
    
    /**
     * Returns a (uniform)  random point in the rectangle [-x,-x]->[x,x]
     **/
    public static PPoint rectangle(double x){
        return new PPoint(between(-x,x),between(-x, x));
    }
    
    /**
     * Returns a (uniform)  random point in the rectangle [-x,-y]->[x,y]
     **/
    public static PPoint rectangle(double x,double y){
        return new PPoint(between(-x,x),between(-y, y));
    }
    
    /**
     * Returns a (uniform)  random point in the rectangle [x1,y1]->[x2,y2]
     **/
    public static PPoint rectangle(double x1,double y1,double x2,double y2){
        return new PPoint(between(x1,x2),between(y1, y2));
    }
    
    /**
     * Returns a (uniform)  random point in the rectangle point1->point2
     **/
    public static PPoint rectangle(PPoint point1,PPoint point2){
        return rectangle(point1.x,point1.y,point2.x,point2.y);
    }
    
    /**
     * Returns a (uniform) random point in the disk radius 0->r
     **/
    public static PPoint disk(double r){
        double randRadius=between(0,r);
        double randTheta=between(0,2*Math.PI);
        return new PPoint(randRadius*Math.cos(randTheta),randRadius*Math.sin(randTheta));
    }
    
    /**
     * Returns a (uniform)  random point in the disk radius 0->r centered at (x,y)
     **/
    public static PPoint disk(double x,double y,double r){
        PPoint point=disk(r);
        point.translate(x,y);
        return point;
    }
    
    /**
     * Returns a normally distributed (with respect to radius) random point
     * centered at (x,y).
     **/
    public static PPoint disk(double r,double std){
        double randRadius=normal(0,std);
        double randTheta=between(0,2*Math.PI);
        return new PPoint(randRadius*Math.cos(randTheta),randRadius*Math.sin(randTheta));
    }
    
    /**
     * Returns a uniformly distributed direction
     */
    public static PPoint direction(){
        double randTheta=between(0,2*Math.PI);
        return new PPoint(Math.cos(randTheta),Math.sin(randTheta));
    }
}
