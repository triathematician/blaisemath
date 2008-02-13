/*
 * CircledPoint.java
 * Created on Feb 13, 2007
 */

package utility;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.Vector;
import sequor.model.PointRangeModel;
import specto.dynamicplottable.Point2D;

/**
 * This class is an extension of the standard Point2D class, enabling individual players to display
 * their range of sight and their capture range, in addition to their positions.
 * 
 * @author Elisha Peterson
 */
public class CircledPoint extends Point2D {
    Vector<Double> radii;

    public CircledPoint(PointRangeModel pointModel, Color color) {
        super(pointModel,color);
        radii=new Vector<Double>();
    }

    public void addRadius(double r){
        radii.add(r);
    }

    @Override
    public void paintComponent(Graphics2D g) {
        super.paintComponent(g);
        if(style==MEDIUM){
            for(double r:radii){
                g.draw(visometry.circle(point,r));
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
    }
}
