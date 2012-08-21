/*
 * Segment2D.java
 * Created on Feb 10, 2008
 */

package specto.euclidean3;

import java.awt.Graphics2D;
import scio.coordinate.R3;
import sequor.style.LineStyle;

/**
 * Implements a segment between two 2D points. Uses two <b>Point2D</b>s at the endpoints to control the position of the segment.
 *
 * @author Elisha Peterson
 */
public class Segment3D extends DynamicPointSet3D {   
    Point3D point1;
    Point3D point2;

    /** Default segment. */
    public Segment3D() {this(0,0,0,1,0,0);}
    /** Construct with two points. */
    public Segment3D(Point3D point1,Point3D point2){
        this.point1=point1;
        super.add(point1);
        this.point2=point2;
        super.add(point2);
    }
    public Segment3D(R3 p1, R3 p2) {
        this(new Point3D(p1), new Point3D(p2)); 
    }
    /** Constructs with coordinates. */
    public Segment3D(double x1,double y1,double z1,double x2,double y2,double z2){this(new Point3D(x1,y1,z1),new Point3D(x2,y2,z2));}

    /** Returns first point */
    public R3 getPoint1() {return point1.getPoint();}
    /** Sets first point */
    public void setPoint1(R3 point1) {this.point1.setPoint(point1);}
    /** Returns second point */
    public R3 getPoint2() {return point2.getPoint();}
    /** Sets second point */
    public void setPoint2(R3 point2) {this.point2.setPoint(point2);}
    /** Set both points at the same time. */
    void setTo(R3 p1, R3 p2) { setPoint1(p1); setPoint2(p2); }
    /** Returns midpoint */
    public R3 getMidpoint(){
        return getPoint1().plus(getPoint2()).multipliedBy(0.5);
    }

    @Override
    public void paintComponent(Graphics2D g,Euclidean3 v) {
        g.setStroke(LineStyle.STROKES[LineStyle.MEDIUM]);
        switch(style.getValue()){
            case LINE_RAY:
            case LINE_LINE:
            case LINE_SEGMENT:
                v.drawLineSegment(g, getPoint1(), getPoint2());
                break;
            case LINE_VECTOR:
                v.drawArrow(g, getPoint1(), getPoint2(), 5);
                break;
        }
    }
    
    // STYLE OPTIONS
    
    /** Represents a lineSegment segment */
    public static final int LINE_SEGMENT = 0;
    /** Draws as a ray */
    public static final int LINE_RAY = 1;
    /** Draw as a line */
    public static final int LINE_LINE = 2;
    /** Draw as a vector */
    public static final int LINE_VECTOR = 3;

    static final String[] styleStrings={"Segment","Ray","Line","Vector"};
    @Override
    public String[] getStyleStrings() {return styleStrings;}
    
    @Override
    public String toString(){return "Segment";}
    
    
    // SUBCLASSES
    
    public static class Line extends Segment3D {
        public Line(Point3D point1,Point3D point2){super(point1,point2);style.setValue(LINE_LINE);}
    }    
    public static class Ray extends Segment3D {
        public Ray(Point3D point1,Point3D point2){super(point1,point2);style.setValue(LINE_RAY);}
    }
    
    public static class Vector extends Segment3D {        
        public Vector(Point3D point1,Point3D point2){super(point1,point2);style.setValue(LINE_VECTOR);}
    }
    
    // CONSTRAINED POINTS
    
//    public PointRangeModel getConstraintModel() {return new LinePointModel(this);}
//    public Point3D getConstrainedPoint() {return new Point3D(new LinePointModel(this));}
//    
//    // INNER CLASSES
//    public class LinePointModel extends PointRangeModel {
//        Point3D point1;
//        Point3D point2;
//        int type;
//
//        public LinePointModel(Segment3D s){
//            point1=s.point1;
//            point1.addChangeListener(this);
//            point2=s.point2;
//            point2.addChangeListener(this);
//            setTo(getMidpoint());
//            type=s.style.getValue();
//        }
//
//        @Override
//        public void setTo(R2 input){
//            try{
//                switch(type){
//                    case LINE_LINE:
//                        super.setTo(input.closestOnLine(point1.getPoint(), point2.getPoint()));
//                        break;
//                    case LINE_RAY:
//                        super.setTo(input.closestOnRay(point1.getPoint(), point2.getPoint()));
//                        break;
//                    case LINE_SEGMENT:
//                    case LINE_VECTOR:
//                        super.setTo(input.closestOnSegment(point1.getPoint(), point2.getPoint()));
//                        break;
//                }
//            }catch(NullPointerException e){}
//        }
//
//        @Override
//        public void stateChanged(ChangeEvent e) {
//            if(point1!=null && e.getSource().equals(point1.prm)|| point2!=null && e.getSource().equals(point2.prm)){setTo(getPoint());}
//            super.stateChanged(e);
//        }
//    } // class Function2D.FunctionPointModel
}