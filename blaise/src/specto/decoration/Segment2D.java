/*
 * Segment2D.java
 * Created on Feb 10, 2008
 */

package specto.decoration;

import javax.swing.event.ChangeEvent;
import specto.dynamicplottable.*;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.swing.JMenu;
import scio.coordinate.R2;
import sequor.model.ComboBoxRangeModel;
import specto.Decoration;
import specto.visometry.Euclidean2;

/**
 * Implements a segment between two 2D points. Uses two <b>Point2D</b>s at the endpoints to control the position of the segment.
 *
 * @author Elisha Peterson
 */
public class Segment2D extends Decoration<Euclidean2> {
    /** Second point of the segment; the first need not be stored since it is listed under "parent" */
    Point2D point2;
    
    /** Default constructor */
    public Segment2D(Point2D point1,Point2D point2){
        super(point1);
        initStyle();
        this.point2=point2;
    }

    /** Returns first point */
    public R2 getPoint1() {return ((Point2D)parent).getPoint();}
    /** Sets first point */
    public void setPoint1(R2 point1) {((Point2D)parent).setPoint(point1);}
    /** Returns second point */
    public R2 getPoint2() {return point2.getPoint();}
    /** Sets second point */
    public void setPoint2(R2 point2) {this.point2.setPoint(point2);}

    @Override
    public void recompute(){}

    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
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

    @Override
    public JMenu getOptionsMenu(){return null;}
    
    
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
    
    /** For selecting the style. */
    protected ComboBoxRangeModel style;
    
    public void initStyle(){
        style=new ComboBoxRangeModel(styleStrings,LINE_SEGMENT,0,3);
        style.addChangeListener(this);
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {}
    
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
}