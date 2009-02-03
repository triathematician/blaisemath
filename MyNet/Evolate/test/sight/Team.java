/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Team.java
 * Created on Dec 4, 2008
 */

package sight;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

/**
 *
 * @author Elisha Peterson
 */
public class Team {

    /** Determines how quickly fitness changes. */
    public float fitScale = 0.5f;
    public float fitMemory = 0.2f;
    
    /** Determines the rate of change of the parameters. */
    public float evolScale = 0.05f;
    public float evolMin = 0.001f;

        int size;
        
        /** Constructs to given size. Random altruism score and agent specs. */
        public Team(int size, float altruism) {
            this.size = size;
            locs = new float[size];
            spec = new float[size];
            for (int i = 0; i < size; i++) { spec[i] = (float) Math.random(); }
            this.altruism = altruism;
            fitness = new Vector<Float>();
            for (int i=0;i<size;i++){fitness.set(i,0.5f);}
            capStat = new Vector<Vector<Integer>>();
            fitStat = new Vector<Float>();
            specStat = new Vector<Vector<Float>>();
            for(int i=0;i<size;i++) { specStat.add(new Vector<Float>()); } 
        }
        /** Locations of the team; ranges from 0 to 1. */
        float[] locs;
        /** Specializations of the agents in the team;
         * ranges from 0 (all speed) to 1 (all sensory).
         * Speed is (1-spec)/100.
         * Sensory range is spec.
         */
        float[] spec;
        /** Level of altruism in the team; ranges from 0 to 1. */
        float altruism;
        /** Locations of targets... those outside perceived area will be set to infinity. */
        Float[] targets;
        /** Target assignment table. */
        HashMap<Integer,Integer> assignments;
        /** Stores fitness of current configuration. */
        Vector<Float> fitness;
        /** Stores number of captures in most recent simulation. */
        Vector<Integer> nCaps;
        
        /** Maintains data on the number of captures per iteration. */
        Vector<Vector<Integer>> capStat;
        /** Maintains data on the positions over time. */
        Vector<Vector<Float>> specStat;
        /** Maintains data on the fitness of team over time. */
        Vector<Float> fitStat;
        
        /** Initializes simulation run. */
        public void initRun() {
            locs = new float[size];
            for (int i = 0; i < locs.length; i++) { locs[i] = (float) Math.random(); }
            targets = new Float[0];
        }
        
        /** Determines whether point is in sensory range of specified agent. */
        public boolean isVisible(float target, int agent) {
            return Math.abs(target - locs[agent]) < spec[agent];
        }
        
        /** Returns capture time for given agent. */
        public float getCaptureTime(float target, int agent) {
            float speed = (1-spec[agent])/200;
            return Math.abs(target - locs[agent]) / speed;
        }
        
        /** Determines targets within view, based on agent specs and given locations. Updates "targets" local variable. */
        public void senseTargets(float[] targets) {
            Vector<Float> result = new Vector<Float>();
            boolean vis = false;
            for (int i = 0; i < targets.length; i++) {
                vis = false;
                for (int j = 0; j < locs.length; j++) {
                    if(isVisible(targets[i],j)) { 
                        result.add(targets[i]);
                        vis = true;
                        break;
                    }
                }
                if (!vis) { result.add(Float.MAX_VALUE); }
            }
            this.targets = new Float[targets.length];
            result.toArray(this.targets);
        }
        
        /** Assigns targets for the team. Greedy strategy.... begins with minimum
         * time to capture first.
         */
        public void assignTargets() {
            // construct table of time-to-capture
            Vector<Vector<Float>> timeTable = new Vector<Vector<Float>>();
            for (int i = 0; i < size; i++) {
                Vector<Float> time = new Vector<Float>();
                for (int j = 0; j < targets.length; j++) {
                    time.add(getCaptureTime(targets[j],i));
                }
                timeTable.add(time);
            }
            // recursive assignment algorithm
            assignments = new HashMap<Integer,Integer>();
            Vector<Integer> teamPos = new Vector<Integer>();
            Vector<Integer> targetPos = new Vector<Integer>();
            for (int i = 0; i < size; i++) { teamPos.add(i); }
            for (int j = 0; j < targets.length; j++) { targetPos.add(j); }
            while (greedySearch(timeTable, teamPos, targetPos, assignments) > 0) {}
        }
        
        /**
         * This searches through the timeTable matrix and finds the minimum value
         * @param timeTable matrix of values
         * @param teamPos stores current locations of teams
         * @param targetPos stores current locations of targets
         * @param assignments stores the row/column entries in the optimal assignments
         * @return
         */
        public static int greedySearch(
                Vector<Vector<Float>> timeTable, 
                Vector<Integer> teamPos,
                Vector<Integer> targetPos,
                HashMap<Integer,Integer> assignments) {
            int minI = 0,minJ = 0;
            Float minValue = Float.MAX_VALUE;
            try {
                minValue = timeTable.get(0).get(0);
            } catch (ArrayIndexOutOfBoundsException e) {
                return 0;
            }
            for (int i = 0; i < timeTable.size(); i++) {
                for (int j = 0; j < timeTable.size(); j++) {
                    if(timeTable.get(i).get(j) < minValue) {
                        minI = i; minJ = j; minValue = timeTable.get(i).get(j);
                    }
                }
            }
            assignments.put(teamPos.get(minI),targetPos.get(minJ));
            timeTable.remove(minI); teamPos.remove(minI); targetPos.remove(minJ);
            for (int i = 0; i < timeTable.size(); i++) { timeTable.get(i).remove(minJ); }
            return timeTable.size() == 0 ?
                0 :
                Math.min(timeTable.size(), timeTable.firstElement().size());
        }
        
        /** Best capture possibilities. */
        Vector<Integer> bestCaps;
        /** Returns potential capture times for each target. */
        public Vector<Float> getCaptureTimes() {
            Vector<Float> result = new Vector<Float>();
            bestCaps = new Vector<Integer>();
            for (int i = 0; i < targets.length; i++) { result.add(Float.MAX_VALUE); bestCaps.add(-1); }
            int j;
            for (Integer i : assignments.keySet()) {
                j = assignments.get(i);
                result.set(j, getCaptureTime(targets[j],i));
                bestCaps.set(j, i);
            }
            return result;
        }
        
        /** Adds capture to a particular player. */
        public void addCapture(int playerNum) {
            if (nCaps==null) {
                nCaps = new Vector<Integer>();
                for(int i=0;i<size;i++){nCaps.add(0);}
            }
            nCaps.set(playerNum, nCaps.get(playerNum)+1);
        }
        
        /** Assigns fitness score based on success in capturing targets
         * @param captureRatio value in [-1,1] defining success relative to other teams
         */
        public void calcFitness(int totTargets) {
            int teamCaptures = 0;
            for(int i=0;i<size;i++){teamCaptures += nCaps.get(i);}
            float teamFit = teamCaptures/(float)totTargets;            
//            captureRatio = (1+captureRatio)/2.0f;
//            fitness = fitMemory*fitness + (1-fitMemory)*captureRatio;
//            if (captureRatio > 0) {
//                fitness += fitScale*(1-fitness)*captureRatio;
//            } else {
//                fitness += fitScale*fitness*captureRatio;
//            }
        }
        
        /** 
         * <p>
         * Evolves the team parameters based on fitness score...
         * the higher the score, the less the change.
         * A fitness value of 1 indicates that relatively little change is required.
         * A fitness value of 0 indicates that a very large change is required.
         * Changes the "specs" only currently.
         * </p>
         * <p>
         * Uses a normmally distributed value about the current location. Picks new
         * values until it obtains one in the interval [0,1]. The standard deviation
         * is SCALE*(1-f)+MIN... so ranges between MIN for f=1 and MIN+SCALE for f=0.
         * </p>
         */
        public void evolve() {
//            float stdev = evolScale*(1-fitness)+evolMin;
            Random seed = new java.util.Random();
            float newSpec = -1;
            for (int i = 0; i < spec.length; i++) {
                newSpec = -1;
                while(newSpec < 0 || newSpec > 1) {
 //                   newSpec = (float) seed.nextGaussian()*stdev + spec[i];
                }
                spec[i] = newSpec;
            }
        }

    /** Output data on the team. */
    public void output(int i, PrintStream out) {
        String result =" Team "+i+": fitness="+fitness+", specs=[";
        for(int j = 0; j < size-1; j++) { result += spec[j]+", "; }
        result += spec[size-1]+"]";
        out.println(result);
    }
    
    /** Outputs data for specific iteration step. */
    public String tabString(int step) {
        String result="";
        if (step == -1) {
            result = "fit\t";
            for(int i = 0; i < size; i++) { result+="\t\t"; }
            return result;
        }
        result = fitStat.get(step) + "\t";
        for(int i = 0; i < size; i++) {
            result += specStat.get(i).get(step) + "\t";
            result += capStat.get(step).get(i) + "\t";
        }
        return result;
    }
    
    /** Logs current data. */
    public void logData() {
        if (nCaps==null) {
            nCaps = new Vector<Integer>();
            for(int i=0;i<size;i++){nCaps.add(0);}
        }
        capStat.add(nCaps);
        nCaps = null;
        for (int i = 0; i < size; i++) { specStat.get(i).add(spec[i]); }
//        fitStat.add(fitness);
    }
}
