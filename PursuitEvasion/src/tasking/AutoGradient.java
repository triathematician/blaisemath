/*
 * Gradient.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package tasking;

import simulation.Agent;
import valuation.Goal;
import java.util.Vector;
import scio.coordinate.R2;
import scio.coordinate.V2;
import simulation.Team;
import utility.DistanceTable;

/**
 * Player considers the entire enemy team, following the gradient to maximize or minimize
 * the sum of distances.
 * <br><br>
 * @author Elisha Peterson
 */
public class AutoGradient extends AutonomousTaskGenerator {

    public AutoGradient(Team target,int type){ super(target,type); }

    @Override
    public V2 generate(Agent agent, DistanceTable table) {
        int POWER = -1;
        R2 dir = new R2();
        for(Agent b:target){
            if(agent.sees(b)){
                dir.translate(new R2(b.loc.x-agent.loc.x,b.loc.y-agent.loc.y).multipliedBy(Math.pow(agent.loc.distance(b.loc),POWER-1)));
            }
        }
        return new V2(agent.loc.plus(dir));
    }
    
//    /** Performs tasking based on a preset goal.
//     * @param team the team to assign tasks to
//     * @param goal the goal used for task assignment */
//    public void assign(Vector<Agent> team,Goal goal,double weight){
//        int POWER=-1;
//        for(Agent a:team){
//            R2 dir=new R2();
//            for(Agent b:goal.getTarget()){
//                if(a.sees(b)){
//                    dir.translate(new R2(b.loc.x-a.loc.x,b.loc.y-a.loc.y).multipliedBy(Math.pow(a.loc.distance(b.loc),POWER-1)));
//                }
//            }
//            a.assignTask(null,new V2(a.loc.plus(dir)),weight);
//        }
//    }    
//    /** Performs tasking based on a preset goal.
//     * @param a the agent to assign tasks to
//     * @param goal the goal used for task assignment */
//    public void assign(Agent a,Goal goal,double weight){
//        int POWER=-1;
//        R2 dir=new R2();
//        for(Agent b:goal.getTarget()){
//            if(a.sees(goal.getTarget().get(0))){
//                dir.translate(new R2(b.loc.x-a.loc.x,b.loc.y-a.loc.y).multipliedBy(Math.pow(a.loc.distance(b.loc),POWER-1)));
//            }
//        }
//        a.assignTask(null,new V2(a.loc.plus(dir)),weight);
//    }
}
