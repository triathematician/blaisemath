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

    private Color axesColor=Color.getHSBColor(0.6f,0.5f,0.6f);
    private Color gridColor=Color.getHSBColor(0.6f,0.3f,0.85f);
    
    private static final Stroke BASIC_STROKE=new BasicStroke();
    private static final float[] dash1={6.0f,4.0f};
    private static final Stroke DASHED_STROKE=new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dash1,0.0f);

    // DRAW METHODS
        
    @Override
    public void recompute(){}
    
    public void paintComponent(Graphics2D g) {
        g.setColor(gridColor);
        g.setStroke(DASHED_STROKE);
        double LENGTH=15;
        for(double i=0;i<2*Math.PI;i+=Math.PI/12){
            g.draw(new Line2D.Double(
                    visometry.toWindowX(LENGTH/40*Math.cos(i)),visometry.toWindowY(LENGTH/40*Math.sin(i)),
                    visometry.toWindowX(LENGTH*Math.cos(i)),visometry.toWindowY(LENGTH*Math.sin(i))));
        }
        java.awt.geom.Point2D.Double left;
        java.awt.geom.Point2D.Double right;
        for(double i=LENGTH/20;i<LENGTH;i+=LENGTH/10){
            left=visometry.toWindow(new R2(-i,-i));
            right=visometry.toWindow(new R2(i,i));
            g.draw(new Ellipse2D.Double(left.x,right.y,right.x-left.x,left.y-right.y));
        }
    }

    public JMenu getOptionsMenu() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
