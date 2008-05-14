/**
 * CaptureCondition.java
 * Created on Apr 25, 2008
 */

package metrics;

import analysis.DataLog;
import java.util.Vector;
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
public class CaptureCondition extends Valuation {
    
    /** Initialize with specified capture distance. */    
    public CaptureCondition(Team owner,Team target,double captureDistance){
        super(owner,target,captureDistance);
    }
    
    /** Checks to see whether capture has occurred in the given DistanceTable. If it has, captured agents are
     * deactivated and a message is sent to the log indicating capture.
     * @param log the log storing significant events for the simulation
     * @return vector of locations at which capture has occurred
     */
    public Vector<R2> check(DistanceTable dt,DataLog log,double time){        
        Vector<R2> result=new Vector<R2>();
        AgentPair closest=dt.min(owner.getActiveAgents(),target.getActiveAgents());
        while((closest != null) && (closest.getDistance() < getThreshold())){
            log.logEvent(owner,closest.getFirst(),target,closest.getSecond(),"Capture",time);
            result.add(closest.getFirst().loc.plus(closest.getSecond().loc).multipliedBy(0.5));
            dt.removeAgents(closest);
            owner.deactivate(closest.getFirst());
            owner.deactivate(closest.getSecond());
            closest=dt.min(owner.getActiveAgents(),target.getActiveAgents());
        }
        return result;
    }
}
