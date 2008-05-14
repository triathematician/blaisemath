/**
 * TrustMap.java
 * Created on May 9, 2008
 */

package behavior;

import java.util.HashMap;
import simulation.Agent;

/**
 * TrustMap represents an assignment of value to a given set of agents. Agents will use
 * this when fusing tasks together to determine the best course of action. A value of 0.0
 * represents no trust, while a value of 1.0 represents complete trust.
 * 
 * @author Elisha Peterson
 */
public class TrustMap extends HashMap<Agent,Double> {

    /** Assigns an agent with complete trust. */
    public Double putTotalTrust(Agent key) {
        return put(key, 1.0);
    }
    
    /** Returns whether or not a particular agent is trusted. */
    public boolean isTrusted(Agent key) {
        return containsKey(key) && get(key)>0.0;
    }
    
    /** Ensures elements of the table have values between 0.0 and 1.0. */           
    @Override
    public Double put(Agent key, Double value) {
        if (value < 0.0) {
            return super.put(key, 0.0);
        } else if (value > 1.0) {
            return super.put(key, 1.0);
        }        
        return super.put(key, value);
    }
}
