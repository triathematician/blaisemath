/*
 * PointSet2D.java
 * Created on Sep 27, 2007, 12:38:05 PM
 */

package specto.euclidean2;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.event.ChangeListener;
import sequor.event.MouseVisometryEvent;
import sequor.model.PointRangeModel;
import specto.DynamicPlottable;
import scio.coordinate.R2;
import sequor.model.ColorModel;
import sequor.style.VisualStyle;
import specto.style.PointStyle;

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
    public Point2D(Color c){this();setColor(c);}
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
    /** Constructor given a PointRangeModel and a ColorModel. */
    public Point2D(PointRangeModel prm, ColorModel colorModel){
        this.prm=prm; 
        if(editable){this.prm.addChangeListener(this);}
        setColorModel(colorModel);
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
    public PointRangeModel getModel(){return prm;}
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
        java.awt.geom.Point2D.Double winCenter = v.toWindow(prm.getPoint());
        g.setColor(getColor());
        ((PointStyle)style).draw(g, winCenter);
        if(label!=null){
            g.setComposite(VisualStyle.COMPOSITE5);
            g.drawString(label,(float)winCenter.x+5,(float)winCenter.y+5);
            g.setComposite(AlphaComposite.SrcOver);
        }
    }
    
    // STYLE SETTINGS
    @Override
    public String[] getStyleStrings() {return VisualStyle.POINT_STYLE_STRINGS;}    
    @Override
    public String toString(){return "Point";}
    @Override
    public void initStyle() {
        color=new ColorModel();
        color.addChangeListener(this);
        style=new PointStyle();
        style.setValue(PointStyle.LARGE);
        style.addChangeListener(this);
    }

    // DYNAMIC EVENT HANDLING
       
    /** Determines if the point was clicked on, given a mouse event. */
    @Override
    public boolean clicked(MouseVisometryEvent<Euclidean2> e) {return withinClickRange(e,prm.getPoint());}
    /** Handles dragging. */
    @Override
    public void mouseDragged(MouseVisometryEvent<Euclidean2> e){if(adjusting){setPoint((R2)e.getCoordinate());}}
    /** Handles a click by cycling the visual style. */
    @Override
    public void mouseClicked(MouseVisometryEvent<Euclidean2> e){if(clicked(e)){style.cycle();adjusting=false;}}    
}
