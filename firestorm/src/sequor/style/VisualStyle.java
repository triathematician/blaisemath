/**
 * VisualStyle.java
 * Created on Mar 25, 2008
 */

package sequor.style;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;

/**
 * <p>
 * Represents a comprehensive visual style that can be applied to a Graphics2D object.
 * </p>
 * 
 * @author Elisha Peterson
 */
public class VisualStyle {
    
    Color color;
    Stroke stroke;
    Paint paint;
    Composite composite;
    
    public VisualStyle(Color c, Stroke s, Paint p, Composite comp){
        this.color=c;
        this.stroke=s;
        this.paint=p;
        this.composite=comp;
    }

    /** Applies graphic styles to the Graphics2D object. */
    public void applyGraphics(Graphics2D g){
        if(color!=null){
            g.setColor(color);
        }
        if(stroke!=null){
            g.setStroke(stroke);
        }
        if(paint!=null){
            g.setPaint(paint);
        }
        if(composite!=null){
            g.setComposite(composite);
        }
    }



    // ADDITIONAL COLORS

    public static final Color LIGHT_BLUE=new Color(50,50,200);
    public static final Color LIGHT_GREEN=new Color(50,200,50);
    public static final Color LIGHT_RED=new Color(200,50,50);
    public final static Color DARK_GREEN = new Color(0.0f, 0.5f, 0.0f);
    
    
    // OPACITY COMPOSITES
    
    public static final Composite COMPOSITE10 = getComposite(1.0f);
    public static final Composite COMPOSITE5 = getComposite(0.5f);
    public static final Composite COMPOSITE2 = getComposite(0.2f);
    public static final Composite COMPOSITE1 = getComposite(0.1f);
    public static final Composite COMPOSITE05 = getComposite(0.05f);
    
    public static Composite getComposite(float f){return AlphaComposite.getInstance(AlphaComposite.SRC_OVER,f);}


    // STATIC STYLES
    
    public static final VisualStyle DEFAULT = new VisualStyle(Color.BLACK, LineStyle.STROKES[LineStyle.MEDIUM], null, COMPOSITE10);
}
