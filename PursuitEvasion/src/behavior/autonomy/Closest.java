/*
 * Closest.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package behavior.autonomy;

import simulation.Agent;
import simulation.Team;
import java.util.ArrayList;
import behavior.Goal;
import utility.DistanceTable;

/**
 * Player only considers the closest enemy.
 * <br><br>
 * @author Elisha Peterson
 */
public class Closest extends Autonomy {

    public Closest(){}
        
    /** Performs tasking based on a preset goal.
     * @param team the team to assign tasks to
     * @param goal the goal used for task assignment */
    public void assign(ArrayList<Agent>  team,Goal goal){
        if(goal.getTarget().size()==1){
            for(Agent p:team){p.assignTask(goal.getTarget().get(0),goal.getType());}
        }
        else{
            DistanceTable dist=new DistanceTable(team,goal.getTarget());
            for(Agent p:team){p.assignTask(dist.min(p,goal.getTarget()).getSecond(),goal.getType());}
        }
    }
}
