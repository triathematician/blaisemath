/*
 * Closest.java
 * Created on Sep 4, 2007, 2:46:01 PM
 */

package behavior.control;

import simulation.Agent;
import simulation.Team;
import java.util.ArrayList;
import java.util.TreeMap;
import behavior.Goal;
import utility.AgentPair;
import utility.DistanceTable;

/**
 * @author Elisha Peterson
 * <br><br>
 * This class
 */
public class Closest extends Control {

// CONSTRUCTORS

    /** Default constructor */
    public Closest(){}

// METHODS:
   /** Assigns tasks (prey) to n/k pursuers. The algorithm uses the complete table
     * of distances between pursuers and evaders to map the closest
     * pursuer-prey pair. This process is repeated until there are no more
     * pursuers or prey available. If there are leftover pursuers, they are
     * assigned to the closest prey.
     * @param team pursuing team
     * @param goal the goal to work with
     */
    public void assign(ArrayList<Agent> team,Goal goal){
        DistanceTable dist=new DistanceTable(team,goal.getTarget());            
        // assign prey by closest-to-pursuer first
        ArrayList<Agent> ps=new ArrayList<Agent>(team);
        ArrayList<Agent> es=new ArrayList<Agent>(goal.getTarget());
        AgentPair closest;
        while(!(ps.isEmpty()||es.isEmpty())){
            closest=dist.min(ps,es);
            closest.getFirst().assignTask(closest.getSecond(),goal.getType());
            ps.remove(closest.getFirst());
            es.remove(closest.getSecond());
        }
        // assign remaining pursuers to closest prey
        for(Agent p:ps){p.assignTask(dist.minVisible(p,goal.getTarget()).getSecond(),goal.getType());}
    }
}
