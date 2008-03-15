/*
 * PointSet2D.java
 * Created on Sep 27, 2007, 12:38:05 PM
 */

package specto.dynamicplottable;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.event.ChangeListener;
import sequor.event.MouseVisometryEvent;
import sequor.model.PointRangeModel;
import specto.DynamicPlottable;
import scio.coordinate.R2;
import specto.visometry.Euclidean2;

/**
 * Represents a point which can be moved around the screen. The data is stored in a PointRangeModel, which restricts the movement of
 * the point to within a particular range.
 * @author ae3263
 */
public class Point2D extends DynamicPlottable<Euclidean2> implements ChangeListener{
    
    // PROPERTIES
    
    protected PointRangeModel prm;
    private String label;
    
    
    // CONSTRUCTORS
    
    /** Default constructor places point at the origin. */
    public Point2D(){this(new PointRangeModel());}
    /** Constructor places point at a given location. */
    public Point2D(R2 value){this(new PointRangeModel(value));}
    public Point2D(double x, double y) {this(new PointRangeModel(x,y));}
    public Point2D(double x, double y,Color c) {this(new PointRangeModel(x,y));setColor(c);}
    /** Constructor given a PointRangeModel and a particular color. */
    public Point2D(PointRangeModel prm,Color c){this(prm);setColor(c);}
    public Point2D(R2 point, Color c, boolean editable) {this(point.x,point.y,c,editable);}
    /** Constructor given a PointRangeModel. */
    public Point2D(PointRangeModel prm){
        this.prm=prm; 
        if(editable){this.prm.addChangeListener(this);}
        setColor(Color.BLUE);
    }
    public Point2D(double x, double y, Color color, boolean editable) {
        this.editable=editable;
        if(editable){prm=new PointRangeModel(x,y);
        }else{prm=new PointRangeModel(new R2(x,y),x,y,x,y);}
        if(editable){prm.addChangeListener(this);}
        setColor(color);
    }
        
    
    // GETTERS AND SETTERS
    
    public R2 getPoint(){return prm.getPoint();}    
    public void setPoint(R2 newValue){prm.setTo(newValue);}
    public void setModel(PointRangeModel prm){
        this.prm.removeChangeListener(this);
        this.prm=prm;
        if(editable){prm.addChangeListener(this);}
    }
    //public PointRangeModel getModel(){return prm;}
    public void setLabel(String s){label=s;}
    
    
    // DRAW ROUTINES    
    
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        R2 point=prm.getPoint();
        g.setColor(getColor());
        switch(style.getValue()){
            case SMALL: 
                g.fill(((Euclidean2)v).dot(point,1)); 
                break;
            case MEDIUM: 
                g.fill(((Euclidean2)v).dot(point,2)); 
                break;
            case LARGE: 
                g.fill(((Euclidean2)v).dot(point,4)); 
                break;
            case CONCENTRIC: 
                g.fill(((Euclidean2)v).dot(point,3));
                g.draw(((Euclidean2)v).dot(point,5));
                break;    
            case CIRCLE: 
                g.draw(((Euclidean2)v).dot(point,5));
                break;    
        }
        if(label!=null){
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
            drawString(g,v,label,prm.getPoint(),5,5);
            g.setComposite(AlphaComposite.SrcOver);
        }
    }
    
    public static void drawString(Graphics2D g,Euclidean2 v,String s,R2 point,double dx,double dy){
        g.drawString(s,(float)(v.toWindowX(point.x)+dx),(float)(v.toWindowY(point.y)-dy));}

    
    // STYLE SETTINGS
    
    public static final int LARGE=0;
    public static final int MEDIUM=1;
    public static final int SMALL=2;
    public static final int CONCENTRIC=3;
    public static final int CIRCLE=4;

    final static String[] styleStrings={"Large","Medium","Small","Concentric","Circle"};
    @Override
    public String[] getStyleStrings() {return styleStrings;}    
    @Override
    public String toString(){return "Point";}

    

    // DYNAMIC EVENT HANDLING
       
    /** Determines if the point was clicked on, given a mouse event. */

    @Override
    public boolean clicked(MouseVisometryEvent<Euclidean2> e) {return withinClickRange(e,prm.getPoint());}
    @Override
    public void mouseDragged(MouseVisometryEvent<Euclidean2> e){if(adjusting){setPoint((R2)e.getCoordinate());}}
    @Override
    public void mouseClicked(MouseVisometryEvent<Euclidean2> e){if(clicked(e)){style.cycle();adjusting=false;}}
}
