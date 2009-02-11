/*
 * CenterOfMass.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package tasking;

import scio.coordinate.R2;
import scio.coordinate.V2;
import simulation.Agent;
import simulation.Team;
import utility.DistanceTable;

/**
 * Player moves based on average direction of target team.
 * <br><br>
 * @author Elisha Peterson
 */
public class AutoDirectionCOM extends AutonomousTaskGenerator {

    public AutoDirectionCOM(Team target,int type){super(target,type);}
        
//    /** Performs tasking based on a preset goal.
//     * @param team the team to assign tasks to
//     * @param goal the goal used for task assignment */
//    public void assign(Vector<Agent> team,Goal goal,double weight){
//        V2 bCOM=new V2();
//        bCOM.setLocation(goal.getTarget().getCenterOfMass());
//        for(Agent p:team){p.assignTask(null,bCOM,weight);}
//    }

    @Override    
    public V2 generate(Agent agent, DistanceTable table) {   
        R2 center = new R2(0, 0);
        int visible=0;
        for (Agent a : target.getActiveAgents()) {
            if(agent.sees(a)){
                center.translateBy(a.loc.v.normalized());
                visible++;
            }
        //System.out.println("agent:"+agent.x+"+"+agent.y);
        }
        if(visible==0){return new V2();}
        center.multiplyBy(1.0/visible);
        //System.out.println("center:"+center.x+"+"+center.y);
        return new V2(agent.loc.plus(center),center);
    }
}
