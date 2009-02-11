package sight;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Vector;
import mas.Agent;
import mas.Simulation;
import mas.Team;
import mas.evol.DNA;

/**
 * Represents a team of agents for use in the "sight" simulation. Stores DNA that evolves over time and determines the parameters of the agents on the team.
 * @author elisha
 */
public class SightTeam extends Team implements Comparable<SightTeam> {

    // VARIABLES USED IN CALCULATING RESULT OF SIMULATION

    /** The agents assigned to each target in the optimal capture. */
    SightAgent[] targeting;
    /** Total number of captures made by the team. */
    int captures = 0;
    /** Determines whether agent specs are generated at random during each iteration. */
    boolean randomSpec = false;

    /** Constructs given template DNA. */
    public SightTeam(DNA dna, boolean randomSpec) {
        controlVars = dna;
        for (int i = 0; i < dna.size(); i++) { agents.add(new SightAgent(getSpec(i))); }
        this.randomSpec = randomSpec;
    }

    @Override
    protected void initControlVars() { captures = 0; }
    public void preIterate() {
        for(Agent a : agents) { ((SightAgent)a).pos = (float) Math.random(); }
        if (randomSpec) { controlVars = ((DNA)controlVars).getRandom(); }
    }

    @Override
    public void gatherInfo(Simulation sim) {
        if(!(sim instanceof SightSim)){ return; }
        Float[] targets = ((SightSim)sim).targets;
        // determine which targets are visible
        boolean[] visible = new boolean[targets.length];
        for (int i = 0; i < targets.length; i++) { visible[i] = sees(targets[i]); }
        // create table of capture times
        Vector<Vector<Float>> timeTable = new Vector<Vector<Float>>(targets.length);
        for (int i = 0; i < targets.length; i++) {
            Vector<Float> times = new Vector<Float>();
            for (Agent a:agents){ times.add(visible[i] ? ((SightAgent)a).timeTo(targets[i]) : Float.MAX_VALUE); }
            timeTable.add(times);
        }
        // recursive assignment algorithm
        HashMap<Integer,Integer> targetTable = new HashMap<Integer,Integer>(); // keys are targets, values are team members
        Vector<Integer> targetPos = new Vector<Integer>();
        for (int j = 0; j < targets.length; j++) { targetPos.add(j); }
        Vector<Integer> agentPos = new Vector<Integer>();
        for (int i = 0; i < agents.size(); i++) { agentPos.add(i); }
        while (greedySearch(timeTable, targetPos, agentPos, targetTable) > 0) {}
        // store optimal assignment
        targeting = new SightAgent[targets.length];
        for (int i = 0; i < targets.length; i++) {
            targeting[i] = (targetTable.containsKey(i) && visible[i]) ? (SightAgent) agents.get(targetTable.get(i)) : null;
        }
    }

    /** Recursive search algorithm. Finds location of minimum value in table.
     * Makes the assignment and eliminates from table.
     * @return number of possible assignments left to make. */
    public static int greedySearch(Vector<Vector<Float>> values, Vector<Integer> iPos, Vector<Integer> jPos, HashMap<Integer,Integer> assignments) {
        int minI = 0,minJ = 0;
        Float minValue;
        try { minValue = values.get(0).get(0);
        } catch (ArrayIndexOutOfBoundsException e) { return 0; }
        for (int i = 0; i < values.size(); i++) {
            for (int j = 0; j < values.firstElement().size(); j++) {
                if(values.get(i).get(j) < minValue) {
                    minI = i; minJ = j; minValue = values.get(i).get(j);
                }
            }
        }
        assignments.put(iPos.get(minI),jPos.get(minJ));
        values.remove(minI); iPos.remove(minI); jPos.remove(minJ);
        for (int i = 0; i < values.size(); i++) { values.get(i).remove(minJ); }
        return values.size() == 0 ? 0 : Math.min(values.size(), values.firstElement().size());
    }

    /** Returns spec i */
    public float getSpec(int i){ return (Float)((DNA)controlVars).get("spec"+i).value; }
    /** Returns whether target is within sight range of any agent on team */
    public boolean sees(float target){
        for (Agent a : agents) { if (((SightAgent)a).sees(target)) { return true; } }
        return false;
    }

    @Override
    public void progressReport(Simulation sim, PrintStream out) {
        //out.println(" Team specs: " + agents.toString() + "; captures=" + captures);
    }

    @Override
    public String toString() { return controlVars.toTabString(); }

    /** Used to sort teams by parameters. */
    public int compareTo(SightTeam o) {
        if (this == o) { return 0; }
        DNA dna1 = (DNA) this.controlVars;
        DNA dna2 = (DNA) o.controlVars;
        int i = 0;
        Float spec1=0f;
        Float spec2=0f;
        try {
            while(true) {
                spec1 = (Float) dna1.get("spec"+i).value;
                spec2 = (Float) dna2.get("spec"+i).value;
                if (spec1 > spec2) { return 1; }
                if (spec1 < spec2) { return -1; }
                i++;
            }
        } catch (NullPointerException e) {}
        return 0;
    }
    


    /** A team contains agents of the following type... their specs must be updated whenever the team DNA evolves. */
    public static class SightAgent extends Agent {
        float spec;
        float pos;
        public SightAgent(float spec){this.spec = spec; }
        // runs before each simulation... randomize position
        @Override public void initControlVars(){ pos = (float) Math.random(); }
        public boolean sees(float target){ return Math.abs(pos - target) < spec; }
        public float timeTo(float target){
            if (spec > .999f) { return Float.MAX_VALUE; }
            float result = Math.abs(pos - target) / (1 - spec) ;
            return result;
        }
        public void setSpec(float spec){this.spec = spec; }
        @Override public String toString(){ return ""+spec; }
    }
}
