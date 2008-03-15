/*
 * PolarGrid2D.java
 * Created on Oct 19, 2007, 12:13:59 PM
 */

package specto.gridplottable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import javax.swing.JMenu;
import specto.DynamicPlottable;
import scio.coordinate.R2;
import specto.visometry.Euclidean2;

/**
 * This class implements a polar grid on a Cartesian plot.
 * <br><br>
 * @author ae3263
 */
public class PolarGrid2D extends DynamicPlottable<Euclidean2> {
    
    // STYLES
    
    private static final float[] dash1={6.0f,4.0f};
    private static final Stroke DASHED_STROKE=new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dash1,0.0f);

    public PolarGrid2D(){
        super();
        color.setValue(Color.getHSBColor(0.6f,0.5f,0.6f));
    }
    
    // DRAW METHODS
        
    @Override
    public void recompute(){}
    
    double THETA_STEPS=24;
    
    public void paintComponent(Graphics2D g,Euclidean2 v) {    
        g.setColor(getColor().brighter());
        g.setStroke(DASHED_STROKE);
        double maxRad=maxRadius(v);
        double minRad=maxRad/40;
        // draw outward lines
        for(double i=0;i<2*Math.PI;i+=2*Math.PI/THETA_STEPS){
            g.draw(new Line2D.Double(
                    v.toWindowX(minRad*Math.cos(i)),v.toWindowY(minRad*Math.sin(i)),
                    v.toWindowX(maxRad*Math.cos(i)),v.toWindowY(maxRad*Math.sin(i))));
        }
        java.awt.geom.Point2D.Double left;
        java.awt.geom.Point2D.Double right;
        for(double i=minRad;i<maxRad;i+=(maxRad-minRad)/10){
            left=v.toWindow(new R2(-i,-i));
            right=v.toWindow(new R2(i,i));
            g.draw(new Ellipse2D.Double(left.x,right.y,right.x-left.x,left.y-right.y));
        }
    }
    
    public double maxRadius(Euclidean2 v){
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
