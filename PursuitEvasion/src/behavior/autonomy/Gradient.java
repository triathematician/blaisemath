/*
 * Gradient.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package behavior.autonomy;

import Euclidean.PPoint;
import Euclidean.PVector;
import simulation.Agent;
import simulation.Team;
import java.util.ArrayList;
import behavior.Goal;
import utility.DistanceTable;

/**
 * Player considers the entire enemy team, following the gradient to maximize or minimize
 * the sum of distances.
 * <br><br>
 * @author Elisha Peterson
 */
public class Gradient extends Autonomy {
    
    public Gradient(){}
    
    /** Performs tasking based on a preset goal.
     * @param team the team to assign tasks to
     * @param goal the goal used for task assignment */
    public void assign(ArrayList<Agent> team,Goal goal){
        int POWER=-1;
        for(Agent a:team){
            PPoint dir=new PPoint();
            for(Agent b:goal.getTarget()){
                if(a.sees(b)){
                    dir.translate(new PPoint(b.x-a.x,b.y-a.y).multiply(Math.pow(a.distanceTo(b),POWER-1)));
                }
            }
            a.assignTask(new PVector(a.plus(dir)),goal.getType());
        }
    }    
    /** Performs tasking based on a preset goal.
     * @param a the agent to assign tasks to
     * @param goal the goal used for task assignment */
    public void assign(Agent a,Goal goal){
        int POWER=-1;
        PPoint dir=new PPoint();
        for(Agent b:goal.getTarget()){
            if(a.sees(goal.getTarget().get(0))){
                dir.translate(new PPoint(b.x-a.x,b.y-a.y).multiply(Math.pow(a.distanceTo(b),POWER-1)));
            }
        }
        a.assignTask(new PVector(a.plus(dir)),goal.getType());
    }
}
