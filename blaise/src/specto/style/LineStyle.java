/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package specto.style;

import specto.style.VisStyle;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import sequor.FiresChangeEvents;

/**
 *
 * @author ae3263
 */
public class LineStyle extends VisStyle {
    
    // GENERAL STYLE OPTIONS
    
    protected Color color;
    protected double thickness;
    protected int style;
    protected Stroke stroke;
    protected int animateStyle;
    
    // STYLE CONSTANTS
    
    public static final int LINE=0;
    public static final int MEDIUM=1;
    public static final int THICK=2;
    public static final int DOTTED=3;
    public static final int SKETCH=4;    
    
    public static final int ANIMATE_DRAW=0;
    public static final int ANIMATE_DOT=1;
    public static final int ANIMATE_TRACE=2;
    public static final int ANIMATE_TRAIL=3;
    
    public static final Stroke THIN_STROKE=new BasicStroke(1.0f);
    public static final Stroke BASIC_STROKE=new BasicStroke(2.0f);
    public static final Stroke MEDIUM_STROKE=new BasicStroke(3.0f);
    public static final Stroke THICK_STROKE=new BasicStroke(4.0f);
    public static final float[] dash2={2.0f,4.0f};
    public static final Stroke DOTTED_STROKE=new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dash2,0.0f);
    public static final float[] dash3={1.0f,4.0f};
    public static final Stroke VERY_DOTTED_STROKE=new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dash3,0.0f);
        
    // GETTERS AND SETTERS
    
    public Color getColor() {return color;}
    public void setColor(Color color) {if(!this.color.equals(color)){this.color=color;fireStateChanged();}}
    public double getThickness() {return thickness;}
    public void setThickness(double thickness) {if(this.thickness!=thickness){this.thickness=thickness;fireStateChanged();}}
    public Stroke getStroke() {return stroke;}
    public void setStroke(Stroke newValue){if(stroke!=newValue){stroke=newValue;fireStateChanged();}}
    
    public int getStyle() {return style;}
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
    
    // IMPLEMENTING ABSTRACT METHODS
    
    @Override
    public FiresChangeEvents clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void copyValuesFrom(FiresChangeEvents parent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
