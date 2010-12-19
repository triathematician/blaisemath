/*
 * SimultaneousLayout.java
 * Created Oct 2, 2010
 */

package org.bm.blaise.scio.graph.layout;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.LongitudinalGraph;

/**
 * Provides a layout mechanism for a longitudinal graph. The layout must contain
 * a pointer to the entire portion of the graph, although the <code>IterativeGraphLayout</code>
 * methods supported will only display a single slice of the graph.
 *
 * @author Elisha Peterson
 */
public class SimultaneousLayout implements IterativeGraphLayout {

    // BASE GRAPH

    /** Stores the underlying longitudinal graph */
    private LongitudinalGraph lGraph;
    /** Layout times */
    private List<Double> times;
    /** Current time of focus */
    double curTime;

    // CONSTANTS

    /** Distance outside which global force acts */
    private static final double MIN_GLOBAL_FORCE_DIST = 2;
    /** Maximum force that can be applied between nodes */
    private static final double MAX_FORCE = 100.0;
    /** Min distance between nodes to keep forces acting properly */
    private static final double MIN_DIST = .01;
    /** Max distance to apply repulsive force */
    private static final double MAX_REPEL_DIST = 5;
    /** Max velocity (limits per-step movement) */
    private static final double MAX_VELOCITY = 5;

    // STATE VARIABLES

    /** Current locations (array for longitudinal) */
    Map<Object, Point2D.Double[]> loc;
    /** Current velocities (array for longitudinal) */
    Map<Object, Point2D.Double[]> vel;

    /** Iteration number */
    int iteration = 0;
    /** Total energy */
    double energy = 0.0;
    /** Damping constant (the "cooling" parameter */
    double dampingC = 0.4;

    /** Temporary map holding positions to be updated in a future iteration */
    Map<Object, Point2D.Double> tempLoc = null;

    // ALGORITHM PARAMETERS

    /** Global attractive constant (keeps vertices closer to origin) */
    double globalC = 1;
    /** Attractive constant */
    double springC = 1;
    /** Repelling constant */
    double repulsiveC = 1;
    /** Time force constant */
    double timeC = 5;

    /** Natural spring length */
    double springL = .5;

    /** Time step per iteration */
    double stepT = 0.05;

    //
    // CONSTRUCTORS
    //

    /** Construct with an underlying longitudinal graph */
    public SimultaneousLayout(LongitudinalGraph lGraph) {
        this.lGraph = lGraph;
        times = lGraph.getTimes();
    }

    //
    // BEAN PATTERNS
    //

    public double getRepulsiveForce() { return repulsiveC; }
    public void setRepulsiveForce(double value) { repulsiveC = value; }
    public double getSpringForce() { return springC; }
    public void setSpringForce(double value) { springC = value; }
    public double getSpringLength() { return springL; }
    public void setSpringLength(double value) { springL = value; }
    public double getTimeForce() { return timeC; }
    public void setTimeForce(double value) { timeC = value; }
    public double getGlobalForce() { return globalC; }
    public void setGlobalForce(double value) { globalC = value; }
    public double getDampingConstant() { return dampingC; }
    public void setDampingConstant(double value) { dampingC = value; }
    public double getTimeStep() { return stepT; }
    public void setTimeStep(double value) { stepT = value; }

    public double getCurTime() { return curTime; }
    public void setCurTime(double time) { curTime = time; }

    /** @return number of times */
    private int getNTimes() { return times.size(); }
    
    //
    // INTERFACE METHODS
    //

    public double getCoolingParameter() { return dampingC; }
    public double getEnergyStatus() { return energy; }
    public int getIteration() { return iteration; }
    public Map<Object, Point2D.Double> getPositions() {
        HashMap<Object, Point2D.Double> result = new HashMap<Object, Point2D.Double>();
        int cur = times.indexOf(curTime);
        if (cur >= 0) {
            for (Object o : lGraph.slice(curTime, false).nodes())
                result.put(o, new Point2D.Double(loc.get(o)[cur].x, loc.get(o)[cur].y));
        } else {
            System.out.println("Attempt to retrieve nodes for unknown time " + curTime);
        }
        return result;
    }

    /** Returns array of length n of points at the origin */
    private Point2D.Double[] pArray(int n) {
        Point2D.Double[] result = new Point2D.Double[n];
        for (int i = 0; i < result.length; i++)
            result[i] = new Point2D.Double();
        return result;
    }

    public <V> void reset(Map<V, Point2D.Double> positions) {
        System.out.println("SL: reset");
        int nt = getNTimes();
        loc = new HashMap<Object, Point2D.Double[]>();
        vel = new HashMap<Object, Point2D.Double[]>();
        for (Object o : lGraph.getAllNodes()) {
            Point2D.Double[] locArr = pArray(nt);
            if (positions.containsKey((V)o)) {
                Point2D.Double loc0 = positions.get((V)o);
                for (int i = 0; i < nt; i++)
                    locArr[i] = new Point2D.Double(loc0.x, loc0.y);
            }
            loc.put(o, locArr);
            vel.put(o, pArray(nt));
        }

        iteration = 0;
        tempLoc = null;
    }

    public void requestPositions(Map<Object, Point2D.Double> positions) {
//        if (tempLoc == null)
//            tempLoc = positions;
//        else
//            tempLoc.putAll(positions);
    }

    public void iterate(Graph g) {
        long t0 = System.currentTimeMillis();

        int nt = getNTimes();
        if (nt == 0)
            return;

        // check for temporary location updates
        if (tempLoc != null) {
            int cur = times.indexOf(curTime);
            if (cur >= 0) {
                for (Object o : tempLoc.keySet()) {
                    loc.get(o)[cur] = tempLoc.get(o);
                    vel.get(o)[cur].setLocation(0, 0);
                }
            } else {
                System.out.println("Attempt to retrieve nodes for unknown time " + curTime);
            }
            tempLoc = null;
        }

        // check for additional nodes not present before
        for (Object v : lGraph.getAllNodes()) {
            if (!loc.containsKey(v)) {
                loc.put(v, pArray(nt));
                vel.put(v, pArray(nt));
            }
        }

        energy = 0;
        Graph gLast = null,
                gCur = null,
                gNext = lGraph.slice(times.get(0), false);
        HashMap<Integer, HashMap<Object, Point2D.Double>> forces = new HashMap<Integer, HashMap<Object, Point2D.Double>>();

        // add forces
        for (int time = 0; time < times.size(); time++) {
            forces.put(time, new HashMap<Object, Point2D.Double>());
            gLast = gCur;
            gCur = gNext;
            gNext = time < nt-1 ? lGraph.slice(times.get(time+1), false) : null;
            for (Object o : gCur.nodes()) {
                Point2D.Double net = new Point2D.Double();
                Point2D.Double pos = loc.get(o)[time];
                addGlobalForce(pos, net);
                addBasicForce(o, pos, gCur, time, net);
                addTimeForce(o, pos, gLast, gCur, gNext, time, net);
                addDamping(vel.get(o)[time], net);
                forces.get(time).put(o, net);
            }
        }

        // now apply forces to velocities and velocities to positions
        for (int time = 0; time < times.size(); time++) {
            for (Object o : lGraph.slice(times.get(time), false).nodes()) {
                Point2D.Double force = forces.get(time).get(o),
                        newVel = vel.get(o)[time],
                        newLoc = loc.get(o)[time];
                newVel.x += stepT * force.x;
                newVel.y += stepT * force.y;
                double spd = newVel.distance(0, 0);
                if (spd > MAX_VELOCITY) {
                    newVel.x *= MAX_VELOCITY / spd;
                    newVel.y *= MAX_VELOCITY / spd;
                    spd = MAX_VELOCITY;
                }
                newLoc.x += stepT * newVel.x;
                newLoc.y += stepT * newVel.y;
//                System.out.println("  ..  new loc for vertex " + o + " at time " + times.get(time) + ": " + newLoc);
                energy += .5 * spd * spd;
            }
        }

        System.out.println(String.format("Iteration %d, %d ms, energy %.3f", iteration, System.currentTimeMillis()-t0, energy));
        iteration ++;
    }

    //
    // UTILITIES
    //

    /**
     * Adds a global attractive force pushing vertex at specified location toward the origin
     * @param loc current location
     * @param net net force
     */
    private void addGlobalForce(Point2D.Double loc, Point2D.Double net) {
        double dist = loc.distance(0,0);
        if (dist > MIN_GLOBAL_FORCE_DIST) {
            net.x += -globalC * loc.x / dist;
            net.y += -globalC * loc.y / dist;
        }
    }

    /**
     * Adds basic repulsive forces (w/ distance squared) at a specified node,
     * attractive forces (w/ distance) between adjacent nodes
     * @param o the node under consideration
     * @param pos position of node
     * @param g graph of interest
     * @param time current time of interest
     * @param net net force
     */
    private void addBasicForce(Object o, Point2D.Double pos, Graph g, int time, Point2D.Double net) {
        for (Object o2 : g.nodes()) {
            if (o == o2)
                continue;
            Point2D.Double to2 = new Point2D.Double(loc.get(o2)[time].x - pos.x, loc.get(o2)[time].y - pos.y);
            double dist = to2.distance(0, 0);
            if (dist < MIN_DIST) {
                net.x += repulsiveC * Math.cos(o.hashCode());
                net.y += repulsiveC * Math.sin(o.hashCode());
            } else {
                if (dist < MAX_REPEL_DIST) {
                    double multiplier = Math.min(repulsiveC / (dist * dist), MAX_FORCE);
                    net.x -= multiplier * to2.x / dist;
                    net.y -= multiplier * to2.y / dist;
                }
                if (g.adjacent(o, o2)) {
                    double displacement = dist - springL;
                    net.x += springC * displacement * to2.x / dist;
                    net.y += springC * displacement * to2.y / dist;
                }
            }
        }
    }

    /** Adds time force attempting to align node to nodes adjacent in time */
    void addTimeForce(Object o, Point2D.Double pos, Graph gLast, Graph gCur, Graph gNext, int time, Point2D.Double net) {
        if (gLast != null && gLast.nodes().contains(o)) {
            Point2D.Double toLast = new Point2D.Double(loc.get(o)[time-1].x-pos.x, loc.get(o)[time-1].y-pos.y);
            net.x += timeC * toLast.x;
            net.y += timeC * toLast.y;
        }
        if (gNext != null && gNext.nodes().contains(o)) {
            Point2D.Double toNext = new Point2D.Double(loc.get(o)[time+1].x-pos.x, loc.get(o)[time+1].y-pos.y);
            net.x += timeC * toNext.x;
            net.y += timeC * toNext.y;
        }
    }

    /** Adds velocity damping force, proportional to/against current direction */
    void addDamping(Point2D.Double vel, Point2D.Double net) {
        net.x -= dampingC * vel.x;
        net.y -= dampingC * vel.y;
    }

}
