/**
 * IteratedTransform.java
 * Created on Apr 4, 2008
 */

package scio.fractal;

import java.awt.geom.*;
import java.util.Vector;
import scio.coordinate.R2Transform;

/**
 * This class has utilities to begin with a shape and iteratively apply given transforms to it.
 * 
 * @author Elisha Peterson
 */
public class IteratedTransform {
    /** This method returns a shape after transforms have been iteratively applied.
     * @param initialShape the shape relative to the unit square [0,1]x[0,1]
     * @param transforms the list of transformations to apply
     * @param steps the number of steps to perform the transformation
     * @param unionSteps whether the returned shape should contain copies of all steps of the transformation, or just the last iteration
     * @param connectShapes whether to connect the created smaller copies
     * @param connectLevels whether to connect the various iterations (if unionSteps is true)
     * @return the iterated shape
     */
    public static GeneralPath iteratedPath(
            GeneralPath initialPath,
            Vector<AffineTransform> transforms,
            int steps,
            boolean unionSteps,
            boolean connectShapes,
            boolean connectLevels)
    {
        GeneralPath result=unionSteps?initialPath:new GeneralPath(GeneralPath.WIND_NON_ZERO,(int)Math.pow(5,steps));
        GeneralPath lastShape=initialPath;
        GeneralPath curShape=new GeneralPath();
        
        for(int i=0;i<steps;i++){
            curShape=new GeneralPath();
            for(AffineTransform at:transforms){
                curShape.append(at.createTransformedShape(lastShape),connectShapes);
            }
            lastShape=curShape;
            if(unionSteps){result.append(lastShape,connectLevels);}
        }
        return unionSteps?result:lastShape;
    }   
    
    /** Returns vector of segments contained in a path. Each element
     * constists of four doubles with x1,x2,y1,y2 of the line segment.
     */
    public static Vector<double[]> getSegments(GeneralPath path){
        Vector<double[]> result=new Vector<double[]>();
        PathIterator pi=path.getPathIterator(null);        
        double[] nextSegment=new double[4];
        double[] coords=new double[6];
        while(!pi.isDone()){
            switch(pi.currentSegment(coords)){
                case(PathIterator.SEG_LINETO):
                    nextSegment[2]=coords[0];
                    nextSegment[3]=coords[1];
                    result.add(nextSegment);                    
                    nextSegment=new double[4];
                case(PathIterator.SEG_MOVETO):
                    nextSegment[0]=coords[0];
                    nextSegment[1]=coords[1];
                    break;
            }
            pi.next();
        }
        return result;
    }
    
    /** Returns a transform for each segment in the given path. */
    public static Vector<AffineTransform> getSegmentTransforms(GeneralPath path){
        return getTransforms(getSegments(path));
    }
    /** Returns vector of transformations comprised of all the given line segments. */
    public static Vector<AffineTransform> getTransforms(Vector<double[]> input){
        Vector<AffineTransform> result=new Vector<AffineTransform>();
        for(double[] curSeg:input){
            result.add(R2Transform.getLineTransform(curSeg[0],curSeg[1],curSeg[2],curSeg[3]));
        }
        return result;
    }
}
