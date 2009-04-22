package Style;

import java.awt.Color;

/**
 * <b>BPlotVectorStyle.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>June 19, 2007, 9:01 AM</i><br><br>
 *
 * Convenience method for storing plot style options for a vector (point+direction).
 * Besides the default color and radius settings, there is also a style setting.
 *
 * Styles to include:
 *      BASIC_ARROW: a simple arrow -->
 *      FANCY_ARROW: arrow with filled head
 *      OPENTEARDROP: an open teardrop shape, point in the desired direction
 *      DOTTEARDROP: as above, with a dot in the middle of the teardrop
 *      TRIANGLE: an isosceles triangle pointed in the desired direction
 *      FILLTRIANGLE: a filled triangle
 *
 * The drawing of course must be handled wherever the point is displayed.
 */
public class BPlotVectorStyle {
    
// Parameters    
    
    /** Color settings */
    private Color color=Color.BLACK;
    //private Color color2;
    /** Size setting (given as a radius) */
    private int radius=2;
    //private int radius2;
    /** Style setting */
    private int style=FANCYARROW;
    /** Whether the element is currently shown */
    private boolean visible=true;
    
// Constants    
    
    /** Style constants */
    public static final int BASICARROW=0;   
    public static final int FANCYARROW=1;   
    public static final int OPENTEARDROP=2;    
    public static final int DOTTEARDROP=3;   
    public static final int TRIANGLE=4;
    public static final int FILLTRIANGLE=5;
    
    /** Validity checker */
    public static boolean isValidStyle(int s){return(s>=0)&&(s<=5);}
    
// Constructors    
    
    /** Constructor: creates a new instance of BPlotPointStyle */
    public BPlotVectorStyle(){}
    /** Constructor with options */
    public BPlotVectorStyle(Color c,int r,int s){
        setColor(c);
        setRadius(r);
        setStyle(s);
    }    
    
// Bean patterns
    
    public Color getColor(){return color;}
    public int getRadius(){return radius;}
    public int getStyle(){return style;}
    public boolean isVisible(){return visible;}
    
    public void setColor(Color c){color=c;}
    public void setRadius(int r){if(r>0){radius=r;}}
    public void setStyle(int s){if(isValidStyle(s)){style=s;}}
    public void setVisible(boolean v){visible=v;}
}
