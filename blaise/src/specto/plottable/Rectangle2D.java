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
    public Rectangle2D(){this(-5,-5,5,5);}
    public Rectangle2D(R2 min,R2 max){this(min.x,min.y,max.x,max.y);}
    public Rectangle2D(double minx,double miny,double maxx,double maxy){min=new R2(minx,miny);max=new R2(maxx,maxy);setColor(Color.GREEN);}
    
    public void setMax(R2 max){this.max.x=max.x;this.max.y=max.y;}
    public R2 getMin(){return min;}
    public R2 getMax(){return max;}

    // DRAW METHODS
    
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        double rWidth=Math.abs(v.toWindowX(max.x)-v.toWindowX(min.x));
        double rHeight=Math.abs(v.toWindowY(max.y)-v.toWindowY(min.y));
        double rx=(v.toWindowX(max.x)<v.toWindowX(min.x)?v.toWindowX(max.x):v.toWindowX(min.x));
        double ry=(v.toWindowY(max.y)<v.toWindowY(min.y)?v.toWindowY(max.y):v.toWindowY(min.y));  
        g.setStroke(PointSet2D.strokes[PointSet2D.REGULAR]);
        g.draw(new java.awt.geom.Rectangle2D.Double(rx,ry,rWidth,rHeight));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.2f));
        g.fill(new java.awt.geom.Rectangle2D.Double(rx,ry,rWidth,rHeight));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }

    @Override
    public String[] getStyleStrings() {return null;}
    public String toString(){return "Rectangle";}
}
