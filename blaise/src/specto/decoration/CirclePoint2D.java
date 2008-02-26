/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package specto.decoration;

import java.awt.Graphics2D;
import java.util.Vector;
import javax.swing.JMenu;
import sequor.style.PointStyle;
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
        Point2D parent=(Point2D)getParent();
        if(parent.style.getStyle()==PointStyle.CONCENTRIC){
            g.setColor(parent.style.getColor());
            for(double r:radii){
                g.draw(visometry.circle(parent.getPoint(),r));
            }
        }
    }

    @Override
    public void recompute() {}
    @Override
    public JMenu getOptionsMenu() {return null;}
}
