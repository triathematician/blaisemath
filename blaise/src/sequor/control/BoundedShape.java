/*
 * BoundedShape.java
 * Created on Mar 16, 2008
 */

package sequor.control;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.*;

/**
 * BoundedShape is a collection of shapes which can be placed within bounding boxes. The basic class contains information about how to draw a particular
 * shape within a specified bounding box. Subsequent classes need only define the coordinates for the shape within the unit square (0,0)->(1,1).
 * 
 * @author Elisha Peterson
 */
public abstract class BoundedShape {
    public static final Rectangle2D.Double DEFAULT_BOX=new Rectangle2D.Double(0,0,1,1);
    public static final int DEFAULT_PADDING=2;    
    
    /** Returns shape in specified bounding box, with specified padding. */    
    public Shape getBoundedShape(Rectangle box,int padding){return getBoundedShape(box.x,box.y,box.width,box.height,padding);}
    /** Returns shape in specified bounding box, with specified padding. */
    public Shape getBoundedShape(Rectangle2D.Double box,int padding){return getBoundedShape(box.x,box.y,box.width,box.height,padding);}
    /** Returns shape within specified coordinates and width/height parameters. */
    public Shape getBoundedShape(double x,double y,double wid,double ht,int padding){
        return getStandardTransform(x+padding,y+padding,wid-2*padding,ht-2*padding).createTransformedShape(getShape());
    }
    /** Returns shape in box between (0,0) and (1,1). This can be easily transformed. This is the only element which needs to be overridden. */
    public abstract Shape getShape();
    /** Returns whether shape can be "clicked". Sometimes, e.g. lines, it can't be. */
    public boolean isClickable(){return true;}
    
    /** Returns transformation taking the box (0,0)->(1,1) to a box at (x,y) with specified width and height. */
    public static AffineTransform getStandardTransform(double x,double y,double wid,double ht){
        return new AffineTransform(wid,0,0,ht,x,y);
    }
    
    
    // BASIC SHAPES
    
    /** Standard RECTANGLE */
    public static BoundedShape RECTANGLE = new BoundedShape(){
        public Shape getShape() {
            return new Rectangle2D.Double(0,0,1,1);
        }
    };
    
    /** Standard circle/ellipse */
    public static BoundedShape ELLIPSE = new BoundedShape(){
        public Shape getShape() {
            return new Ellipse2D.Double(0,0,1,1);
        }
    };
    
    /** Horizontal line */
    public static BoundedShape LINE_HORIZONTAL = new BoundedShape(){
        public Shape getShape() {
            return new Line2D.Double(0,.5,1,.5);
        }
        @Override
        public boolean isClickable(){return false;}
    };
    
    /** Horizontal line */
    public static BoundedShape LINE_VERTICAL = new BoundedShape(){
        public Shape getShape() {
            return new Line2D.Double(.5,0,.5,1);
        }
        @Override
        public boolean isClickable(){return false;}
    };
    
    /** "Diamond" shape: <|> */
    public static BoundedShape DIAMOND = new BoundedShape(){
        public Shape getShape() {
            Path2D.Double result=new Path2D.Double();
            result.moveTo(.5,0);
            result.lineTo(1,.5);
            result.lineTo(.5,1);
            result.lineTo(0,.5);
            result.closePath();
            return result;
        }
    };   
    
    /** "BOWTIE" shape: |><| */
    public static BoundedShape BOWTIE = new BoundedShape(){
        public Shape getShape() {
            Path2D.Double result=new Path2D.Double();
            result.moveTo(0,0);
            result.lineTo(0,1);
            result.lineTo(.5,.5);
            result.lineTo(1,1);
            result.lineTo(1,0);
            result.lineTo(.5,.5);
            result.closePath();
            return result;
        }
    };   
    
    
    // PEN SHAPES
    
    /** Pen (vertical bar) */
    public static BoundedShape PEN_SHAPE = new BoundedShape(){
        @Override
        public Shape getShape() {
            Path2D.Double result=new Path2D.Double();
            result.moveTo(0,1);
            result.lineTo(.25,1);
            result.lineTo(1,.25);
            result.lineTo(.75,0);
            result.lineTo(0,.75);
            result.closePath();
            return result;
        }        
    };
    
    
    // ANIMATION CONTROL SHAPES
    
    /** Triangle as appears on a "play" symbol: > */
    public static BoundedShape PLAY_TRIANGLE = new BoundedShape(){
        @Override
        public Shape getShape() {
            Path2D.Double result=new Path2D.Double();
            result.moveTo(.1,0);
            result.lineTo(1,.5);
            result.lineTo(.1,1);
            result.closePath();
            return result;
        }
    };
    
    /** Pause style shape: || */
    public static BoundedShape PLAY_PAUSE = new BoundedShape(){
        public Shape getShape() {
            Path2D.Double result=new Path2D.Double();
            result.append(new Rectangle2D.Double(.1,0,.25,1),false);
            result.append(new Rectangle2D.Double(.65,0,.25,1),false);
            return result;
        }
    };
    
    /** "PLAY_FF" shape: >> */    
    public static BoundedShape PLAY_FF = new BoundedShape(){
        public Shape getShape() {
            Path2D.Double result=new Path2D.Double();
            result.append(PLAY_TRIANGLE.getBoundedShape(0,0,.5,1,0),false);
            result.append(PLAY_TRIANGLE.getBoundedShape(.5,0,.5,1,0),false);
            return result;
        }
    };
    
    /** "Slow Play" shape: |> */  
    public static BoundedShape PLAY_SLOW = new BoundedShape(){
        public Shape getShape() {
            Path2D.Double result=new Path2D.Double();
            result.append(new Rectangle2D.Double(.1,0,.25,1),false);
            result.append(PLAY_TRIANGLE.getBoundedShape(.5,0,.5,1,0),false);
            return result;
        }
    };
    
    /** "Restart" shape: |<- */  
    public static BoundedShape PLAY_RESTART = new BoundedShape(){
        public Shape getShape() {
            Path2D.Double result=new Path2D.Double();
            result.append(new Rectangle2D.Double(0,0,.25,1),false);
            result.append(PLAY_TRIANGLE.getBoundedShape(.65,0,-.45,1,0),false);
            result.append(new Rectangle2D.Double(.6,.4,.4,.2),false);
            return result;
        }
    };
}
