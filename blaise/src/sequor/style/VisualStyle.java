/**
 * VisualStyle.java
 * Created on Mar 25, 2008
 */

package sequor.style;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;

/**
 *
 * @author Elisha Peterson
 */
public class VisualStyle {
    Color color;
    Stroke stroke;
    Paint paint;
    Composite composite;
    
    public VisualStyle(Color c,Stroke s,Paint p,Composite comp){
        this.color=c;
        this.stroke=s;
        this.paint=p;
        this.composite=comp;
    }
    
    public void applyGraphics(Graphics2D g){
        if(color!=null){g.setColor(color);}
        if(stroke!=null){g.setStroke(stroke);}
        if(paint!=null){g.setPaint(paint);}
        if(composite!=null){g.setComposite(composite);}
    }
    
    
    // STANDARD STROKES
    
    public static final Stroke VERY_THIN_STROKE=new BasicStroke(0.5f);
    public static final Stroke THIN_STROKE=new BasicStroke(1.0f);
    public static final Stroke BASIC_STROKE=new BasicStroke(2.0f);
    public static final Stroke MEDIUM_STROKE=new BasicStroke(3.0f);
    public static final Stroke THICK_STROKE=new BasicStroke(4.0f);
    public static final float[] dash2={2.0f,4.0f};
    public static final Stroke DOTTED_STROKE=new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dash2,0.0f);
    public static final float[] dash3={1.0f,4.0f};
    public static final Stroke VERY_DOTTED_STROKE=new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dash3,0.0f);  
    
    // ADDITIONAL COLORS
    
    public static final Color LIGHT_BLUE=new Color(50,50,200);
    public static final Color LIGHT_GREEN=new Color(50,200,50);
    public static final Color LIGHT_RED=new Color(200,50,50);
    
    // OPACITY COMPOSITES
    
    public static final Composite COMPOSITE10=getComposite(1.0f);
    public static final Composite COMPOSITE5=getComposite(0.5f);
    public static final Composite COMPOSITE2=getComposite(0.2f);
    public static final Composite COMPOSITE1=getComposite(0.1f);
    
    public static Composite getComposite(float f){return AlphaComposite.getInstance(AlphaComposite.SRC_OVER,f);}
    
    // STATIC STYLES
    
    public static final VisualStyle DEFAULT=new VisualStyle(Color.BLACK,THIN_STROKE,null,COMPOSITE10);
}
