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
import java.util.HashSet;
import scio.matrix.HashHashMatrix;

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
public class DistanceTable extends HashHashMatrix<Agent,Agent,Double> {

// PROPERTIES
    
    /** The min/max values in the table. */
    double min,max;
    
    /** The current timestep */
    double time = 0.0;
    
    
// CONSTRUCTORS    

    /** Constructs the table based on two collections of agents, so that the distance from one agent to another can be looked up directly.
     * @param keya  the first set of agents; forms the set of hash keys
     * @param keyb  the second set of agents; forms the set of hash values
     */
    public DistanceTable(Collection<Agent> keya,Collection<Agent> keyb){
        super(keya.toArray(),keyb.toArray());
        recalculate(0.0);
    }
    /** Constructs the table based upon several different teams (or collections of agents). The algorithm will compute the distances between
     * every pair of agents for any team, including the team to itself. Values may be looked up by the object identifier corresponding to any
     * agent.
     * @param teams     any collection of teams
     */
    public DistanceTable(Collection<Team> teams){
        super(0,0);
        HashSet<Agent> keys = new HashSet<Agent>();
        for(Team t:teams){keys.addAll(t);}
        super.init(keys.toArray(),keys.toArray());
        recalculate(0.0);
    }
    
    
    // BEAN PATTERNS
    
    /** Override... says if the table is empty, i.e. if either keySet is null
     * @return  true if empty, otherwise false.
     */
    public boolean isEmpty(){
        return getNumCols()==0 || getNumRows()==0;
    }
    
    /** Returns current time. */
    public double getTime() { return time; }

    
    
    
    // METHODS    

    /** Recalculates all distances in the table */
    public void recalculate(double time){
        this.time = time;
        for(Agent a:getRows()){
            for(Agent b:getCols()){
                put(a,b,a.loc.distance(b.loc));
            }
        }
    }
    
    /** Removes single agent from the table. */
    public void removeAgent(Agent a){
        deleteRow(a);
        deleteCol(a);
    }
    
    /** Removes specified agent pair from the table.
     * @param agents the pair of Agents to be removed
     */
    public void removeAgents(AgentPair agents){
        removeAgent(agents.first);
        removeAgent(agents.second);
    }
    
    
    // BASIC QUERY METHODS
    
    /** Returns distance between agent i and evader j
     * @param row ith agent 
     * @param col jth evader
     * @return distance
     */
    @Override
    public Double get(Agent row, Agent col) {
        try {
            return super.get(row, col);
        } catch (NullPointerException e) {
            return Double.POSITIVE_INFINITY;
        }
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
        return (result.first == null) ? null : result;
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

    /** Returns average distance between two teams. */
    public Double average(Collection<Agent> ta, Collection<Agent> tb) {
        double total=0;
        int num=0;
        for (Agent a:ta){
            for(Agent b:tb){
                total+=get(a,b);
                num++;
            }
        }
        return total/num;
    }
    
    
// GLOBAL QUERIES    
    
    /** Outputs the table's minimum value
     * @return minimum distance between the two agent collections and the corresponding agents */
    public AgentPair min(){return min(getRows(),getCols());}
    
    /** Outputs the table's maximum value
     * @return maximum distance between the two agent collections and the corresponding agents */
    public AgentPair max(){return max(getRows(),getCols());}
    
    
// QUERY METHODS: SPECIALIZED MIN/MAX RETURNS
        
    /** Outputs the minimum distance between an agent and a team
     * @param a the agent
     * @param tb the team
     * @return the minimum value in the distance table */
    public AgentPair minVisible(Agent a,Collection<Agent> tb){
        AgentPair result=new AgentPair();
        for(Agent b:tb){if(a.sees(b)){result.replaceIfLessBy(a,b,get(a,b));}}
        return result;
    }
     public AgentPair min(Agent a,Collection<Agent> tb){
        AgentPair result=new AgentPair();
        for(Agent b:tb){result.replaceIfLessBy(a,b,get(a,b));}
        return result;
    }
    
    /** Outputs the maximum distance between an agent and a team
     * @param a the agent
     * @param tb the team
     * @return the maximum value in the distance table */
    public AgentPair maxVisible(Agent a,Collection<Agent> tb){
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
        for(Agent b:getRows()){if(get(a,b)<d){result.add(b);}}
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
