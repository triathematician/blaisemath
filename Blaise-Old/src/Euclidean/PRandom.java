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
    public static double Between(double low,double high){
        return ((high-low)*Math.random()+low);
    }
    
    /**
     * Returns number in the standard (Gaussian) normal distribution.
     **/
    public static double Normal(){
        return ((new java.util.Random()).nextGaussian());
    }
    
    /**
     * Returns a point in the normal distribution with given mean and standard
     * deviation.
     **/
    public static double Normal(double mean,double std){
        return (std*(Normal()+mean));
    }
    
    // TWO-DIMENSIONAL DISTRIBUTIONS
    
    /**
     * Returns a (uniform)  random point in the rectangle [-x,-x]->[x,x]
     **/
    public static PPoint Rectangle(double x){
        return new PPoint(Between(-x,x),Between(-x,x));
    }
    
    /**
     * Returns a (uniform)  random point in the rectangle [-x,-y]->[x,y]
     **/
    public static PPoint Rectangle(double x,double y){
        return new PPoint(Between(-x,x),Between(-y,y));
    }
    
    /**
     * Returns a (uniform)  random point in the rectangle [x1,y1]->[x2,y2]
     **/
    public static PPoint Rectangle(double x1,double y1,double x2,double y2){
        return new PPoint(Between(x1,x2),Between(y1,y2));
    }
    
    /**
     * Returns a (uniform)  random point in the rectangle point1->point2
     **/
    public static PPoint Rectangle(PPoint point1,PPoint point2){
        return Rectangle(point1.x,point1.y,point2.x,point2.y);
    }
    
    /**
     * Returns a (uniform) random point in the disk radius 0->r
     **/
    public static PPoint Disk(double r){
        double randRadius=Between(0,r);
        double randTheta=Between(0,2*Math.PI);
        return new PPoint(randRadius*Math.cos(randTheta),randRadius*Math.sin(randTheta));
    }
    
    /**
     * Returns a (uniform)  random point in the disk radius 0->r centered at (x,y)
     **/
    public static PPoint Disk(double x,double y,double r){
        PPoint point=Disk(r);
        point.translate(x,y);
        return point;
    }
    
    /**
     * Returns a normally distributed (with respect to radius) random point
     * centered at (x,y).
     **/
    public static PPoint Disk(double r,double std){
        double randRadius=Normal(0,std);
        double randTheta=Between(0,2*Math.PI);
        return new PPoint(randRadius*Math.cos(randTheta),randRadius*Math.sin(randTheta));
    }
}
