/*
 * TwoLine.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package tasking;

import simulation.Agent;
import java.util.ArrayList;
import goal.Goal;
import scio.coordinate.V2;
import utility.DistanceTable;

/**
 * @author Elisha Peterson
 */
public class AutoTwoClosest extends Autonomy {

    public AutoTwoClosest(){}
        
    /** Performs tasking based on a preset goal.
     * @param team the team to assign tasks to
     * @param goal the goal used for task assignment */
    public void assign(ArrayList<Agent> team,Goal goal,double weight){        
        DistanceTable dist=new DistanceTable(team,goal.getTarget());
        Agent one,two;
        for(Agent p:team){
            V2 bPERP=new V2();
            one=dist.minVisible(p,goal.getTarget()).getSecond();
            dist.get(p).remove(one);
            two=dist.minVisible(p,goal.getTarget()).getSecond();
            try{
                bPERP.setLocation(p.loc.closestOnLine(one.loc,two.loc));
                p.assignTask(null,bPERP,goal,weight);
            }catch(NullPointerException e){}
        }

    }
}
