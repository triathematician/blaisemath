/*
 * SpringLayout.java
 * Created May 13, 2010
 */

package org.bm.blaise.scio.graph.layout;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bm.blaise.scio.graph.Graph;

/**
 * Simple energy-layout engine
 * 
 * @author Elisha Peterson
 */
public class SpringLayout implements IterativeGraphLayout {

    // CONSTANTS

    /** Distance outside which global force acts */
    private static final double MINIMUM_GLOBAL_FORCE_DISTANCE = 1;
    /** Maximum force that can be applied between nodes */
    private static final double MAX_FORCE = 100.0;
    /** Min distance to assume between nodes */
    private static final double MIN_DIST = .01;
    /** Max distance to apply repulsive force */
    private static final double MAXIMUM_REPEL_DISTANCE = 5;

    // STATE VARIABLES

    /** Current locations */
    Map<Object, Point2D.Double> loc;
    /** Current velocities */
    Map<Object, Point2D.Double> vel;
    
    /** Iteration number */
    int iteration = 0;
    /** Total energy */
    double energy = 0.0;
    /** Damping constant (the "cooling" parameter */
    double dampingC = 0.5;

    /** Temporary map holding positions to be updated in a future iteration */
    Map<Object, Point2D.Double> tempLoc = null;

    // ALGORITHM PARAMETERS

    /** Global attractive constant (keeps vertices closer to origin) */
    double globalC = .5;
    /** Attractive constant */
    double springC = 10;
    /** Repelling constant */
    double repulsiveC = 1;

    /** Natural spring length */
    double springL = .5;

    /** Time step per iteration */
    double stepT = 0.1;

    //
    // CONSTRUCTORS
    //

    /**
     * Construct for specified graph, using specified default layout
     * @param g the graph to layout
     * @param initialLayout the initial layout mechanism
     * @param initialParameters the parameters for the initial layout
     */
    public SpringLayout(Graph g, StaticGraphLayout initialLayout, double... initialParameters) {
        reset(initialLayout.layout(g, initialParameters));
    }

    /** Construct using specified starting locations */
    public SpringLayout(Map<Object, Point2D.Double> positions) {
        reset(positions);
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
    public double getGlobalForce() { return globalC; }
    public void setGlobalForce(double value) { globalC = value; }
    public double getDampingConstant() { return dampingC; }
    public void setDampingConstant(double value) { dampingC = value; }
    public double getTimeStep() { return stepT; }
    public void setTimeStep(double value) { stepT = value; }

    //
    // INTERFACE METHODS
    //

    public double getCoolingParameter() { return dampingC; }
    public double getEnergyStatus() { return energy; }
    public int getIteration() { return iteration; }
    public Map<Object,Point2D.Double> getPositions() { return loc; }

    public final <V> void reset(Map<V, Point2D.Double> positions) {
        loc = new HashMap<Object, Point2D.Double>();
        vel = new HashMap<Object, Point2D.Double>();
        for (V v : positions.keySet()) {
            loc.put(v, positions.get(v) == null ? new Point2D.Double() : positions.get(v));
            vel.put(v, new Point2D.Double());
        }
        iteration = 0;
        tempLoc = null;
    }

    public void requestPositions(Map<Object, Point2D.Double> positions) {
        if (tempLoc == null)
            tempLoc = positions;
        else
            tempLoc.putAll(positions);
    }

    public void iterate(Graph g) {
        long t0 = System.currentTimeMillis();

        List nodes = g.nodes();
        boolean directed = g.isDirected();

        // check for temporary location updates
        if (tempLoc != null) {
            for (Entry<?, Point2D.Double> en : tempLoc.entrySet())
                if (nodes.contains(en.getKey())) {
                    loc.put(en.getKey(), en.getValue());
                    vel.get(en.getKey()).setLocation(0, 0);
                }
            tempLoc = null;
        }

        // check for additional nodes not present before
        for (Object v : nodes) {
            if (!loc.containsKey(v)) {
                loc.put(v, new Point2D.Double());
                vel.put(v, new Point2D.Double());
            }
        }

        energy = 0;

        // helper variables
        Point2D.Double iLoc, iVel, jLoc;
        double dist;
        // compute energies
        for (Entry<Object, Point2D.Double> iEntry : loc.entrySet()) {
            iLoc = iEntry.getValue();
            iVel = vel.get(iEntry.getKey());
            Point2D.Double netForce = new Point2D.Double();
            // global force
            addGlobalForce(netForce, iLoc);
            assert !Double.isNaN(netForce.x) && !Double.isNaN(netForce.y) && !Double.isInfinite(netForce.x) && !Double.isInfinite(netForce.y);
            for (Entry<Object, Point2D.Double> jEntry : loc.entrySet()) {
                if (iEntry.getKey() != jEntry.getKey()) {
                    jLoc = jEntry.getValue();
                    dist = iLoc.distance(jLoc);
                    // repulsive force from other nodes
                    if (dist < MAXIMUM_REPEL_DISTANCE)
                        addRepulsiveForce(netForce, iLoc, jLoc, dist);
                    assert !Double.isNaN(netForce.x) && !Double.isNaN(netForce.y) && !Double.isInfinite(netForce.x) && !Double.isInfinite(netForce.y);
                    // symmetric attractive force from adjacencies
                    if (g.adjacent(iEntry.getKey(), jEntry.getKey()) || 
                            (directed && g.adjacent(jEntry.getKey(), iEntry.getKey()))) {
                        addSpringForce(netForce, iLoc, jLoc, dist);
                        assert !Double.isNaN(netForce.x) && !Double.isNaN(netForce.y) && !Double.isInfinite(netForce.x) && !Double.isInfinite(netForce.y);
                    }
                }
            }
            iVel.x = dampingC * (iVel.x + stepT * netForce.x);
            iVel.y = dampingC * (iVel.y + stepT * netForce.y);
            // cap the total speed
            double speed = iVel.distance(0,0);
            if (speed > 2) {
                iVel.x /= speed;
                iVel.y /= speed;
                speed = 2;
            }
            iLoc.x += stepT * iVel.x;
            iLoc.y += stepT * iVel.y;
            energy += .5 * speed * speed;
        }
        iteration ++;
//        System.out.println("Iteration " + iteration + ", " + (System.currentTimeMillis()-t0) + "ms");
    }

    //
    // UTILITIES
    //

    /**
     * Adds a global attractive force pushing vertex at specified location toward the origin
     * @param sum vector representing the sum of forces (will be adjusted)
     * @param iLoc location of a single vertex
     */
    private void addGlobalForce(Point2D.Double sum, Point2D.Double iLoc) {
        double dist = iLoc.distance(0,0);
        if (dist > MINIMUM_GLOBAL_FORCE_DISTANCE) {
            sum.x += -globalC * iLoc.x / dist;
            sum.y += -globalC * iLoc.y / dist;
        }
    }

    /**
     * Adds repulsive force at vertex i1 pointing away from vertex i2.
     * @param sum vector representing the sum of forces (will be adjusted)
     * @param iLoc location of first vertex
     * @param jLoc location of second vertex
     * @param dist distance between vertices
     */
    private void addRepulsiveForce(Point2D.Double sum, Point2D.Double iLoc, Point2D.Double jLoc, double dist) {
        if (iLoc == jLoc)
            return;
        if (dist == 0) {
            double angle = Math.random()*2*Math.PI;
            sum.x += repulsiveC * Math.cos(angle);
            sum.y += repulsiveC * Math.sin(angle);
        } else {
            double multiplier = Math.min(repulsiveC / (dist*dist), MAX_FORCE);
            sum.x += multiplier * (iLoc.x - jLoc.x) / dist;
            sum.y += multiplier * (iLoc.y - jLoc.y) / dist;
        }
    }

    /** Adds spring force at vertex i1 pointing to vertex i2.
     * @param sum vector representing the sum of forces (will be adjusted)
     * @param iLoc location of first vertex
     * @param jLoc location of second vertex
     * @param dist distance between vertices
     */
    private void addSpringForce(Point2D.Double sum, Point2D.Double iLoc, Point2D.Double jLoc, double dist) {
        if (dist == 0) {
            sum.x += springC / (MIN_DIST * MIN_DIST);
            sum.y += 0;
        } else {
            double displacement = dist - springL;
            sum.x += springC * displacement * (jLoc.x - iLoc.x) / dist;
            sum.y += springC * displacement * (jLoc.y - iLoc.y) / dist;
        }
    }

    //
    // STATIC INSTANCES
    //

    /** A static version of the spring layout algorithm */
    public static final StaticSpringLayout STATIC_INSTANCE = new StaticSpringLayout();

    /**
     * Provides a static implementation of the spring layout, formed by iterating
     * with a cooling parameter until reaching a low-energy state.
     */
    public static class StaticSpringLayout implements StaticGraphLayout {
        int minSteps = 100;
        int maxSteps = 5000;
        double energyChangeThreshold = 5e-7;
        double coolStart = 0.65;
        double coolEnd = 0.1;

        public Map<Object, Point2D.Double> layout(Graph g, double... parameters) {
            SpringLayout sl = new SpringLayout(g, StaticGraphLayout.CIRCLE, 5);
            double lastEnergy = Double.MAX_VALUE;
            double energyChange = 9999;
            int step = 0;
            while (step < minSteps || (step < maxSteps && Math.abs(energyChange) > energyChangeThreshold)) {
                // adjust cooling parameter
                double cool01 = 1-step*step/(maxSteps*maxSteps);
                sl.dampingC = coolStart*cool01 + coolEnd*(1-cool01);
                sl.iterate(g);
                energyChange = sl.energy - lastEnergy;
                lastEnergy = sl.energy;
                step++;
                if (step % 50 == 0)
                    System.out.println("step = " + step + ", energy = " + lastEnergy);
            }
            System.out.println("Stopped @ " + step + " steps.");
            return sl.getPositions();
        }

    }

}
