/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package specto.decoration;

import java.awt.Graphics2D;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import sequor.style.PointStyle;
import specto.Decoration;
import specto.dynamicplottable.Point2D;
import specto.visometry.Euclidean2;

/**
 *
 * @author ae3263
 */
public class CirclePoint extends Decoration<Point2D,Euclidean2> {
    Vector<Double> radii;

    public CirclePoint(Point2D parent) {
        setParent(parent);
        radii=new Vector<Double>();
    }

    public void addRadius(double r){
        radii.add(r);
    }

    @Override
    public void paintComponent(Graphics2D g) {
        if(getParent().style.getStyle()==PointStyle.CONCENTRIC){
            g.setColor(getParent().style.getColor());
            for(double r:radii){
                g.draw(visometry.circle(getParent().getPoint(),r));
            }
        }
    }

    @Override
    public void recompute() {}
    @Override
    public JMenu getOptionsMenu() {return null;}
    @Override
    public void stateChanged(ChangeEvent e) {}
}
