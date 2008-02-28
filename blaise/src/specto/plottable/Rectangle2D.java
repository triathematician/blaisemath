/*
 * Rectangle2D.java
 * 
 * Created on Sep 25, 2007, 4:24:07 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package specto.plottable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import javax.swing.JMenu;
import specto.Plottable;
import scio.coordinate.R2;
import specto.visometry.Euclidean2;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
public class Rectangle2D extends Plottable<Euclidean2>{
    R2 min=null;
    R2 max=null;
    public Rectangle2D(Euclidean2 vis){this(vis,-1,-1,1,1);}
    public Rectangle2D(Euclidean2 vis,R2 min,R2 max){this(vis,min.x,min.y,max.x,max.y);}
    public Rectangle2D(Euclidean2 vis,double minx,double miny,double maxx,double maxy){super(vis);min=new R2(minx,miny);max=new R2(maxx,maxy);}
    public void setMax(R2 max){this.max.x=max.x;this.max.y=max.y;}
    public R2 getMin(){return min;}
    public R2 getMax(){return max;}

    // DRAW METHODS
    
    @Override
    public void recompute(){}
    
    @Override
    public void paintComponent(Graphics2D g) {
        double rWidth=Math.abs(visometry.toWindowX(max.x)-visometry.toWindowX(min.x));
        double rHeight=Math.abs(visometry.toWindowY(max.y)-visometry.toWindowY(min.y));
        double rx=(visometry.toWindowX(max.x)<visometry.toWindowX(min.x)?visometry.toWindowX(max.x):visometry.toWindowX(min.x));
        double ry=(visometry.toWindowY(max.y)<visometry.toWindowY(min.y)?visometry.toWindowY(max.y):visometry.toWindowY(min.y));  
        g.setColor(Color.BLUE);
        g.draw(new java.awt.geom.Rectangle2D.Double(rx,ry,rWidth,rHeight));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float).2));
        g.fill(new java.awt.geom.Rectangle2D.Double(rx,ry,rWidth,rHeight));
    }

    public JMenu getOptionsMenu() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
