/*
 * FixedPath.java
 * Created on Aug 28, 2007, 11:21:31 AM
 */

package behavior;

import simulation.Agent;
import scio.coordinate.R2;
import scio.coordinate.V2;

/**
 * @author Elisha Peterson<br><br>
 * 
 * Moves on a fixed path relative to time rather than position.
 * Follows the path as best as possible. If unable to follow the path, heads towards where it SHOULD be at that time.
 */
public class ApproachPath extends Behavior {  
    public R2 direction(Agent self,V2 target,double t){
        return self.getPositionTime(t).minus(self.loc).normalized();
    }
}
