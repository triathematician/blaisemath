/*
 * Farthest.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package tasking;

import scio.coordinate.V2;
import simulation.Agent;
import valuation.Goal;
import java.util.Vector;
import simulation.Team;
import utility.DistanceTable;

/**
 * Player considers only the farthest enemy.
 * <br><br>
 * @author Elisha Peterson
 */
public class AutoFarthest extends AutonomousTaskGenerator {

    public AutoFarthest(Team target,int type){ super(target,type);}

    @Override
    public V2 generate(Agent agent, DistanceTable table) {
        if(target.size()==1) {
            if(agent.sees(target.firstElement())){
                return target.firstElement().loc;
            } else {
                return new V2();
            }
        } else {
            return table.maxVisible(agent,target).getSecondLoc();
        }
    }
        
//    /** Performs tasking based on a preset goal.
//     * @param team the team to assign tasks to
//     * @param goal the goal used for task assignment */
//    public void assign(Vector<Agent> team,Goal goal,double weight){
//        DistanceTable dist=new DistanceTable(team,goal.getTarget());
//        for(Agent p:team){
//            p.assignTask(null,dist.maxVisible(p,goal.getTarget()).getSecondLoc(),weight);
//        }
//    }
}
