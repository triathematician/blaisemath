/*
 * R2.java
 * Created on Sep 14, 2007, 8:10:55 AM
 */

package scio.coordinate;

import java.text.NumberFormat;

/**
 * This class wraps a Point2D as a coordinate.
 * 
 * @author Elisha Peterson
 */
//@XmlAccessorType(XmlAccessType.NONE)
public class R2 extends java.awt.geom.Point2D.Double implements EuclideanElement<R2> {
    public static final R2 ORIGIN = new R2(0,0);
    public static final R2 INFINITY = new R2(java.lang.Double.POSITIVE_INFINITY,java.lang.Double.POSITIVE_INFINITY);
    
    public R2(){super(0,0);}
    public R2(double x,double y){super(x,y);}
    public R2(java.awt.geom.Point2D.Double p){x=p.x;y=p.y;}


    // BEAN PATTERNS
    public void setTo(R2 pt) { x=pt.x; y=pt.y; }
//    @XmlAttribute @Override public double getX() { return x; }
//    public void setX(double x){this.x = x;}
//    @XmlAttribute @Override public double getY() { return y; }
//    public void setY(double y){this.y = y;}
    
    
    // OPERATIONS WHICH CHANGE THE POINT
    
    public R2 translateBy(R2 b){x+=b.x; y+=b.y; return this;}
    public R2 translateBy(double x,double y){this.x+=x; this.y+=y; return this;}    
    public R2 multiplyBy(double d) {this.x*=d; this.y*=d; return this;}
    public void invertInCircleOfRadius(double r){multiplyBy(r/magnitudeSq());}
    
    // OPERATIONS WHICH DO NOT CHANGE THE POINT
    
    public double dot(R2 b){return x*b.x+y*b.y;}
    public double cross(R2 b){return x*b.y-y*b.x;}
    public double magnitude(){return Math.sqrt(x*x+y*y);}
    public double magnitudeSq(){return x*x+y*y;}
    public double angle(){return (x<0?Math.PI:0)+Math.atan(y/x);}
    public R2 plus(R2 b){return new R2(x+b.x,y+b.y);}
    public R2 plus(double bx, double by){return new R2(x+bx,y+by);}
    public R2 minus(R2 b){return new R2(x-b.x,y-b.y);}
    public R2 minus(double bx, double by){return new R2(x-bx,y-by);}
    public R2 times(double m){return new R2(m*x,m*y);}
    public R2 multipliedBy(double m){return new R2(m*x,m*y);}
    public R2 toXY(){return new R2(x*Math.cos(y),y*Math.sin(y));}
    public R2 toRTheta(){return new R2(magnitude(),angle());}
    public R2 scaledToLength(double l){
        double magnitude=magnitude();
        if(magnitude==0){return new R2();}
        return new R2(x*l/magnitude,y*l/magnitude);
    }
    public R2 normalized(){return scaledToLength(1.0);}
    public R2 rotatedBy(double angle){
        return new R2(Math.cos(angle)*x-Math.sin(angle)*y,Math.sin(angle)*x+Math.cos(angle)*x);
    }
    public R2 componentParallelTo(R2 vec) {
        return vec.normalized().multipliedBy(this.dot(vec.normalized()));
    }    
    public R2 componentPerpendicularTo(R2 vec){
        return minus(componentParallelTo(vec));
    }
    
    // MORE GENERAL METHODS

    /**
     * Returns point along the line between point1 and point2 which is closest
     * to itself, aka. the point which makes a perpendicular with the line.
     * Mathematically this can be computed by minimizing the distanceTo between
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


    
    // FOR COORDINATE INTERFACE
    
    public boolean equals(Coordinate c2){
        if(!(c2 instanceof R2)){return false;}
        return (x==((R2)c2).x)&&(y==((R2)c2).y);
    }

//    public double distanceTo(Coordinate p2) {
//        if(!(p2 instanceof R2)){return -1;}
//        return super.distanceTo((R2)p2);
//    }
    
    final static NumberFormat nf=NumberFormat.getInstance();
        
    /** Overwrite default string */    
    @Override
    public String toString(){
        return "("+nf.format(x)+", "+nf.format(y)+")";
    }
    
    
    // REQUIRED METHODS FROM SUPERTYPE

    public Coordinate copy() { return (Coordinate) clone(); }

    public int getLength() { return 2; }
    public double getElement(int position) { return (position == 0) ? x : y; }    
    public void setElement(int position, double value) { if (position == 0) { x = value; } else { y = value; } }
    public void addToElement(int position, double value) { if (position == 0) { x += value; } else { y += value; } }
    public void multiplyElement(int position, double value) { if (position == 0) { x *= value; } else { y *= value; } }
    public double dotProduct(R2 p2) throws ArrayIndexOutOfBoundsException {
        EuclideanElement p2e = (EuclideanElement) p2;
        if (p2e.getLength() == 2) {
            return p2e.getElement(0)*x + p2e.getElement(1)*y;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    public double distanceTo(R2 p2) { 
        return ((java.awt.geom.Point2D.Double)p2).distance(this);
    }
    public R2 zero() { return new R2(); }
    
    
    // STATIC METHODS
    
    /** Returns point constructed in polar coords. */
    public static R2 getPolar(double radius, double angle) {
        return new R2(radius*Math.cos(angle),radius*Math.sin(angle));
    }
    
    /** Returns point at infinity at the given angle, approximated as a very, very large point. */
    public static R2 atInfiniteAngle(double angle) {
        return getPolar(java.lang.Double.MAX_VALUE, Math.cos(angle));
    }
    
    /** Returns slope between two points. */
    public static java.lang.Double slope(R2 p1, R2 p2) { return (p2.y-p1.y)/(p2.x-p1.x); }

    /** Returns angle formed by given vector. */
    public static double angle(double x,double y){return (x<0?Math.PI:0)+Math.atan(y/x); }

    /** Returns center of circle passing through three points. Center is at infinity if points are colinear. */
    public static R2 threePointCircleCenter(R2 p1, R2 p2, R2 p3) {
        java.lang.Double m12 = slope(p1,p2);
        java.lang.Double m23 = slope(p2,p3);
        
        // points are colinear, or one of the slopes is infinite
        if (m12.equals(m23) || m12.isInfinite() && m23.isInfinite()) {
            return R2.INFINITY;
        } else if (m12.isInfinite()) {
            return threePointCircleCenter(p2, p3, p1);
        } else if (m23.isInfinite()) {
            return threePointCircleCenter(p3, p1, p2);
        }
        
        // points are not colinear
        java.lang.Double xCenter = ( m12 * m23 * (p1.y - p3.y) + m23 * (p1.x + p2.x) - m12 * (p2.x + p3.x) ) / (2 * (m23 - m12));        
        java.lang.Double yCenter = ( m12.equals(0.0) ) ?
            (-xCenter + (p1.x + p2.x) / 2) / m12 + (p1.y + p2.y) / 2 :
            (-xCenter + (p2.x + p3.x) / 2) / m23 + (p2.y + p3.y) / 2 ;
        
        return new R2(xCenter, yCenter);
    }
}
