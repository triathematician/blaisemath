/*
 * TwoLine.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package tasking;

import simulation.Agent;
import scio.coordinate.V2;
import simulation.Team;
import utility.DistanceTable;

/**
 * @author Elisha Peterson
 */
public class AutoTwoClosest extends AutonomousTaskGenerator {

    public AutoTwoClosest(Team target,int type){ super(target,type); }
          
    @Override
    public V2 generate(Agent agent, DistanceTable table) {
        return new V2();
    }

//    /** Performs tasking based on a preset goal.
//     * @param team the team to assign tasks to
//     * @param goal the goal used for task assignment */
//    public void assign(Vector<Agent> team,Goal goal,double weight){        
//        Collection<Agent> temp=goal.getTarget();
//        DistanceTable dist=new DistanceTable(team,temp);
//        Agent one,two;
//        for(Agent p:team){
//            V2 bPERP=new V2();
//            one=dist.minVisible(p,temp).getSecond();
//            temp.remove(one);
//            two=dist.minVisible(p,temp).getSecond();
//            temp.add(one);
//            try{
//                bPERP.setLocation(p.loc.closestOnLine(one.loc,two.loc));
//                p.assignTask(null,bPERP,weight);
//            }catch(NullPointerException e){}
//        }
//    }
}
