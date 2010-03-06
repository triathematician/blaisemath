/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * FractalShape2D.java
 * Created on Apr 4, 2008
 */

package specto.euclidean2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.util.Vector;
import scio.coordinate.R2;
import scio.coordinate.R2Transform;
import scio.fractal.IteratedTransform;
import sequor.model.IntegerRangeModel;
import sequor.style.LineStyle;
import specto.Plottable;
import specto.euclidean2.Euclidean2;
import sequor.style.PointStyle;

/**
 *
 * @author Elisha Peterson
 */
public abstract class FractalShape2D extends DynamicPointSet2D {
    /** Whether the shape should be filled. */
    boolean filled=false;
    /** The shape which will be drawn. */
    Shape s;    
    /** The number of iterations to apply. */
    protected IntegerRangeModel maxIter;
    /** Transforms used to generate smaller copies of the initial shape. */
    Vector<AffineTransform> replicators;
    
    public FractalShape2D(){
        
    }
    
    @Override
    public void add(Plottable<Euclidean2> p) {
        if(p instanceof Point2D){
            p.style.setValue(PointStyle.RING);
        }
        super.add(p);
    }

    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        g.setColor(getColor().brighter());
        g.setStroke(LineStyle.STROKES[LineStyle.THIN]);
        if(s!=null){
            if(filled){
                g.fill(v.getAffineTransformation().createTransformedShape(s));
            }else{
                g.draw(v.getAffineTransformation().createTransformedShape(s));
            }
        }
        g.setColor(getColor());
        super.paintComponent(g, v);
    }
    
    public IntegerRangeModel getIterModel(){return maxIter;} 

    /** Transform taking the underlying set to the unit box. */
    public AffineTransform getStandardTransform(){
        R2 p1=getPoint(0);
        R2 p2=getPoint(plottables.size()-1);
        return R2Transform.getLineTransform(p1.x,p1.y,p2.x,p2.y);
    }
    
    // STYLE OPTIONS
    
    @Override
    public String toString(){return "Fractal Shape";}
    
    
    // INNER CLASSES
    
    /** Iterates a given edge structure many times. Based off of the underlying points. */
    public static class Edges extends FractalShape2D{
        /** Initializes with given points. */
        public Edges(R2 p1,R2 p2){
            maxIter=new IntegerRangeModel(2,0,5);
            setColor(Color.BLUE);
            add(p1);
            add(p2);        
            style.setValue(STYLE_POINTS_ONLY);
        }    

        @Override
        public void recompute(Euclidean2 v) {   
            try{
                Path2D.Double initialCurve = getPath(false);
                AffineTransform at=getStandardTransform();
                AffineTransform at2=at.createInverse();
                initialCurve=(Path2D.Double) at2.createTransformedShape(initialCurve);
                replicators=IteratedTransform.getTransforms(IteratedTransform.getSegments(initialCurve));
                s=IteratedTransform.iteratedPath(initialCurve,replicators,maxIter.getValue(),false,true,false);
                s=at.createTransformedShape(s);
            }catch(NoninvertibleTransformException e){}
        }

        @Override
        public String toString(){return "Fractal Edge";}
    } // CLASS FractalShape2D.Edges
    
    
    /** Creates a space-filling curve. */
    public static class SpaceFilling extends FractalShape2D {
        public SpaceFilling(){this(new R2(0,0),new R2(0,1));add(1,1);add(1,0);}

        /** Constructor specifying points in repeated shape.
         * These should be contained in the unit square to be properly used.
         * @param p1
         * @param p2
         */
        public SpaceFilling(R2 p1, R2 p2) {
            maxIter=new IntegerRangeModel(4,0,6);
            setColor(Color.BLUE);
            add(p1);
            add(p2);
            style.setValue(STYLE_POINTS_ONLY);
            replicators=new Vector<AffineTransform>();
            replicators.add(new AffineTransform(0,.5,.5,0,0,0));
            replicators.add(new AffineTransform(.5,0,0,.5,0,.5));
            replicators.add(new AffineTransform(.5,0,0,.5,.5,.5));
            replicators.add(new AffineTransform(0,-.5,-.5,0,1,.5));
        }

        final R2Transform shrink=new R2Transform(.5,0,0,.5,.25,.25);

        /** Recomputes the shape. */
        @Override
        public void recompute(Euclidean2 v){
            try{
                Path2D.Double initialCurve=getPath(false);
                AffineTransform at=getStandardTransform();
                AffineTransform at2=at.createInverse();
                initialCurve=(Path2D.Double) at2.createTransformedShape(initialCurve);
                initialCurve=(Path2D.Double) shrink.createTransformedShape(initialCurve);
                s=IteratedTransform.iteratedPath(initialCurve,replicators,maxIter.getValue(),false,true,false);
                s=at.createTransformedShape(s);
            }catch(NoninvertibleTransformException e){}
        }

        @Override
        public String toString(){return "Space Filling Curve";}
    } // CLASS FractalShape2D.SpaceFilling
    
    
    /** Creates a Sierpinski Triangle. */
    public static class Sierpinski extends FractalShape2D {
        public Sierpinski(){this(new R2(0,0),new R2(.5,Math.sqrt(3)/2), new R2(1,0));}
        public Sierpinski(R2 p1,R2 p2,R2 p3){
            maxIter=new IntegerRangeModel(4,0,7);
            setColor(Color.GRAY);
            add(p1);
            add(p2);
            add(p3);
            style.setValue(STYLE_POINTS_ONLY);
            replicators=new Vector<AffineTransform>();
            replicators.add(new AffineTransform(.5,0,0,.5,0,0));
            replicators.add(new AffineTransform(.5,0,0,.5,.25,Math.sqrt(3)/4));
            replicators.add(new AffineTransform(.5,0,0,.5,.5,0));
            filled=true;
        }

        /** Recomputes the shape. */
        @Override
        public void recompute(Euclidean2 v){
            try{
                Path2D.Double initialCurve=getPath(true);
                AffineTransform at=getStandardTransform();
                AffineTransform at2=at.createInverse();
                initialCurve=(Path2D.Double) at2.createTransformedShape(initialCurve);
                s=IteratedTransform.iteratedPath(initialCurve,replicators,maxIter.getValue(),false,false,false);
                s=at.createTransformedShape(s);
            }catch(NoninvertibleTransformException e){}
        }

        @Override
        public String toString(){return "Sierpinski Triangle";}
    }
}
