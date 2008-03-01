/*
 * PointSet2D.java
 * Created on Sep 27, 2007, 12:38:05 PM
 */

package specto.dynamicplottable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.model.PointRangeModel;
import specto.DynamicPlottable;
import scio.coordinate.R2;
import specto.style.PointStyle;
import specto.visometry.Euclidean2;

/**
 * Represents a point which can be moved around the screen. The data is stored in a PointRangeModel, which restricts the movement of
 * the point to within a particular range.
 * @author ae3263
 */
public class Point2D extends DynamicPlottable<Euclidean2> implements ChangeListener{
    
    // PROPERTIES
    
    protected PointRangeModel prm;
    public PointStyle style;    
    
    
    // CONSTRUCTORS
    
    /** Default constructor places point at the origin. */
    public Point2D(Euclidean2 vis){this(vis,new PointRangeModel());}
    /** Constructor places point at a given location. */
    public Point2D(Euclidean2 vis,R2 value){this(vis,new PointRangeModel(value));}
    /** Constructor given a PointRangeModel and a particular color. */
    public Point2D(Euclidean2 vis,PointRangeModel prm,Color c){this(vis,prm);setColor(c);}
    /** Constructor given a PointRangeModel. */
    public Point2D(Euclidean2 vis,PointRangeModel prm){
        super(vis);
        this.prm=prm; 
        if(mobile){this.prm.addChangeListener(this);}
        style=new PointStyle();
        style.addChangeListener(this);
        setColor(Color.BLUE);
    }
    public Point2D(Euclidean2 vis, double x, double y, Color color, boolean mobile) {
        super(vis);
        this.mobile=mobile;
        if(mobile){prm=new PointRangeModel(x,y);
        }else{prm=new PointRangeModel(new R2(x,y),x,y,x,y);}
        style=new PointStyle();
        style.addChangeListener(this);
        if(mobile){prm.addChangeListener(this);}
        setColor(color);
    }
        
    
    // GETTERS AND SETTERS
    
    public R2 getPoint(){return prm.getPoint();}    
    public void setPoint(R2 newValue){prm.setTo(newValue);}
    
    // DRAW ROUTINES
    
    @Override
    public void recompute(){}
    
    @Override
    public void paintComponent(Graphics2D g) {
        R2 point=prm.getPoint();
        g.setColor(getColor());
        switch(style.getStyle()){
        case PointStyle.SMALL: g.fill(((Euclidean2)visometry).dot(point,1)); break;
        case PointStyle.MEDIUM: g.fill(((Euclidean2)visometry).dot(point,2)); break;
        case PointStyle.LARGE: g.fill(((Euclidean2)visometry).dot(point,4)); break;
        case PointStyle.CONCENTRIC: g.fill(((Euclidean2)visometry).dot(point,3));g.draw(((Euclidean2)visometry).dot(point,5));break;    
        case PointStyle.CIRCLE: g.draw(((Euclidean2)visometry).dot(point,5));break;    
        }
    }
    

    // DYNAMIC EVENT HANDLING
    
    /** Determines if the point was clicked on, given a mouse event. */
    @Override
    public boolean clicked(MouseEvent e){
        if(!mobile||e==null){return false;}
        return Math.abs(e.getX()-visometry.toWindowX(prm.getX()))+Math.abs(e.getY()-visometry.toWindowY(prm.getY()))<CLICK_EDIT_RANGE;
    }    
    @Override
    public void mousePressed(MouseEvent e){if(clicked(e)){adjusting=true;}}
    @Override
    public void mouseDragged(MouseEvent e){if(adjusting){setPoint(visometry.toGeometry(e.getPoint()));}}
    @Override
    public void mouseReleased(MouseEvent e){mouseDragged(e);adjusting=false;}
    @Override
    public void mouseClicked(MouseEvent e){if(clicked(e)){style.cycleStyle();adjusting=false;}}
    
    
    // IMPLEMENTING ABSTRACT METHODS    

    @Override
    public JMenu getOptionsMenu() {
        JMenu result=new JMenu("Point Style");
        result.add(getColorButton());
        return result;
    }

    
    
    @Override
    public void stateChanged(ChangeEvent e) {
        fireStateChanged();
    }
}
