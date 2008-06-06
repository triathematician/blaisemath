/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package specto.style;

import java.awt.Graphics2D;
import java.awt.Stroke;
import sequor.style.VisualStyle;

/**
 *
 * @author ae3263
 */
public class LineStyle extends Style {
    
    // GENERAL STYLE OPTIONS
    
    protected int style;
    protected Stroke stroke;
    
    // CONSTRUCTOR
    
    public LineStyle() { super(VisualStyle.LINE_STYLE_STRINGS); }
    public LineStyle(int style) { super(VisualStyle.LINE_STYLE_STRINGS); setStyle(style); }
    
    // STYLE CONSTANTS  
        
    public static final int VERY_THIN=0;
    public static final int THIN=1;
    public static final int BASIC=2;
    public static final int MEDIUM=3;
    public static final int THICK=4;  
    public static final int DOTTED=5;  
    public static final int VERY_DOTTED=6;
        
    // GETTERS AND SETTERS
    
    public int getStyle() {return style;}
    public void setStyle(int newValue){
        if(style!=newValue){
            switch(newValue){
                case VERY_THIN: stroke=VisualStyle.VERY_THIN_STROKE; break;
                case THIN: stroke=VisualStyle.THIN_STROKE; break;
                case BASIC: stroke=VisualStyle.MEDIUM_STROKE; break;
                case MEDIUM: stroke=VisualStyle.THICK_STROKE; break;
                case THICK: stroke=VisualStyle.VERY_THICK_STROKE; break;
                case DOTTED: stroke=VisualStyle.DOTTED_STROKE; break;
                case VERY_DOTTED: stroke=VisualStyle.VERY_DOTTED_STROKE; break;
                default:break;
            }
            style=newValue;
            fireStateChanged();
        }    
    }

    // REQUIRED METHODS
    
    @Override
    public void apply(Graphics2D g) {
        g.setStroke(stroke);
    }
}
