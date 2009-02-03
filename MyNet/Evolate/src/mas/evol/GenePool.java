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
import mas.ParameterSpace;

/**
 * <p>Represents a collection of agents with recombinant DNA spaces.
 * May also permit the introduction of error.</p>
 * @author Elisha Peterson
 */
public abstract class GenePool extends mas.Team {
    
    /** Team fitness scores. */
    public HashMap<mas.Agent,Float> fitness;
    /** Cumulative fitness of agents (used to generate fit agents at random). */
    public WeightTable<mas.Agent> fitSpace;
    /** Describes source of agents (used to assess validity of the simulation). */
    protected Vector<Integer> srcMap;
    /** Index of the fittest agent. */
    int fittestSource = -1;
    /** Whether a better agent has been found. */
    boolean improved = false;
    
    /** Stores data on the simulation. */
    public Vector<GeneLogEntry> geneLog;

    /** Determines the evolution rules. */
    public int[] rules;
    public static float[] fRules = {.3f,.2f,.25f,.15f,.1f};

    /** Probability of point mutations. */
    protected float mProb = 0.1f;
    /** Error introduced upon mutation. */
    protected float mErr = 0.0f;

    /** Default constructor */
    public GenePool(int[] rules,float mProb,float mErr){ this(rules, mProb, mErr, null); }
    /** Default constructor */
    public GenePool(int[] rules,float mProb,float mErr,DNA template){
        this.rules = rules;
        this.mProb = mProb;
        this.mErr = mErr;
        int size = 0;
        for (int i = 0; i < rules.length; i++) { size += rules[i]; }
        agents = new Vector<Agent>(size);
        if (template==null) { return; }
        for (int i = 0; i < size; i++) {
            agents.add(getAgentFrom((DNA)template.getRandom()));
        }
    }

    
    /** Assigns fitness scores. */
    public void assignFitness(mas.Simulation sim){
        if (fitness==null) { fitness = new HashMap<mas.Agent,Float>(); }
        fitness.clear();
        for(mas.Agent a : agents) { fitness.put(a,getFitness(a,sim)); }
        //System.out.println(fitness);
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
    public float getPointMutationProbability(){ return mProb; }

    /** Returns amount of error to introduce into descendants. */
    public float getMutationError(){ return mErr; }
    
    /** Evolves team (general algorithm). By default all descendants are generated
     * based on fitness scores. */
    public void evolve() { evolve(rules); }

    /** Generates based on an array describing how many agents corresponding to each rule. */
    public void evolve(int[] nRule) { evolve(nRule[0],nRule[1],nRule[2],nRule[3],nRule[4]); }
    
    /** Evolves team based on fitness scores.
     * @param nTop the number of top scoring descendants to keep around (no change to parameters)
     * @param nTopErr the number of top scoring descendants to keep around (with error)
     * @param nCross the number of descendants to generate based on cross-breeding agents
     * @param nFit the number of descendants to generate based purely on "fitness" scores
     * @param nRandom the number of descendants to generate at "random"
     */
    public void evolve(int nTop, int nTopErr, int nCross, int nFit, int nRandom) {        
        srcMap = getRuleMap(nTop, nTopErr, nCross, nFit, nRandom);
        Vector<mas.Agent> result = fitSpace.getTop(nTop);
        //System.out.println("  A: " + result);
        result.addAll(getMutatedDescendant(fitSpace.getTop(nTopErr),mProb,mErr));
        //System.out.println("  B: " + result);
        for(int i=0; i<nCross; i++) { result.add(getCrossDescendant()); }
        //System.out.println("  C: " + result);
        for(int i=0; i<nFit; i++) { result.add(getMutatedDescendant(getFitDescendant(),mProb,mErr));}
        //System.out.println("  D: " + result);
        for(int i=0; i<nRandom; i++) { result.add(getMutatedDescendant(getRandomDescendant(),mProb,mErr));}
        //System.out.println("  E: " + result);
        agents = result;
    }

    /** String descriptions of the evolution rules */
    static String[] RULES = { "Top", "Mut[Top]", "Crossbred", "Mut[Fit]", "Random" };

    /** Generates map describing how each of the agents arises. */
    private Vector<Integer> getRuleMap(int nTop, int nTopErr, int nCross, int nFit, int nRandom) {
        Vector<Integer> result = new Vector<Integer>();
        for(int i = 0; i<nTop; i++) { result.add(0); }
        for(int i = 0; i<nTopErr; i++) { result.add(1); }
        for(int i = 0; i<nCross; i++) { result.add(2); }
        for(int i = 0; i<nFit; i++) { result.add(3); }
        for(int i = 0; i<nRandom; i++) { result.add(4); }
        return result;
    }

    public mas.Agent getAgentFrom(DNA dna) { return new mas.Agent(dna); }

    /** Returns random DNA */
    public DNA getRandomDNA() {
        ParameterSpace ps = agents.firstElement().getControlVars();
        return (DNA) ps.getRandom();
    }
    /** Returns fit DNA */
    public DNA getFitDNA() {
        ParameterSpace ps = ((mas.Agent)getFitDescendant()).getControlVars();
        return (ps instanceof DNA) ? (DNA) ps : null;
    }
    /** Generates a single fit agent at random. */
    public mas.Agent getFitDescendant() { return fitSpace.getRandom(); }
    /** Generates an agent cross-breed at random. */
    public mas.Agent getCrossDescendant() { return getAgentFrom(DNA.cross(getFitDNA(),getFitDNA(),getMutationError())); }
    /** Generates a single random agent. */
    public mas.Agent getRandomDescendant() { return getAgentFrom(getRandomDNA()); }
    /** Introduces error into descendant agent... by default no error is introduced. */
    public mas.Agent getMutatedDescendant(mas.Agent input, float pointProb, float err){
        return getAgentFrom(((DNA)input.getControlVars()).mutation(pointProb, err));
    }
    /** Introduces error into descendant agent... by default no error is introduced. */
    public Vector<mas.Agent> getMutatedDescendant(Vector<mas.Agent> input, float pointProb, float err){
        Vector<mas.Agent> result = new Vector<mas.Agent>(input.size());
        for(mas.Agent a : input) { result.add(getMutatedDescendant(a, pointProb, err)); }
        return result;
    }

    @Override
    public void progressReport(mas.Simulation sim, PrintStream out) {
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
        /** Returns top fitness. */
        public float getTopFitness() { return scoreMap == null ? null : scoreMap.lastEntry().getKey(); }
                
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
        public int evolType;

        public GeneLogEntry(int simStep, Agent agent, float fitness, int evolType) {
            this.simStep = simStep;
            this.agent = agent;
            this.fitness = fitness;
            this.evolType = evolType;
        }
        
        @Override
        public String toString() {
             return "Simulation step "+simStep
                    + ": Fittest Agent is " + agent.toString()
                    + " with fitness " + fitness
                    + " (evolution rule: " + evolType + "="+(evolType==-1?"Initial":RULES[evolType])+")";
        }
        
        public String toTabString() {
             return simStep + "\t" + fitness + "\t" + agent.toString() + "\t" + evolType;
        }
    }
    
    /** Prints log table. */
    public void printLog(PrintStream out) {
        if (geneLog == null) { return; }
        for (GeneLogEntry gle : geneLog) { out.println(gle.toTabString()); }
        // count entries in type table
        HashMap<Integer,Integer> ruleCount = new HashMap<Integer,Integer>();
        int rule=-1;
        for (int i=-1;i<=4;i++){ruleCount.put(i,0);}
        for (GeneLogEntry gle : geneLog) {
            rule = gle.evolType;
            ruleCount.put(rule, ruleCount.get(rule)+1);
        }
        System.out.println(
                "Summary of Successful Mutation Rules: "
                //+ruleCount.get(0) + " " + RULES[0] + "; "
                + ruleCount.get(1) + " " + RULES[1] + "; "
                + ruleCount.get(2) + " " + RULES[2] + "; "
                + ruleCount.get(3) + " " + RULES[3] + "; "
                + ruleCount.get(4) + " " + RULES[4]
                );
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
