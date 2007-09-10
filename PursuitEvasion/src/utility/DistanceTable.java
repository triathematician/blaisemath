/*
 * DistanceTable.java
 * Created on Aug 28, 2007, 10:28:39 AM
 */

package utility;

import agent.Agent;
import agent.Team;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author Elisha Peterson
 * <br><br>
 * This class calculates and contains a table of distances between players on two
 * different teams. Recalculation is only done when necessary. Also contains routines
 * for producing the minimum and maximum values in the table. Separate tables are
 * necessary for each pair of teams, if relevant. These are used with the Goal class
 * to determine whether a goal has been achieved.
 * <br><br>
 * Uses nested hashmaps to populate a matrix of distances between two
 * collections of agents. Able to recalculate, pick out specific values,
 * minimum values, etc.
 */
public class DistanceTable extends HashMap<Agent,HashMap<Agent,Double>> {

// PROPERTIES
    
    /** The min/max values in the table. */
    double min,max;
    
    /** The list of agents for first key set. */
    ArrayList<Agent> keya;    
    /** The list of agents for the second key set. */
    ArrayList<Agent> keyb;    
    
    
// CONSTRUCTORS    

    /** Generic constructor */
    public DistanceTable(){
        keya=new ArrayList<Agent>();
        keyb=new ArrayList<Agent>();
    }       
    /** Constructs given two collections of agents 
     * @param keya the first set of agents
     * @param keyb the second set of agents */
    public DistanceTable(Collection<Agent> keya,Collection<Agent> keyb){
        this();
        this.keya.addAll(keya);
        this.keyb.addAll(keyb);
        recalculate();
    }
    /** Constructs given a collection of teams
     * @param teams the set of teams */
    public DistanceTable(Collection<Team> teams){
        this();
        for(Team t:teams){keya.addAll(t);}
        keyb.addAll(keya);
        recalculate();
    }
    
    
// BEAN PATTERNS
    
    /** Override... says if the table is empty, i.e. if either keySet is null
     * @return true if empty, otherwise false. */
    public boolean isEmpty(){return(keya.size()==0||keyb.size()==0);}
    
    
// METHODS    

    /** Recalculates all distances in the table */
    public void recalculate(){
        for(Agent a:keya){
            put(a,new HashMap<Agent,Double>());
            for(Agent b:keyb){
                get(a).put(b,a.distanceTo(b.getPoint()));
            }
        }
    }
    
    /** Remove an agent from all nested HashMaps
     * @param agent the agent */
    public void removeSecond(Agent agent){for(Agent a:keya){get(a).remove(agent);}}
    
    
// BASIC QUERY METHODS
    
    /** Outputs the distance between two particular agents
     * @param a the first agent
     * @param b the second agent
     * @return the distance between the agents */
    public double get(Agent a,Agent b){
        Double result=get(a).get(b);
        return result==null?Double.MAX_VALUE:result;
    }

    /** Returns minimum between two collections of agents
     * @param ta the first collection of agents
     * @param tb the second collection of agents
     * @return the minimum agent/agent/distance as an AgentPair */
    public AgentPair min(Collection<Agent> ta,Collection<Agent> tb){
        AgentPair result=new AgentPair();
        for(Agent a:ta){
            for(Agent b:tb){
                result.replaceIfLessBy(a,b,get(a,b));
            }
        }
        return result;
    }
    
    /** Returns maximum between two collections of agents
     * @param ta the first collection of agents
     * @param tb the second collection of agents
     * @return the maximum agent/agent/distance as an AgentPair */
    public AgentPair max(Collection<Agent> ta,Collection<Agent> tb){
        AgentPair result=new AgentPair(0);
        for(Agent a:ta){
            for(Agent b:tb){
                result.replaceIfMoreBy(a,b,get(a,b));
            }
        }
        return result;
    }
    
    
// GLOBAL QUERIES    
    
    /** Outputs the table's minimum value
     * @return minimum distance between the two agent collections and the corresponding agents */
    public AgentPair min(){
        AgentPair result=new AgentPair();
        for(Agent a:keya){
            for(Agent b:keyb){
                result.replaceIfLessBy(a,b,get(a,b));
            }
        }
        return result;
    }
    
    /** Outputs the table's maximum value
     * @return maximum distance between the two agent collections and the corresponding agents */
    public AgentPair max(){
        AgentPair result=new AgentPair(0);
        for(Agent a:keya){
            for(Agent b:keyb){
                result.replaceIfMoreBy(a,b,get(a,b));
            }
        }
        return result;
    }
    
    
// QUERY METHODS: SPECIALIZED MIN/MAX RETURNS
        
    /** Outputs the minimum distance between an agent and a team
     * @param a the agent
     * @param tb the team
     * @return the minimum value in the distance table */
    public AgentPair min(Agent a,Team tb){
        AgentPair result=new AgentPair();
        for(Agent b:tb){result.replaceIfLessBy(a,b,get(a,b));}
        return result;
    }
    
    /** Outputs the maximum distance between an agent and a team
     * @param a the agent
     * @param tb the team
     * @return the maximum value in the distance table */
    public AgentPair max(Agent a,Team tb){
        AgentPair result=new AgentPair(0);
        for(Agent b:tb){result.replaceIfMoreBy(a,b,get(a,b));}
        return result;
    }
    
    
// QUERY METHODS: SUBSET OF AGENTS SPECIFIED BY A GIVEN DISTANCE

    /** Returns a list of agents within a fixed distance of given agent.
     * @param a the agent
     * @param d fixed distances
     * @return agents within distance d of agent a */
    public ArrayList<Agent> getAgentsInRadius(Agent a,double d){
        ArrayList<Agent> result=new ArrayList<Agent>();
        for(Agent b:keya){if(get(a,b)<d){result.add(b);}}
        return result;
    }
    
    /** Returns list of agents on a specified team within a fixed distance of a given agent.
     * @param a the agent
     * @param t the team to consider
     * @param d the fixed distance
     * @return agents on team t within distance d of agent a */
    public ArrayList<Agent> getAgentsInRadius(Agent a,Collection<Agent> t,double d){
        ArrayList<Agent> result=new ArrayList<Agent>();
        for(Agent b:t){if(get(a,b)<d){result.add(b);}}
        return result;
    }
}
