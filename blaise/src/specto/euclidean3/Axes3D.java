/*
 * NewGrid2D.java
 * Created on Mar 22, 2008
 */

package specto.euclidean3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import scio.coordinate.R3;
import specto.DynamicPlottable;

/**
 * Axes2D represents the second generation of axes drawing, with support for multiple drawing modes, labels, etc. The Grid2D class separately
 * maintains the grid. The spacing of the two classes should be adjusted separately.
 * 
 * @author Elisha Peterson
 */
public class Axes3D extends DynamicPlottable<Euclidean3> {
    
    // CONSTRUCTOR
    public Axes3D(){
        setColor(Color.GRAY);
        style.setValue(1);
    }    
    
    // PAINT METHODS
    
    private static final int TICK_HEIGHT=5;
    private static final int IDEAL_TICK_SPACE=40;    
    static Point2D XOFFSET=new Point2D.Double(5,-3);
    static Point2D YOFFSET=new Point2D.Double(10,-5);
    static Point2D ZOFFSET=new Point2D.Double(10,-5);
    
    private String xLabel="x";
    private String yLabel="y";
    private String zLabel="z";
    private static final DecimalFormat nf=new DecimalFormat("##0.#");   
    
    @Override
    public void paintComponent(Graphics2D g, Euclidean3 v) {
        switch(style.getValue()) {
            case 0 :
                g.draw(v.lineSegment(new R3(0,0,0), new R3(6,0,0)));
                g.draw(v.lineSegment(new R3(0,0,0), new R3(0,6,0)));
                g.draw(v.lineSegment(new R3(0,0,0), new R3(0,0,6)));    
                break;
            case 1 :
                g.draw(v.lineSegment(new R3(-6,-6,-6), new R3(6,-6,-6)));
                g.draw(v.lineSegment(new R3(-6,-6,-6), new R3(-6,6,-6)));
                g.draw(v.lineSegment(new R3(-6,-6,-6), new R3(-6,-6,6)));
                g.draw(v.lineSegment(new R3(6,-6,-6), new R3(6,6,-6)));
                g.draw(v.lineSegment(new R3(6,-6,-6), new R3(6,-6,6)));
                g.draw(v.lineSegment(new R3(-6,6,-6), new R3(6,6,-6)));
                g.draw(v.lineSegment(new R3(-6,6,-6), new R3(-6,6,6)));
                g.draw(v.lineSegment(new R3(-6,-6,6), new R3(6,-6,6)));
                g.draw(v.lineSegment(new R3(-6,-6,6), new R3(-6,6,6)));
                g.draw(v.lineSegment(new R3(-6,6,6), new R3(6,6,6)));
                g.draw(v.lineSegment(new R3(6,-6,6), new R3(6,6,6)));
                g.draw(v.lineSegment(new R3(6,6,-6), new R3(6,6,6)));
                break;
        }
    }
    
    
    // STYLE SETTINGS    
    
    @Override
    public String toString(){return "Axes";}

    @Override
    public String[] getStyleStrings() {
        String[] result = {"Standard", "Box"};
        return result;
    }
}
