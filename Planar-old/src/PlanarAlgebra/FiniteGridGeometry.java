package PlanarAlgebra;

import Euclidean.PPoint;
import Interface.BGeometry;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * <b>FiniteGridGeometry.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 9, 2007, 7:34 AM</i><br><br>
 *
 * This geometry converts an integer point range [0,n]x[0,m] to the
 *   plot window boundaries. At the moment, the points are just evenly
 *   spaced across the range, with a bit of a boundary on the outside.
 */
public class FiniteGridGeometry extends BGeometry<Point> {
    final int TOP=5;
    final int BOTTOM=5;
    final int LEFT=5;
    final int RIGHT=5;
    /**
     * Constructor: creates a new instance of FiniteGridGeometry
     */
    public FiniteGridGeometry(){min=new Point();max=new Point();setBounds(1,1);}
    public FiniteGridGeometry(Container owner){setWindow(owner);min=new Point();max=new Point();setBounds(1,1);}
    public FiniteGridGeometry(Container owner,int w,int h){setWindow(owner);min=new Point();max=new Point();setBounds(w,h);}
    
    public void setMin(Point min){}
    public void setMax(Point max){this.max=max;}
    public void setBounds(int w,int h){min.move(0,0);max.move(w,h);}
    
    public Point2D.Double toWindow(Point tp){return toWindow(tp.x,tp.y);}
    public Point2D.Double toWindow(int x,int y){return toWindow((double)x,(double)y);}
    public Point2D.Double toWindow(double x,double y){
        return new Point2D.Double((double)x/max.x*(wWidth()-LEFT-RIGHT)+LEFT,
                wHeight()-(double)y/max.y*(wHeight()-TOP-BOTTOM)-TOP);
    }
    
    public Point toGeometry(int wx, int wy){
        return new Point((int)((wx-LEFT)/(wWidth()-LEFT-RIGHT)*max.x),
                (int)((wHeight()+TOP-wy)/(wHeight()-TOP-BOTTOM)*max.y));
    }
    
    /** Returns true if a grid point was clicked, otherwise false. */
    public Point clicked(MouseEvent e){
        Point2D.Double pt=toWindow(toGeometry(e.getX(),e.getY()));
        if (pt.distanceSq(e.getX(),e.getY())<30){
            return toGeometry(e.getX(),e.getY());
        }
        return null;
    }

    public Point closest(MouseEvent e) {
        return toGeometry(e.getX(),e.getY());
    }
}
