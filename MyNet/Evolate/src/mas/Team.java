/**
 * Team.java
 * Created on Dec 9, 2008
 */

package mas;

import java.io.PrintStream;
import java.util.Vector;

/**
 * <p>Represents a collection of agents in a multi-agent system.</p>
 * 
 * @author Elisha Peterson
 */
public class Team extends Agent {

    /** The agents on this team. */
    protected Vector<Agent> agents;

    public Team() {
        agents = new Vector<Agent>();
    }

    public Vector<Agent> getAgents() { return agents; }
    
    /** Initializes the team's control variables, and all agent control variables. */
    @Override
    protected void initControlVars() {
        super.initControlVars();
        for(Agent a : agents) { a.initControlVars(); }
    }
        
    /** Tells agents on team to sense their environment. */
    public void gatherInfo(Simulation sim){}
    /** Tells agents on team to sense their environment. */
    public void communicate(Simulation sim){}
    /** Tells agents on team to sense their environment. */
    public void adjustState(Simulation sim){}
    
    /** Prints progress report. */    
    public void progressReport(Simulation sim,PrintStream out) {}
}
