/*
 * Euclidean1.java
 * Created on Feb 11, 2008
 */

package specto.visometry;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Vector;
import javax.swing.JMenuItem;
import scio.coordinate.R1;
import specto.Visometry;

/**
 * <p>
 * Euclidean1 is ...
 * </p>
 * @author Elisha Peterson
 */
public class Euclidean1 extends Visometry<R1> {
    
    // PROPERTIES
    
    /** Product parameter to convert to window coordinates. */
    private double m;
    /** Shift parameter to convert to window coordinates. */
    private double b;
    
    /** Minimum x value in the window. */
    private double minX;
    /** Maximum x value in the window. */
    private double maxX;
    
    /** The y-value of the line (same for all displayed points) */
    private double yValue;
    
    // CONSTRUCTORS
    
    // CONVERSION METHODS

    @Override
    public Double toWindow(R1 cp) {
        return new Point2D.Double(m*cp.getValue()+b,yValue);
    }

    @Override
    public R1 toGeometry(Point wp) {
        return new R1((wp.getX()-b)/m);
    }

    @Override
    public void setBounds(R1 minPoint, R1 maxPoint) {
        if(!minPoint.equals(maxPoint)){
            minX=minPoint.getValue();
            maxX=maxPoint.getValue();
            computeTransformation();
        }
    }

    @Override
    public double computeTransformation(){
        m=getWindowWidth()/(maxX-minX);
        b=getWindowMin().getX()-m*minX;
        return 1;
    }

    @Override
    public Vector<JMenuItem> getMenuItems() {
        return null;
    }

}
