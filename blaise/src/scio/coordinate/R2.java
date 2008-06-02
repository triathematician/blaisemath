/*
 * R2.java
 * Created on Sep 14, 2007, 8:10:55 AM
 */

package scio.coordinate;

import java.awt.geom.Point2D;
import java.text.NumberFormat;

/**
 * This class wraps a Point2D as a coordinate.
 * 
 * @author Elisha Peterson
 */
public class R2 extends Point2D.Double implements EuclideanElement {
    public static final R2 Origin=new R2(0,0);
    
    public R2(){super(0,0);}
    public R2(double x,double y){super(x,y);}
    public R2(Point2D.Double p){x=p.x;y=p.y;}

    // OPERATIONS WHICH CHANGE THE POINT
    
    public void translate(R2 b){x+=b.x;y+=b.y;}
    public void translate(double x,double y){this.x+=x;this.y+=y;}    
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
    
    // STATIC METHODS
    
    public static double angle(double x,double y){return (x<0?Math.PI:0)+Math.atan(y/x); }
    
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
    
    /** Overwrite default string */    
    @Override
    public String toString(){
        NumberFormat nf=NumberFormat.getInstance();
        return "("+nf.format(x)+", "+nf.format(y)+")";
    }
    
    
    // REQUIRED METHODS FROM SUPERTYPE

    public Coordinate copy() { return (Coordinate) clone(); }

    public int getLength() { return 2; }
    public double getElement(int position) { return (position == 0) ? x : y; }    
    public void setElement(int position, double value) { if (position == 0) { x = value; } else { y = value; } }
    public void addToElement(int position, double value) { if (position == 0) { x += value; } else { y += value; } }
    public void multiplyElement(int position, double value) { if (position == 0) { x *= value; } else { y *= value; } }
    public double dotProduct(InnerProductSpaceElement p2) throws ArrayIndexOutOfBoundsException {
        EuclideanElement p2e = (EuclideanElement) p2;
        if (p2e.getLength() == 2) {
            return p2e.getElement(0)*x + p2e.getElement(1)*y;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    public double distanceTo(Coordinate p2) {
        return ((Point2D.Double)p2).distance(this);
    }
    public VectorSpaceElement zero() { return new R2(); }
    public VectorSpaceElement plus(VectorSpaceElement p2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public VectorSpaceElement minus(VectorSpaceElement p2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public VectorSpaceElement translateBy(VectorSpaceElement p2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public VectorSpaceElement multiplyBy(double d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
