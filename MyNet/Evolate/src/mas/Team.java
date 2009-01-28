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
public abstract class Team extends Agent {

    /** The agents on this team. */
    protected Vector<Agent> agents;

    
    /** Initializes the team's control variables, and all agent control variables. */
    @Override
    protected void initializeControlVars() {
        super.initializeControlVars();
        for(Agent a : agents) { a.initializeControlVars(); }
    }
    
    /** Initializes the team's state variables, and all agent state variables. */
    @Override
    protected void initializeStateVars() {
        super.initializeStateVars();
        for(Agent a : agents) { a.initializeStateVars(); }
    }
    
    /** Tells agents on team to sense their environment. */
    abstract public void gatherInfo(Simulation sim);
    /** Tells agents on team to sense their environment. */
    abstract public void communicate(Simulation sim);
    /** Tells agents on team to sense their environment. */
    abstract public void adjustState(Simulation sim);
    
    /** Prints progress report. */    
    public void progressReport(mas.Simulation sim,PrintStream out) {}
}
