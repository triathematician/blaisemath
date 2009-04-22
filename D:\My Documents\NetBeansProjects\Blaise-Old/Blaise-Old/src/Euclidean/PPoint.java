package Euclidean;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 * PPoint.java<br>
 * Author: Elisha Peterson<br>
 * Created circa February 5, 2007<br><br>
 *
 * This class supports double-valued Cartesian coordinates (x,y) and supports
 *   drawing on a Graphics2D object. It also contains several methods for
 *   manipulating the points, computing distances, etc.
 */

public class PPoint{
    // properties
    public double x,y;
    
    // basic constructor
    public PPoint(){x=0;y=0;}
    
    // basic getters & setters
    public double getX(){return x;}
    public void setX(double x){this.x=x;}
    public double getY(){return y;}
    public void setY(double y){this.y=y;}
    
    // additional getters/setters
    public void setXY(double x0,double y0){x=x0;y=y0;}
    public void setRTheta(double r,double theta){x=r*Math.cos(theta);y=r*Math.sin(theta);}
    public void setXYRTheta(double x0,double y0,double r,double theta){x=x0+r*Math.cos(theta);y=y0+r*Math.sin(theta);}
    public PPoint getPoint(){return new PPoint(this);}
    public void setPoint(PPoint point){x=point.x;y=point.y;}
    public Point2D.Double getPoint2D(){return new Point2D.Double(x,y);}
    public void setPoint2D(Point2D.Double point2D){x=point2D.x;y=point2D.y;}
    
    // static methods
    public static double magnitude(double x,double y){return Math.sqrt(x*x+y*y);}
    public static double length(double x,double y){return magnitude(x,y);}
    public static double magnitudeSquared(double x,double y){return x*x+y*y;}
    
    // further constructors
    public PPoint(double x0,double y0){x=x0;y=y0;}
    public PPoint(PPoint point){x=point.x;y=point.y;}
    public PPoint(Point2D point){x=point.getX();y=point.getY();}
    
    // getPoint methods: basic properties
    public double magnitude(){return magnitude(x,y);}
    public double length(){return length(x,y);}
    public double magnitudeSquared(){return magnitudeSquared(x,y);}
    
    // getPoint methods: given an affine transform
    public PPoint getAfterTransform(AffineTransform at){return new PPoint(at.transform(getPoint2D(),null));}
    public PPoint getBeforeTransform(AffineTransform at) throws NoninvertibleTransformException{return new PPoint(at.inverseTransform(getPoint2D(),null));}
    
    // basic manipulation methods: manipulate and return this point
    public PPoint translate(double x0,double y0){x+=x0;y+=y0;return this;}
    public PPoint translate(PPoint point){return translate(point.x,point.y);}
    public PPoint multiply(double c){x*=c;y*=c;return this;}
    public PPoint scaleMagnitudeTo(double d){return(length()==0)?this:multiply(d/length());} // scales to length d
    public PPoint normalize(){return scaleMagnitudeTo(1);} // normalizes the vector

    // getPoint methods: return new point
    public PPoint toward(double x1,double y1){return new PPoint(x1-x,y1-y);}
    public PPoint toward(PPoint point){return toward(point.x,point.y);}
    public PPoint unitToward(PPoint point){double d=distanceTo(point);return(d==0)?new PPoint():new PPoint((point.x-x)/d,(point.y-y)/d);}
    public PPoint from(double x1,double y1){return new PPoint(x-x1,y-y1);}
    public PPoint from(PPoint point){return from(point.x,point.y);}
    public PPoint unitFrom(PPoint point){double d=distanceTo(point);return(d==0)?new PPoint():new PPoint((x-point.x)/d,(y-point.y)/d);}
    public PPoint plus(double x1,double y1){return new PPoint(x+x1,y+y1);}
    public PPoint plus(PPoint point){return plus(point.x,point.y);}
    public PPoint multipliedBy(double c){return new PPoint(x*c,y*c);}
    
    // inversion methods
    public PPoint invertInCircleOfRadius(double r){return multiply(r/magnitudeSquared());}
    
    // two point/vector operations
    public double dot(double x1,double y1){return x*x1+y*y1;}
    public double dot(PPoint point){return x*point.x+y*point.y;}
    public double distanceTo(double x1,double y1){return Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1));}
    public double distanceTo(PPoint point){return distanceTo(point.x,point.y);}
    
    // three point operations
    
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
    public PPoint closestOnLine(PPoint point1,PPoint point2){
        double t=(point1.from(this).dot(point1.from(point2)))/((point1.from(point2)).magnitudeSquared());
        return new PPoint(point1.x+t*(point2.x-point1.x),point1.y+t*(point2.y-point1.y));
    }
    
//    public static void main(String[] args){
//        PPoint p=new PPoint(0,-1);
//        PPoint q=new PPoint(2,0);
//        PPoint r=new PPoint(1,1);
//        PPoint s=r.closestOnLine(p,q);
//        System.out.println("p="+p.x+","+p.y);
//        System.out.println("q="+q.x+","+q.y);
//        System.out.println("r="+r.x+","+r.y);
//        System.out.println("s="+s.x+","+s.y);
//    }
    
    /** Standard output */
    public String toString(){return "("+x+","+y+")";}
}

