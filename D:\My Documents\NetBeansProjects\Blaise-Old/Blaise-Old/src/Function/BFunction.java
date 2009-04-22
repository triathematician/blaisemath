package Function;

import Blaise.BPlotPath2D;
import Euclidean.PPoint;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * <b>BFunction.java<br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>February 21, 2007, 8:33 AM</i><br><br>
 * 
 * Contains basic methods that any function should contain.
 */
public abstract class BFunction {
    /** Stores string which describes the function **/
    private String string;
    
    /**
     * Constructor: creates a new instance of BFunction
     */
    public BFunction(String string){this.string=string;}
    
    /** Required fields to Override */
    public double valueAt(double x){return 0;}   
    public PPoint pointAt(double x){return new PPoint(x,valueAt(x));}
      
    public void draw(Graphics2D g,AffineTransform at,Dimension dimension,Color c,int drawFormat){
        BPlotPath2D functionPlot=new BPlotPath2D();
        for (int i=0;i<=dimension.getWidth();i++){
            functionPlot.add(pointAt(i*at.getScaleX()+at.getTranslateX()));
        }
        functionPlot.paintComponent(g,at,c);
    }
}
