/**
 * MeshRoot2D.java
 * Created on May 23, 2008
 */

package scio.function;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import scio.coordinate.R2;
import scio.coordinate.R3;
import scio.coordinate.VectorSpaceElement;

/**
 * This class is intended to solve equations of the form f(x,y)=C. Returns a path of points representing the solution curve.
 * 
 * @author Elisha Peterson
 */
public class MeshRoot2D {
    
    /** Returns complete solution curve to f(x,y)=C, as approximated by the <b>flatTriangleRoot</b> method below. */
    public static Path2D.Double findRoots(Function<R2, Double> function,
            double x1, double y1, double x2, double y2,
            double refinement, double precision, int maxIterations) {
        // TODO add code
        return null;
    }
   
    /** Returns solution to f(x,y)=C on a given rectangle, where the function is approximated by four planes in space,
     * corresponding to four triangles of the rectangle. */
    public static Path2D.Double flatTriangleRoot(Function<R2, Double> function, double x1, double y1, double x2, double y2) throws FunctionValueException{
        Path2D.Double result = new Path2D.Double();
        double zll = function.getValue(new R2(x1,y1));
        double zul = function.getValue(new R2(x1,y2));
        double zur = function.getValue(new R2(x2,y2));
        double zlr = function.getValue(new R2(x2,y1));
        double zmid = function.getValue(new R2((x1+x2)/2,(y1+y2)/2));
        
        // call lineOnPlane method below four times, once for each plane.
        //
        // rethinking this... perhaps it would be better to find points of intersection of z=C with the eight lines between the five        
        // specified points. Unless the plane is completely flat, there should be either 0 or 2 points of intersection in each triangle.
        // then we merely combine the points to form the path
        
        return result;
    }
    
    /** Returns segment of intersection of z=C with the portion of a plane bounded by three particular points. */
    public static Path2D.Double lineOnPlane(double zValue, R3 p1, R3 p2, R3 p3) {
        double tripleProd = R3.tripleProduct(p1, p2, p3);
        R3 crossSum = (R3) p1.cross(p2).plus(p2.cross(p3)).plus(p3.cross(p1));
        double slope = -crossSum.getX()/crossSum.getY();
        double intercept = (tripleProd - zValue * crossSum.getZ()) / crossSum.getY();
        Path2D.Double result = new Path2D.Double ();
        // need to produce intersection of line with given slope/intercept (with respect to x/y) and triangle bounded by p1,p2,p3
        //
        return result;
    }
}
