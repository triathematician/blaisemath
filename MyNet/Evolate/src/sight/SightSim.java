/*
 * SightSim.java
 * Created Feb 2009
 */

package sight;

import java.util.HashMap;
import java.util.Vector;
import mas.Agent;
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
    public boolean roundRobin = true;

    public SightSim(int iter, Vector<Team> teams, int nTarg) { 
        super(iter, teams);
        targets = new Float[nTarg];
    }

    @Override
    public void initControlVars() {
        captures = 0;
        super.initControlVars();
    }

    /** Runs before the simulation starts. Initializes all the variables. */
    @Override
    public void preIterate() {
        for (int i = 0; i < targets.length; i++) { 
            targets[i] = (float) Math.random();
        }
        for (Team t : teams) { 
            ((SightTeam)t).preIterate();
        }
    }

    /** Here is the calculation of which team gets the capture, and assignment of captures. */
    @Override
    protected void postIterate() {
        SightTeam[] winners = new SightTeam[targets.length];
        // determine who gets each target
        if (compList == null) {
            if (pool1==null || pool2==null) {
                compList = getRoundRobin(teams);
            } else if (roundRobin) {
                compList = getRoundRobin(set1, set2);
            } else {
                compList = getTopPairing(set1, set2);
            }
        }
        if (captureData == null) { 
            captureData = new Vector<HashMap<SightTeam,Integer>>();
        }
        for(int ii=0; ii < compList.size(); ii++) {
            // initialize lists
            Vector<Team> vt = compList.get(ii);
            if (captureData.size() <= ii) {
                HashMap<SightTeam,Integer> h = new HashMap<SightTeam,Integer>();
                for(Team t : vt) { 
                    h.put((SightTeam) t, 0);
                }
                captureData.add(h);
            }
            // compute captures
            HashMap<SightTeam,Integer> capList = captureData.get(ii);
            for (int i = 0; i < targets.length; i++) {
                float bestTime = 100000;
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
                    if(set1==null || set1.contains(bestTeam)){ 
                        captures++;
                    }
                }
            }
        }
        // normalize if not a round robin
        if (!roundRobin && pool1!=null && pool2!=null) {
            ((SightTeam)pool1.getAgents().get(0)).nRounds = pool2.getAgents().size();
            ((SightTeam)pool2.getAgents().get(0)).nRounds = pool1.getAgents().size();
        }
        //System.out.println("Sim results: targets="+SightSimu.arrString(targets)+"\n captures="+SightSimu.arrString(winners));
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

    /** List of capture information. Each element is for a different competition among teams. */
    Vector<HashMap<SightTeam,Integer>> captureData;

    /** Pools of players that might compete head-to-head. */
    Team pool1, pool2;
    
    Vector<Team> set1, set2;

    /** Sets up the two groups of teams in the simulation. If two sets are given,
     * the teams will compete in a round-robin style tournament.
     */
    public void setCompetitors(Team pool1, Team pool2){ 
        this.pool1 = pool1;
        this.pool2 = pool2;
        assignPools();
    }

    /** Constructs sets of teams within the given pools. If two sets are given,
     * the teams will compete in a round-robin style tournament.
     */
    public void assignPools() {
        if (set1==null) { 
            set1 = new Vector<Team>();
        } else {
            set1.clear();
        }
        if (set2==null) { 
            set2 = new Vector<Team>();
        } else {
            set2.clear();
        }
        for (Agent a : pool1.getAgents()) { 
            if (a instanceof Team) {
                set1.add((Team)a);
            }
        }
        for (Agent a : pool2.getAgents()) { 
            if (a instanceof Team) {
                set2.add((Team)a);
            }
        }
    }

    /** Retunrs all pairings of the given list of teams. */
    public Vector<Vector<Team>> getRoundRobin(Vector<Team> teams) {
        Vector<Vector<Team>> list = new Vector<Vector<Team>>();
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i+1; j < teams.size(); j++) {
                Vector<Team> cross = new Vector<Team>();
                cross.add(teams.get(i));
                cross.add(teams.get(j));
                list.add(cross);
            }
        }
        return list;
    }

    /** Returns all pairs of teams based on two input sets. */
    public Vector<Vector<Team>> getRoundRobin(Vector<Team> sideOne, Vector<Team> sideTwo) {
        Vector<Vector<Team>> list = new Vector<Vector<Team>>();
        for (Team t1 : sideOne) {
            for (Team t2 : sideTwo) {
                Vector<Team> cross = new Vector<Team>();
                cross.add(t1);
                cross.add(t2);
                list.add(cross);
            }
        }
        return list;
    }

    /** Returns pairing with just the first team of each set. */
    public Vector<Vector<Team>> getTopPairing(Vector<Team> sideOne, Vector<Team> sideTwo) {
        Vector<Vector<Team>> list = new Vector<Vector<Team>>();
        for (int i = 0; i < sideOne.size(); i++) {
            for (int j = 0; j < sideTwo.size(); j++) {
                if (i==0 || j==0) {
                    Vector<Team> cross = new Vector<Team>();
                    cross.add(sideOne.get(i));
                    cross.add(sideTwo.get(j));
                    list.add(cross);
                }
            }
        }
        return list;
    }

}
