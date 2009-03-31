/**
 * CaptureCondition.java
 * Created on Apr 25, 2008
 */
package metrics;

import analysis.SimulationLog;
import java.util.Vector;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import scio.coordinate.R2;
import simulation.Team;
import utility.AgentPair;
import utility.DistanceTable;

/**
 * This class is used to determine whether two agents should be removed from the game, within
 * a specific capture distance.
 * 
 * @author Elisha Peterson
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CaptureCondition extends Valuation {

    // CONSTANTS
    
    public static final int REMOVEBOTH = 0;
    public static final int REMOVETARGET = 1;
    public static final int AGENTSAFE = 2;
    
    // SIMULATION PARAMETERS
    
    /** Whether to remove both players from the field when this occurs, or just the target, or just the agent. */
    int removal = REMOVEBOTH;
    
    // CONSTRUCTORS

    public CaptureCondition() {   
        vs.setName("Capture Condition");     
    }

    /** Initialize with specified capture distance. */
    public CaptureCondition(Vector<Team> teams, Team owner, Team target, double captureDistance, int removal) {
        super(teams, owner, target, captureDistance);
        vs.setName("Capture Condition");
        this.removal = removal;
    }

    /** Checks to see whether capture has occurred in the given DistanceTable. If it has, captured agents are
     * deactivated and a message is sent to the log indicating capture.
     * @param log the log storing significant events for the simulation
     * @param cap the capture table
     * @param time the simulation time
     * @return vector of locations at which capture has occurred
     */
    public Vector<R2> check(DistanceTable dt, SimulationLog log, CaptureMap cap, double time) {
        Vector<R2> result = new Vector<R2>();
        AgentPair closest = dt.min(vs.owner.getActiveAgents(), vs.target.getActiveAgents());
        while ((closest != null) && (closest.getDistance() < getThreshold())) {
            log.logCaptureEvent(vs.owner, closest.getFirst(), vs.target, closest.getSecond(), "Capture", dt, time);
            result.add(closest.getSecond().loc);
            if (removal == REMOVEBOTH) {
                dt.removeAgents(closest);
                vs.owner.deactivate(closest.getFirst());
                vs.target.deactivate(closest.getSecond());
                cap.logCapture(closest.getFirst(), closest.getSecond(), time);
            } else if (removal == REMOVETARGET) {
                dt.removeAgent(closest.getSecond());
                vs.target.deactivate(closest.getSecond());
                cap.logCapture(closest.getFirst(), closest.getSecond(), time);
            } else {
                dt.removeAgent(closest.getFirst());
                vs.owner.deactivate(closest.getFirst());
                cap.logCapture(closest.getFirst(), closest.getFirst(), time);
            }
            closest = dt.min(vs.owner.getActiveAgents(), vs.target.getActiveAgents());
        }
        return result;
    }
    
    // BEAN PATTERNS
    
    @XmlAttribute(name="removalCode")
    public int getRemoval() { return removal; }
    public void setRemoval(int removal) { this.removal = removal; }
}
