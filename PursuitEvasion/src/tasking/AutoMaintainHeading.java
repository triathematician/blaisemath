/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * AutoMaintainHeading.java
 * Created on Jan 26, 2009
 */

package tasking;

import scio.coordinate.V2;
import simulation.Agent;
import simulation.Team;
import utility.DistanceTable;

/**
 * Keeps agent on current heading.
 * @author Elisha Peterson
 */
public class AutoMaintainHeading extends AutonomousTaskGenerator {

    public AutoMaintainHeading(Team target,int type){ super(target,type);}
        
    @Override
    public V2 generate(Agent agent, DistanceTable table) {
        return null;
    }
}
