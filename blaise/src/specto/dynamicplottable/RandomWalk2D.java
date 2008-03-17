/*
 * RandomWalk2D.java
 * Created on Mar 15, 2008
 */

package specto.dynamicplottable;

import java.util.Vector;
import scio.coordinate.R2;
import scio.random.PRandom;
import sequor.model.DoubleRangeModel;
import sequor.model.PointRangeModel;

/**
 * RandomWalk2D is a class which implements random walks. The random walk is computed via a maximum distancePerTime (distancePerTime) and a
 * distribution of angles changes (angleParameter).
 * 
 * @author Elisha Peterson
 */
public class RandomWalk2D extends InitialPointSet2D {
    
    /** Speed */
    DoubleRangeModel distancePerTime;
    /** Change in angle per time step */
    DoubleRangeModel angleParameter;
    
    public RandomWalk2D(){this(new PointRangeModel());}
    public RandomWalk2D(Point2D parent) {this(parent.prm);}
    public RandomWalk2D(PointRangeModel prm){
        super(prm);
        distancePerTime=new DoubleRangeModel(.1,.001,10,.001);
        angleParameter=new DoubleRangeModel(.1,0,10,.001);
    }
    
    public DoubleRangeModel getAngleParameterModel(){return angleParameter;}
    public DoubleRangeModel getDistancePerTimeModel(){return distancePerTime;}

    @Override
    public void recompute() {
        Vector<R2> newPath=new Vector<R2>();
        newPath.add(getPoint());
        double theta=0;
        double dtheta=this.angleParameter.getValue();
        double speed=this.distancePerTime.getValue();
        R2 lastPoint=getPoint();
        for(int i=0;i<length.getValue();i++){
            theta+=PRandom.normal(0,dtheta);
            lastPoint.translate(speed*Math.cos(theta),speed*Math.sin(theta));
            newPath.add(new R2(lastPoint.x,lastPoint.y));
        }
        path.setPath(newPath);
    }    
    
    @Override    
    public String toString(){return "Random Walk";}
}
