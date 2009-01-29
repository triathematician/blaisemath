/**
 * EvolStep.java
 * Created on Dec 9, 2008
 */

package mas.evol;

import java.util.Vector;
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
    public EvolSim(int nSteps) { this(nSteps, null); }
    /** Initializes with a set simulation. */
    public EvolSim(int nSteps, mas.Simulation sim2) { super(nSteps); this.sim2 = sim2; }
    
    /** Initializes the simulation. */
    public void inititialize(){}

    /** Runs an iteration of the simulation. */
    @Override
    public void iterate() {
        sim2.initialize();
        if(sim2 != null) { sim2.run(); }
        GenePool gp;
        for (Team t : getTeams()) {
            gp = (GenePool) t;
            gp.assignFitness(sim2);
            gp.evolve();
        }
    }
}
