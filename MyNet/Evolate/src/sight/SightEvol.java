/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sight;

import java.util.Vector;
import mas.Agent;
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
public class SightEvol {

    static int NSTEPS = 50; // total number of evolutionary steps to perform

    static int ITER = 50000; // number of iterations to perform during each round
    static int NTARGETS = 1; // number of targets
    static int TEAM_SIZE = 1; // number of players per team

    static int[] NUM_TEAMS = {2,2,2,2,2}; // composition of team evolution rules
    static int TOT = 10; // sum of # of teams
    final static float ERROR = 0.002f; // mutation error in each team
    final static float POINTPROB = 1f; // probability of point mutation for each parameter

    /** Used as template for DNA construction. */
    static DNA TEMPLATE = new DNA() { @Override public void initialize(){ for (int i = 0; i < TEAM_SIZE; i++) { set ("spec"+i,new Allele.Flt(0.0f,1.0f,0.5f)); } } };

    public static void main(String[] args) { main1(args); }

    /** represents evolutionary algorithm. */
    public static void main1(String[] args) { // evolutionary strategy against random opponents
        GenePool pool1 = getEvolPool();
        Team pool2 = getStaticTeam(.5f,.5f,true);
        Vector<Team> teams = new Vector<Team>();
        for (Agent a : pool1.getAgents()) { teams.add((Team)a); }
        for (Agent a : pool2.getAgents()) { teams.add((Team)a); }
        SightSim ss = new SightSim(ITER, teams, NTARGETS);
        ss.setCompetitors(pool1, pool2);
        new EvolSim(NSTEPS, pool1, ss).run();
        pool1.printLog(System.out);
        pool1.printAgentList(System.out);
    }

    /** represents co-evolutionary algorithm. */
    public static void main2(String[] args) {
        GenePool pool1 = getEvolPool();
        GenePool pool2 = getEvolPool();
        Vector<Team> pools = new Vector<Team>();
        pools.add(pool1);
        pools.add(pool2);
        Vector<Team> teams = new Vector<Team>();
        for (Agent a : pool1.getAgents()) { teams.add((Team)a); }
        for (Agent a : pool2.getAgents()) { teams.add((Team)a); }
        SightSim ss = new SightSim(ITER, teams, NTARGETS);
        ss.setCompetitors(pool1, pool2);
        new EvolSim(NSTEPS, pools, ss).run();
        pool1.printLog(System.out);
        pool2.printLog(System.out);
    }

    /** Returns evolving pool of teams. */
    public static GenePool getEvolPool() {
        return new GenePool(NUM_TEAMS, POINTPROB, ERROR, TEMPLATE){
            @Override public float getFitness(Agent agent, Simulation sim) { return TOT * ((SightTeam) agent).captures / (float) ((SightSim)sim).captures; }
            @Override public Agent getAgentFrom(DNA dna) { return new SightTeam(dna, false); }
        };
    }

    final static int[] SINGLE = {1,0,0,0,0}; // never evolves

    /** Returns static teams whose parameters AND positions are generated at random, to serve as a baseline for comparison. */
    public static Team getStaticTeam(float f1, float f2, final boolean randomize) {
        final float[] specs = {f1,f2};
        DNA template = new DNA() { @Override public void initialize(){ for (int i = 0; i < TEAM_SIZE; i++) { set ("spec"+i,new Allele.Flt(0.0f,1.0f,specs[i])); } } };
        return new GenePool(SINGLE, 0.0f, 0.0f, template) {
            @Override public float getFitness(Agent agent, Simulation sim) { return TOT * ((SightTeam) agent).captures / (float) ((SightSim)sim).captures; }
            @Override public Agent getAgentFrom(DNA dna) { return new SightTeam(dna, randomize); }
        };
    }

    /** Returns group of teams with fixed compositions. */
    public static Team getSpecTeams() {
        Vector<Float[]> specs = new Vector<Float[]>();
        for (float f1=0.0f; f1<=1.0f; f1+=0.2f) {
            for (float f2=0.0f; f2<=1.0f; f2+=0.2f) {
                Float[] nf = {f1, f2};
                specs.add(nf);
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
        @Override public float getFitness(Agent agent, Simulation sim) { return TOT * ((SightTeam) agent).captures / (float) ((SightSim)sim).captures; }
        @Override public Agent getAgentFrom(DNA dna) { return new SightTeam(dna, false); }
    }

    public static String arrString(Object[] arr) {
        String result="[";
        for (int i = 0; i < arr.length-1; i++) { result += arr[i] + ", "; }
        result += arr[arr.length-1] +"]";
        return result;
    }
}
