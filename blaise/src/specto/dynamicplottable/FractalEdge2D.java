/**
 * FractalEdge2D.java
 * Created on Mar 11, 2008
 */

package specto.dynamicplottable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import scio.coordinate.R2;
import sequor.component.RangeTimer;
import sequor.model.IntegerRangeModel;
import specto.Plottable;
import specto.plottable.PointSet2D;
import specto.visometry.Euclidean2;

/**
 * Iterates a given edge structure many times. Based off of the underlying points.
 * @author Elisha Peterson
 */
public class FractalEdge2D extends DynamicPointSet2D {
    
    protected IntegerRangeModel maxIter;

    /** base points determining the fractal (need at least three). */
    Vector<Point2D> points;
    /** The path of the fractal. */
    PointSet2D edges;
    
    public FractalEdge2D(R2 p1,R2 p2){
        points=new Vector<Point2D>();
        maxIter=new IntegerRangeModel(5,0,6);
        setColor(Color.BLUE);
        add(p1);
        add(p2);
        edges=new PointSet2D();
        style.setValue(STYLE_POINTS_ONLY);
    }

    @Override
    public void add(Plottable<Euclidean2> p) {
        if(p instanceof Point2D){
            p.style.setValue(Point2D.CIRCLE);
            if(points.size()==0){
                points.add((Point2D)p);}
            else{
                points.insertElementAt((Point2D)p,points.size()-1);
            }
            super.add(p);
        }
    }

    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        g.setColor(getColor().brighter());
        edges.paintComponent(g, v);
        g.setColor(getColor());
        super.paintComponent(g, v);
    }
    

    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v, RangeTimer t) {
        g.setColor(getColor().brighter());
        edges.paintComponent(g,v,t);
        g.setColor(getColor());
        super.paintComponent(g,v);
    }

    @Override
    public int getAnimatingSteps() {
        return edges.getAnimatingSteps();
    }
    

    @Override
    public void recompute() {
        try {
            super.recompute();
            R2Transform at = getStandardTransform(points.get(0).getPoint(), points.lastElement().getPoint());
            Vector<R2> generators = new Vector<R2>();
            java.awt.geom.Point2D.Double tempPoint;
            for (int i = 1; i < points.size() - 1; i++) {
                tempPoint=(java.awt.geom.Point2D.Double)at.inverseTransform(points.get(i).getPoint(),null);
                generators.add(new R2(tempPoint.x,tempPoint.y));
            }
            // iterations
            Vector<R2> temp = new Vector<R2>();
            Vector<R2> result = new Vector<R2>();
            result.add(new R2(0,0));
            result.addAll(generators);
            result.add(new R2(1,0));
            for(int i=0;i<maxIter.getValue();i++){
                temp.clear();
                for(int j=0;j<result.size()-1;j++){
                    temp.add(result.get(j));
                    temp.addAll(getSubPoints(result.get(j),result.get(j+1),generators));
                }
                temp.add(result.lastElement());
                result.clear();
                result.addAll(temp);
            }            
            edges.setPath(at.transform(result));
            
        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(FractalEdge2D.class.getName()).log(Level.SEVERE, null, ex);
        }
        // subsequent iterations
        
    }
    
    /** Returns transformed vector of points specified by generators. */
    Vector<R2> getSubPoints(R2 first,R2 last,Vector<R2> generators) throws NoninvertibleTransformException{
        R2Transform at=getStandardTransform(first,last);
        Vector<R2> result = new Vector<R2>();
        for(R2 p:generators){result.add(at.transform(p));}
        return result;
    }
    R2Transform getStandardTransform(R2 first,R2 last){
        return new R2Transform(last.x-first.x,last.y-first.y,-last.y+first.y,last.x-first.x,first.x,first.y);
    }
    
    public IntegerRangeModel getIterModel(){return maxIter;}    

    @Override
    public JMenu getOptionsMenu() {
        JMenu result=edges.getOptionsMenu();
        result.setText("Fractal Edge Options");
        return result;
    }
    @Override
    public String toString(){return "Fractal Edge";}
    
    /** Modified version of AffineTransform */
    class R2Transform extends AffineTransform{
        public R2Transform(double m00, double m10, double m01, double m11, double m02, double m12) {
            super(m00, m10, m01, m11, m02, m12);
        }
        
        public R2 transform(R2 input){
            java.awt.geom.Point2D.Double temp=null;
            temp=(java.awt.geom.Point2D.Double)super.transform(input,null);
            return new R2(temp.x,temp.y);
        }        
        
        public Vector<R2> transform(Vector<R2> inputs){
            Vector<R2> result=new Vector<R2>();
            for(R2 p:inputs){result.add(transform(p));}
            return result;
        }   
    }
}
