package sight2;

import java.util.HashMap;
import java.util.Vector;
import mas.Simulation;

/**
 * Has a few agents
 * @author elisha
 */
public class MyTeam extends mas.Team {

    float[] spec;
    
    HashMap<Integer,Integer> assignments;
    Vector<Integer> bestCaps;
    Vector<Integer> nCaps;
    float[] locs;
    Float[] targets = new Float[0];
    int size;

    public MyTeam(int size){this.size = size; }

    @Override
    public void initStateVars() {
        locs = new float[size];
        for (int i = 0; i < locs.length; i++) { locs[i] = (float) Math.random(); }
        targets = new Float[0];
    }

    /** Gathers information about which agents are visible. Information is stored in the targets variable for later use. */
    @Override
    public void gatherInfo(Simulation sim) {
        float[] ts = ((MySim)sim).targets;
        Vector<Float> result = new Vector<Float>();
        boolean vis = false;
        for (int i = 0; i < ts.length; i++) {
            vis = false;
            for (int j = 0; j < locs.length; j++) {
                if(isVisible(ts[i],j)) {
                    result.add(ts[i]);
                    vis = true;
                    break;
                }
            }
            if (!vis) { result.add(Float.MAX_VALUE); }
        }
        targets = new Float[ts.length];
        result.toArray(this.targets);
    }

    @Override
    public void communicate(Simulation sim) {}

    /** Assigns targets for the team. Greedy strategy.... begins with minimum time to capture first. */
    @Override
    public void adjustState(Simulation sim) {
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


    /** Determines whether point is in sensory range of specified agent. */
    public boolean isVisible(float target, int agent) { return Math.abs(target - locs[agent]) < spec[agent]; }

    /** Returns capture time for given agent. */
    public float getCaptureTime(float target, int agent) {
        return Math.abs(target - locs[agent]) / ((1-spec[agent])/200);
    }
    
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


    

    /** Recursive search algorithm. Finds location of minimum value in table.
     * Makes the assignment and eliminates from table.
     * @return number of possible assignments left to make. */
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

}
