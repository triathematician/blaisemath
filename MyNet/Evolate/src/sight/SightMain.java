/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sight;

import java.util.Vector;
import mas.Agent;
import mas.ParameterSpace;
import mas.Simulation;
import mas.Team;
import mas.evol.Allele;
import mas.evol.DNA;
import mas.evol.EvolSim;
import mas.evol.GenePool;

/**
 *
 * @author elisha
 */
public class SightMain {

    static int NSTEPS = 100;
    static int ITER = 100000;
    static int TEAM_SIZE = 2;
    static int NTARGETS = 2;
    static int[] NUM_TEAMS = {1,1,0,0,3};
    final static float ERROR = 0.05f;
    final static float POINTPROB = 1.0f;

    /** Used as template for DNA construction. */
    static DNA TEMPLATE = new DNA() { @Override public void initialize(){ for (int i = 0; i < TEAM_SIZE; i++) { set ("spec"+i,new Allele.Flt(0.0f,1.0f,0.5f)); } } };

    public static void main(String[] args) {
        GenePool pool = new GenePool(NUM_TEAMS, POINTPROB, ERROR, TEMPLATE){
            @Override public float getFitness(Agent agent, Simulation sim) { return ((SightTeam) agent).captures / (float) ((SightSim)sim).captures; }
            @Override public Agent getAgentFrom(DNA dna) { return new SightTeam(dna); }
        };
        Vector<Team> teams = new Vector<Team>();
        for (Agent a : pool.getAgents()) { teams.add((Team)a); }
        new EvolSim(NSTEPS, pool, new SightSim(ITER,teams, NTARGETS)).run();
        pool.printLog(System.out);
        pool.printAgentList(System.out);
    }

    public static String arrString(Object[] arr) {
        String result="[";
        for (int i = 0; i < arr.length-1; i++) { result += arr[i] + ", "; }
        result += arr[arr.length-1] +"]";
        return result;
    }
}
