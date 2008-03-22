/**
 * BoundedWidthShape.java
 * Created on Mar 17, 2008
 */

package sequor.control;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.*;
import java.awt.geom.Rectangle2D.Double;

/**
 * Represents a bounded shape which also depends on a particular parameter for sizing information, e.g. a RoundedRectangle.
 * 
 * @author Elisha Peterson
 */
public abstract class BoundedWidthShape extends BoundedShape {    

    // BASIC SHAPES
    
    public BoundedWidthShape(double parameter){setParameter(parameter);}
    
    double parameter;
    
    public double getParameter(){return parameter;}
    public void setParameter(double parameter){this.parameter=parameter;}

    public Shape getBoundedShape(Rectangle box, double parameter, int padding) {setParameter(parameter);return super.getBoundedShape(box, padding);}
    public Shape getBoundedShape(Double box, double parameter, int padding) {setParameter(parameter);return super.getBoundedShape(box, padding);}
    public Shape getBoundedShape(double x, double y, double wid, double ht, double parameter, int padding){setParameter(parameter);return super.getBoundedShape(x, y, wid, ht, padding);}
    
    /** Round Rectangle. Parameter represents the rounding of the corners in pixels. */
    public static class RoundRectangle extends BoundedWidthShape {
        public RoundRectangle(double parameter){super(parameter);}
        @Override
        public Shape getBoundedShape(double x,double y,double wid,double ht,int padding){
            return new RoundRectangle2D.Double(x+padding,y+padding,wid-2*padding,ht-2*padding,parameter,parameter);
        }
        public Shape getShape() {
            return new RoundRectangle2D.Double(0,0,1,1,.1,.1);
        }
    };    

    /** Standard ring, inner radius half of the outer radius. Parameter represents the inner radius as a percentage of the outer. */
    public static class Ring extends BoundedWidthShape{
        public Ring(double parameter){super(parameter);}
        public Shape getShape() {
            Area a=new Area(new Ellipse2D.Double(0,0,1,1));
            a.exclusiveOr(new Area(new Ellipse2D.Double(.5-parameter,.5-parameter,2*parameter,2*parameter)));
            return a;
        }
    };

    
    // RING WITH SPECIFIED INNER RADIUS
    
    // COMPOUND SHAPES
    
    /** Series of large round dots. */
    public static class DotDotDot extends BoundedWidthShape{
        public DotDotDot(double parameter){super(parameter);}
        @Override
        public Shape getBoundedShape(double x,double y,double wid,double ht,int padding){
            Path2D.Double shape=new Path2D.Double();
            double dWidth=wid-2*padding;
            double dHeight=ht-2*padding;
            double dotWidth;
            if(dWidth>dHeight){
                int n=(int)(dWidth/dHeight);
                if(n==1){n=2;}
                double step=(dWidth-dHeight)/n;
                for(int i=0;i<=n;i++){
                    dotWidth=.5*dHeight*(1+i/(double)n);
                    shape.append(new Ellipse2D.Double(
                            x+dHeight/2+i*step-dotWidth/2,
                            y+dHeight/2-dotWidth/2,
                            dotWidth,dotWidth)
                            ,false);
                }
                return shape;
            }else if(dHeight>dWidth){
                int n=(int)(dHeight/dWidth);
                if(n==1){n=2;}
                float step=(float)(dHeight-dWidth)/(float)n;
                for(int i=0;i<=n;i++){
                    dotWidth=.5*dWidth*(1+i/(double)n);
                    shape.append(new Ellipse2D.Double(
                            x+dWidth/2-dotWidth/2,
                            y+dWidth/2+i*step-dotWidth/2,
                            dotWidth,dotWidth)
                            ,false);
                }
                return shape;
            }else{
                return super.getBoundedShape(x,y,dWidth,dHeight,padding);
            }
        }
        public Shape getShape() {return new Ellipse2D.Double(0,0,1,1);}
    };    
}
