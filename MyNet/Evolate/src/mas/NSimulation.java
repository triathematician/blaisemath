/**
 * NSimulation.java
 * Created on Dec 10, 2008
 */

package mas;

import java.util.Vector;

/**
 * <p> Simulation that runs for a fixed number of steps.</p>
 * @author Elisha Peterson
 */
public class NSimulation extends Simulation {

    public int nSteps;    
    public int curStep;
    
    /** Constructs simulation with a single team. */
    public NSimulation(SimStep step, int nSteps, Team team) {
        super(step, team);
        this.nSteps = nSteps;
    }
    
    /** Constructs simulation with several teams. */
    public NSimulation(SimStep step, int nSteps, Vector<Team> teams) {
        super(step,teams);
        this.nSteps = nSteps;
    }

    @Override
    public void initialize() { super.initialize(); curStep = 0; }
    @Override
    protected void postIterate() { curStep++; }
    @Override
    public boolean isFinished() { return curStep >= nSteps; }
    
    /** Prints current step. */
    @Override
    public String toString(){ return "Simulation step "+curStep+"/"+nSteps; }
}
