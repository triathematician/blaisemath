/*
 * FixedPath.java
 * Created on Aug 28, 2007, 11:21:31 AM
 */

package behavior.evasion;

import Euclidean.PPoint;
import agent.Agent;

/**
 * @author Elisha Peterson<br><br>
 * 
 * Moves on a fixed path relative to time rather than position.
 */
public class FixedPath extends behavior.Behavior {  
    /**
     * Computes desired direction of travel
     * @param self      the agent exhibiting this behavior
     * @param target    the agent targeted by the behavior
     * @param t         the current time stamp
     * @return          the direction of travel corresponding to this behavior
     */
    public PPoint direction(Agent self,Agent target,double t){
        switch(pathType){
        case 0: return start.translate(direction.multiply(t));
        case 1: return start.translate(r*Math.cos(theta0+w*t),r*Math.sin(theta0+w*t));
        }
        return new PPoint();
    }


// PROPERTIES
    
    /** Type of path... 0=line, 1=circle for now */
    int pathType=0;
    
    /** Starting position. */
    PPoint start;
    
    /** Used for linear path. */
    PPoint direction;
    
    /** Used for circular path. */
    double r,theta0,w;    
}
