/*
 * Segment2D.java
 * Created on Feb 10, 2008
 */

package specto.dynamicplottable;

import javax.swing.event.ChangeEvent;
import specto.dynamicplottable.*;
import java.awt.Graphics2D;
import java.awt.Shape;
import scio.coordinate.R2;
import sequor.model.PointRangeModel;
import specto.Constrains2D;
import specto.plottable.PointSet2D;
import specto.visometry.Euclidean2;

/**
 * Implements a segment between two 2D points. Uses two <b>Point2D</b>s at the endpoints to control the position of the segment.
 *
 * @author Elisha Peterson
 */
public class Segment2D extends DynamicPointSet2D implements Constrains2D {   
    Point2D point1;
    Point2D point2;

    /** Default segment. */
    public Segment2D() {this(0,0,1,0);}
    /** Construct with two PointRangeModel's */
    public Segment2D(PointRangeModel prm1,PointRangeModel prm2){
        this(new Point2D(prm1),new Point2D(prm2));
    }
    /** Construct with two points. */
    public Segment2D(Point2D point1,Point2D point2){
        this.point1=point1;
        super.add(point1);
        this.point2=point2;
        super.add(point2);
    }
    /** Constructs with coordinates. */
    public Segment2D(double x1,double y1,double x2,double y2){this(new Point2D(x1,y1),new Point2D(x2,y2));}

    /** Returns first point */
    public R2 getPoint1() {return point1.getPoint();}
    /** Sets first point */
    public void setPoint1(R2 point1) {this.point1.setPoint(point1);}
    /** Returns second point */
    public R2 getPoint2() {return point2.getPoint();}
    /** Sets second point */
    public void setPoint2(R2 point2) {this.point2.setPoint(point2);}
    /** Returns midpoint */
    public R2 getMidpoint(){
        return getPoint1().plus(getPoint2()).multipliedBy(0.5);
    }

    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        g.setStroke(PointSet2D.strokes[PointSet2D.REGULAR]);
        switch(style.getValue()){
            case LINE_RAY:
                Shape ray=v.ray(getPoint1(),getPoint2(),10.0);
                g.draw(ray);
                g.fill(ray);
                break;
            case LINE_LINE:
                g.draw(v.line(getPoint1(),getPoint2()));
                break;
            case LINE_VECTOR:
                Shape arrow=v.arrow(getPoint1(), getPoint2(),10.0);
                g.draw(arrow);
                g.fill(arrow);
                break;
            case LINE_SEGMENT:
            default:
                g.draw(v.lineSegment(getPoint1(),getPoint2()));
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
    
    public static class Line extends Segment2D {
        public Line(Point2D point1,Point2D point2){super(point1,point2);style.setValue(LINE_LINE);}
    }    
    public static class Ray extends Segment2D {
        public Ray(Point2D point1,Point2D point2){super(point1,point2);style.setValue(LINE_RAY);}
    }
    
    public static class Vector extends Segment2D {        
        public Vector(Point2D point1,Point2D point2){super(point1,point2);style.setValue(LINE_VECTOR);}
    }
    
    // CONSTRAINED POINTS
    
    public PointRangeModel getConstraintModel() {return new LinePointModel(this);}
    public Point2D getConstrainedPoint() {return new Point2D(new LinePointModel(this));}
    
    // INNER CLASSES
    public class LinePointModel extends PointRangeModel {
        Point2D point1;
        Point2D point2;
        int type;

        public LinePointModel(Segment2D s){
            point1=s.point1;
            point1.addChangeListener(this);
            point2=s.point2;
            point2.addChangeListener(this);
            setTo(getMidpoint());
            type=s.style.getValue();
        }

        @Override
        public void setTo(R2 input){
            try{
                switch(type){
                    case LINE_LINE:
                        super.setTo(input.closestOnLine(point1.getPoint(), point2.getPoint()));
                        break;
                    case LINE_RAY:
                        super.setTo(input.closestOnRay(point1.getPoint(), point2.getPoint()));
                        break;
                    case LINE_SEGMENT:
                    case LINE_VECTOR:
                        super.setTo(input.closestOnSegment(point1.getPoint(), point2.getPoint()));
                        break;
                }
            }catch(NullPointerException e){}
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if(point1!=null && e.getSource().equals(point1.prm)|| point2!=null && e.getSource().equals(point2.prm)){setTo(getPoint());}
            super.stateChanged(e);
        }
    } // class Function2D.FunctionPointModel
}