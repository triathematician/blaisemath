/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * RandomFlame2D.java
 * Created on Apr 1, 2008
 */

package specto.dynamicplottable;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import scio.coordinate.R2;
import scio.random.Random2D;
import sequor.model.PointRangeModel;
import specto.visometry.Euclidean2;

/**
 *
 * @author Elisha Peterson
 */
public class RandomFlame2D extends Point2D {
    double height;
    double width;
    
    public RandomFlame2D(double height,double width){
        this.height=height;
        this.width=width;
        setColor(Color.RED);
        style.setValue(SMALL);
    }
    public RandomFlame2D(double x,double y,double height,double width) {
        super(x,y);
        this.height=height;
        this.width=width;
        setColor(Color.RED);
        style.setValue(SMALL);
    }
    public RandomFlame2D(PointRangeModel prm,double height,double width) {
        super(prm);
        this.height=height;
        this.width=width;
        setColor(Color.RED);
        style.setValue(SMALL);
    }

    // Variables for random motion
    // Note: x coordinates are x coords, y coordinates are "widths" or distanceTo between edges of the flame (should be positive)
    
    R2 top;
    R2 upper;
    R2 mid;
    R2 bottom;
    R2 ht;
    
    final R2 topAnchor=new R2(-.05,.2);
    final R2 upperAnchor=new R2(-.25,.6);
    final R2 midAnchor=new R2(-1.1,2);
    final R2 bottomAnchor=new R2(0,.1);
    R2 heightAnchor;
    
    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        super.paintComponent(g, v);
        R2 p1=new R2(v.toWindow(getPoint()));
        if(top==null){
            top=new R2(topAnchor);
            upper=new R2(upperAnchor);
            mid=new R2(midAnchor);
            bottom=new R2(bottomAnchor);
            heightAnchor=new R2(1,0);
            ht=new R2(heightAnchor);
        }else{
            transformWeighted(top,Random2D.rectNormal(.02,0),topAnchor,.995);
            transformWeighted(upper,Random2D.rectNormal(.01,.01),upperAnchor,.999);
            transformWeighted(mid,Random2D.rectNormal(.01,.01),midAnchor,.99);
            transformWeighted(bottom,Random2D.rectNormal(.01,0),bottomAnchor,.95);
            transformWeighted(ht,Random2D.rectNormal(.01,0),heightAnchor,.99);
        }
        // create the path shape
        Path2D.Double outline=new Path2D.Double();
        outline.moveTo(top.x,1*ht.x);
        outline.curveTo(top.x+upper.x+upper.y,.7,bottom.x+mid.x+mid.y,.1*ht.x,bottom.x,0);
        outline.curveTo(bottom.x+mid.x,.1*ht.x,top.x+upper.x,.7*ht.x,top.x,1*ht.x);
        outline.transform(new AffineTransform(width,0,0,-height,p1.x,p1.y));
        // draw the various layers of the shape
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.3f));
        drawCopy(g,Color.GRAY,outline,p1,1.1,.02);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
        drawCopy(g,new Color(0.9f,0.1f,0.1f),outline,p1,1,0);
        drawCopy(g,Color.ORANGE,outline,p1,.95,-.06);
        drawCopy(g,Color.YELLOW,outline,p1,.9,-.08);
        drawCopy(g,Color.WHITE,outline,p1,.8,-.12);
        drawCopy(g,Color.WHITE,outline,p1,.6,-.14);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }
    
    /** Draws shifted/transformed version of the shape in specified color. */
    public void drawCopy(Graphics2D g,Color c,Shape s,R2 p1,double scale,double yShift){
        g.setColor(c);
        g.fill(new AffineTransform(scale,0,0,scale,(1-scale)*p1.x,(1-scale)*p1.y+yShift*height).createTransformedShape(s));
    }
    
    /** Transforms the input point according to "weighted" points in the x and y directions. Thus, permits
     * random motion without getting too far away from (x,y)
     * @param x the point to be shifted
     * @param dx the generated point shift
     * @param anchor the anchoring point
     * @param weight the relative weighting of dx vs anchor (a number between 0 and 1)
     * @return point between x+dx and anchor specified by weight
     */
    public R2 transformWeighted(R2 x,R2 dx,R2 anchor,double weight){
        x.setLocation(x.plus(dx).multipliedBy(weight).plus(anchor.multipliedBy(1-weight)));
        return x;
    }
}
