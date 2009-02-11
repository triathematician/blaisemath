/**
 * EvolStep.java
 * Created on Dec 9, 2008
 */

package mas.evol;

import java.util.Vector;
import mas.Agent;
import mas.Simulation;
import mas.Team;

/** 
 * <p>Represents an evolutionary program. Parameters are adjusted based on fitness scores, and a GenePool.
 * An underlying simulation is run multiple times. The outcome of subsidiary simulations is used to generate
 * fitness scores, and the next generations of each team. The sim step involves creating and running a simulation,
 * assessing the fitness of the result, and using that to regenerate each team.
 * </p>
 * @author Elisha Peterson
 */
public class EvolSim extends mas.NSimulation {
    
    /** Simulation to run at each step. */
    public mas.Simulation sim2;
    
    /** Initializes with no simulation. */
    public EvolSim(int nSteps) { super(nSteps); this.sim2 = null; }
    public EvolSim(int nSteps, Team team) { super(nSteps,team); }
    public EvolSim(int nSteps, Vector<Team> teams) { super(nSteps,teams); }
    /** Initializes with a set simulation. */
    public EvolSim(int nSteps, mas.Simulation sim2) { super(nSteps); this.sim2 = sim2; }
    public EvolSim(int nSteps, Team team, mas.Simulation sim2) { super(nSteps,team); this.sim2 = sim2;}
    public EvolSim(int nSteps, Vector<Team> teams, mas.Simulation sim2) { super(nSteps,teams); this.sim2 = sim2; }

    public Simulation getSim2() { return sim2; }
    public void setSim2(Simulation sim2) { this.sim2 = sim2; }

    /** Runs an iteration of the simulation. */
    @Override
    public void iterate() {
        if(sim2 != null) { sim2.run(); }
        super.iterate();
    }

    @Override
    public void postIterate() {
        Vector<Team> oldTeams = new Vector<Team>();
        Vector<Team> newTeams = new Vector<Team>();
        for (Team t : getTeams()) {
            if (t instanceof GenePool) {
                for (Agent a : t.getAgents()) { if (a instanceof Team) { oldTeams.add((Team)a); } }
                ((GenePool)t).assignFitness(sim2);
                ((GenePool)t).evolve();
                for (Agent a: t.getAgents()) {
                    if (a instanceof Team) { newTeams.add((Team)a); }
                }
            } 
        }
        super.postIterate();
        if (sim2 != null) { 
            sim2.replaceTeams(oldTeams,newTeams);
        }
    }
}
