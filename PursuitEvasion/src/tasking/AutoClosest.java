/*
 * Closest.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package tasking;

import simulation.Agent;
import java.util.Vector;
import scio.coordinate.V2;
import simulation.Team;
import utility.DistanceTable;

/**
 * Player only considers the closest enemy.
 * <br><br>
 * @author Elisha Peterson
 */
public class AutoClosest extends AutonomousTaskGenerator {

    public AutoClosest(Team target,int type){ super(target,type);}
        
//    /** Performs tasking based on a preset goal.
//     * @param team the team to assign tasks to
//     * @param goal the goal used for task assignment */
//    public void assign(Vector<Agent> team,Goal goal,double weight){
//        if(goal.getTarget().size()==1){
//            for(Agent p:team){
//                if(p.sees(goal.getTarget().get(0))){p.assignTask(null,goal.getTarget().get(0).loc,weight);}
//            }
//        }
//        else{
//            DistanceTable dist=new DistanceTable(team,goal.getTarget());
//            for(Agent p:team){p.assignTask(null,dist.minVisible(p,goal.getTarget()).getSecondLoc(),weight);}
//        }
//    }

    @Override
    public V2 generate(Agent agent, DistanceTable table) {
        if(target.size()==1) {
            if(agent.sees(target.firstElement())){
                return target.firstElement().loc;
            } else {
                return null;
            }
        } else {
            return table.minVisible(agent,target).getSecondLoc();
        }
    }
}
