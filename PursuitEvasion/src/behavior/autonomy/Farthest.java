/*
 * Farthest.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package behavior.autonomy;

import simulation.Agent;
import simulation.Team;
import java.util.ArrayList;
import behavior.Goal;
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
    public void assign(ArrayList<Agent> team,Goal goal){
        DistanceTable dist=new DistanceTable(team,goal.getTarget());
        for(Agent p:team){p.assignTask(dist.maxVisible(p,goal.getTarget()).getSecond(),goal.getType());}
    }
}
