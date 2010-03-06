/*
 * PointSet2D.java
 * Created on Sep 27, 2007, 12:38:05 PM
 */

package specto.euclidean3;

import scio.coordinate.Coordinate;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.event.ChangeListener;
import sequor.event.MouseVisometryEvent;
import specto.DynamicPlottable;
import scio.coordinate.R3;
import sequor.model.ColorModel;
import sequor.style.VisualStyle;
import sequor.style.LineStyle;
import sequor.style.PointStyle;

/**
 * Represents a point which can be moved around the screen. The data is stored in a PointRangeModel, which restricts the movement of
 * the point to within a particular range.
 * @author ae3263
 */
public class Point3D extends DynamicPlottable<Euclidean3> implements ChangeListener{
    
    // PROPERTIES
    
    protected R3 prm;

    private String label;
    
    
    // CONSTRUCTORS
    
    /** Default constructor places point at the origin. */
    public Point3D(){
        this(new R3());
    }

    /** Constructor places point at a given location. */
    public Point3D(R3 value){
        prm = value;
    }

    public Point3D(double x, double y, double z) { 
        this(new R3(x,y,z));
    }

    public Point3D(Color c){
        this();
        setColor(c);
    }

    public Point3D(double x, double y, double z, Color c) {
        this(x,y,z);
        setColor(c);
    }

    /** Constructor given a PointRangeModel and a particular color. */
    public Point3D(R3 value, Color c){
        this(value);
        setColor(c);
    }

    /** Constructor given a PointRangeModel and a ColorModel. */
    public Point3D(R3 value, ColorModel colorModel){
        prm = value;
        setColorModel(colorModel);
    }
        
    
    // GETTERS AND SETTERS
    
    public R3 getPoint(){return prm;}    
    public void setPoint(R3 newValue){ if (!newValue.equals(prm)) { prm = newValue; } }
    public void setLabel(String s){label=s;}
    
    
    // DRAW ROUTINES    
    
    @Override
    public void paintComponent(Graphics2D g,Euclidean3 v) {
        g.setStroke(LineStyle.STROKES[LineStyle.THIN]);
        v.drawDot(g, prm, 4);
        if(label!=null){
            v.drawString(g, prm, label);
            g.setComposite(AlphaComposite.SrcOver);
        }
    }
    
    // STYLE SETTINGS

    @Override
    public String toString(){return "Point";}

    @Override
    public void initStyle() {
        super.initStyle();
        style = new PointStyle(PointStyle.LARGE);
        style.addChangeListener(this);
    }

    // DYNAMIC EVENT HANDLING

    @Override
    public boolean withinClickRange(MouseVisometryEvent<Euclidean3> e, Coordinate c) {
        try {
            return super.withinClickRange(e, e.getSourceVisometry().proj.getValue(prm));
        } catch (Exception ex) {
            return false;
        }
    }
       
    /** Determines if the point was clicked on, given a mouse event. */
    @Override
    public boolean clicked(MouseVisometryEvent<Euclidean3> e) {return withinClickRange(e,prm);}
}
