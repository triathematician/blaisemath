/*
 * CirclePoint2D.java
 * Created on February 20, 2008
 */

package specto.decoration;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.util.Vector;
import javax.swing.JMenu;
import specto.style.PointStyle;
import specto.Decoration;
import specto.dynamicplottable.Point2D;
import specto.visometry.Euclidean2;

/**
 * A list of radii around a central point.
 * @author Elisha Peterson
 */
public class CirclePoint2D extends Decoration<Euclidean2> {
    Vector<Double> radii;

    public CirclePoint2D(Point2D parent) {
        super(parent);
        radii=new Vector<Double>();
    }

    public void addRadius(double r){radii.add(r);}

    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        Point2D ptParent=(Point2D)parent;
        if(ptParent.style.getStyle()==PointStyle.CONCENTRIC){
            g.setColor(ptParent.getColor().brighter());
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.15f));
            for(double r:radii){
                g.fill(v.circle(ptParent.getPoint(),r));                
            }
            g.setComposite(AlphaComposite.SrcOver);
        }
    }

    @Override
    public void recompute() {}
    @Override
    public JMenu getOptionsMenu() {return null;}
}
