/*
 * DistanceTable.java
 * Created on Aug 28, 2007, 10:28:39 AM
 */

package utility;

import simulation.Agent;
import simulation.Team;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * This class calculates and contains a table of distances between players on two
 * different teams. Recalculation is only done when necessary. Also contains routines
 * for producing the minimum and maximum values in the table. Separate tables are
 * necessary for each pair of teams, if relevant. These are used with the Goal class
 * to determine whether a goal has been achieved.
 * <p>
 * Uses nested hashmaps to populate a matrix of distances between two
 * collections of agents. Able to recalculate, pick out specific values,
 * minimum values, etc.
 * </p>
 * @author Elisha Peterson
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

    /** Generic constructor, for two teams with no agents. */
    public DistanceTable(){
        keya=new ArrayList<Agent>();
        keyb=new ArrayList<Agent>();
    }       
    /** Constructs the table based on two collections of agents, so that the distance from one agent to another can be looked up directly.
     * @param keya  the first set of agents; forms the set of hash keys
     * @param keyb  the second set of agents; forms the set of hash values
     */
    public DistanceTable(Collection<Agent> keya,Collection<Agent> keyb){
        this();
        this.keya.addAll(keya);
        this.keyb.addAll(keyb);
        recalculate();
    }
    /** Constructs the table based upon several different teams (or collections of agents). The algorithm will compute the distances between
     * every pair of agents for any team, including the team to itself. Values may be looked up by the object identifier corresponding to any
     * agent.
     * @param teams     any collection of teams
     */
    public DistanceTable(Collection<Team> teams){
        this();
        for(Team t:teams){keya.addAll(t);}
        keyb.addAll(keya);
        recalculate();
    }
    
    
// BEAN PATTERNS
    
    /** Override... says if the table is empty, i.e. if either keySet is null
     * @return  true if empty, otherwise false.
     */
    @Override
    public boolean isEmpty(){return(keya.size()==0||keyb.size()==0);}
    
    
// METHODS    

    /** Recalculates all distances in the table */
    public void recalculate(){
        for(Agent a:keya){
            put(a,new HashMap<Agent,Double>());
            for(Agent b:keyb){
                //if(Double.isNaN(a.distance(b))){System.out.println("nan... ("+a.getX()+","+a.getY()+" and ("+b.getX()+","+b.getY()+")");}
                get(a).put(b,a.loc.distance(b.loc));
            }
        }
    }
    
    /** Removes an agent key.
     * @param agent     the Agent to be removed
     */
    public void outerRemoveAgent(Agent agent){
        keya.remove(agent);
        remove(agent);
    }
    
    /** Remove an agent from all nested HashMaps
     * @param agent     the Agent to be removed
     */
    public void innerRemoveAgent(Agent agent){
        keyb.remove(agent);
        for(Agent a:keya){
            get(a).remove(agent);
        }
    }
    
    /** Removes specified agent pair from the table.
     * @param agents    the pair of Agents to be removed
     */
    public void removeAgents(AgentPair agents){
        outerRemoveAgent(agents.first);
        outerRemoveAgent(agents.second);
        innerRemoveAgent(agents.first);
        innerRemoveAgent(agents.second);
    }
    
    
// BASIC QUERY METHODS
    
    /** Outputs the distance between two particular agents
     * @param a the first agent
     * @param b the second agent
     * @return the distance between the agents */
    public double get(Agent a,Agent b){
        //System.out.println("distance request: Agent "+a+" and Agent "+b);
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
        if(result.first==null){return null;}
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
    public AgentPair minVisible(Agent a,Team tb){
        AgentPair result=new AgentPair();
        for(Agent b:tb){if(a.sees(b)){result.replaceIfLessBy(a,b,get(a,b));}}
        return result;
    }
    
    /** Outputs the maximum distance between an agent and a team
     * @param a the agent
     * @param tb the team
     * @return the maximum value in the distance table */
    public AgentPair maxVisible(Agent a,Team tb){
        AgentPair result=new AgentPair(0);
        for(Agent b:tb){if(a.sees(b)){result.replaceIfMoreBy(a,b,get(a,b));}}
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
