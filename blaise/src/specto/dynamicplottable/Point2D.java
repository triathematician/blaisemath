/*
 * PointSet2D.java
 * Created on Sep 27, 2007, 12:38:05 PM
 */

package specto.dynamicplottable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.model.PointRangeModel;
import specto.DynamicPlottable;
import scio.coordinate.R2;
import specto.visometry.Euclidean2;

/**
 * Represents a point which can be moved around the screen and fires state changes if moved.
 * @author ae3263
 */
public class Point2D extends DynamicPlottable<Euclidean2>{
    protected R2 point;
    public Point2D(){
        point=new R2(0,0);
    }
    public Point2D(R2 value){
        point=value;
    }

    
    // UPDATE ROUTINES
    
    public void setPoint(R2 newValue){
        if(!newValue.equals(point)){
            if(prm==null){
                point=newValue;
                fireStateChanged();
            }else{
                prm.setTo(newValue.x,newValue.y);
            }
        }
    }
    
    public R2 getPoint(){return point;}
    
    // DRAW ROUTINES
    
    @Override
    public void recompute(){}
    
    @Override
    public void paintComponent(Graphics2D g) {
        g.setColor(color);
        switch(style){
        case SMALL: g.fill(drawDot(1)); break;
        case MEDIUM: g.fill(drawDot(2)); break;
        case LARGE: g.fill(drawDot(4)); break;
        case CONCENTRIC: g.fill(drawDot(3));g.draw(drawDot(5));break;            
        }
    }
    
    public Ellipse2D.Double drawDot(int r){
        java.awt.geom.Point2D.Double pt=visometry.toWindow(point);
        return new Ellipse2D.Double(pt.x-r,pt.y-r,2*r,2*r);
    }
    

    // STYLE SETTINGS
    
    protected Color color=Color.RED;
    protected int style=LARGE;
    
    public static final int SMALL=0;
    public static final int MEDIUM=1;
    public static final int LARGE=2;
    public static final int CONCENTRIC=3;
    
    public void setColor(Color newValue){
        if(color!=newValue){
            color=newValue;
            fireStateChanged();
        }
    }
    public void setStyle(int newValue){
        if(newValue!=style&&newValue>=0&&newValue<=4){
            style=newValue;
            fireStateChanged();
        }
    }
    public int getStyle(){
        return style;
    }
    
    
    // DYNAMIC EVENT HANDLING
    
    /** Determines if the point was clicked on, given a mouse event. */
    @Override
    public boolean clicked(MouseEvent e){
        if(!mobile||e==null){return false;}
        return Math.abs(e.getX()-visometry.toWindowX(point.x))+Math.abs(e.getY()-visometry.toWindowY(point.y))<CLICK_EDIT_RANGE;
    }
    
    @Override
    public void mousePressed(MouseEvent e){if(clicked(e)){adjusting=true;}}
    @Override
    public void mouseDragged(MouseEvent e){if(adjusting){setPoint(visometry.toGeometry(e.getPoint()));}}
    @Override
    public void mouseReleased(MouseEvent e){mouseDragged(e);adjusting=false;}
    @Override
    public void mouseClicked(MouseEvent e){if(clicked(e)){setStyle((style+1)%4);adjusting=false;}}
    
    
    // TEMPORARY ROUTINES
    
    // TODO Remove This!!
    PointRangeModel prm;
    public Point2D(final PointRangeModel prm){
        setPoint(prm);
        this.prm=prm;
        setPoint(prm);
        prm.addChangeListener(
                new ChangeListener(){public void stateChanged(ChangeEvent e){setPoint(prm);}}            
        );
    }
    public Point2D(final PointRangeModel prm,Color c){
        this(prm);
        setColor(c);
    }
    public void setPoint(PointRangeModel prm){
        if(prm!=null){
            if(point==null){
                point=new R2(prm.getPoint().x,prm.getPoint().y);
            }else{
                point.x=prm.getPoint().x;
                point.y=prm.getPoint().y;
            }
            fireStateChanged();
        }
    }

    @Override
    public JMenu getOptionsMenu() {return null;}
}
