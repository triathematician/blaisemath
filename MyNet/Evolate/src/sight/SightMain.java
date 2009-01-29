package sight;

import java.util.Vector;

/**
 * The purpose of this class is to test an evolutionary strategy for specialization
 * of agents within a system.
 * 
 * @author ae3263
 */
public class SightMain {
        
    static int NTARGET = 2;        
    static int NSTEPS = 1000;
    static int NTEAMS = 2;
    static int NPLAYERS = 2;
    static int SIMS_PER_STEP = 100;
    static int PRINT_STEP = 10;
    static int LOG_STEP = 1;
        
    Vector<Team> teams;
    Vector<Integer> captures = null;
    
    /** Generates targets. */
    public float[] getTargets() {
        float[] target = new float[NTARGET];
        for (int i = 0; i < target.length; i++) {  target[i] = (float) Math.random(); }
        return target;
    }
    
    /** Moves teams and determines who gets targets. */
    public void moveTeams(float[] targets) {
        if (captures==null) { captures = new Vector<Integer>(); }
        Vector<Vector<Float>> captureTimes = new Vector<Vector<Float>>();
        for (int i=0; i < teams.size(); i++) { captureTimes.add(teams.get(i).getCaptureTimes()); }
        for (int i=0; i < teams.size(); i++) { captures.add(0); }
        for (int j=0; j < targets.length; j++) {
            float bestTime = Float.MAX_VALUE;
            int bestTeam = -1;
            int bestPlayer = -1;
            for (int i=0; i < teams.size(); i++) {
                if (captureTimes.get(i).get(j) < bestTime) {
                    bestTime = captureTimes.get(i).get(j);
                    bestTeam = i;
                    bestPlayer = teams.get(i).bestCaps.get(j);
                }
            }
            // add the capture to the team with the minimum distance
            if (bestTeam != -1) {
                captures.set(bestTeam, captures.get(bestTeam)+1);
                teams.get(bestTeam).addCapture(bestPlayer);
            }
        }
    }
    
    int iter = 0;
    
    /** Runs the simulation. */
    public void runSim() {
        // Print data on each team and log data
        iter++;
        if ( iter % PRINT_STEP == 0) {
            System.out.println("Iteration "+iter);
            for (int i = 0; i < teams.size(); i++) { teams.get(i).output(i, System.out); }
        }
        if ( iter % LOG_STEP == 0) {
            for (Team t : teams) { t.logData(); }
        }
        // run over several iterations
        captures = null;
        for (int i = 0; i < SIMS_PER_STEP; i++) {
            iterate();
        }
        // Sends teams info to update their fitness scores
        int totCaptures = 0;
        int maxCaptures = 0;
        for (int i = 0; i < captures.size(); i++) { 
            totCaptures += captures.get(i);
            if (captures.get(i) > maxCaptures) { maxCaptures = captures.get(i); }
        }
        for (int i = 0; i < teams.size(); i++) {
            if (totCaptures == 0) {
                // default to giving all agents the same fitness score (0.0f is "average")
 //               teams.get(i).calcFitness(0.0f);
            } else {
                // adjust fitness using how far team is above the average
//                float diff = captures.get(i)-(float)totCaptures/teams.size();
  //              teams.get(i).calcFitness(.1f*diff/(1+.1f*Math.abs(diff)));
            }
        }
        // Tells teams to regenerate (alter parameters based on fitness scores)
        for (Team t : teams) { t.evolve(); }
    }
    
    /** Runs single iteration. */
    public void iterate() {
        // 0. Reset variables
        for(int i = 0; i < teams.size(); i++) { teams.get(i).initRun(); }
        // 1. Generate targets
        float[] targets = getTargets();
        // 2. Determine targets in range
        // 3. Team assignments based on minimum time to capture
        for(int i = 0; i < teams.size(); i++) { teams.get(i).senseTargets(targets); teams.get(i).assignTargets(); }
        // 4. Determines which team gets each target
        moveTeams(targets);
    }
    
    /** Runs the evolving states. */
    public void evolate() {
        // set up teams
        teams = new Vector<Team>();
        for(int i = 0; i < NTEAMS; i++) { teams.add(new Team(NPLAYERS, 0.5f)); }
        teams.get(0).evolMin=0.0f;
        teams.get(0).evolScale=0.0f;
        // run the simulation
        iter = -1;
        for(int i = 0; i < NSTEPS; i++) {
            runSim();
        }
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SightMain sim = new SightMain();
        sim.evolate();
        int steps = sim.teams.get(0).capStat.size();
        for (int i = -1; i < steps; i++) {
            for(Team t: sim.teams) {
                System.out.print(t.tabString(i));
            }
            System.out.print("\n");
        }
    }

}
