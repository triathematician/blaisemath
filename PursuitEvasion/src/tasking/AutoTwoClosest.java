/*
 * TwoLine.java
 * Created on Sep 4, 2007, 2:17:30 PM
 */

package tasking;

import java.util.Comparator;
import java.util.TreeSet;
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
    public V2 generate(final Agent agent, final DistanceTable table) {
        TreeSet<Agent> closest = new TreeSet<Agent> (
            new Comparator<Agent>() {
                public int compare(Agent o1, Agent o2) {
                    return (int) Math.signum(table.get(agent, o1) - table.get(agent, o2));
                }
        });
        for(Agent a : target) {
            if (agent.sees(a)) { closest.add(a); }
        }
        try {
            V2 result = new V2(agent.loc.closestOnLine(closest.pollFirst().loc,closest.pollFirst().loc));
            return result;
        } catch (Exception e) {
            if(target.size()==1) {
                if(agent.sees(target.firstElement())){
                    return target.firstElement().loc;
                } else {
                    return null;
                }
            }
        }
        return agent.loc;
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
