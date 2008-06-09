/*
 * CirclePoint2D.java
 * Created on February 20, 2008
 */

package specto.euclidean2;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.util.Vector;
import sequor.model.PointRangeModel;
import specto.euclidean2.Euclidean2;
import specto.style.PointStyle;

/**
 * A list of radii around a central point.
 * @author Elisha Peterson
 */
public class CirclePoint2D extends Point2D {
    Vector<Double> radii;

    public CirclePoint2D(PointRangeModel prm){
        super(prm);
        radii=new Vector<Double>();
    }
    public CirclePoint2D(Point2D parent) {this(parent.prm);}

    /** Removes all radii decorating the point. */
    public void deleteRadii() {
        radii.clear();
    }
    /** Adds radius to the point. */
    public void addRadius(double r){
        radii.add(r);
    }


    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        if(style.getValue()==PointStyle.CONCENTRIC){
            g.setColor(getColor().brighter());
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.15f));
            for(double r:radii){
                g.fill(v.circle(getPoint(),r));                
            }
            g.setComposite(AlphaComposite.SrcOver);
        }
        super.paintComponent(g, v);
    }
}
