/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * R2Transform.java
 * Created on Apr 4, 2008
 */

package scio.coordinate;

import java.awt.geom.AffineTransform;
import java.util.Vector;

/**
 *
 * @author Elisha Peterson
 */
public class R2Transform extends AffineTransform{
    public R2Transform(double m00, double m10, double m01, double m11, double m02, double m12) {
        super(m00, m10, m01, m11, m02, m12);
    }

    public R2 transform(R2 input){
        java.awt.geom.Point2D.Double temp=null;
        temp=(java.awt.geom.Point2D.Double)super.transform(input,null);
        return new R2(temp.x,temp.y);
    }        

    public Vector<R2> transform(Vector<R2> inputs){
        Vector<R2> result=new Vector<R2>();
        for(R2 p:inputs){result.add(transform(p));}
        return result;
    }     
    
    /** Returns affine transformation taking [0,1] to the given line segment; just a rotation, dilation, and shift */
    public static AffineTransform getLineTransform(double x1,double y1,double x2,double y2){
        return new AffineTransform(x2-x1,y2-y1,y1-y2,x2-x1,x1,y1);
    }
}

