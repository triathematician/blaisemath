/*
 * Gradient.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package behavior.autonomy;

import Euclidean.PPoint;
import agent.Agent;
import agent.Team;
import task.Goal;
import utility.DistanceTable;

/**
 * @author Elisha Peterson
 */
public class Gradient extends Autonomy {

    public Gradient(){}
        
    /** Performs tasking based on a preset goal.
     * @param team the team to assign tasks to
     * @param goal the goal used for task assignment */
    public void assign(Team team,Goal goal){        
        int POWER=-1;
        DistanceTable dist=new DistanceTable(team,goal.getTarget());
        for(Agent a:team){
            PPoint dir=new PPoint(0,0);
            for(Agent b:goal.getTarget()){
                PPoint temp=new PPoint(a);
                temp.translate(-b.x,-b.y);
                temp.multiply(Math.pow(dist.get(a,b),POWER-1));
                dir.translate(temp);
            }
            Agent bGRAD=new Agent();
            bGRAD.setPoint(a.translate(dir));
            a.assignTask(bGRAD,goal.isSeek());
        }
    }
}
