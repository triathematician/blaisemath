/*
 * Leading.java 
 * Created on Aug 28, 2007, 11:21:25 AM
 */

package behavior.pursuit;

import Euclidean.PPoint;
import agent.Agent;

/**
 * @author Elisha Peterson<br><br>
 * 
 * Moves towards a position in front of the target which depends upon the leadFactor.
 */
public class Leading extends behavior.Behavior {    
    /**
     * Computes desired direction of travel
     * @param self      the agent exhibiting this behavior
     * @param target    the agent targeted by the behavior
     * @param t         the current time stamp
     * @return          the direction of travel corresponding to this behavior
     */
    public PPoint direction(Agent self,Agent target,double t){
        if(self.v.magnitude()==0){return self.toward(target).normalize();}
        return self.toward(target.plus(target.v.multipliedBy(self.as.getLeadFactor()*self.distanceTo(target)/self.v.magnitude()))).normalize();
    }
}
