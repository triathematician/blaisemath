/*
 * Cooperation.java
 * Created on Aug 28, 2007, 10:27:51 AM
 */

package tasking;

import simulation.Agent;
import simulation.Team;
import tasking.AutoGradient;
import java.util.TreeMap;
import metrics.Goal;
import scio.coordinate.R2;
import scio.coordinate.V2;
import utility.DistanceTable;

/**
 * @author Elisha Peterson
 * <br><br>
 * This class is intended to contain support for <i>cooperative</i> tasking.
 */
public class Cooperation {

    public Cooperation() {
    }

    // TODO make this compatible with current tasking/DistanceTable implementation
    /** Initial communication algorithm:
     * 1) Agents rank desired prey (within view)... perhaps by distanceTo?
     * 2) Agents communicate intentions with other nearby agents.
     * 3) Agents request changes to current situations.
     * 4) Matching swaps are agreed upon and enacted.
     * @param pursuers pursuing team
     * @param prey evading team
     * @param currentAssignment the currently assigned pairings of predators to prey
     * @return treemap which pairs the pursuers to prey (by number in each team)
     */
    public static TreeMap<Integer,Integer> communicationAssignment(Team pursuers,Team prey,
            TreeMap<Integer,Integer> currentAssignment){
        //double r1=pursuers.getCommDistance();
        //double r2=pursuers.getSensorDistance();
        /** Determine nearby prey and possible collaborators. */
        //for(int i=0;i<pursuers.size();i++){
        //    pursuers.get(i).rankTask(currentAssignment.get(i),prey);
        //    pursuers.get(i).assignCollaborators(pursuers);
        //}
        /** Make requests for change to other collaborators. */
        //for(Agent a:pursuers){a.requestChanges(pursuers);}
        /** Send out agreement responses, and make the necessary change to current assignment. */
        //for(Agent a:pursuers){a.acknowledgeChanges(pursuers);}
        /** Finally, request current targets from each pursuer. */
        TreeMap<Integer,Integer> result=new TreeMap<Integer,Integer>();
        /** Populate result table. */
        return result;
    }
    
    
    // TODO adjust return value here!
    // TODO fix the logic here! it's broken!
    /** Returns directions based on evasion in the inverted plane. The given
     * agent will attempt to avoid both the enemy object, when centered at 0, and
     * its teammates in the inverted plane. The result on the normal value is
     * chasing down the prey while simultaneously avoiding teammates... this translates
     * to a surround-like myBehavior which is completely autonomous, when all agents
     * agree to perform the same myBehavior.
     * @param ta team to assign tasks to
     * @param tb opposing team
     * @return treemap which pairs the pursuers to prey (by number in each team)
     */
    public static TreeMap<Agent,R2> assignInvertedPlaneEvade(Team ta,Team tb,double weight){
        TreeMap<Agent,R2> result=new TreeMap<Agent,R2>();
        DistanceTable dist=new DistanceTable(ta.agents,tb.agents);
        for(Agent a:ta.agents){
            // TODO smart assignment of cat!
            V2 cat=tb.agents.get(0).loc;
            // invert all in circle of radius 1 centered about the cat
            for(Agent agent:ta.agents){
                agent.loc.translate(-cat.x,-cat.y);
                agent.loc.invertInCircleOfRadius(1);
            }
            // go through each team member and perform combo evasion
            // assign goal positions
            for(Agent aa:ta.agents){
                Team temp=new Team(ta);
                temp.remove(aa);
                aa.loc.v=new R2(aa.loc);
                // TODO make this work!!!
                // new AutoGradient().assign(aa,new Goal(),weight);
            }
            
            // now perform inversion again... both points
            for(Agent aa:ta.agents){
                aa.loc.invertInCircleOfRadius(1);aa.loc.translate(cat.x,cat.y);
                aa.loc.v.invertInCircleOfRadius(1);aa.loc.v.translate(cat.x,cat.y);
            }
        }
        return result;
    }
}
