/*
 * PointSet2D.java
 * 
 * Created on Sep 27, 2007, 12:38:05 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package specto.dynamicplottable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import specto.Animatable;
import specto.DynamicPlottable;
import sequor.component.RangeTimer;
import scio.coordinate.R2;
import specto.visometry.Euclidean2;

/**
 *
 * @author ae3263
 */
public class PointSet2D extends DynamicPlottable<Euclidean2> implements Animatable<Euclidean2>{
    Vector<R2> points;
    public PointSet2D(){
        setOptionsMenuBuilding(true);
        points=new Vector<R2>();
    }
    public PointSet2D(Vector<R2> points,Color c){
        setOptionsMenuBuilding(true);
        this.points=points;
        setColor(c);
    }
    public PointSet2D(Color c){
        setOptionsMenuBuilding(true);
        points=new Vector<R2>();
        setColor(c);
    }
    
    public Vector<R2> getPath(){
        return points;
    }
    
    
    // DRAW METHODS
    
    @Override
    public void recompute(){}

    @Override
    public void paintComponent(Graphics2D g) {
        if(points.size()==0){return;}
        g.setColor(color);
        g.setStroke(stroke);
        g.draw(drawPath(0,points.size()));
    }
    @Override
    public void paintComponent(Graphics2D g,RangeTimer t){
        if(points.size()==0){return;}
        g.setColor(color);
        g.setStroke(stroke);
        switch(animateStyle){
        case ANIMATE_DRAW:g.draw(drawPath(t.getStartStep(),t.getCurrentStep()));break;
        case ANIMATE_DOT:g.fill(drawDot(t.getCurrentStep()));break;
        case ANIMATE_TRACE:g.draw(drawPath(0,points.size()));g.fill(drawDot(t.getCurrentStep()));break;
        case ANIMATE_TRAIL:
            g.setColor(color.brighter());
            g.draw(drawPath(0,t.getCurrentStep()));
            
            g.setColor(color.darker());
            g.draw(drawPath(t.getCurrentStep()-5,t.getCurrentStep()));
            g.fill(drawDot(t.getCurrentStep()));
            break;
        }        
    }
    
    public Shape drawDot(int pos){
        int posB=pos<0?0:(pos>=points.size()?points.size()-1:pos);
        return visometry.dot(points.get(posB),3.0);
    }
    
    public Path2D.Double drawPath(int start,int end){
        Path2D.Double result=new Path2D.Double();
        int startB=start<0?0:(start>=points.size()?points.size()-1:start);
        int endB=end<0?0:(end>=points.size()?points.size()-1:end);
        result.moveTo(points.get(startB).x,points.get(startB).y);
        for(int i=startB;i<=endB;i++){result.lineTo(points.get(i).x,points.get(i).y);}
        result.transform(visometry.getAffineTransformation());
        return result;
    }
    
    // STYLE SETTINGS
    
    protected Color color=Color.RED;
    protected Stroke stroke=MEDIUM_STROKE;
    private int style=MEDIUM;
    private int animateStyle=ANIMATE_TRAIL;
    
    public static final int LINE=0;
    public static final int MEDIUM=1;
    public static final int THICK=2;
    public static final int DOTTED=3;
    public static final int SKETCH=4;    
    
    public static final int ANIMATE_DRAW=0;
    public static final int ANIMATE_DOT=1;
    public static final int ANIMATE_TRACE=2;
    public static final int ANIMATE_TRAIL=3;
    
    public static final Stroke BASIC_STROKE=new BasicStroke(2.0f);
    public static final Stroke MEDIUM_STROKE=new BasicStroke(3.0f);
    public static final Stroke THICK_STROKE=new BasicStroke(4.0f);
    public static final float[] dash2={2.0f,2.0f};
    public static final Stroke DOTTED_STROKE=new BasicStroke(2.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dash2,0.0f);
    public static final float[] dash3={1.0f,4.0f};
    public static final Stroke VERY_DOTTED_STROKE=new BasicStroke(2.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dash3,0.0f);
    
    public void setColor(Color newValue){if(color!=newValue){color=newValue;fireStateChanged();}}
    public void setStroke(Stroke newValue){if(stroke!=newValue){stroke=newValue;fireStateChanged();}}
    public void setStyle(int newValue){
        if(style!=newValue){
            switch(newValue){
            case LINE:stroke=BASIC_STROKE;break;
            case MEDIUM:stroke=MEDIUM_STROKE;break;
            case THICK:stroke=THICK_STROKE;break;
            case DOTTED:stroke=VERY_DOTTED_STROKE;break;
            case SKETCH:break;
            default:break;
            }
            style=newValue;
            fireStateChanged();
        }
    }
    
    
    // EVENT HANDLING
    
    @Override
    public boolean clicked(MouseEvent e){return false;}

    public JMenu getOptionsMenu() {
        JMenu result=new JMenu("Path");
        result.add(new JMenuItem("Color",null));
        JMenu strokeSubMenu=new JMenu("Stroke");
        strokeSubMenu.add(new JMenuItem("Regular")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(LINE);}            
        });
        strokeSubMenu.add(new JMenuItem("Medium")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(MEDIUM);}            
        });
        strokeSubMenu.add(new JMenuItem("Thick")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(THICK);}            
        });
        strokeSubMenu.add(new JMenuItem("Dotted")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(DOTTED);}            
        });
        result.add(strokeSubMenu);
        JMenu animateSubMenu=new JMenu("Animation Style");
        animateSubMenu.add(new JMenuItem("Draw")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){animateStyle=ANIMATE_DRAW;}
        });
        animateSubMenu.add(new JMenuItem("Dot")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){animateStyle=ANIMATE_DOT;}
        });
        animateSubMenu.add(new JMenuItem("Trace")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){animateStyle=ANIMATE_TRACE;}
        });
        animateSubMenu.add(new JMenuItem("Trail")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){animateStyle=ANIMATE_TRAIL;}
        });
        result.add(animateSubMenu);
        return result;
    }
}
