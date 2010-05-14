/*
 * EnergyLayout.java
 * Created May 13, 2010
 */

package org.bm.blaise.scio.graph.layout;

import java.awt.geom.Point2D;
import org.bm.blaise.scio.graph.Edge;
import org.bm.blaise.scio.graph.NeighborSetInterface;
import org.bm.blaise.scio.graph.SimpleGraph;

/**
 * Simple energy-layout engine
 * 
 * @author Elisha Peterson
 */
public class EnergyLayout {

    /** Provides vertices and connections */
    NeighborSetInterface nsi;
    /** Provides initial layout */
    StaticGraphLayout initialLayout = StaticGraphLayout.RANDOM;

    /** Repelling constant */
    double repulsiveC = 2;
    /** Attractive constant */
    double springC = 1;
    /** Natural spring length */
    double springL = .5;
    /** Global attractive constant (keeps vertices closer to origin) */
    double globalC = .25;

    /** Damping constant */
    double dampingC = 0.75;
    /** Time step per iteration */
    double stepC = 0.5;

    /** Current locations */
    Point2D.Double[] loc;
    /** Current velocities */
    transient Point2D.Double[] vel;
    /** Total energy */
    transient double energy = 0.0;

    /** Construct for given graph */
    public EnergyLayout(NeighborSetInterface nsi, double initialRadius) {
        this.nsi = nsi;
        loc = initialLayout.layout(nsi, initialRadius);
        vel = new Point2D.Double[loc.length];
        for (int i = 0; i < loc.length; i++)
            vel[i] = new Point2D.Double();
    }

    /** Construct for given graph, using specified starting locations */
    public EnergyLayout(NeighborSetInterface nsi, Point2D.Double[] loc) {
        initialize(nsi, loc);
    }

    /** Initialzie layout for given graph, given starting locations */
    public void initialize(NeighborSetInterface nsi, Point2D.Double[] loc) {
        this.nsi = nsi;
        this.loc = loc;
        vel = new Point2D.Double[loc.length];
        for (int i = 0; i < loc.length; i++)
            vel[i] = new Point2D.Double();
    }


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


    /** @return current list of point locations */
    public Point2D.Double[] getPoints() {
        return loc;
    }

    /** Iterate the energy layout algorithm, moving the points slightly */
    public void iterate() {
        energy = 0;
        double nodeMass = 1;
        int size = nsi.getSize();
        for (int i = 0; i < size; i++) {
            Point2D.Double netForce = new Point2D.Double();
            addGlobalForce(netForce, i);
            for (int j = 0; j < size; j++) {
                // repulsive force from other nodes
                addRepulsiveForce(netForce, i, j);
                // attractive force from adjacencies
                if (!(nsi instanceof SimpleGraph) && nsi.adjacent(i, j))
                    addSpringForce(netForce, i, j);
            }
            if (nsi instanceof SimpleGraph) {
                for (Edge e : ((SimpleGraph)nsi).getEdges()) {
                    if (e.getSource() == i)
                        addSpringForce(netForce, i, e.getSink());
                    else if (e.getSink() == i)
                        addSpringForce(netForce, i, e.getSource());
                }
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
//        System.out.println("Total energy = " + energy);
    }

    /** Adds a global attractive force pushing vertex i1 toward the origin */
    void addGlobalForce(Point2D.Double sum, int i) {
        double dist = loc[i].distance(0, 0);
        sum.x += -globalC * loc[i].x / dist;
        sum.y += -globalC * loc[i].y / dist;
    }

    /** Adds repulsive force at vertex i1 pointing away from vertex i2. */
    void addRepulsiveForce(Point2D.Double sum, int i1, int i2) {
        if (i1 == i2)
            return;
        double distSq = loc[i1].distanceSq(loc[i2]);
        sum.x += repulsiveC * (loc[i1].x - loc[i2].x) / distSq / distSq;
        sum.y += repulsiveC * (loc[i1].y - loc[i2].y) / distSq / distSq;
    }

    /** Adds spring force at vertex i1 pointing to vertex i2. */
    void addSpringForce(Point2D.Double sum, int i1, int i2) {
        if (i1 == i2)
            return;
        double dist = loc[i1].distance(loc[i2]);
        double displacement = dist - springL;
        sum.x += springC * displacement * (loc[i2].x - loc[i1].x) / dist;
        sum.y += springC * displacement * (loc[i2].y - loc[i1].y) / dist;
    }


}
