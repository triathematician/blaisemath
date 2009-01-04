/*
 * Euclidean1.java
 * Created on Feb 11, 2008
 */

package specto.euclidean1;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.JMenuItem;
import scio.coordinate.R1;
import sequor.model.DoubleRangeModel2;
import specto.PlotPanel;
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
    private Double m;
    /** Shift parameter to convert to window coordinates. */
    private Double b;
    
    /** Boundaries of the window. */
    private DoubleRangeModel2 bounds;    
    /** The y-value of the line (same for all displayed points) */
    private double yValue = 0.0;
    
    // CONSTRUCTORS
    
    public Euclidean1(){super();bounds=new DoubleRangeModel2(0,-5,5);}
    public Euclidean1(PlotPanel p){super(p);bounds=new DoubleRangeModel2(0,-5,5);}

    // BEANS
    
    public double getActualMin() { return bounds.getMinimum(); }
    public double getActualMax() { return bounds.getMaximum(); }
    public double getActualWidth() { return bounds.getRange(); }
    
    // CONVERSION METHODS

    public Point2D.Double toWindow(Double x){ return new Point2D.Double(m*x+b,yValue); }
    @Override
    public Point2D.Double toWindow(R1 cp) { return new Point2D.Double(m*cp.getValue()+b,yValue); }
    public Vector<Double> toWindow(Vector<Double> xGrid) {
        Vector<Double> result = new Vector<Double>();
        for(Double x:xGrid){ result.add(m*x+b); }
        return result;
    }

    @Override
    public R1 toGeometry(Point wp) {
        return new R1((wp.getX()-b)/m);
    }

    @Override
    public void setBounds(R1 minPoint, R1 maxPoint) {
        if(!minPoint.equals(maxPoint)){
            bounds.setMinimum(minPoint.getValue());
            bounds.setMaximum(maxPoint.getValue());
            computeTransformation();
        }
    }

    @Override
    public double computeTransformation(){
        m=getWindowWidth()/bounds.getRange();
        b=getWindowMin().getX()-m*bounds.getMinimum();
        yValue = container.getHeight()/2;
        return 1;
    }

    @Override
    public Vector<JMenuItem> getMenuItems() {
        return null;
    }
}
