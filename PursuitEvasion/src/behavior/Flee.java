/*
 * Flee.java
 * Created on Aug 28, 2007, 11:21:31 AM
 */

package behavior;

import simulation.Agent;
import scio.coordinate.R2;
import scio.coordinate.V2;

/**
 * Moves directly away from an agent.
 * 
 * @author Elisha Peterson<br><br>
 */
public class Flee extends behavior.Behavior {  
    /**
     * Computes desired direction of travel
     * @param self      the agent exhibiting this behavior
     * @param target    the agent targeted by the behavior
     * @param t         the current time stamp
     * @return          the direction of travel corresponding to this behavior
     */
    public R2 direction(Agent self,V2 target,double t){
        if(target==null){return new R2();}
        return self.loc.minus(target).normalized();
    }
}
