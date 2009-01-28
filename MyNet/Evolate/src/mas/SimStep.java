/**
 * SimStep.java
 * Created on Dec 9, 2008
 */

package mas;

import java.util.Vector;

/**
 * <p>Represents a single simulation step within a multi-agent system. By default,
 * a step involves three actions performed by teams simultaneously:
 * <ul>
 * <li>Agents gather information about their environment.
 * <li>Agents communicate with their teammates.
 * <li>Agents act on this information (adjusting their current state).
 * </ul>
 * </p>
 * @author Elisha Peterson
 */
public class SimStep {

    /** Determines process of iteration.
     * @param sim the underlying simulation
     * @param controlVars simulation control variables
     * @param teams teams in the simulation
     */
    public void iterate(Simulation sim) {
        for(Team t : sim.getTeams()) { t.gatherInfo(sim); }
        for(Team t : sim.getTeams()) { t.communicate(sim); }
        for(Team t : sim.getTeams()) { t.adjustState(sim); }
    }

}
