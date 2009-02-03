/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sight2;

import java.util.Vector;

/**
 * Describes the search/matching steps
 * @author elisha
 */
public class MySim extends mas.SimStep {

    Vector<MyTeam> teams;
    Vector<Integer> captures;
    public static final int NTARGET = 2;
    public float[] targets;

    public MySim(MyTeam team){teams = new Vector<MyTeam>(); teams.add(team);}
    public MySim(Vector<MyTeam> teams){this.teams = teams; }

    /** Prior to iteration, generate the targets. */
    @Override
    public void preIterate() {
        if (targets==null) { targets = new float[NTARGET]; }
        for (int i = 0; i < targets.length; i++) {  targets[i] = (float) Math.random(); }
    }

    /** After iteration, make the proper assignments to determine captures. */
    @Override
    protected void postIterate() {
        if (captures==null) { captures = new Vector<Integer>(); }
        Vector<Vector<Float>> captureTimes = new Vector<Vector<Float>>();
        for (int i=0; i < teams.size(); i++) { captureTimes.add(teams.get(i).getCaptureTimes()); }
        for (int i=0; i < teams.size(); i++) { captures.add(0); }
        for (int j=0; j < targets.length; j++) {
            float bestTime = Float.MAX_VALUE;
            int bestTeam = -1;
            int bestPlayer = -1;
            for (int i=0; i < teams.size(); i++) {
                if (captureTimes.get(i).get(j) < bestTime) {
                    bestTime = captureTimes.get(i).get(j);
                    bestTeam = i;
                    bestPlayer = teams.get(i).bestCaps.get(j);
                }
            }
            // add the capture to the team with the minimum distance
            if (bestTeam != -1) {
                captures.set(bestTeam, captures.get(bestTeam)+1);
                teams.get(bestTeam).addCapture(bestPlayer);
            }
        }
    }
}
