/*
 * Farthest.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package behavior.autonomy;

import agent.Agent;
import agent.Team;
import task.Goal;
import utility.DistanceTable;

/**
 * Player considers only the farthest enemy.
 * <br><br>
 * @author Elisha Peterson
 */
public class Farthest extends Autonomy {

    public Farthest(){}
        
    /** Performs tasking based on a preset goal.
     * @param team the team to assign tasks to
     * @param goal the goal used for task assignment */
    public void assign(Team team,Goal goal){
        DistanceTable dist=new DistanceTable(team,goal.getTarget());
        for(Agent p:team){p.assignTask(dist.max(p,goal.getTarget()).getSecond(),goal.getType());}
    }
}
