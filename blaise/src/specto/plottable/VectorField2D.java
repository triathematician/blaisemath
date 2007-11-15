/*
 * Parametric2D.java
 * Created on Sep 27, 2007, 1:12:35 PM
 */

// TODO when function is assigned to this, compute the appropriate range of vector lengths

package specto.plottable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import scio.function.Function;
import specto.Plottable;
import scio.coordinate.R2;
import specto.visometry.Euclidean2;

/**
 * Draws a one-input/two-output curve on the Cartesian Plane. Requires two functions and a range of t-values.
 * @author ae3263
 */
public class VectorField2D extends Plottable<Euclidean2> {
    Color color;
    Function<R2,R2> function;
    public static final Function<R2,R2> DEFAULT_FUNCTION=new Function<R2,R2>(){
        public R2 getValue(R2 p){return new R2(p.y,-p.y-10*Math.sin(p.x));}
        public R2 minValue(){return new R2(-10.0,-10.0);}
        public R2 maxValue(){return new R2(10.0,10.0);}
        };
    public VectorField2D(){
        this(DEFAULT_FUNCTION);
    }
    public VectorField2D(Function<R2,R2> function){
        setOptionsMenuBuilding(true);
        color=Color.GRAY;
        this.function=function;
    }

    private int style=TRAILS;
    public static final int LINES=0;            // lines in the direction of the curves
    public static final int ARROWS=1;           // arrows in the direction of the curves
    public static final int DOT_LINES=2;        // ?? not sure what i was going for
    public static final int TRAILS=3;           // plot a few points forward and back from the current point
        
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
        case DOT_LINES:
            for(double px:visometry.getSparseXRange(30)){
                for(double py:visometry.getSparseYRange(30)){
                    g.fill(visometry.dot(new R2(px,py),2));
                    drawLine(g,new R2(px,py));
                }
            }
            break;
        case LINES:
            for(double px:visometry.getSparseXRange(30)){
                for(double py:visometry.getSparseYRange(30)){
                    drawLine(g,new R2(px,py));
                }
            }
            break;
        case ARROWS:
            for(double px:visometry.getSparseXRange(30)){
                for(double py:visometry.getSparseYRange(30)){
                    drawVector(g,new R2(px,py));
                }
            }
            break;
        case TRAILS:
            for(double px:visometry.getSparseXRange(30)){
                for(double py:visometry.getSparseYRange(30)){
                    drawTrail(g,new R2(px,py));
                }
            }
            break;
        }
    }
    
    /** Return angle and desired length of the arrow. The length is in window coordinates! */    
    public R2 getArrow(R2 p){
        double maxLength=function.maxValue().minus(function.minValue()).magnitude();
        R2 result=function.getValue(p);
        double magnitude=result.magnitude();
        double angle=result.angle();
        return new R2(25.0*magnitude/maxLength,angle);
    }
    
    /** Draws a vector in the direction of the field at the current point. */
    public void drawVector(Graphics2D g,R2 p){
        R2 arrow=getArrow(p);
        Point2D.Double winPoint=visometry.toWindow(p);
        Point2D.Double start=new Point2D.Double(winPoint.x-arrow.x*Math.cos(-arrow.y),winPoint.y-arrow.x*Math.sin(-arrow.y));
        Point2D.Double end=new Point2D.Double(winPoint.x+arrow.x*Math.cos(-arrow.y),winPoint.y+arrow.x*Math.sin(-arrow.y));
        Point2D.Double arrow1=new Point2D.Double(end.x-5*Math.cos(-arrow.y-Math.PI/6),end.y-5*Math.sin(-arrow.y-Math.PI/6));
        Point2D.Double arrow2=new Point2D.Double(end.x-5*Math.cos(-arrow.y+Math.PI/6),end.y-5*Math.sin(-arrow.y+Math.PI/6));
        g.draw(new Line2D.Double(start,end));
        g.draw(new Line2D.Double(end,arrow1));
        g.draw(new Line2D.Double(end,arrow2));
    }
    
    /** Draws a line in the direction of the field at the current point. */
    public void drawLine(Graphics2D g,R2 p){
        R2 arrow=getArrow(p);
        Point2D.Double winPoint=visometry.toWindow(p);
        Point2D.Double start=new Point2D.Double(winPoint.x-arrow.x*Math.cos(-arrow.y),winPoint.y-arrow.x*Math.sin(-arrow.y));
        Point2D.Double end=new Point2D.Double(winPoint.x+arrow.x*Math.cos(-arrow.y),winPoint.y+arrow.x*Math.sin(-arrow.y));
        g.draw(new Line2D.Double(start,end));       
    }
    
    /** Draws a short trail along the field at the current point. */
    public void drawTrail(Graphics2D g,R2 p){
        int NUM=10;
        double STEP=.1;
        // reverse-direction and forward-direction points
        Vector<R2> forward=new Vector<R2>();
        Vector<R2> backward=new Vector<R2>();
        forward.add(p);
        backward.add(p);
        for(int i=1;i<NUM;i++){
            forward.add(forward.get(i-1).plus(function.getValue(forward.get(i-1)).scaledToLength(STEP)));
            backward.add(backward.get(i-1).minus(function.getValue(backward.get(i-1)).scaledToLength(STEP)));
        }
        Path2D.Double path=new Path2D.Double();
        path.moveTo(visometry.toWindowX(p.x),visometry.toWindowY(p.y));
        for(int i=1;i<NUM;i++){
            path.lineTo(visometry.toWindowX(forward.get(i).x),visometry.toWindowY(forward.get(i).y));
        }
        path.moveTo(visometry.toWindowX(p.x),visometry.toWindowY(p.y));
        for(int i=1;i<NUM;i++){
            path.lineTo(visometry.toWindowX(backward.get(i).x),visometry.toWindowY(backward.get(i).y));
        }
        g.draw(path);
    }

    public JMenu getOptionsMenu() {
        JMenu result=new JMenu("Vector Field");
        result.add(new JMenuItem("Color",null));
        result.addSeparator();
        result.add(new JMenuItem("Lines")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(LINES);}            
        });
        result.add(new JMenuItem("Dot Lines")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(DOT_LINES);}            
        });
        result.add(new JMenuItem("Arrows")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(ARROWS);}            
        });
        result.add(new JMenuItem("Trails")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(TRAILS);}            
        });
        return result;
    } 
}

