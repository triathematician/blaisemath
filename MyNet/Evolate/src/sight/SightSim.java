/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sight;

import java.util.Vector;
import mas.NSimulation;
import mas.Team;

/**
 * This describes the "micro-sim"... generating the targets and determining which teams are successful
 * in reaching the targets first.
 * @author elisha
 */
public class SightSim extends NSimulation {
    public Float[] targets;
    public int captures = 0;
    public SightSim(int iter,Vector<Team> teams, int nTarg) { super(iter,teams); targets = new Float[nTarg]; }
    @Override
    public void initControlVars() { captures = 0; super.initControlVars(); }
    @Override
    public void preIterate() {
        for (int i = 0; i < targets.length; i++) { targets[i] = (float) Math.random(); }
        for (Team t : teams) { ((SightTeam)t).preIterate(); }
    }
    @Override
    protected void postIterate() {
        SightTeam[] winners = new SightTeam[targets.length];
        // determine who gets each target
        for (int i = 0; i < targets.length; i++) {
            float bestTime = 100;
            SightTeam bestTeam = null;
            for(Team t : teams) {
                SightTeam st = (SightTeam) t;
                if (st.targeting[i] != null && st.targeting[i].timeTo(targets[i]) < bestTime) {
                    bestTime = st.targeting[i].timeTo(targets[i]);
                    bestTeam = st;
                }
            }
            winners[i] = bestTeam;
            if (bestTeam != null) {
                bestTeam.captures++;
                captures++;
            }
        }
        //System.out.println("Sim results: targets="+SightMain.arrString(targets)+"\n captures="+SightMain.arrString(winners));
        super.postIterate();
    }
}
