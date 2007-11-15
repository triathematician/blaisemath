/*
 * Seek.java 
 * Created on Aug 28, 2007, 11:21:25 AM
 */

package behavior;

import simulation.Agent;
import scio.coordinate.R2;
import scio.coordinate.V2;

/**
 * @author Elisha Peterson<br><br>
 * 
 * Behavior moving directly towards an agent.
 */
public class Seek extends behavior.Behavior {   
    public R2 direction(Agent self,V2 target,double t){
        if(target==null){return new R2();}
        return target.minus(self.loc).normalized();
    }
}
