/*
 * PolarGrid2D.java
 * Created on Oct 19, 2007, 12:13:59 PM
 */

package specto.euclidean2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Vector;
import specto.DynamicPlottable;
import scio.coordinate.R2;
import sequor.style.LineStyle;
import specto.NiceRangeGenerator;
import specto.euclidean2.Euclidean2;

/**
 * This class implements a polar grid on a Cartesian plot.
 * <br><br>
 * @author ae3263
 */
public class PolarGrid2D extends DynamicPlottable<Euclidean2> {
    
    // STYLES

    private static final int IDEAL_GRID_SPACE=40;
    
    public PolarGrid2D(){
        super();
        setColor(Color.getHSBColor(.4f,.3f,0.9f));
    }
    
    // DRAW METHODS
            
    double THETA_STEPS=24;
    
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        double maxRad=maxRadius(v);
        double minRad=maxRad/40;
        
        Point2D.Double origin=v.toWindow(0,0);
        origin.x=Math.min(Math.max(origin.x,0),v.getWindowWidth());
        origin.y=Math.min(Math.max(origin.y,0),v.getWindowHeight());

        NiceRangeGenerator spacing=new NiceRangeGenerator.StandardRange();
        Vector<Double> rGrid=spacing.niceRange(minRad, maxRad,
                (double)IDEAL_GRID_SPACE*v.getDrawWidth()/v.getWindowWidth());
        g.setColor(getColor());
        g.setStroke(LineStyle.STROKES[LineStyle.VERY_THIN]);

        // draw outward lines
        for(double i=0;i<2*Math.PI;i+=2*Math.PI/THETA_STEPS){
            g.draw(new Line2D.Double(
                    v.toWindowX(minRad*Math.cos(i)),v.toWindowY(minRad*Math.sin(i)),
                    v.toWindowX(maxRad*Math.cos(i)),v.toWindowY(maxRad*Math.sin(i))));
        }

        // draw circles
        java.awt.geom.Point2D.Double left;
        java.awt.geom.Point2D.Double right;
        for(Double r : rGrid) {
            left=v.toWindow(new R2(-r,-r));
            right=v.toWindow(new R2(r,r));
            g.draw(new Ellipse2D.Double(left.x,right.y,right.x-left.x,left.y-right.y));
        }
    }
    
    public static double maxRadius(Euclidean2 v){
        double x1=v.getActualMin().x;
        double x2=v.getActualMax().x;
        double y1=v.getActualMin().y;
        double y2=v.getActualMax().y;
        double result=(x1*x1>x2*x2)?(x1*x1):(x2*x2);
        result+=(y1*y1>y2*y2)?(y1*y1):(y2*y2);
        return Math.sqrt(result);
    }

    @Override
    public String[] getStyleStrings() {return null;}
    @Override
    public String toString(){return "Polar Grid";}
}
