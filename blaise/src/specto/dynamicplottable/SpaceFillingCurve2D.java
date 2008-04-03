/**
 * SpaceFillingCurve2D.java
 * Created on Apr 2, 2008
 */

package specto.dynamicplottable;

import java.util.Vector;
import scio.coordinate.R2;
import specto.plottable.PointSet2D;

/**
 * Used to generate space-filling curves. The computation is done by looking at the path starting
 * at (0,0), passing through any points specified (hopefully in the unit square), and ending at (1,0).
 * At each step, four copies of the previous shape are made, transformed, and placed into smaller rectangles.
 * @author Elisha Peterson
 */
public class SpaceFillingCurve2D extends FractalEdge2D {

    public SpaceFillingCurve2D(){
        this(new R2(1,1),new R2(0,1));
        edges.style.setValue(PointSet2D.THIN);
    }
    
    /** Constructor specifying points in repeated shape.
     * These should be contained in the unit square to be properly used.
     * @param p1
     * @param p2
     */
    public SpaceFillingCurve2D(R2 p1, R2 p2) {
        super(p1, p2);
    }

    final R2Transform shrink=new R2Transform(.5,0,0,.5,.25,.25);
    final R2Transform[] copy={
        new R2Transform(0,.5,.5,0,0,0),
        new R2Transform(.5,0,0,.5,0,.5),
        new R2Transform(.5,0,0,.5,.5,.5),
        new R2Transform(0,-.5,-.5,0,1,.5)};
    
    @Override
    public void recompute() {
        Vector<R2> curShape=new Vector<R2>();
        curShape.add(new R2());
        for(Point2D p:points){curShape.add(p.getPoint());}
        curShape.add(new R2(1,0));
        for(R2 p:curShape){shrink.transform(p,p);}
        Vector<R2> temp=new Vector<R2>();
        for(int i=0;i<maxIter.getValue();i++){
            temp.clear();
            for(int j=0;j<4;j++){
                temp.addAll(copy[j].transform(curShape));
            }
            curShape.clear();
            curShape.addAll(temp);
        }
        edges.setPath(curShape);
    }
}
