/*
 * Rectangle2D.java
 * Created on Sep 25, 2007, 4:24:07 PM
 */

package specto.euclidean2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import specto.Plottable;
import scio.coordinate.R2;
import sequor.model.PointRangeModel;
import sequor.style.LineStyle;
import specto.euclidean2.Euclidean2;

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
    /** Initializes rectangle as the "boundary space" of the given model... i.e. the PointRangeModel requires
     * its point to be within this rectangle.
     * @param prm the point model
     */
    public Rectangle2D(final PointRangeModel prm) {
        min = prm.getMinimum();
        max = prm.getMaximum();
        prm.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                min.setTo(prm.getMinimum());
                max.setTo(prm.getMaximum());
                fireStateChanged();
            }
        });
    }
    public Rectangle2D(final PointRangeModel min, final PointRangeModel max) {
        this.min = min.getPoint();
        this.max = max.getPoint();
        min.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                Rectangle2D.this.min.setTo(min.getPoint());
                fireStateChanged();
            }
        });
        max.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                Rectangle2D.this.max.setTo(max.getPoint());
                fireStateChanged();
            }
        });
    }
    
    public void setMin(R2 min){this.min.x=min.x;this.min.y=min.y;}
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
        g.setStroke(LineStyle.STROKES[LineStyle.MEDIUM]);
        g.draw(new java.awt.geom.Rectangle2D.Double(rx,ry,rWidth,rHeight));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.2f));
        g.fill(new java.awt.geom.Rectangle2D.Double(rx,ry,rWidth,rHeight));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }

    @Override
    public String[] getStyleStrings() {return null;}
    public String toString(){return "Rectangle";}
}
