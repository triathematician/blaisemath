/*
 * Closest.java
 * Created on Sep 4, 2007, 2:46:01 PM
 */

package tasking;

import java.util.Collection;
import simulation.Agent;
import java.util.Vector;
import simulation.Team;
import utility.AgentPair;
import utility.DistanceTable;

/**
 * @author Elisha Peterson
 * <br><br>
 * This class
 */
public class ControlClosest extends TaskGenerator {

    public ControlClosest(Team target,int type){ super(target,type); }
    
   /** Assigns tasks (prey) to n/k pursuers. The algorithm uses the complete table
     * of distances between pursuers and evaders to map the closest
     * pursuer-prey pair. This process is repeated until there are no more
     * pursuers or prey available. If there are leftover pursuers, they are
     * assigned to the closest prey.
     * @param team pursuing team
     */
    @Override
    public void generate(Collection<Agent> team, DistanceTable table, double priority) {
        DistanceTable dist=new DistanceTable(team,target.agents);            
        // assign prey by closest-to-pursuer first
        Vector<Agent> ps=new Vector<Agent>(team);
        Vector<Agent> es=new Vector<Agent>(target.getActiveAgents());
        AgentPair closest;
        do{        
            closest=dist.min(ps,es);
            if(closest!=null){
                closest.getFirst().assign(new Task(this,closest.getSecondLoc(),type,priority));
                ps.remove(closest.getFirst());
                es.remove(closest.getSecond());
            }
        }while(!ps.isEmpty()&&!es.isEmpty()&&closest!=null);
        // assign remaining pursuers to closest prey
        for(Agent p:ps){
            p.assign(new Task(this,dist.min(p,target.getActiveAgents()).getSecondLoc(),type,priority));
        }
    }
}
