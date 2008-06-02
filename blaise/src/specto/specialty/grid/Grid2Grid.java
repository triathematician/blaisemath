/**
 * Grid2Grid.java
 * Created on Apr 8, 2008
 */

package specto.specialty.grid;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import scio.coordinate.Z2;
import sequor.event.MouseVisometryEvent;
import specto.DynamicPlottable;

/**
 * A grid which can be placed atop a Grid2 visometry.
 * @author Elisha Peterson
 */
public class Grid2Grid extends DynamicPlottable<Grid2> {
    
    /** Location of currently selected coordinate (if any) */    
    Z2 selected;
    
    public Grid2Grid(){
        super();
        selected=new Z2(0,0);
        setColor(Color.LIGHT_GRAY);
    }
    
    // PAINT METHODS
    
    @Override
    public void paintComponent(Graphics2D g, Grid2 v) {        
        g.setColor(getColor());
        for(Integer i:v.xBounds.getValueRange(true,0)){
            for(Integer j:v.yBounds.getValueRange(true,0)){
                drawDot(g,v,new Z2(i,j),5,false);
            }
        }
        if(selected!=null){
            g.setColor(new Color(178,178,255));
            drawDot(g,v,selected,5,true);
        }
    }

    public void drawDot(Graphics2D g, Grid2 v, Z2 point,double radius,boolean showCoord){
        Point2D.Double center=v.toWindow(point);
        g.draw(new Ellipse2D.Double(center.x-radius,center.y-radius,2*radius,2*radius));
        if(showCoord){g.drawString(point.toString(),(float)(center.x+radius),(float)(center.y-radius));}
    }
    
    
    // MOUSE EVENT HANDLING

    @Override
    public void mouseMoved(MouseVisometryEvent<Grid2> e) {
        super.mouseMoved(e);
        selected=(Z2)e.getCoordinate();
        fireStateChanged();
    }
    
    
    // REQUIRED ELEMENTS
    
    @Override
    public String[] getStyleStrings() {return null;}
    
    @Override
    public String toString() { return "Grid"; }
}
