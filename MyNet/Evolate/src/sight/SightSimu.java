/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sight;

import java.util.TreeMap;
import java.util.Vector;
import mas.Agent;
import mas.Simulation;
import mas.Team;
import mas.evol.Allele;
import mas.evol.DNA;
import mas.evol.GenePool;

/**
 *
 * @author elisha
 */
public class SightSimu {
    static int ITER = 50000; // number of iterations to perform during each round
    static int NTARGETS = 1; // number of targets
    static int TEAM_SIZE = 1; // number of players per team
    static float STEP = 0.01f; // STEP size for sims with fixed parameters

    public static void main(String[] args) { main1(args); }

    public static void main1(String[] args) { // configuration for computing optimum parameters by brute force
        Team pool1 = getSpecTeams();
        Team pool2 = getStaticTeam(0f,0f,true);
        Vector<Team> teams = new Vector<Team>();
        for (Agent a : pool1.getAgents()) { teams.add((Team)a); }
        for (Agent a : pool2.getAgents()) { teams.add((Team)a); }
        SightSim ss = new SightSim(ITER, teams, NTARGETS);
        ss.setCompetitors(pool1, pool2);
        ss.run();
        for(TreeMap<SightTeam,Integer> h : ss.captureData) {
            System.out.println(h);
        }
    }

    // configuration for round-robin simulation, suitable for game-theoretic analysis
    public static void main2(String[] args) {
        Vector<Team> teams = new Vector<Team>();
        for (Agent a : getSpecTeams().getAgents()) { teams.add((Team)a); } // teams with fixed parameters
        SightSim ss = new SightSim(ITER, teams, NTARGETS);
        ss.run();
        for(TreeMap<SightTeam,Integer> h : ss.captureData) {
            System.out.println(h);
        }
    }

    final static int[] SINGLE = {1,0,0,0,0}; // never evolves

    /** Returns static teams whose parameters AND positions are generated at random, to serve as a baseline for comparison. */
    public static Team getStaticTeam(float f1, float f2, final boolean randomize) {
        final float[] specs = {f1,f2};
        DNA template = new DNA() { @Override public void initialize(){ for (int i = 0; i < TEAM_SIZE; i++) { set ("spec"+i,new Allele.Flt(0.0f,1.0f,specs[i])); } } };
        return new GenePool(SINGLE, 0.0f, 0.0f, template) {
            @Override public float getFitness(Agent agent, Simulation sim) { return ((SightTeam) agent).captures / (float) ((SightSim)sim).captures; }
            @Override public Agent getAgentFrom(DNA dna) { return new SightTeam(dna, randomize); }
        };
    }

    /** Returns group of teams with fixed compositions. */
    public static Team getSpecTeams() {
        Vector<Float[]> specs = new Vector<Float[]>();
        if (TEAM_SIZE == 1) {
            for (float f1=0.0f; f1<1.01f; f1+=STEP) {
                Float[] nf = {f1};
                specs.add(nf);
            }
        } else if (TEAM_SIZE == 2) {
            for (float f1=0.0f; f1<=1.0f; f1+=STEP) {
                for (float f2=f1; f2<=1.0f; f2+=STEP) {
                    Float[] nf = {f1, f2};
                    specs.add(nf);
                }
            }
        }
        return new FixedPool(specs);
    }

    static class FixedPool extends GenePool {
        static DNA template = new DNA() { @Override public void initialize(){ for (int i = 0; i < TEAM_SIZE; i++) { set ("spec"+i,new Allele.Flt(0.0f,1.0f,0.5f)); } } };
        public FixedPool(Vector<Float[]> specs){
            super(SINGLE,0.0f,0.0f,template);
            agents.clear();
            for(Float[] ff : specs) {
                DNA dna = new DNA();
                for(int i = 0; i < ff.length; i++) { dna.set("spec"+i,new Allele.Flt(0.0f,1.0f,ff[i])); }
                agents.add(getAgentFrom(dna));
            }
        }
        @Override public float getFitness(Agent agent, Simulation sim) { return ((SightTeam) agent).captures / (float) ((SightSim)sim).captures; }
        @Override public Agent getAgentFrom(DNA dna) { return new SightTeam(dna, false); }
    }

    public static String arrString(Object[] arr) {
        String result="[";
        for (int i = 0; i < arr.length-1; i++) { result += arr[i] + ", "; }
        result += arr[arr.length-1] +"]";
        return result;
    }
}
