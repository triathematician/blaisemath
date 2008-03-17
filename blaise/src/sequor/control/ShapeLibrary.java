/*
 * ShapeLibrary.java
 * Created on Mar 16, 2008
 */

package sequor.control;

import java.awt.Shape;
import java.awt.geom.*;

/**
 * ShapeLibrary is a collection of shapes which can be placed within bounding boxes.
 * @author Elisha Peterson
 */
public class ShapeLibrary {
    public static class BoundedElement {
        Shape s;
        public BoundedElement(){}
        public Shape initShape(double x,double y,double wid,double ht,double padding){
            s=new Rectangle2D.Double(x+padding,y+padding,wid-2*padding,ht-2*padding);            
            return s;
        }
        public Shape getShape(){return s;}
    }
    
    public static class PlayTriangle extends BoundedElement {      
        public PlayTriangle(double buttonSize, double padding){this(0,0,buttonSize,buttonSize,padding);}  
        public PlayTriangle(double x,double y,double wid,double ht,double padding){initShape(x,y,wid,ht,padding);}
        @Override
        public Shape initShape(double x,double y,double wid,double ht,double padding){
            if(padding!=0){return initShape(x+padding,y+padding,wid-2*padding,ht-2*padding,0);}
            Path2D.Double result=new Path2D.Double();
            result.moveTo(x+wid/12,y);
            result.lineTo(x+wid,y+ht/2);
            result.lineTo(x+wid/12,y+ht);
            result.closePath();
            s=result;
            return result;
        }
    } 
    
    public static class PlayPause extends BoundedElement {       
        public PlayPause(double buttonSize, double padding){this(0,0,buttonSize,buttonSize,padding);} 
        public PlayPause(double x,double y,double wid,double ht,double padding){initShape(x,y,wid,ht,padding);}
        @Override
        public Shape initShape(double x,double y,double wid,double ht,double padding){
            if(padding!=0){return initShape(x+padding,y+padding,wid-2*padding,ht-2*padding,0);}
            Path2D.Double result=new Path2D.Double();
            result.append(new Rectangle2D.Double(x+wid/12,y,wid/3,ht),false);
            result.append(new Rectangle2D.Double(x+7*wid/12,y,wid/3,ht),false);
            s=result;
            return result;
        }
    } 
    
    public static class PlayStop extends BoundedElement {  
        public PlayStop(double buttonSize, double padding){this(0,0,buttonSize,buttonSize,padding);} 
        public PlayStop(double x,double y,double wid,double ht,double padding){initShape(x,y,wid,ht,padding);}
    }
    
    public static class PlayFaster extends BoundedElement {   
        public PlayFaster(double buttonSize, double padding){this(0,0,buttonSize,buttonSize,padding);}     
        public PlayFaster(double x,double y,double wid,double ht,double padding){initShape(x,y,wid,ht,padding);}
        @Override
        public Shape initShape(double x,double y,double wid,double ht,double padding){
            if(padding!=0){return initShape(x+padding,y+padding,wid-2*padding,ht-2*padding,0);}
            Path2D.Double result=new Path2D.Double();
            result.append(new PlayTriangle(x+wid/12,y,wid/2,ht,0).getShape(),false);
            result.append(new PlayTriangle(x+wid/2,y,wid/2,ht,0).getShape(),false);
            s=result;
            return result;
        }
    } 
    
    public static class PlaySlower extends BoundedElement {        
        public PlaySlower(double buttonSize, double padding){this(0,0,buttonSize,buttonSize,padding);}
        public PlaySlower(double x,double y,double wid,double ht,double padding){initShape(x,y,wid,ht,padding);}
        @Override
        public Shape initShape(double x,double y,double wid,double ht,double padding){
            if(padding!=0){return initShape(x+padding,y+padding,wid-2*padding,ht-2*padding,0);}
            Path2D.Double result=new Path2D.Double();
            result.append(new Rectangle2D.Double(x+wid/12,y,wid/4,ht),false);
            result.append(new PlayTriangle(x+wid/2,y,wid/2,ht,0).getShape(),false);
            s=result;
            return result;
        }
    }    
    
    public static class PlayRestart extends BoundedElement {        
        public PlayRestart(double buttonSize, double padding){this(0,0,buttonSize,buttonSize,padding);}
        public PlayRestart(double x,double y,double wid,double ht,double padding){initShape(x,y,wid,ht,padding);}
        @Override
        public Shape initShape(double x,double y,double wid,double ht,double padding){
            if(padding!=0){return initShape(x+padding,y+padding,wid-2*padding,ht-2*padding,0);}
            Path2D.Double result=new Path2D.Double();
            result.append(new Rectangle2D.Double(x,y,wid/4,ht),false);
            result.append(new PlayTriangle(x+2*wid/3,y,-5*wid/12,ht,0).getShape(),false);
            result.append(new Rectangle2D.Double(x+2*wid/3,y+3*ht/8,wid/3,ht/4),false);
            s=result;
            return result;
        }
    } 
}
