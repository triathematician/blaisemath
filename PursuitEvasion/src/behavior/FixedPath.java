/*
 * FixedPath.java
 * Created on Aug 28, 2007, 11:21:31 AM
 */

package behavior;

import Euclidean.PPoint;
import Euclidean.PVector;
import simulation.Agent;

/**
 * @author Elisha Peterson<br><br>
 * 
 * Moves on a fixed path relative to time rather than position.
 */
public class FixedPath extends Behavior {  
    /** Follows the path as best as possible. If unable to follow the path, heads towards where it SHOULD be at that time. */
    public PPoint direction(Agent self,PVector target,double t){return self.unitToward(self.getPositionTime(t));}
}
