/*
 * Parametric2D.java
 * Created on Sep 27, 2007, 1:12:35 PM
 */

// TODO when function is assigned to this, compute the appropriate range of vector lengths

package specto.plottable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import scio.function.Function;
import scio.function.FunctionValueException;
import specto.Plottable;
import scio.coordinate.R2;
import sequor.model.ComboBoxRangeModel;
import specto.decoration.DESolution2D;
import specto.visometry.Euclidean2;

/**
 * Draws a one-input/two-output curve on the Cartesian Plane. Requires two functions and a range of t-values.
 * @author ae3263
 */
public class VectorField2D extends Plottable<Euclidean2> implements ChangeListener {
    Function<R2,R2> function;
    public static final Function<R2,R2> DEFAULT_FUNCTION=new Function<R2,R2>(){
        public R2 getValue(R2 p){return new R2(p.y,-p.y-10*Math.sin(p.x));}
        @Override
        public Vector<R2> getValue(Vector<R2> x) {
            Vector<R2> result=new Vector<R2>(x.size());
            for(R2 r:x){result.add(getValue(r));}
            return result;
        }
        public R2 minValue(){return new R2(-10.0,-10.0);}
        public R2 maxValue(){return new R2(10.0,10.0);}
        };
    public VectorField2D(){this(DEFAULT_FUNCTION);}
    public VectorField2D(Function<R2,R2> function){
        initStyle();
        setColor(Color.GRAY);
        this.function=function;
    }
    
    // DRAW METHODS
        
    @Override
    public void recompute(){}
    
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v){
        R2 point,start,end;
        switch(style.getValue()){
        case DOT_LINES:
            for(double px:v.getSparseXRange(30)){
                for(double py:v.getSparseYRange(30)){
                    try {
                        point=new R2(px,py);
                        g.fill(v.dot(point,2));
                        start=point.plus(DESolution2D.getScaledVector(function, point, 1.0));
                        end=point.plus(DESolution2D.getScaledVector(function, point, -1.0));
                        g.draw(v.lineSegment(start,end));   
                    } catch (FunctionValueException ex) {}
                }
            }
            break;
        case LINES:
            for(double px:v.getSparseXRange(30)){
                for(double py:v.getSparseYRange(30)){
                    try {
                        point=new R2(px,py);
                        start=point.plus(DESolution2D.getScaledVector(function, point, 1.0));
                        end=point.plus(DESolution2D.getScaledVector(function, point, -1.0));
                        g.draw(v.lineSegment(start,end));   
                    } catch (FunctionValueException ex) {}
                }
            }
            break;
        case ARROWS:
            Shape arrow;
            for(double px:v.getSparseXRange(30)){
                for(double py:v.getSparseYRange(30)){
                    try {
                        point=new R2(px,py);
                        arrow=v.arrow(point,point.plus(DESolution2D.getScaledVector(function, point, 1.0)),5.0);
                        g.draw(arrow);
                        g.fill(arrow);
                    } catch (FunctionValueException ex) {}
                }
            }
            break;
        case TRAILS:
            for(double px:v.getSparseXRange(30)){
                for(double py:v.getSparseYRange(30)){
                    try{
                        point=new R2(px,py);
                        g.draw(v.path(DESolution2D.calcNewton(function, point, NUM, STEP)));
                        g.draw(v.path(DESolution2D.calcNewton(function, point, NUM, -STEP)));
                    }catch(FunctionValueException e){}
                }
            }
            break;
        }
    }
    
    int NUM=10;
    double STEP=.1;
    
    // STYLE METHODS

    public ComboBoxRangeModel style;
    public static final int LINES=0;            // lines in the direction of the curves
    public static final int DOT_LINES=1;        // ?? not sure what i was going for
    public static final int ARROWS=2;           // arrows in the direction of the curves
    public static final int TRAILS=3;           // plot a few points forward and back from the current point
    static final String[] styleStrings={"Slopelines","Slopelines with dots","Vectors","Flows"};
    
    public void initStyle(){
        style=new ComboBoxRangeModel(styleStrings,TRAILS,0,3);
        style.addChangeListener(this);        
    }
    
    public JMenu getOptionsMenu() {
        JMenu result=new JMenu("Vector Field Options");
        result.add(getColorMenuItem());
        result.add(style.getSubMenu("Display Style"));
        try{
            for(JMenuItem mi:getDecorationMenuItems()){result.add(mi);}
        }catch(NullPointerException e){}
        return result;
    }

    public void stateChanged(ChangeEvent e) {redraw();}
}

