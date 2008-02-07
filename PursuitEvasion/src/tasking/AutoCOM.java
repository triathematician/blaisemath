/*
 * CenterOfMass.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package tasking;

import simulation.Agent;
import goal.Goal;
import java.util.Vector;
import scio.coordinate.V2;

/**
 * Player seeks/flees the center-of-mass of the enemy team.
 * <br><br>
 * @author Elisha Peterson
 */
public class AutoCOM extends Autonomy {

    public AutoCOM(){}
        
    /** Performs tasking based on a preset goal.
     * @param team the team to assign tasks to
     * @param goal the goal used for task assignment */
    public void assign(Vector<Agent> team,Goal goal,double weight){
        V2 bCOM=new V2();
        bCOM.setLocation(goal.getTarget().getCenterOfMass());
        for(Agent p:team){p.assignTask(null,bCOM,goal,weight);}
    }
}
