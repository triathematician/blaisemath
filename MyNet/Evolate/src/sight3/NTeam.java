/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sight3;

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
    Vector<Vector<Float>> capTimes;

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
    @Override public void adjustState(Simulation sim) {}

    /** Checks whether anyone on the team can see the target. */
    public boolean isVisible(float target){ 
        for(Agent a:agents){ if(((NAgent)a).isVisible(target)){return true;} }
        return false;
    }
}
