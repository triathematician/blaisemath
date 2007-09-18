/*
 * Flee.java
 * Created on Aug 28, 2007, 11:21:31 AM
 */

package behavior.evasion;

import Euclidean.PPoint;
import Euclidean.PVector;
import simulation.Agent;

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
    public PPoint direction(Agent self,PVector target,double t){return self.unitFrom(target);}
}
