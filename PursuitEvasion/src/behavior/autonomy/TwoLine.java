/*
 * TwoLine.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package behavior.autonomy;

import agent.Agent;
import agent.Team;
import task.Goal;
import utility.DistanceTable;

/**
 * @author Elisha Peterson
 */
public class TwoLine extends Autonomy {

    public TwoLine(){}
        
    /** Performs tasking based on a preset goal.
     * @param team the team to assign tasks to
     * @param goal the goal used for task assignment */
    public void assign(Team team,Goal goal){        
        DistanceTable dist=new DistanceTable(team,goal.getTarget());
        Agent one,two;
        for(Agent p:team){
            Agent bPERP=new Agent();
            one=dist.min(p,goal.getTarget()).getSecond();dist.get(p).remove(one);
            bPERP.setPoint(p.closestOnLine(one,dist.min(p,goal.getTarget()).getSecond()));
            p.assignTask(bPERP,goal.isSeek());
        }

    }
}
