package Euclidean;

import Euclidean.PPoint;
/**
 * PVector.java<br>
 * Author: Elisha Peterson<br>
 * Created circa February 5, 2007<br><br>
 * 
 * This class implements a sort of pointed vector. It stores both a
 *   position (fields inherited from PPoint) and a direction (new PPoint
 *   field). For now, this allows the point to be "moved" in a nice way.
 *   More functionality will be added in due time.
 */

public class PVector extends PPoint{
    /** Stores the velocity/direction. **/
    public PPoint v;
    
    /**
     * Creates a new instance. Defaults to (0,0) position and direction.
     **/
    public PVector(){x=0;y=0;v=new PPoint();}
    public PVector(double x0,double y0){x=x0;y=y0;v=new PPoint();}
    public PVector(PPoint point){x=point.x;y=point.y;v=new PPoint();}
    
    /**
     * Uses the vector to translate the position of the point
     **/
    public void move(){x+=v.x;y+=v.y;}
}

