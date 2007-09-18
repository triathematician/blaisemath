/*
 * Seek.java 
 * Created on Aug 28, 2007, 11:21:25 AM
 */

package behavior.pursuit;

import Euclidean.PPoint;
import Euclidean.PVector;
import simulation.Agent;

/**
 * @author Elisha Peterson<br><br>
 * 
 * Behavior moving directly towards an agent.
 */
public class Seek extends behavior.Behavior {   
    public PPoint direction(Agent self,PVector target,double t){return self.unitToward(target);}
}
