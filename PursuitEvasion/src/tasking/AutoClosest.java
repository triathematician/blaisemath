/*
 * Closest.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package tasking;

import simulation.Agent;
import goal.Goal;
import java.util.Vector;
import utility.DistanceTable;

/**
 * Player only considers the closest enemy.
 * <br><br>
 * @author Elisha Peterson
 */
public class AutoClosest extends Autonomy {

    public AutoClosest(){}
        
    /** Performs tasking based on a preset goal.
     * @param team the team to assign tasks to
     * @param goal the goal used for task assignment */
    public void assign(Vector<Agent> team,Goal goal,double weight){
        if(goal.getTarget().size()==1){
            for(Agent p:team){
                if(p.sees(goal.getTarget().get(0))){p.assignTask(null,goal.getTarget().get(0).loc,goal,weight);}
            }
        }
        else{
            DistanceTable dist=new DistanceTable(team,goal.getTarget());
            for(Agent p:team){p.assignTask(null,dist.minVisible(p,goal.getTarget()).getSecondLoc(),goal,weight);}
        }
    }
}
