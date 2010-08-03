/*
 * EnergyLayout.java
 * Created May 13, 2010
 */

package org.bm.blaise.scio.graph.layout;

import java.awt.geom.Point2D;
import java.util.ArrayList;
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
public class EnergyLayout implements IterativeGraphLayout {

    // CONSTANTS

    /** Maximum force that can be applied between nodes */
    private static final double MAX_FORCE = 100.0;
    /** Min distance to assume between nodes */
    private static final double MIN_DIST = .01;
    /** Distance outside which global force acts */
    private static final double MINIMUM_GLOBAL_FORCE_DISTANCE = 1;

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
    Map<? extends Object, Point2D.Double> tempLoc = null;

    // ALGORITHM PARAMETERS

    /** Repelling constant */
    double repulsiveC = 1;
    /** Attractive constant */
    double springC = 10;
    /** Natural spring length */
    double springL = .5;
    /** Global attractive constant (keeps vertices closer to origin) */
    double globalC = .5;

    /** Time step per iteration */
    double stepC = 0.1;

    //
    // CONSTRUCTORS
    //

    /**
     * Construct for specified graph, using specified default layout
     * @param g the graph to layout
     * @param initialLayout the initial layout mechanism
     * @param initialParameters the parameters for the initial layout
     */
    public <V> EnergyLayout(Graph<V> g, StaticGraphLayout initialLayout, double... initialParameters) {
        reset(initialLayout.layout(g, initialParameters));
    }

    /** Construct using specified starting locations */
    public <V> EnergyLayout(Map<V, Point2D.Double> positions) {
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
    public double getTimeStep() { return stepC; }
    public void setTimeStep(double value) { stepC = value; }

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
        for (Entry<V, Point2D.Double> en : positions.entrySet()) {
            loc.put(en.getKey(), en.getValue() == null ? new Point2D.Double() : en.getValue());
            vel.put(en.getKey(), new Point2D.Double());
        }
        iteration = 0;
        tempLoc = null;
    }

    public <V> void requestPositions(Map<V, Point2D.Double> positions) {
        tempLoc = positions;
    }

    @SuppressWarnings("element-type-mismatch")
    public <V> void iterate(Graph<V> g) {
        List<V> nodes = g.nodes();
        // check for temporary location updates
        if (tempLoc != null) {
            for (Entry<?, Point2D.Double> en : tempLoc.entrySet())
                if (nodes.contains(en.getKey())) {
                    loc.put(en.getKey(), en.getValue());
                    vel.put(en.getKey(), new Point2D.Double());
                }
            tempLoc = null;
        }
        // check for additional nodes not present before
        for (V v : nodes) {
            if (!loc.containsKey(v)) {
                loc.put(v, new Point2D.Double());
                vel.put(v, new Point2D.Double());
            }
        }
        energy = 0;
        double nodeMass = 1;

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
                jLoc = iEntry.getValue();
                dist = iLoc.distance(jLoc);
                if (iEntry.getKey() != jEntry.getKey()) {
                    // repulsive force from other nodes
                    addRepulsiveForce(netForce, iLoc, jLoc, dist);
                    assert !Double.isNaN(netForce.x) && !Double.isNaN(netForce.y) && !Double.isInfinite(netForce.x) && !Double.isInfinite(netForce.y);
                    // symmetric attractive force from adjacencies
                    if (g.adjacent((V) iEntry.getKey(), (V) jEntry.getKey())) {
                        addSpringForce(netForce, iLoc, jLoc, dist);
                        assert !Double.isNaN(netForce.x) && !Double.isNaN(netForce.y) && !Double.isInfinite(netForce.x) && !Double.isInfinite(netForce.y);
                    }
                }
            }
            iVel.x = dampingC * (iVel.x + stepC * netForce.x);
            iVel.y = dampingC * (iVel.y + stepC * netForce.y);
            // cap the total speed
            double speed = iVel.distance(0,0);
            if (speed > 2) {
                iVel.x /= speed;
                iVel.y /= speed;
                speed = 2;
            }
            iLoc.x += stepC * iVel.x;
            iLoc.y += stepC * iVel.y;
            energy += .5 * nodeMass * speed * speed;
        }
        iteration ++;
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
            sum.x += repulsiveC / (MIN_DIST * MIN_DIST);
            sum.y += 0;
        } else {
            double multiplier = Math.min(repulsiveC / (dist*dist), MAX_FORCE);
            if (multiplier > MAX_FORCE) multiplier = MAX_FORCE;
            sum.x += multiplier * (iLoc.x - iLoc.x) / dist;
            sum.y += multiplier * (iLoc.y - iLoc.y) / dist;
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
            sum.x += springC * displacement * (iLoc.x - iLoc.x) / dist;
            sum.y += springC * displacement * (iLoc.y - iLoc.y) / dist;
        }
    }


}
