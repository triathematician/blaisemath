/*
 * Farthest.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package tasking;

import simulation.Agent;
import goal.Goal;
import java.util.Vector;
import utility.DistanceTable;

/**
 * Player considers only the farthest enemy.
 * <br><br>
 * @author Elisha Peterson
 */
public class AutoFarthest extends Autonomy {

    public AutoFarthest(){}
        
    /** Performs tasking based on a preset goal.
     * @param team the team to assign tasks to
     * @param goal the goal used for task assignment */
    public void assign(Vector<Agent> team,Goal goal,double weight){
        DistanceTable dist=new DistanceTable(team,goal.getTarget());
        for(Agent p:team){
            p.assignTask(null,dist.maxVisible(p,goal.getTarget()).getSecondLoc(),goal,weight);
        }
    }
}
