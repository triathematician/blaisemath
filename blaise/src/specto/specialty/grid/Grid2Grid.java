/**
 * Grid2Grid.java
 * Created on Apr 8, 2008
 */

package specto.specialty.grid;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import scio.coordinate.I2;
import specto.Plottable;

/**
 * A grid which can be placed atop a Grid2 visometry.
 * @author Elisha Peterson
 */
public class Grid2Grid extends Plottable<Grid2> {
    @Override
    public void paintComponent(Graphics2D g, Grid2 v) {
        Point2D.Double center;
        for(Integer i:v.xBounds.getValueRange(true,0)){
            for(Integer j:v.yBounds.getValueRange(true,0)){
                center=v.toWindow(new I2(i,j));
                g.fill(new Ellipse2D.Double(center.x-5,center.y-5,10,10));
            }
        }
    }

    @Override
    public String[] getStyleStrings() {return null;}
}
