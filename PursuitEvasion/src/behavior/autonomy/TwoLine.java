/*
 * TwoLine.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package behavior.autonomy;

import simulation.Agent;
import simulation.Team;
import java.util.ArrayList;
import behavior.Goal;
import utility.DistanceTable;

/**
 * @author Elisha Peterson
 */
public class TwoLine extends Autonomy {

    public TwoLine(){}
        
    /** Performs tasking based on a preset goal.
     * @param team the team to assign tasks to
     * @param goal the goal used for task assignment */
    public void assign(ArrayList<Agent> team,Goal goal){        
        DistanceTable dist=new DistanceTable(team,goal.getTarget());
        Agent one,two;
        for(Agent p:team){
            Agent bPERP=new Agent();
            one=dist.minVisible(p,goal.getTarget()).getSecond();dist.get(p).remove(one);
            two=dist.minVisible(p,goal.getTarget()).getSecond();
            bPERP.setPoint(p.closestOnLine(one,two));
            p.assignTask(bPERP,goal.getType());
        }

    }
}
