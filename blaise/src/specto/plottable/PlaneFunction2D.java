/*
 * PlaneFunction2D.java
 * 
 * Created on Sep 27, 2007, 1:19:00 PM
 */

package specto.plottable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import scio.function.Function;
import scio.function.FunctionValueException;
import specto.Plottable;
import scio.coordinate.R2;
import specto.visometry.Euclidean2;

/**
 * Draws a two-input/one-output function on the Cartesian Plane. Requires such a function to work. Multiple style settings will be supported.
 * @author ae3263
 */
public class PlaneFunction2D extends Plottable<Euclidean2>{
    Color color;
    Function<R2,Double> function;
    private static final Function<R2,Double> DEFAULT_FUNCTION=new Function<R2,Double>(){
        @Override
        public Double getValue(R2 p){return 2-Math.cos(p.x)*Math.cos(p.x)-Math.sin(p.y)*Math.sin(p.y);}
        @Override
        public Double minValue(){return 0.0;}
        @Override
        public Double maxValue(){return 4.0;}
        @Override
        public Vector<Double> getValue(Vector<R2> x) {
            Vector<Double> result=new Vector<Double>(x.size());
            for(R2 r:x){result.add(getValue(r));}
            return result;
        }
        };
    public PlaneFunction2D(Euclidean2 vis){this(vis,DEFAULT_FUNCTION);}
    public PlaneFunction2D(Euclidean2 vis,Function<R2,Double> function){
        super(vis);
        setOptionsMenuBuilding(true);
        color=Color.ORANGE;
        this.function=function;
    }

    private int style=DOTS;
    public static final int DOTS=0;
    public static final int COLORS=1;
    public static final int CONTOURS=2;
    public static final int DENSITY=3;
    
    public void setStyle(int newStyle){
        if(style!=newStyle){
            style=newStyle;
            fireStateChanged();
        }
    }
    
    // DRAW METHODS
    
    
    @Override
    public void recompute(){}
        
    @Override
    public void paintComponent(Graphics2D g){
        g.setColor(color);
        switch(style){
        case DOTS:
            for(double px:visometry.getSparseXRange(20)){
                for(double py:visometry.getSparseYRange(20)){
                try {
                    g.fill(drawDot(px, py, getRadius(px, py)));
                } catch (FunctionValueException ex) {}
                }
            }
            break;
        case COLORS:
            break;
        case CONTOURS:
            break;
        case DENSITY:
            break;
        }
    }
    
    public double getRadius(double x,double y) throws FunctionValueException{
        double value=function.getValue(new R2(x,y));
        value=8*(value-function.minValue())/(function.maxValue()-function.minValue())+1;
        return(value<0)?0:value;
    }
    
    public Ellipse2D.Double drawDot(double x,double y,double r){
        return new Ellipse2D.Double(visometry.toWindowX(x)-r,visometry.toWindowY(y)-r,2*r,2*r);
    }

    public JMenu getOptionsMenu() {
        JMenu result=new JMenu("Plane Function");
        result.add(new JMenuItem("Color",null));
        result.addSeparator();
        result.add(new JMenuItem("Dots")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(DOTS);}            
        });
        result.add(new JMenuItem("Colors")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(COLORS);}            
        });
        result.add(new JMenuItem("Contours")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(CONTOURS);}            
        });
        result.add(new JMenuItem("Density")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(DENSITY);}            
        });
        return result;
    }
}

