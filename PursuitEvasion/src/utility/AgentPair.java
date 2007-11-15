/*
 * AgentPair.java
 * Created on Sep 4, 2007, 7:41:50 AM
 */

package utility;

import simulation.Agent;
import scio.coordinate.V2;

/**
 * @author Elisha Peterson
 * <br><br>
 * This class maintains two agents and the distance between them.
 */
public class AgentPair {
    
    /** The two agents involved */
    Agent first;
    Agent second;    
    /** The distance between them */
    double distance;
    
    // CONSTRUCTORS    
    
    public AgentPair(){this(null,null,Double.MAX_VALUE);}
    public AgentPair(double d){this(null,null,d);}
    public AgentPair(Agent a,Agent b,double d){first=a;second=b;distance=d;}
    
    // BEAN PATTERNS: GETTERS/SETTERS
    
    public Agent getFirst(){return first;}
    public V2 getFirstLoc(){return first==null?null:first.loc;}
    public Agent getSecond(){return second;}
    public V2 getSecondLoc(){return second==null?null:second.loc;}
    public double getDistance(){return distance;}    
    
// METHODS TO CHANGE THE AGENTS STORED HERE
    
    public void replaceIfLessBy(Agent a,Agent b,double d){
        if(d<distance){
            first=a;
            second=b;
            distance=d;
        }        
    }
    public void replaceIfMoreBy(Agent a,Agent b,double d){
        if(d>distance){
            first=a;
            second=b;
            distance=d;
        }
    }
}
