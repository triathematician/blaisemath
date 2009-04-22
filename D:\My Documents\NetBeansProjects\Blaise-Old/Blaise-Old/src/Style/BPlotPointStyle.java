package Style;

import java.awt.Color;

/**
 * <b>BPlotPointStyle.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>June 19, 2007, 9:01 AM</i><br><br>
 *
 * Convenience method for storing plot style options for a single point.
 * Besides the default color and radius settings, there is also a style setting.
 *
 * Styles to include:
 *      BASIC: filled circle
 *      OPEN: open circle
 *      CONCENTRIC: dot inside open circle
 *      CROSS: an "X"
 *      BOX: an open square
 *      FILLBOX: a filled square
 *      TRIANGLE: a triangle
 *      FILLTRIANGLE: a filled triangle
 *
 * The drawing of course must be handled wherever the point is displayed.
 */
public class BPlotPointStyle {
    
// Parameters    
    
    /** Color settings */
    private Color color=Color.BLACK;
    //private Color color2;
    /** Size setting (given as a radius) */
    private float radius=3.0f;
    //private int radius2;
    /** Style setting */
    private int style=BASIC;
    /** Whether the element is currently shown */
    private boolean visible=true;
    
// Constants    
    
    /** Style constants */
    public static final int BASIC=0;
    public static final int OPEN=1;
    public static final int CONCENTRIC=2;
    public static final int CROSS=3;
    public static final int BOX=4;
    public static final int FILLBOX=5;
    public static final int TRIANGLE=6;
    public static final int FILLTRIANGLE=7;
    /** Validity checker */
    public static boolean isValidStyle(int s){return(s>=0)&&(s<=7);}
    
// Constructors    
    
    /** Constructor: creates a new instance of BPlotPointStyle */
    public BPlotPointStyle(){}
    /** Constructor with options */
    public BPlotPointStyle(Color c,float r,int s){
        setColor(c);
        setRadius(r);
        setStyle(s);
    }    
    
// Bean patterns
    
    public Color getColor(){return color;}
    public float getRadius(){return radius;}
    public int getStyle(){return style;}
    public boolean isVisible(){return visible;}
    
    public void setColor(Color c){color=c;}
    public void setRadius(float r){if(r>0){radius=r;}}
    public void setStyle(int s){if(isValidStyle(s)){style=s;}}
    public void setVisible(boolean v){visible=v;}
}
