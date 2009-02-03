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
public abstract class Simulation extends Entity {
    
    /** Agents are stored as collections of teams. */
    protected Vector<Team> teams;

    /** Constructs simulation */
    public Simulation() { 
        teams = new Vector<Team>();
        controlVars = new ParameterSpace();
    }

    /** Constructs simulation with a single team. */
    public Simulation(Team team) {
        this();
        if(team!=null){teams.add(team);}
    }
    
    /** Constructs simulation with several teams. */
    public Simulation(Vector<Team> teams) {
        this();
        this.teams = teams;
    }
    
    /** Returns list of teams. */
    public Vector<Team> getTeams() { return teams; }
    public void setTeams(Vector<Team> teams) { this.teams = teams; }

    @Override
    protected void initControlVars() {
        super.initControlVars();
        for (Team t : teams) { t.initControlVars(); }
    }
    
    /** Main simulation algorithm. */
   public void run() {
        initialize();
        while (!isFinished()) {
            preIterate();
            iterate();
            postIterate();
        }
    }

    /** Initializes the simulation. */
    public void initialize() { initControlVars(); }
    /** Pre-iteration step. */
    protected void preIterate(){}
    /** Iterates the simulation. */
    public void iterate(){
        for(Team t : getTeams()) { t.gatherInfo(this); }
        for(Team t : getTeams()) { t.communicate(this); }
        for(Team t : getTeams()) { t.adjustState(this); }
    }
    /** Post-iteration step. */
    protected void postIterate(){
        for(Team t:getTeams()){ t.progressReport(this,System.out); }
    }    
    /** Generates stop condition. */
    public boolean isFinished(){ return true; }

}
