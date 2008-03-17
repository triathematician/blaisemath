/*
 * R2.java
 * Created on Sep 14, 2007, 8:10:55 AM
 */

package scio.coordinate;

import java.awt.geom.Point2D;

/**
 * This class wraps a Point2D as a coordinate.
 * <br><br>
 * @author Elisha Peterson
 */
public class R2 extends Point2D.Double implements Coordinate {
    public static final R2 Origin=new R2(0,0);
    
    public R2(){super(0,0);}
    public R2(double x,double y){super(x,y);}
    public R2(Point2D.Double p){x=p.x;y=p.y;}

    // OPERATIONS WHICH CHANGE THE POINT
    
    public void translate(R2 b){x+=b.x;y+=b.y;}
    public void translate(double x,double y){this.x+=x;this.y+=y;}
    public void multiplyBy(double c){x*=c;y*=c;}
    public void invertInCircleOfRadius(double r){multiplyBy(r/magnitudeSq());}
    
    // OPERATIONS WHICH DO NOT CHANGE THE POINT
    
    public double dot(R2 b){return x*b.x+y*b.y;}
    public double magnitude(){return Math.sqrt(x*x+y*y);}
    public double magnitudeSq(){return x*x+y*y;}
    public double angle(){return (x<0?Math.PI:0)+Math.atan(y/x);}
    public R2 plus(R2 b){return new R2(x+b.x,y+b.y);}
    public R2 minus(R2 b){return new R2(x-b.x,y-b.y);}
    public R2 times(double m){return new R2(m*x,m*y);}
    public R2 toXY(){return new R2(x*Math.cos(y),y*Math.sin(y));}
    public R2 toRTheta(){return new R2(magnitude(),angle());}
    public R2 scaledToLength(double l){
        double magnitude=magnitude();
        if(magnitude==0){return new R2();}
        return new R2(x*l/magnitude,y*l/magnitude);
    }
    public R2 normalized(){return scaledToLength(1.0);}
    public R2 multipliedBy(double c){return new R2(c*x,c*y);}
    
    /**
     * Returns point along the line between point1 and point2 which is closest
     * to itself, aka. the point which makes a perpendicular with the line.
     * Mathematically this can be computed by minimizing the distance between
     * (x,y) and p1+t*(p2-p1) as t varies. Mathematically, the result is
     *
     *       (p1-p).(p1-p2)
     *    t= --------------
     *         ||p1-p2||^2
     **/
    public R2 closestOnLine(R2 point1,R2 point2){
        double t=(point1.minus(this).dot(point1.minus(point2)))/point1.distanceSq(point2);
        return new R2(point1.x+t*(point2.x-point1.x),point1.y+t*(point2.y-point1.y));
    }
    /**
     * Returns point on line SEGMENT between point1 and point2 which is closest to this point.
     */
    public R2 closestOnSegment(R2 point1,R2 point2){
        double t=(point1.minus(this).dot(point1.minus(point2)))/point1.distanceSq(point2);
        if(t<0){t=0;}else if(t>1){t=1;}
        return new R2(point1.x+t*(point2.x-point1.x),point1.y+t*(point2.y-point1.y));        
    }
    /**
     * Returns point on RAY from point1 to point2 which is closest to this point.
     */
    public R2 closestOnRay(R2 point1,R2 point2){
        double t=(point1.minus(this).dot(point1.minus(point2)))/point1.distanceSq(point2);
        if(t<0){t=0;}
        return new R2(point1.x+t*(point2.x-point1.x),point1.y+t*(point2.y-point1.y));           
    }

    public boolean equals(Coordinate c2){return (x==((R2)c2).x)&&(y==((R2)c2).y);}
}
