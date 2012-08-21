/**
 * MeshRoot2D.java
 * Created on May 23, 2008
 */

package scio.function;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.Vector;
import scio.coordinate.R2;
import scio.coordinate.R3;

/**
 * This class is intended to solve equations of the form f(x,y)=C. Returns a path of points representing the solution curve.
 * 
 * @author Elisha Peterson
 */
public class MeshRoot2D {
    
    public static Path2D.Double findRoots(Function<R2, Double> function, double zValue, R2 p1, R2 p2, double refinement, double precision, int maxIterations) throws FunctionValueException {
        return findRoots(function, zValue, p1.x, p1.y, p2.x, p2.y, refinement, precision, maxIterations);
    }
    
    /** Returns complete solution curve to f(x,y)=C, as approximated by the <b>addFlatTriangles</b> method below. */
    public static Path2D.Double findRoots(Function<R2, Double> function, double zValue,
            double x1, double y1, double x2, double y2,
            double refinement, double precision, int maxIterations) throws FunctionValueException {
      
        int xSteps = (int) Math.ceil((x2-x1)/refinement);
        int ySteps = (int) Math.ceil((y2-y1)/refinement);
        
        Vector<R2> inputs = new Vector<R2> (xSteps*ySteps);        
        for (int i = 0; i <= xSteps; i++) {
            for (int j = 0; j <= ySteps; j++) {
                inputs.add(new R2(x1+refinement*i, y1+refinement*j));
            }
        }        
        //Vector<Double> outputs = function.getValue(inputs);
        
        Vector<R2> midInputs = new Vector<R2>((xSteps-1)*(ySteps-1));        
        for (int i = 0; i < xSteps; i++) {
            for (int j = 0; j < ySteps; j++) {
                midInputs.add(new R2(x1+refinement*(i+.5),y1+refinement*(j+.5)));
            }
        }
       // Vector<Double> midOutputs = function.getValue(midInputs);

        // TODO use above function values to compute the "flat triangle" elements below...
        
        Path2D.Double path = new Path2D.Double();        
        for (double x = x1; x <= x2; x += refinement) {
            for (double y = y1; y <= y2; y += refinement) {
                addFlatTriangles(path, function, zValue, x, y, x+refinement, y+refinement);
            }
        }
        
        return path;
    }
   
    /** Returns solution to f(x,y)=C on a given rectangle, where the function is approximated by four planes in space,
     * corresponding to four triangles of the rectangle. */
    public static Path2D.Double addFlatTriangles(Path2D.Double path,
            Function<R2, Double> function, double zValue, 
            double x1, double y1, double x2, double y2) throws FunctionValueException {
        //System.out.println("Inputs: z=" + zValue + " and (" + x1 + "," + y1 + ") and (" + x2 + "," + y2 + ")");

        // initialize points
        
        R3 pll = new R3(x1,y1,function.getValue(new R2(x1,y1)));
        R3 pul = new R3(x1,y2,function.getValue(new R2(x1,y2)));
        R3 pur = new R3(x2,y2,function.getValue(new R2(x2,y2)));
        R3 plr = new R3(x2,y1,function.getValue(new R2(x2,y1)));
        R3 pmid = new R3((x1+x2)/2,(y1+y2)/2,function.getValue(new R2((x1+x2)/2,(y1+y2)/2)));
        
        // find points of intersection
        
        R2 upper = findPointOnSegment(pul, pur, zValue);
        R2 right = findPointOnSegment(pur, plr, zValue);
        R2 lower = findPointOnSegment(plr, pll, zValue);
        R2 left = findPointOnSegment(pll, pul, zValue);
        R2 upperLeft = findPointOnSegment(pmid, pul, zValue);
        R2 upperRight = findPointOnSegment(pmid, pur, zValue);
        R2 lowerRight = findPointOnSegment(pmid, plr, zValue);
        R2 lowerLeft = findPointOnSegment(pmid, pll, zValue);
        
        // construct result
                
        addSegment(path,upperLeft,upper,upperRight);
        addSegment(path,upperRight,right,lowerRight);
        addSegment(path,lowerRight,lower,lowerLeft);                
        addSegment(path,lowerLeft,left,upperLeft);
        
        return path;
    }
    
    /** Returns point along line segment between p1 and p2 at which z=C, or null if the point is not on the segment.
     * @param p1 first point of segment
     * @param p2 second point of segment
     * @param zValue z value
     * @return point along segment at given z value, or null if no such value exists
     */
    public static R2 findPointOnSegment(R3 p1, R3 p2, double zValue) {
        //System.out.println("p1: " + p1 + ", p2: " + p2 + ", z: " + zValue);
        if (p1.getZ() == p2.getZ()) {
            return p1.getZ() == zValue ? p1.projectXY() : null;
        } else {
            double t = (zValue-p1.getZ())/(p2.getZ()-p1.getZ());
            if (t < 0 || t > 1){ return null; }
            R3 rTemp = p1.plus(p2.minus(p1).times(t));
            return new R2(rTemp.getElement(0),rTemp.getElement(1));
        }
    }
    
    /** Returns segment between two of three points (if not null) */
    public static Shape addSegment(Path2D.Double shape,R2 p1, R2 p2, R2 p3) {
        try {
            if (p1 == null) {
                shape.append(new Line2D.Double(p2, p3), false);
                //System.out.println("appending: "+p2 +", "+p3);
            } else if (p2 == null) {
                shape.append(new Line2D.Double(p1, p3), false);           
                //System.out.println("appending: "+p1 +", "+p3);
            } else if (p3 == null) {
                shape.append(new Line2D.Double(p1, p2), false);
                //System.out.println("appending: "+p1 +", "+p2);
            }
        } catch (NullPointerException e) { }
        return shape;
    }
    
    /** Returns segment of intersection of z=C with the portion of a plane bounded by three particular points. */
    public static Path2D.Double lineOnPlane(double zValue, R3 p1, R3 p2, R3 p3) {
        double tripleProd = R3.tripleProduct(p1, p2, p3);
        R3 crossSum = (R3) p1.crossProduct(p2).plus(p2.crossProduct(p3)).plus(p3.crossProduct(p1));
        double slope = -crossSum.getX()/crossSum.getY();
        double intercept = (tripleProd - zValue * crossSum.getZ()) / crossSum.getY();
        Path2D.Double result = new Path2D.Double ();
        // need to produce intersection of line with given slope/intercept (with respect to x/y) and triangle bounded by p1,p2,p3
        //
        return result;
    }
}
