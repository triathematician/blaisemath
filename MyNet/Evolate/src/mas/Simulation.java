/**
 * Simulation.java
 * Created on Dec 9, 2008
 */

package mas;

import java.util.Vector;

/**
 * <p>Represents a simulation of a multi-agent system.</p>
 * @author Elisha Peterson
 */
public abstract class Simulation {
    
    /** Constructs simulation with a single teams. */
    public Simulation(SimStep step, Team team) {
        this.step = step;
        teams = new Vector<Team>();
        teams.add(team);
        controlVars = new ParameterSpace();
        stateVars = new ParameterSpace();
    }
    
    /** Constructs simulation with several teams. */
    public Simulation(SimStep step, Vector<Team> teams) {
        this.step = step;
        this.teams = teams;
        controlVars = new ParameterSpace();
        stateVars = new ParameterSpace();
    }
    

    /** Simulation Parameters. */
    ParameterSpace controlVars;
    
    /** Simulation state space... variables that change during the simulation. */
    ParameterSpace stateVars;

    /** Agents are stored as collections of teams. */
    Vector<Team> teams;
    
    /** Class describing how to perform a single iteration of the sim. */
    SimStep step;
    
    /** Returns list of teams. */
    public Vector<Team> getTeams() { return teams; }
    
    /** Initializes the simulation. */
    public void initialize() {
        controlVars.initialize();
        stateVars.initialize();
        for (Team t : teams) { t.initializeControlVars(); t.initializeStateVars(); }
    }
    
    /** Main simulation algorithm. */
    public void run() {
        initialize();
        while (!isFinished()) {
            preIterate();
            step.iterate(this);
            postIterate();
        }
    }
    
    /** Pre-iteration step. */
    protected void preIterate(){}
    /** Post-iteration step. */
    protected void postIterate(){}
    
    /** Generates stop condition. */
    public boolean isFinished(){ return true; }

}
