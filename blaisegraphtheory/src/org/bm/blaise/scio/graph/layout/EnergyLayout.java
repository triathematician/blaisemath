/*
 * EnergyLayout.java
 * Created May 13, 2010
 */

package org.bm.blaise.scio.graph.layout;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private static final double GLOBAL_FORCE_DIST = 1;

    // STATE VARIABLES

    /** Current locations */
    ArrayList<Point2D.Double> loc;
    /** Current velocities */
    transient ArrayList<Point2D.Double> vel;
    /** Total energy */
    transient double energy = 0.0;

    // ALGORITHM PARAMETERS

    /** Repelling constant */
    double repulsiveC = 1;
    /** Attractive constant */
    double springC = 10;
    /** Natural spring length */
    double springL = .5;
    /** Global attractive constant (keeps vertices closer to origin) */
    double globalC = .5;

    /** Damping constant */
    double dampingC = 0.45;
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
    public EnergyLayout(Graph g, StaticGraphLayout initialLayout, double... initialParameters) {
        reset(g, initialLayout.layout(g, initialParameters));
    }

    /** Construct using specified starting locations */
    public EnergyLayout(Graph g, Point2D.Double[] loc) {
        reset(g, loc);
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

    /** 
     * Resets the locations of the nodes in the layout; sets all velocities to zero.
     * @param g the graph to use for layout
     * @param loc new node locations
     */
    public void reset(Graph g, Point2D.Double[] loc) {
        if (loc == null || g == null || g.nodes() == null || g.nodes().size() != loc.length)
            throw new IllegalArgumentException("Cannot reset layout with graph " + g + " and locations " + Arrays.toString(loc));
        this.loc = new ArrayList<Point2D.Double>();
        this.vel = new ArrayList<Point2D.Double>();
        for (Point2D.Double p : loc) {
            this.loc.add(p);
            this.vel.add(new Point2D.Double());
        }
    }

    /** Iterate the energy layout algorithm, moving the points slightly */
    public void iterate(Graph g) {
        List l = g.nodes();
        energy = 0;
        double nodeMass = 1;
        int order = g.order();

        // ensure appropriate number of elements; otherwise add to or delete from iLoc
        while (order > loc.size()) {
            this.loc.add(new Point2D.Double());
            this.vel.add(new Point2D.Double());
        }

        // compute energies
        Point2D.Double iLoc, iVel;
        for (int i = 0; i < order; i++) {
            iLoc = loc.get(i); iVel = vel.get(i);
            Point2D.Double netForce = new Point2D.Double();
            addGlobalForce(netForce, i);
            if (Double.isNaN(netForce.x)) System.out.println("infinite netforce 1");
            for (int j = 0; j < order; j++) {
                // repulsive force from other nodes
                addRepulsiveForce(netForce, i, j);
                if (Double.isNaN(netForce.x)) System.out.println("infinite netforce 2");
                // symmetric attractive force from adjacencies
                if (g.adjacent(l.get(i), l.get(j)) || g.adjacent(l.get(j), l.get(i))) {
                    addSpringForce(netForce, i, j);
                    if (Double.isNaN(netForce.x)) System.out.println("infinite netforce 3");
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
    }

    public Point2D.Double[] getPointArray() {
        return loc.toArray(new Point2D.Double[]{});
    }

    //
    // UTILITIES
    //

    /** Adds a global attractive force pushing vertex i1 toward the origin */
    private void addGlobalForce(Point2D.Double sum, int i) {
        Point2D.Double iLoc = loc.get(i);
        double dist = iLoc.distance(0, 0);
        if (dist > GLOBAL_FORCE_DIST) {
            sum.x += -globalC * iLoc.x / dist;
            sum.y += -globalC * iLoc.y / dist;
        }
    }

    /** Adds repulsive force at vertex i1 pointing away from vertex i2. */
    private void addRepulsiveForce(Point2D.Double sum, int i1, int i2) {
        if (i1 == i2)
            return;
        Point2D.Double iLoc1 = loc.get(i1);
        Point2D.Double iLoc2 = loc.get(i2);
        double dist = iLoc1.distance(iLoc2);
        if (dist == 0) {
            sum.x += repulsiveC / (MIN_DIST * MIN_DIST);
            sum.y += 0;
        } else {
            double multiplier = Math.min(repulsiveC / (dist*dist), MAX_FORCE);
            if (multiplier > MAX_FORCE) multiplier = MAX_FORCE;
            sum.x += multiplier * (iLoc1.x - iLoc2.x) / dist;
            sum.y += multiplier * (iLoc1.y - iLoc2.y) / dist;
        }
    }

    /** Adds spring force at vertex i1 pointing to vertex i2. */
    private void addSpringForce(Point2D.Double sum, int i1, int i2) {
        if (i1 == i2)
            return;
        Point2D.Double iLoc1 = loc.get(i1);
        Point2D.Double iLoc2 = loc.get(i2);
        double dist = iLoc1.distance(iLoc2);
        if (dist == 0) {
            sum.x += springC / (MIN_DIST * MIN_DIST);
            sum.y += 0;
        } else {
            double displacement = dist - springL;
            sum.x += springC * displacement * (iLoc2.x - iLoc1.x) / dist;
            sum.y += springC * displacement * (iLoc2.y - iLoc1.y) / dist;
        }
    }


}
