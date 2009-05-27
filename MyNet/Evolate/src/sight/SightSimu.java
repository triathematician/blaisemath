/*
 * SightSimu.java
 * Created Feb 2009
 */

package sight;

import java.util.HashMap;
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
    
    // number of iterations to perform during each round
    static int ITER = 100000;
    
    // number of targets
    static int NTARGETS = 1;

    // number of players per team
    static int TEAM_SIZE = 2;
    
    // never evolves
    final static int[] SINGLE = {1,0,0,0,0};

    // PSTEP size for sims with fixed parameters
    static float PMIN = 0.0f;

    // PSTEP size for sims with fixed parameters
    static float PMAX = 1.0f;

    // PSTEP size for sims with fixed parameters
    static float PSTEP = 0.1f;

    public static void main(String[] args) { 
        main_bruteforce(args);
    }

    // configuration for computing optimum parameters by brute force (against a random team)
    public static void main_bruteforce(String[] args) {
        Team pool1 = getSpecTeams();
        Team pool2 = getStaticTeam(0f,0.8f,false);
        Vector<Team> teams = new Vector<Team>();

        // Adds all the teams in the pool of fixed spec teams 
        for (Agent a : pool1.getAgents()) {
            teams.add((Team)a);
        }

        // Add teams with random specs
        for (Agent a : pool2.getAgents()) {
            teams.add((Team)a);
        }

        SightSim ss = new SightSim(ITER, teams, NTARGETS);
        ss.setCompetitors(pool1, pool2);
        ss.run();

        SightTeam tFixed = null;
        SightTeam tRandom = null;
        for(HashMap<SightTeam,Integer> h : ss.captureData) {
            for (SightTeam st : h.keySet()) {
                if (st.randomSpec) {
                    tRandom = st;
                } else {
                    tFixed = st;
                }
            }
            System.out.println(tFixed.toString() + "\t" + h.get(tFixed) + "\t" + h.get(tRandom));
        }
    }

    // configuration for round-robin simulation, suitable for game-theoretic analysis
    public static void main_roundrobin(String[] args) {
        Vector<Team> teams = new Vector<Team>();
        // add  teams with fixed parameters
        for (Agent a : getSpecTeams().getAgents()) { 
            teams.add((Team)a);
        }
        SightSim ss = new SightSim(ITER, teams, NTARGETS);
        ss.run();
        for(HashMap<SightTeam,Integer> h : ss.captureData) {
            System.out.println(h);
        }
    }


    /** Returns static teams whose parameters AND positions are generated at random,
     * to serve as a baseline for comparison.
     * @param f1 first parameter for the team
     * @param f2 second parameter for the team
     * @param randomize whether to randomize the team each simulation
     */
    public static Team getStaticTeam(float f1, float f2, final boolean randomize) {
        final float[] specs = {f1, f2};
        DNA template = new DNA() {
            @Override public void initialize(){
                for (int i = 0; i < TEAM_SIZE; i++) {
                    set ("spec"+i, new Allele.Flt(0.0f, 1.0f, specs[i]));
                }
            }
        };
        return new GenePool(SINGLE, 0.0f, 0.0f, template) {
            @Override public float getFitness(Agent agent, Simulation sim) {
                return ((SightTeam) agent).captures / (float) ((SightSim)sim).captures;
            }
            @Override public Agent getAgentFrom(DNA dna) {
                return new SightTeam(dna, randomize);
            }
        };
    }

    /** Returns group of teams with fixed compositions. */
    public static Team getSpecTeams() {
        Vector<Float[]> specs = new Vector<Float[]>();
        if (TEAM_SIZE == 1) {
            for (float f1=PMIN; f1<PMAX+PSTEP/2; f1+=PSTEP) {
                Float[] nf = {f1};
                specs.add(nf);
            }
        } else if (TEAM_SIZE == 2) {
            for (float f1=PMIN; f1<=PMAX+PSTEP/2; f1+=PSTEP) {
                for (float f2=f1; f2<=PMAX+PSTEP/2; f2+=PSTEP) {
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
                for(int i = 0; i < ff.length; i++) { 
                    dna.set("spec"+i, new Allele.Flt(0.0f, 1.0f, ff[i]));
                }
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
