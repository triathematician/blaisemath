/*
 * Segment2D.java
 * Created on Feb 10, 2008
 */

package specto.decoration;

import javax.swing.event.ChangeEvent;
import specto.dynamicplottable.*;
import java.awt.Graphics2D;
import javax.swing.JMenu;
import scio.coordinate.R2;
import specto.Decoration;
import specto.PlottableGroup;
import specto.visometry.Euclidean2;

/**
 * <p>
 * Implements a segment between two 2D points. Uses two <b>R2</b>s for the endpoints
 * and connects them with a solid line. Optionally should have two <b>Point2D</b>s at the
 * endpoints to control the position of the segment.
 * </p>
 * @author Elisha Peterson
 */
public class Segment2D extends Decoration<Euclidean2> {
    /** Second point of the segment; the first need not be stored since it is listed under "parent" */
    Point2D point2;
    
    /** Default constructor */
    public Segment2D(Point2D point1,Point2D point2){
        super(point1);
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
    public void paintComponent(Graphics2D g) {
        paintLine(g);
    }
    
    /** Draws the segment between the two points */
    public void paintLine(Graphics2D g){
        // TODO add draw methods
        switch(type){
            case LINE_SEGMENT:
            case LINE_RAY:
            case LINE_LINE:
                g.draw(visometry.line(getPoint1(),getPoint2()));
        }
    }

    @Override
    public JMenu getOptionsMenu(){return null;}
    
    
    // STYLE OPTIONS
    
    /** Represents a line segment */
    public static final int LINE_SEGMENT = 0;
    /** Draws as a ray */
    public static final int LINE_RAY = 1;
    /** Draw as a line */
    public static final int LINE_LINE = 2;
    /** Switch for which type of line this is. */
    private int type = LINE_SEGMENT;
    /** Returns the type of line being displayed. */
    public int getType() {return type;}
    /** Sets the type of line being displayed. */
    public void setType(int type) {this.type = type;}

    @Override
    public void stateChanged(ChangeEvent e) {}
    
    // TODO further options for display style
}
