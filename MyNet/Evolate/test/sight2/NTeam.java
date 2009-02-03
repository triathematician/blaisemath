/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sight2;

import java.util.HashMap;
import java.util.Vector;
import mas.Agent;
import mas.Simulation;
import mas.Team;

/**
 * Represents several agents.
 * @author elisha
 */
public class NTeam extends Team {

    int size;
    Vector<Integer> vTarg;
    Vector<Integer> nCaps;
    Vector<Vector<Float>> capTimes;
    HashMap<Integer,Integer> assignments;
    Vector<Integer> bestCaps;

    public NTeam(int size){
        this.size=size;
        for(int i=0;i<size;i++){agents.add(new NAgent((float) Math.random()));}
    }

    /** Collects visibility and target time information. */
    @Override
    public void gatherInfo(Simulation sim) {
        NSim nsim = (NSim) sim;
        if(vTarg==null){vTarg = new Vector<Integer>(); } else { vTarg.clear(); }
        if(capTimes==null){capTimes = new Vector<Vector<Float>>(); } else { capTimes.clear(); }
        for (int i = 0; i < nsim.targets.length; i++) {
            Vector<Float> cap = new Vector<Float>();
            if(isVisible(nsim.targets[i])){ 
                vTarg.add(i);
                for(int j = 0; j < size; j++) { cap.add(((NAgent)agents.get(j)).timeTo(nsim.targets[i])); }
            } else {
                for(int j = 0; j < size; j++) { cap.add(Float.MAX_VALUE); }
            }
        }
    }

    @Override public void communicate(Simulation sim) {}

    /** Assigns ideal targets via greedy search strategy... begins with minimum time to capture, and continues assigning team members
     * to targets until no more targets are left. */
    @Override public void adjustState(Simulation sim) {
        assignments = new HashMap<Integer,Integer>();
        Vector<Integer> teamPos = new Vector<Integer>();
        Vector<Integer> targetPos = new Vector<Integer>();
        for (int i = 0; i < size; i++) { teamPos.add(i); }
        for (int j = 0; j < ((NSim)sim).targets.length; j++) { targetPos.add(j); }
        while (greedySearch(capTimes, teamPos, targetPos, assignments) > 0) {}
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

    /** Checks whether anyone on the team can see the target. */
    public boolean isVisible(float target){ 
        for(Agent a:agents){ if(((NAgent)a).isVisible(target)){return true;} }
        return false;
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
}
