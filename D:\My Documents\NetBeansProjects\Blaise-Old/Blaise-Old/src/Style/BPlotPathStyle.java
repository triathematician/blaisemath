package Style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * <b>BPlotPathStyle.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>June 19, 2007, 9:01 AM</i><br><br>
 *
 * Handles various path display settings. Basic options are color and width.
 *
 * Supported styles include:
 *      PLAIN: a basic line                                                   ------------
 *      DASHED: a dashed line                                                 - - - - - - 
 *      DOTTED: a dotted line                                                 ............  
 *
 * Later styles to implement include:           
 *      DIRECTED: a line with an arrow/arrows indicating direction            --->---->---
 *      DOUBLE: two parallel lines in place of one                            ============
 *      POINTED: points drawn on the list of endpoints specified by the path: *    *     *
 *      PTDASHED: points on the endpoints, plus dashes *in between* these:    * -- * --- *
 *
 * The drawing of course must be handled wherever the path is displayed.
 *
 */
public class BPlotPathStyle {
    
// Parameters    
    
    /** Color settings */
    private Color color=Color.BLACK;
    //private Color color2;
    /** Size setting (given as a radius) */
    private float width=1.0f;
    //private int radius2;
    /** Style setting */
    private int style=PLAIN;
    /** Whether the element is currently shown */
    private boolean visible=true;
    
// Constants    
    
    /** Style constants */
    public static final int PLAIN=0;   
    public static final int DIRECTED=1;
    public static final int DOUBLE=2;
    public static final int DASHED=3;
    public static final int DOTTED=4;
    public static final int POINTED=5;        // Pointed places a dot at the endpoints of all segments
    public static final int PTDASHED=6;
    /** Validity checker */
    public static boolean isValidStyle(int s){return(s>=0)&&(s<=6);}
    
// Constructors    
    
    /** Constructor: creates a new instance of BPlotPointStyle */
    public BPlotPathStyle(){}
    /** Constructor with options */
    public BPlotPathStyle(Color c,float w,int s){
        setColor(c);
        setWidth(w);
        setStyle(s);
    }    
    
// Bean patterns
    
    public Color getColor(){return color;}
    public double getWidth(){return width;}
    public int getStyle(){return style;}
    public boolean isVisible(){return visible;}
    
    public void setColor(Color c){color=c;}
    public void setWidth(float w){if(w>0){width=w;}}
    public void setStyle(int s){if(isValidStyle(s)){style=s;}}
    public void setVisible(boolean v){visible=v;}
    
// High-level routines
    
    /** Returns stroke associated to the given style. */
    public Stroke getStroke(){
            float[] dashes={6*width,4*width};
            float[] dots={width,2*width};
        switch(style){
            case PLAIN:
            case DIRECTED:
            case DOUBLE: return new BasicStroke(width);
            case DASHED: 
            case PTDASHED: return new BasicStroke(width,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dashes,0.0f);
            case DOTTED:
            case POINTED: return new BasicStroke(width,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dots,0.0f);
        }
        return new BasicStroke(width);
    }

    public int getDASHED() {
        return DASHED;
    }
    /** Operates on Graphics2D using the given style. */
    public void applyToGraphics(Graphics2D g){
        g.setColor(color);
        g.setStroke(getStroke());
    }
}