/*
 * FixedPath.java
 * Created on Aug 28, 2007, 11:21:31 AM
 */

package behavior.independent;

import behavior.*;
import Euclidean.PPoint;
import Euclidean.PVector;
import simulation.Agent;

/**
 * @author Elisha Peterson<br><br>
 * 
 * Moves on a fixed path relative to time rather than position.
 * Follows the path as best as possible. If unable to follow the path, heads towards where it SHOULD be at that time.
 */
public class ApproachPath extends Behavior {  
    public PPoint direction(Agent self,PVector target,double t){return self.unitToward(self.getPositionTime(t));}
}
