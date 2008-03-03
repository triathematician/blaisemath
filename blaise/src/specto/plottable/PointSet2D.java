/*
 * PointSet2D.java
 * 
 * Created on Sep 27, 2007, 12:38:05 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package specto.plottable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import specto.Animatable;
import sequor.component.RangeTimer;
import scio.coordinate.R2;
import sequor.model.ComboBoxRangeModel;
import specto.Plottable;
import specto.VisualStyle;
import specto.visometry.Euclidean2;

/**
 *
 * @author ae3263
 */
public class PointSet2D extends Plottable<Euclidean2> implements Animatable<Euclidean2>,ChangeListener{
    Vector<R2> points;
    public PointSet2D(){this(new Vector<R2>(),Color.BLACK);}
    public PointSet2D(Color c){this(new Vector<R2>(),c);}
    public PointSet2D(Vector<R2> points,Color c){
        initStyle();
        setColor(c);
        this.points=points;
    }
    
    public Vector<R2> getPath(){
        return points;
    }
    public void setPath(Vector<R2> path){points=path;}
    
    
    // DRAW METHODS
    
    @Override
    public void recompute(){}

    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v){
        if(points.size()==0){return;}
        g.setStroke(strokes[style.getValue()]);
        g.draw(drawPath(v,0,points.size()));
    }
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v,RangeTimer t){
        if(points.size()==0){return;}
        g.setStroke(strokes[style.getValue()]);
        switch(animateStyle.getValue()){
            case ANIMATE_DRAW:
                g.draw(drawPath(v,t.getStartStep(),t.getCurrentStep()));
                break;
            case ANIMATE_DOT:
                g.fill(drawDot(v,t.getCurrentStep()));
                break;
            case ANIMATE_TRACE:
                g.draw(drawPath(v,0,points.size()));
                g.fill(drawDot(v,t.getCurrentStep()));
                break;
            case ANIMATE_TRAIL:
            default:
                g.setColor(color.getValue().brighter());
                g.draw(drawPath(v,0,t.getCurrentStep()));

                g.setColor(color.getValue().darker());
                g.draw(drawPath(v,t.getCurrentStep()-5,t.getCurrentStep()));
                g.fill(drawDot(v,t.getCurrentStep()));
                break;
        }        
    }
    
    public Shape drawDot(Euclidean2 v,int pos){
        int posB=pos<0?0:(pos>=points.size()?points.size()-1:pos);
        return v.dot(points.get(posB),3.0);
    }
    
    public Path2D.Double drawPath(Euclidean2 v,int start,int end){
        Path2D.Double result=new Path2D.Double();
        int startB=start<0?0:(start>=points.size()?points.size()-1:start);
        int endB=end<0?0:(end>=points.size()?points.size()-1:end);
        result.moveTo(points.get(startB).x,points.get(startB).y);
        for(int i=startB;i<=endB;i++){result.lineTo(points.get(i).x,points.get(i).y);}
        result.transform(v.getAffineTransformation());
        return result;
    }
    
    // STYLE SETTINGS

    public static final int REGULAR=0;
    public static final int MEDIUM=1;
    public static final int THICK=2;
    public static final int DOTTED=3;
    public static final int SKETCH=4;    

    public static final float[] dash2={1.0f,4.0f};
    public static final float[] dash3={2.0f,6.0f};
    public static final Stroke[] strokes={
        new BasicStroke(2.0f),
        new BasicStroke(3.0f),
        new BasicStroke(4.0f),
        new BasicStroke(2.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dash2,0.0f),
        new BasicStroke(2.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dash3,0.0f)};
    
    static final String[] styleStrings={"Regular","Medium","Thick","Dotted","Sketch"};
    public ComboBoxRangeModel style;
    
    public static final int ANIMATE_DRAW=0;
    public static final int ANIMATE_DOT=1;
    public static final int ANIMATE_TRACE=2;
    public static final int ANIMATE_TRAIL=3;
    
    static final String[] animateStyleStrings={"Draw path","Moving dot","Trace path","Draw with trail"};
    public ComboBoxRangeModel animateStyle;

    public void initStyle(){
        style=new ComboBoxRangeModel(styleStrings,MEDIUM,0,4);
        style.addChangeListener(this);
        animateStyle=new ComboBoxRangeModel(animateStyleStrings,ANIMATE_TRAIL,0,3);
        animateStyle.addChangeListener(this);
    }
    


    // EVENT HANDLING
    
    @Override
    public JMenu getOptionsMenu() {
        JMenu result=new JMenu("Path Options");
        result.add(getColorMenuItem());
        result.add(style.getSubMenu("Line Style"));
        result.add(animateStyle.getSubMenu("Animation Style"));
        try{
            for(JMenuItem mi:getDecorationMenuItems()){result.add(mi);}
        }catch(NullPointerException e){}
        return result;
    }

    public void stateChanged(ChangeEvent e) {redraw();}
}
