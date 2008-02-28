/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author ae3263
 */
public class CirclePoint2D extends Decoration<Euclidean2> {
    Vector<Double> radii;

    public CirclePoint2D(Point2D parent) {
        super(parent);
        radii=new Vector<Double>();
    }

    public void addRadius(double r){radii.add(r);}

    @Override
    public void paintComponent(Graphics2D g) {
        Point2D ptParent=(Point2D)parent;
        if(ptParent.style.getStyle()==PointStyle.CONCENTRIC){
            g.setColor(ptParent.getColor().brighter());
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.15f));
            for(double r:radii){
                g.fill(visometry.circle(ptParent.getPoint(),r));                
            }
            g.setComposite(AlphaComposite.SrcOver);
        }
    }

    @Override
    public void recompute() {}
    @Override
    public JMenu getOptionsMenu() {return null;}
}
