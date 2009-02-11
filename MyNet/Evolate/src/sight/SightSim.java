/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sight;

import java.util.TreeMap;
import java.util.Vector;
import mas.Agent;
import mas.NSimulation;
import mas.Team;
import mas.evol.GenePool;

/**
 * This describes the "micro-sim"... generating the targets and determining which teams are successful
 * in reaching the targets first.
 * @author elisha
 */
public class SightSim extends NSimulation {

    public Float[] targets;
    public int captures = 0;

    public SightSim(int iter, Vector<Team> teams, int nTarg) { super(iter,teams); targets = new Float[nTarg]; }

    @Override
    public void initControlVars() { captures = 0; super.initControlVars(); }

    @Override
    public void preIterate() {
        for (int i = 0; i < targets.length; i++) { targets[i] = (float) Math.random(); }
        for (Team t : teams) { ((SightTeam)t).preIterate(); }
    }

    /** Here is the calculation of which team gets the capture, and assignment of captures. */
    @Override
    protected void postIterate() {
        SightTeam[] winners = new SightTeam[targets.length];
        // determine who gets each target
        if (compList == null) { computeRoundRobin(); }
        if (captureData == null) { captureData = new Vector<TreeMap<SightTeam,Integer>>(); }
        for(int ii=0; ii< compList.size(); ii++) {
            Vector<Team> vt = compList.get(ii);
            if (captureData.size() <= ii) {
                TreeMap<SightTeam,Integer> h = new TreeMap<SightTeam,Integer>();
                for(Team t : vt) { h.put((SightTeam) t,0); }
                captureData.add(h);
            }
            TreeMap<SightTeam,Integer> capList = captureData.get(ii);
            for (int i = 0; i < targets.length; i++) {
                float bestTime = 100;
                SightTeam bestTeam = null;
                for(Team t : vt) {
                    SightTeam st = (SightTeam) t;
                    if (st.targeting[i] != null && st.targeting[i].timeTo(targets[i]) < bestTime) {
                        bestTime = st.targeting[i].timeTo(targets[i]);
                        bestTeam = st;
                    }
                }
                winners[i] = bestTeam;
                if (bestTeam != null) {
                    bestTeam.captures++;
                    try{
                        capList.put(bestTeam, capList.get(bestTeam)+1);
                    }catch(NullPointerException e){
                        //System.out.println("duh");
                    }
                    if(set1==null || set1.contains(bestTeam)){ captures++; }
                }
            }
        }
        //System.out.println("Sim results: targets="+SightMain.arrString(targets)+"\n captures="+SightMain.arrString(winners));
        super.postIterate();
    }

    @Override
    public void replaceTeams(Vector<Team> oldT, Vector<Team> newT) {
        super.replaceTeams(oldT, newT);
        assignPools();
        compList = null;
        captureData = null;
    }


    Vector<Vector<Team>> compList;
    Vector<TreeMap<SightTeam,Integer>> captureData;
    Team pool1, pool2;
    Vector<Team> set1, set2;
    
    public void setCompetitors(Team pool1, Team pool2){ this.pool1 = pool1; this.pool2 = pool2; assignPools(); }

    public void assignPools() {
        if (set1==null) { set1 = new Vector<Team>(); } else { set1.clear(); }
        if (set2==null) { set2 = new Vector<Team>(); } else { set2.clear(); }
        for (Agent a : pool1.getAgents()) { if (a instanceof Team) { set1.add((Team)a); } }
        for (Agent a : pool2.getAgents()) { if (a instanceof Team) { set2.add((Team)a); } }
    }

    /** This determines the listing of teams that will compete head-to-head for resources. */
    public void computeRoundRobin() {
        compList = new Vector<Vector<Team>>();
        if (pool1==null || pool2==null) {
            for (int i = 0; i < teams.size(); i++) {
                for (int j = i+1; j < teams.size(); j++) {
                    Vector<Team> cross = new Vector<Team>();
                    cross.add(teams.get(i));
                    cross.add(teams.get(j));
                    compList.add(cross);
                }
            }
        } else {
            for (Team t1 : set1) {
                for (Team t2 : set2) {
                    Vector<Team> cross = new Vector<Team>();
                    cross.add(t1);
                    cross.add(t2);
                    compList.add(cross);
                }
            }
        }
    }
}
