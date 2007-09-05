/*
 * Seek.java 
 * Created on Aug 28, 2007, 11:21:25 AM
 */

package behavior.pursuit;

import Euclidean.PPoint;
import agent.Agent;

/**
 * @author Elisha Peterson<br><br>
 * 
 * Behavior moving directly towards an agent.
 */
public class Seek extends behavior.Behavior {    
    public Seek(){}
    /**
     * Computes desired direction of travel
     * @param self      the agent exhibiting this behavior
     * @param target    the agent targeted by the behavior
     * @param t         the current time stamp
     * @return          the direction of travel corresponding to this behavior
     */
    public PPoint direction(Agent self,Agent target,double t){
        return self.toward(target).normalize();
    }
}
