/*
 * PointSet2D.java
 * Created on Sep 27, 2007, 12:38:05 PM
 */

package specto.euclidean3;

import java.util.logging.Level;
import java.util.logging.Logger;
import scio.function.FunctionValueException;
import specto.euclidean2.*;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.event.ChangeListener;
import sequor.model.ColorModel;
import specto.Animatable;
import scio.coordinate.R2;
import scio.coordinate.R3;
import sequor.component.RangeTimer;
import sequor.model.StringRangeModel;
import sequor.model.PointRangeModel;
import sequor.style.VisualStyle;
import specto.Plottable;

/**
 *
 * @author Elisha Peterson
 */
public class PointSet3D extends Plottable<Euclidean3> implements Animatable<Euclidean3>,ChangeListener{
    
    Vector<R3> points;
    private String label;
    
    public PointSet3D(){this(new Vector<R3>(),Color.BLACK);}
    public PointSet3D(Color c){this(new Vector<R3>(),c);}
    public PointSet3D(Vector<R3> points,Color c){
        initStyle();
        setColor(c);
        this.points=points;
    }
    public PointSet3D(Vector<R3> points,Color c, int style){
        initStyle();
        this.style.setValue(style);
        setColor(c);
        this.points=points;
    }
    public PointSet3D(Vector<R3> points, ColorModel colorModel) {
        initStyle();
        setColorModel(colorModel);
        this.points=points;
    }
    public PointSet3D(Vector<R3> points, ColorModel colorModel, int style) {
        initStyle();
        this.style.setValue(style);
        setColorModel(colorModel);
        this.points=points;
    }
    
    
    // BEAN PATTERNS
    
    /** Whether this element animates. */    
    public boolean animationOn=true;
    public void setAnimationOn(boolean newValue) { animationOn=newValue; }
    public boolean isAnimationOn() { return animationOn; }
    
    public Vector<R3> getPath(){return points;}
    public void setPath(Vector<R3> path){points=path;}
    public void setLabel(String s){label=s;}
    
    
    // DRAW METHODS

    @Override
    public void paintComponent(Graphics2D g,Euclidean3 v){  
        if(points==null || points.size()==0){return;}
        g.setStroke(strokes[style.getValue()]);
        if(style.getValue().equals(POINTS_ONLY)){
            for(int i=0;i<points.size();i++){ drawDot(g,v,i); }
        } else {
            drawPath(g,v,0,points.size());
        }
        if(label!=null){
            java.awt.geom.Point2D.Double winCenter = v.toWindow(points.firstElement());
            g.setComposite(VisualStyle.COMPOSITE5);
            g.drawString(label,(float)winCenter.x+5,(float)winCenter.y+5);
        }
        g.setComposite(AlphaComposite.SrcOver);
    }
    @Override
    public void paintComponent(Graphics2D g,Euclidean3 v,RangeTimer t){
        if(points==null || points.size()==0){return;}
        if(style.getValue().equals(POINTS_ONLY)){
            for(int i=0;i<points.size();i++){ drawDot(g,v,i); }
        } else {
            int curVal=t.getCurrentIntValue();
            g.setStroke(strokes[style.getValue()]);
            switch(animateStyle.getValue()){
                case ANIMATE_DRAW:
                    drawPath(g,v,t.getFirstIntValue(),curVal);
                    break;
                case ANIMATE_DOT:
                    drawDot(g,v,curVal);
                    break;
                case ANIMATE_TRACE:
                    drawPath(g,v,0,points.size());
                    drawDot(g,v,curVal);
                    break;
                case ANIMATE_TRAIL:
                default:
                    drawPath(g,v,0,curVal);
                    drawPath(g,v,curVal-5,curVal);
                    drawDot(g,v,curVal);
                    break;
            }  
            if (curVal >= points.size()) { curVal = points.size() - 1; }
            java.awt.geom.Point2D.Double labelCenter = v.toWindow(points.get(curVal));
            g.setComposite(VisualStyle.COMPOSITE5);
            g.drawString(points.get(curVal).toString(),(float)labelCenter.x+5,(float)labelCenter.y-5);
        }
        g.setComposite(AlphaComposite.SrcOver);
        if(label!=null){
            java.awt.geom.Point2D.Double winCenter = v.toWindow(points.firstElement());
            g.setComposite(VisualStyle.COMPOSITE5);
            g.drawString(label,(float)winCenter.x+5,(float)winCenter.y+5);
        }  
        g.setComposite(AlphaComposite.SrcOver);  
    }
    
    /** Draws dot at given position. */
    public void drawDot(Graphics2D g, Euclidean3 v, int pos){
        int posB=pos<0?0:(pos>=points.size()?points.size()-1:pos);
        v.fillDot(g,points.get(posB),3.0);
    }
        
    /** Draws path as either stereographic image or regular, between given start and end location*/
    public void drawPath(Graphics2D g, Euclidean3 v, int start, int end){
        if (v.isStereo()){
            g.setComposite(VisualStyle.COMPOSITE5);
            g.setColor(v.leftColor);
            g.draw(drawPath(v, start, end, 1));
            g.setColor(v.rightColor);
            g.draw(drawPath(v, start, end, 2));
        } else {
            g.draw(drawPath(v, start, end, 0));
        }
    }
        
    public Path2D.Double drawPath(Euclidean3 v,int start,int end,int eye){
        Vector<R2> projPoints = new Vector<R2>();
        try {
            switch (eye) {
                case 1: projPoints = v.proj.getValueLeft(points); break;
                case 2: projPoints = v.proj.getValueRight(points); break;
                default: projPoints = v.proj.getValue(points);
            }
        } catch (FunctionValueException ex) { }
        Path2D.Double result=new Path2D.Double();
        int startB=start<0?0:(start>=projPoints.size()?projPoints.size()-1:start);
        int endB=end<0?0:(end>=projPoints.size()?projPoints.size()-1:end);
        result.moveTo(projPoints.get(startB).x,projPoints.get(startB).y);
        for(int i=startB;i<=endB;i++){result.lineTo(projPoints.get(i).x,projPoints.get(i).y);}
        v.transform(result);
        return result;
    }
    
    
    // STYLE SETTINGS

    public static final int REGULAR=0;
    public static final int THIN=1;
    public static final int MEDIUM=2;
    public static final int THICK=3;
    public static final int DOTTED=4;
    public static final int SKETCH=5;    
    public static final int POINTS_ONLY=6;

    public static final float[] dash2={1.0f,4.0f};
    public static final float[] dash3={2.0f,6.0f};
    public static final Stroke[] strokes={
        new BasicStroke(2.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL),
        new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL),
        new BasicStroke(3.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL),
        new BasicStroke(4.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL),
        new BasicStroke(2.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL,10.0f,dash2,0.0f),
        new BasicStroke(2.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL,10.0f,dash3,0.0f),
        new BasicStroke(2.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL)
    };
    
    public static final String[] styleStrings={"Regular","Thin","Medium","Thick","Dotted","Sketch","Points Only"};
    @Override
    public String[] getStyleStrings() {return styleStrings;}
    
    public static final int ANIMATE_DRAW=0;
    public static final int ANIMATE_DOT=1;
    public static final int ANIMATE_TRACE=2;
    public static final int ANIMATE_TRAIL=3;
    
    static final String[] animateStyleStrings={"Draw path","Moving dot","Trace path","Draw with trail"};
    public StringRangeModel animateStyle;

    @Override
    public void initStyle(){
        super.initStyle();
        animateStyle=new StringRangeModel(animateStyleStrings,ANIMATE_TRAIL,0,3);
        animateStyle.addChangeListener(this);
    }    
    @Override
    public String toString(){return "Path or Set of Points";}

    
    // EVENT HANDLING
    
    @Override
    public JMenu getOptionsMenu() {return animateStyle.appendToMenu(super.getOptionsMenu());}
    
    
    // ANIMATION ELEMENTS

    public int getAnimatingSteps() {return (points==null)?0:points.size();}
}
