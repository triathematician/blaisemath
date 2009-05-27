/*
 * SightEvol.java
 * Created Feb 2009
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
 * @author Elisha Peterson
 */
public class SightEvol {

    // SIMULATION PARAMETERS

    /** total number of evolutionary steps to perform. */
    static int NSTEPS = 1000;

    /** Number of iterations per round. */
    static int ITER = 10000;

    /** Total number of teams in the evolutionary pool. */
    static int TOT = 10;

    /** Team evolution rules. Should sum to TOT */
    static int[] NUM_TEAMS = {2,2,2,2,2};

    /** Fixed rule defining a team that never evolves */
    final static int[] SINGLE = {1,0,0,0,0};

    /** Mutation error per copy. */
    final static float ERROR = 0.02f;

    /** Probability of point mutation per copy, per parameter. */
    final static float POINTPROB = 1f;

    /** Number of targets. */
    static int NTARGETS = 1;

    /** Number of players per team. */
    static int TEAM_SIZE = 1;



    /** Used as template for DNA construction. This creates a single parameter
     * for every agent on the team, bounded between 0f and 1f.
     */
    static DNA TEMPLATE = new DNA() { 
        @Override public void initialize(){
            for (int i = 0; i < TEAM_SIZE; i++) {
                set ("spec" + i, new Allele.Flt(0.0f, 1.0f, 0.5f));
            }
        }
    };



    /** Call the main algorithm. */
    public static void main(String[] args) { 
        main_coEvolutionary(args);
    }

    /** Simulates an evolutionary strategy, run against random opponents. */
    public static void main_randomOpponent(String[] args) {
        GenePool pool1 = getEvolPool();
        Team pool2 = getStaticTeam(.5f, .5f, true);
        Vector<Team> teams = new Vector<Team>();
        for (Agent a : pool1.getAgents()) { 
            teams.add((Team)a);
        }
        for (Agent a : pool2.getAgents()) { 
            teams.add((Team)a);
        }
        SightSim ss = new SightSim(ITER, teams, NTARGETS);
        ss.setCompetitors(pool1, pool2);
        new EvolSim(NSTEPS, pool1, ss).run();
        pool1.printLog(System.out);
        //pool1.printAgentHistory(System.out, true);
    }

    /** Simulates a co-evolutionary strategy, with two competing teams. */
    public static void main_coEvolutionary(String[] args) {
        GenePool pool1 = getEvolPool();
        GenePool pool2 = getEvolPool();
        Vector<Team> pools = new Vector<Team>();
        pools.add(pool1);
        pools.add(pool2);
        Vector<Team> teams = new Vector<Team>();
        for (Agent a : pool1.getAgents()) { 
            teams.add((Team)a);
        }
        for (Agent a : pool2.getAgents()) { 
            teams.add((Team)a);
        }
        SightSim ss = new SightSim(ITER, teams, NTARGETS);
        ss.roundRobin = false;
        ss.setCompetitors(pool1, pool2);
        new EvolSim(NSTEPS, pools, ss).run();
        pool1.printLog(System.out);
        pool2.printLog(System.out);
    }



    /** Returns a pool of teams with parameters that evolve over time based
     * on the specified fitness function. */
    public static GenePool getEvolPool() {
        return new GenePool(NUM_TEAMS, POINTPROB, ERROR, TEMPLATE){
            /** The fitness rule is the number of captures made by this team. */
            @Override public float getFitness(Agent agent, Simulation sim) { 
                return TOT * ((SightTeam) agent).captures / (float) ((SightSim)sim).captures / ((SightTeam) agent).nRounds;
            }
            @Override public Agent getAgentFrom(DNA dna) { 
                return new SightTeam(dna, false);
            }
        };
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
                return ((SightTeam) agent).captures / (float) ((SightSim)sim).captures / ((SightTeam) agent).nRounds;
            }
            @Override public Agent getAgentFrom(DNA dna) { 
                return new SightTeam(dna, randomize);
            }
        };
    }

    /** Returns group of teams with specified specs (that never change!) */
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
        static DNA template = new DNA() { 
            @Override public void initialize() {
                for (int i = 0; i < TEAM_SIZE; i++) {
                    set ("spec"+i,new Allele.Flt(0.0f, 1.0f, 0.5f));
                }
            }
        };

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

        @Override public float getFitness(Agent agent, Simulation sim) {
            return TOT * ((SightTeam) agent).captures / (float) ((SightSim)sim).captures;
        }

        @Override public Agent getAgentFrom(DNA dna) { 
            return new SightTeam(dna, false);
        }
    }

    public static String arrString(Object[] arr) {
        String result="[";
        for (int i = 0; i < arr.length-1; i++) { 
            result += arr[i] + ", ";
        }
        result += arr[arr.length-1] +"]";
        return result;
    }
}
