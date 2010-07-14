/*
 * EnergyLayout.java
 * Created May 13, 2010
 */

package org.bm.blaise.scio.graph.layout;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import org.bm.blaise.scio.graph.Graph;

/**
 * Simple energy-layout engine
 * 
 * @author Elisha Peterson
 */
public class EnergyLayout implements IterativeGraphLayout {

    // STATE VARIABLES

    /** Current locations */
    Point2D.Double[] loc;
    /** Current velocities */
    transient Point2D.Double[] vel;
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
    double dampingC = 0.75;
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
     * @param loc new node locations
     */
    public void reset(Graph g, Point2D.Double[] loc) {
        if (loc == null || g == null || g.nodes() == null || g.nodes().size() != loc.length)
            throw new IllegalArgumentException("Cannot reset layout with graph " + g + " and locations " + Arrays.toString(loc));
        this.loc = loc;
        vel = new Point2D.Double[loc.length];
        for (int i = 0; i < loc.length; i++)
            vel[i] = new Point2D.Double();
    }

    /** Iterate the energy layout algorithm, moving the points slightly */
    public Point2D.Double[] iterate(Graph g) {
        List l = g.nodes();
        energy = 0;
        double nodeMass = 1;
        int order = g.order();
        for (int i = 0; i < order; i++) {
            Point2D.Double netForce = new Point2D.Double();
            addGlobalForce(netForce, i);
            for (int j = 0; j < order; j++) {
                // repulsive force from other nodes
                addRepulsiveForce(netForce, i, j);
                // attractive force from adjacencies
                if (g.adjacent(l.get(i), l.get(j)))
                    addSpringForce(netForce, i, j);
            }
            vel[i].x = dampingC * (vel[i].x + stepC * netForce.x);
            vel[i].y = dampingC * (vel[i].y + stepC * netForce.y);
            // cap the total speed
            double speed = vel[i].distance(0,0);
            if (speed > 2) {
                vel[i].x /= speed;
                vel[i].y /= speed;
                speed = 2;
            }
            loc[i].x += stepC * vel[i].x;
            loc[i].y += stepC * vel[i].y;
            energy += .5 * nodeMass * speed * speed;
        }
        return loc;
    }

    public Point2D.Double[] getPoints() {
        return loc;
    }

    //
    // UTILITIES
    //

    /** Adds a global attractive force pushing vertex i1 toward the origin */
    private void addGlobalForce(Point2D.Double sum, int i) {
        double dist = loc[i].distance(0, 0);
        sum.x += -globalC * loc[i].x / dist;
        sum.y += -globalC * loc[i].y / dist;
    }

    /** Adds repulsive force at vertex i1 pointing away from vertex i2. */
    private void addRepulsiveForce(Point2D.Double sum, int i1, int i2) {
        if (i1 == i2)
            return;
        double distSq = loc[i1].distanceSq(loc[i2]);
        sum.x += repulsiveC * (loc[i1].x - loc[i2].x) / distSq / distSq;
        sum.y += repulsiveC * (loc[i1].y - loc[i2].y) / distSq / distSq;
    }

    /** Adds spring force at vertex i1 pointing to vertex i2. */
    private void addSpringForce(Point2D.Double sum, int i1, int i2) {
        if (i1 == i2)
            return;
        double dist = loc[i1].distance(loc[i2]);
        double displacement = dist - springL;
        sum.x += springC * displacement * (loc[i2].x - loc[i1].x) / dist;
        sum.y += springC * displacement * (loc[i2].y - loc[i1].y) / dist;
    }


}
