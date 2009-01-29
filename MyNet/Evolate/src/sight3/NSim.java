package sight3;

import mas.Simulation;

/**
 * Simulation that has a number of targets. Each team generates a target assignment, then
 * the agents that have the fastest times get the targets.
 * 
 * @author elisha
 */
public class NSim extends Simulation {
    int numTargets;
    int numTeams;
    int teamSize;

    public NSim() { this(2,5,2); }
    public NSim(int ntm,int ntar,int tsz){
        numTargets=ntar;
        numTeams=ntm;
        for (int i = 0; i < numTeams; i++) { teams.add(new NTeam(tsz)); }
    }
    
    float[] targets;

    @Override
    public void initialize() {
        targets = new float[numTargets];
        for (int i = 0; i < targets.length; i++) {targets[i] = (float) Math.random(); }
    }

    public void iterate() {
        // each team computes assignments and generates optimal 
        // then each team
    }
}
