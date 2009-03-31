/**
 * CaptureMap.java
 * Created on Mar 31, 2009
 */

package metrics;

import java.util.Collection;
import java.util.HashMap;
import scio.matrix.HashMatrix;
import simulation.Agent;

/**
 *
 * @author Elisha Peterson
 */
public class CaptureMap extends HashMatrix<Agent,Integer> {

    /** Stores time since last capture. */
    protected HashMap<Agent,Double> capTime;

    /** Construct map. */
    public CaptureMap(Agent[] agents) {
        super(agents);
        super.fillMatrix(0);
        capTime = new HashMap<Agent,Double>();
        for (int i=0; i<agents.length; i++) {
            capTime.put(agents[i], -Double.MAX_VALUE);
        }
    }

    /** Adds capture to the table. */
    public void logCapture(Agent agent, Agent target, double time) {
        put(agent, target, (Integer) get(agent,target) + 1);
        capTime.put(agent, time);
    }

    /** Return number of captures made by subsets of agents. */
    public int getCaptures(Collection<Agent> agents, Collection<Agent> targets) {
        int tot = 0;
        for (Agent a : agents) {
            for (Agent t: targets) {
                tot += get(a, t);
            }
        }
        return tot;
    }

    /** Return number of agents that have reached safety. */
    public int getSafe(Collection<Agent> agents) {
        int tot = 0;
        for (Agent a : agents) {
            tot += get(a, a);
        }
        return tot;
    }

    /** Returns minimum time since last capture. */
    public double getTimeSinceCapture(Collection<Agent> agents, double time) {
        double minTime = Double.MAX_VALUE;
        for (Agent a : agents) {
            minTime = Math.min(minTime, time - capTime.get(a));
        }
        return minTime;
    }
}
