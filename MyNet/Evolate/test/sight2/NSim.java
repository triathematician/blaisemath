package sight2;

import java.util.Vector;
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
    
    Vector<Integer> captures = null;

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

    @Override
    public void iterate() {
        super.iterate();
        moveTeams();
    }

    /** Determines which team gets the capture. */
    public void moveTeams() {
        if (captures==null) { captures = new Vector<Integer>(); }
        Vector<Vector<Float>> captureTimes = new Vector<Vector<Float>>();
        for (int i=0; i < teams.size(); i++) { captureTimes.add(((NTeam)teams.get(i)).getCaptureTimes()); }
        for (int i=0; i < teams.size(); i++) { captures.add(0); }
        for (int j=0; j < targets.length; j++) {
            float bestTime = Float.MAX_VALUE;
            int bestTeam = -1;
            int bestPlayer = -1;
            for (int i=0; i < teams.size(); i++) {
                if (captureTimes.get(i).get(j) < bestTime) {
                    bestTime = captureTimes.get(i).get(j);
                    bestTeam = i;
                    bestPlayer = ((NTeam)teams.get(i)).bestCaps.get(j);
                }
            }
            // add the capture to the team with the minimum distance
            if (bestTeam != -1) {
                captures.set(bestTeam, captures.get(bestTeam)+1);
                ((NTeam)teams.get(bestTeam)).addCapture(bestPlayer);
            }
        }
    }
}
