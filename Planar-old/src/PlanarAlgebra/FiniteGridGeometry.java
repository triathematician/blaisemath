package PlanarAlgebra;

import Euclidean.PPoint;
import Interface.BGeometry;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class FiniteGridGeometry implements BGeometry<Point> {
    final int TOP=5;
    final int BOTTOM=5;
    final int LEFT=5;
    final int RIGHT=5;
    Point min;
    Point max;
    Container owner;
    /**
     * Constructor: creates a new instance of FiniteGridGeometry
     */
    public FiniteGridGeometry(){setBounds(1,1);}
    public FiniteGridGeometry(Container owner){setWindow(owner);setBounds(1,1);}
    public FiniteGridGeometry(Container owner,int w,int h){setWindow(owner);setBounds(w,h);}
    
    public void setMin(Point min){}
    public void setMax(Point max){this.max=max;}
    public void setBounds(int w,int h){min=new Point(0,0);max=new Point(w,h);}
    
    public Point2D.Double toWindow(Point tp){return toWindow(tp.x,tp.y);}
    public Point2D.Double toWindow(int x,int y){return toWindow((double)x,(double)y);}
    public Point2D.Double toWindow(double x,double y){
        return new Point2D.Double((double)x/max.x*(getWinWidth()-LEFT-RIGHT)+LEFT,
                getWinHeight()-(double)y/max.y*(getWinHeight()-TOP-BOTTOM)-TOP);
    }
    
    public Point toGeometry(int wx, int wy){
        //System.out.println("max.x="+max.x+", max.y="+max.y);
        //System.out.println("wx= "+wx+", winwid="+getWinWidth());
        return new Point((int)((wx-LEFT)/((double)getWinWidth()-LEFT-RIGHT)*max.x),
                (int)((getWinHeight()+TOP-wy)/((double)getWinHeight()-TOP-BOTTOM)*max.y));
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
    
    public void setWindow(Container owner){this.owner=owner;}

    public Point getWinMin() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point getWinCtr() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point getWinMax() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getWinWidth(){return owner.getWidth();}
    public int getWinHeight(){return owner.getHeight();}

    public Point setBounds(Point min, Point max) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point getGeoMin(){return min;}

    public Point getGeoCtr() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point getGeoMax(){return max;}

    public Point toGeometry(Point p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void computeTransformation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addActionListener(ActionListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeActionListener(ActionListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void fireActionPerformed(String code) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
