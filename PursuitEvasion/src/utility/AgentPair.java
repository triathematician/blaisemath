/*
 * AgentPair.java
 * Created on Sep 4, 2007, 7:41:50 AM
 */

package utility;

import agent.Agent;

/**
 * @author Elisha Peterson
 * <br><br>
 * This class maintains two agents and the distance between them.
 */
public class AgentPair {

// PROPERTIES
    
    /** The two agents involved */
    Agent first;
    Agent second;
    
    /** The distance between them */
    double distance;
    
    
// CONSTRUCTORS    
    
    /** Default constructor... sets up for min calculations */    
    public AgentPair(){
        first=null;second=null;distance=Double.MAX_VALUE;
    }
    /** Constructs with given initial distance
     * @param d the initial distance to begin with */
    public AgentPair(double d){
        first=null;second=null;this.distance=d;
    }
    /** Constructs with given intial set of agents/distance
     * @param a the first agent
     * @param b the second agent
     * @param d the distance between them */
    public AgentPair(Agent a,Agent b,double d){
        this.first=a;this.second=b;this.distance=d;
    }
    
    
// BEAN PATTERNS: GETTERS/SETTERS
    
    /** Returns first agent
     * @return the first agent */
    public Agent getFirst(){return first;}
    /** Returns second agent
     * @return the second agent */
    public Agent getSecond(){return second;}
    /** Returns distance 
     * @return distance between the two agents */
    public double getDistance(){return distance;}
    
    
// METHODS TO CHANGE THE AGENTS STORED HERE
    
    /** Changes the agents if new pair has a smaller distance.
     * @param a the new first agent
     * @param b the new second agent 
     * @param d the distance between them */
    public void replaceIfLessBy(Agent a,Agent b,double d){
        if(d<this.distance){this.first=a;this.second=b;this.distance=d;}
    }
    
    /** Changes the agents if new pair has a greater distance.
     * @param a the new first agent
     * @param b the new second agent 
     * @param d the distance between them */
    public void replaceIfMoreBy(Agent a,Agent b,double d){
        if(d>this.distance){this.first=a;this.second=b;this.distance=d;}
    }
}
