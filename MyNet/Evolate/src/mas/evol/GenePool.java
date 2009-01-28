/**
 * GenePool.java
 * Created on Dec 9, 2008
 */

package mas.evol;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;
import mas.Agent;

/**
 * <p>Represents a collection of agents with recombinant DNA spaces.
 * May also permit the introduction of error.</p>
 * @author Elisha Peterson
 */
public abstract class GenePool extends mas.Team {
    
    /** Team fitness scores. */
    protected HashMap<mas.Agent,Float> fitness;
    /** Cumulative fitness of agents (used to generate fit agents at random). */
    protected WeightTable<mas.Agent> fitSpace;
    /** Describes source of agents (used to assess validity of the simulation). */
    protected Vector<Integer> srcMap;
    /** Index of the fittest agent. */
    int fittestSource = -1;
    /** Whether a better agent has been found. */
    boolean improved = false;
    
    /** Stores data on the simulation. */
    public Vector<GeneLogEntry> geneLog;
    
    /** Assigns fitness scores. */
    public void assignFitness(mas.Simulation sim){
        if (fitness==null) { fitness = new HashMap<mas.Agent,Float>(); }
        fitness.clear();
        for(mas.Agent a : agents) { fitness.put(a,getFitness(a,sim)); }
        fitSpace = new WeightTable<mas.Agent>(fitness);
        try {
            fittestSource = srcMap.get(agents.indexOf(fitSpace.getTopScorer()));
        } catch (Exception e) {
            fittestSource = -1;
        }
        improved = (fittestSource != 0);
    }
    /** Assigns fitness to individual agent. */
    abstract public float getFitness(mas.Agent agent, mas.Simulation sim);
    
    /** Returns amount of error to introduce into descendants. */
    public float getError(){ return 0.0f; }
    
    /** Evolves team (general algorithm). By default all descendants are generated
     * based on fitness scores. */
    public void evolve() { 
        int nTop = agents.size()/3; // 0-keep top portion of population
        int nTopErr = agents.size()/3; // 1-top portion with introduced error
        int nCross = agents.size()/12; // 2-cross-breeding proportion
        int nFit = agents.size()/12; // 3-randomly selected based on proportional fitness, with error
        int nRandom = agents.size()-nTop-nTopErr-nCross-nFit; // 4-generate remainder at random
        evolve(nTop, nTopErr, nCross, nFit, nRandom); 
    }
    
    /** Evolves team based on fitness scores.
     * @param nTop the number of top scoring descendants to keep around (no change to parameters)
     * @param nTopErr the number of top scoring descendants to keep around (with error)
     * @param nCross the number of descendants to generate based on cross-breeding agents
     * @param nFit the number of descendants to generate based purely on "fitness" scores
     * @param nRandom the number of descendants to generate at "random"
     */
    public void evolve(int nTop, int nTopErr, int nCross, int nFit, int nRandom) {        
        srcMap = getSrcMap(nTop, nTopErr, nCross, nFit, nRandom);        
        Vector<mas.Agent> result = fitSpace.getTop(nTop);
        //System.out.println("  A: " + result);
        result.addAll(badCopy(fitSpace.getTop(nTopErr),getError()));
        //System.out.println("  B: " + result);        
        for(int i=0; i<nFit; i++) { result.add(badCopy(getFitDescendant(),getError())); }
        //System.out.println("  C: " + result);
        for(int i=0; i<nCross; i++) { result.add(getCrossDescendant()); }
        //System.out.println("  D: " + result);
        for(int i=0; i<nRandom; i++) { result.add(badCopy(getRandomDescendant(),getError())); }
        //System.out.println("  E: " + result);
        agents = result;
    }   
    
    /** Generates map describing how each of the agents arises. */
    private Vector<Integer> getSrcMap(int nTop, int nTopErr, int nCross, int nFit, int nRandom) {
        Vector<Integer> result = new Vector<Integer>();
        for(int i = 0; i<nTop; i++) { result.add(0); }
        for(int i = 0; i<nTopErr; i++) { result.add(1); }
        for(int i = 0; i<nCross; i++) { result.add(2); }
        for(int i = 0; i<nFit; i++) { result.add(3); }
        for(int i = 0; i<nRandom; i++) { result.add(4); }
        return result;
    }
    
    /** Generates a single fit agent at random. */
    public mas.Agent getFitDescendant() { 
        return (mas.Agent) fitSpace.getRandom(); 
    }
    /** Generates an agent cross-breed at random. */
    public mas.Agent getCrossDescendant() { 
        return new mas.Agent(
                ((DNA)getFitDescendant().getStateVars()).cross(getFitDescendant(), getError())
                ); 
    }
    /** Generates a single random agent. */
    public mas.Agent getRandomDescendant() { 
        return new mas.Agent(
                ((DNA)getFitDescendant().getStateVars()).getRandom()); 
    }    
    /** Introduces error into descendant agent... by default no error is introduced. */
    public Vector<mas.Agent> badCopy(Vector<mas.Agent> input, float error){
        Vector<mas.Agent> result = new Vector<mas.Agent>();
        for(mas.Agent a : input) { result.add(badCopy(a, error)); }
        return result;
    }
    /** Introduces error into descendant agent... by default no error is introduced. */
    public mas.Agent badCopy(mas.Agent input, float error){ 
        return new mas.Agent(((DNA)input.getStateVars()).mutation(error));
    }

    @Override
    public void adjustState(mas.Simulation sim) {}
    @Override
    public void communicate(mas.Simulation sim) {}
    @Override
    public void gatherInfo(mas.Simulation sim) {}

    @Override
    public void progressReport(mas.Simulation sim,PrintStream out) {
        //out.println("fitness: " + fitSpace.scoreMap);
        //out.println(" agents: " + agents);
        //out.println(" src: " + srcMap);
        if (improved) {
            Entry<Float,Agent> fittest = fitSpace.scoreMap.lastEntry();
            int simStep = sim instanceof mas.NSimulation ? ((mas.NSimulation)sim).curStep : -1;
            GeneLogEntry gle = new GeneLogEntry(simStep, fittest.getValue(), fittest.getKey(), fittestSource);
            out.println(gle.toString());
            if (geneLog == null) { geneLog = new Vector<GeneLogEntry>(); }
            geneLog.add(gle);
        }
    }

    
    
    
    /** Class which will generate a random object out of a set, based on relative probabilities
     * of occurence assigned via a HashMap
     */
    protected class WeightTable<O extends Object> {
        TreeMap<Float,O> scoreMap;
        TreeMap<Float,O> cumScoreMap;
        Float totScore;
        
        public WeightTable(HashMap<? extends O,Float> weightMap) {
            float cumFitness = 0;
            scoreMap = new TreeMap<Float,O>();
            cumScoreMap = new TreeMap<Float,O>();
            for(O o : weightMap.keySet()) {
                scoreMap.put(weightMap.get(o), o);
                cumFitness += weightMap.get(o);
                cumScoreMap.put(cumFitness, o);
            }
            totScore = cumFitness;
        }
        
        /** Returns top weighted agent. */
        public O getTopScorer() { return scoreMap == null ? null : scoreMap.lastEntry().getValue(); }
                
        public O getRandom() {
            float v = (float) (totScore * Math.random());
            for(Float f : cumScoreMap.keySet()) {
                if (v > f) { continue; }
                return cumScoreMap.get(f);
            }
            return null;
        }
        
        public Vector<O> getBottom(int n) {
            Vector<O> result = new Vector<O>();
            for(O o: scoreMap.values()) {
                if (result.size() < n) {
                    result.add(o);
                } else {
                    break;
                }
            }
            return result;
        }
        
        public Vector<O> getTop(int n) {
            Vector<O> result = new Vector<O>();
            for(O o: scoreMap.descendingMap().values()) {
                if (result.size() < n) {
                    result.add(o);
                } else {
                    break;
                }
            }
            return result;
        }
    }
    
    /** Stores snapshot of one point in evolution of gene pool */
    public class GeneLogEntry {
        int simStep;
        public mas.Agent agent;
        public float fitness;
        public int source;

        public GeneLogEntry(int simStep, Agent agent, float fitness, int source) {
            this.simStep = simStep;
            this.agent = agent;
            this.fitness = fitness;
            this.source = source;
        }
        
        @Override
        public String toString() {
             return "Simulation step "+simStep
                    + ": Fittest Agent is " + agent.toString()
                    + " with fitness " + fitness
                    + " (source: " + source + ")";
        }
        
        public String toTabString() {
             return simStep + "\t" + fitness + "\t" + agent.toString() + "\t" + source;
        }
    }
    
    /** Prints log table. */
    public void printLog(PrintStream out) {
        if (geneLog == null) { return; }
        for (GeneLogEntry gle : geneLog) { out.println(gle.toTabString()); }
    }
    
    /** Prints list of agents. */
    public void printAgentList(PrintStream out) {
        if (geneLog == null) { return; }
        out.print("{ ");
        for (GeneLogEntry gle : geneLog) {
            out.print(gle.agent.toString());
            if(! gle.equals(geneLog.lastElement())) { out.print(", "); }
        }
        out.print("}");
        
    }
}
