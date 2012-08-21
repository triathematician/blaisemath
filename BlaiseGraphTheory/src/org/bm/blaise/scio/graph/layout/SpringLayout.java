/*
 * SpringLayout.java
 * Created May 13, 2010
 */

package org.bm.blaise.scio.graph.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphUtils;
import org.bm.blaise.scio.graph.WeightedGraph;

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
    private static final double MAX_REPEL_DIST = 2;

    // STATE VARIABLES

    /** Current locations */
    private final Map<Object, Point2D.Double> loc = new HashMap<Object, Point2D.Double>();
    /** Current velocities */
    private final Map<Object, Point2D.Double> vel = new HashMap<Object, Point2D.Double>();
    
    /** Iteration number */
    int iteration = 0;
    /** Total energy */
    double energy = 0.0;
    
    /** The maximum weight between edges (used for weighted graphs only) */
    double maxWeight = 1;

    /** Temporary map holding positions to be updated in a future iteration */
    private Map<Object, Point2D.Double> tempLoc = null;
    /** Whether tempLoc should contain all the nodes or not */
    private boolean resetNodes = false;

    // ALGORITHM PARAMETERS

    //<editor-fold defaultstate="collapsed" desc="Parameters CLASS DEFN">
    public static class Parameters {
        /** Global attractive constant (keeps vertices closer to origin) */
        double globalC = .5;
        /** Attractive constant */
        double springC = 10;
        /** Natural spring length */
        double springL = .5;
        /** Repelling constant */
        double repulsiveC = 1;
        
        /** Damping constant (the "cooling" parameter */
        double dampingC = 0.5;
        /** Time step per iteration */
        double stepT = 0.1;
        /** The maximum speed (movement per unit time) */
        double maxSpeed = 2;

        public double getDampingConstant() {
            return dampingC;
        }

        public void setDampingConstant(double dampingC) {
            this.dampingC = dampingC;
        }

        public double getGlobalForce() {
            return globalC;
        }

        public void setGlobalForce(double globalC) {
            this.globalC = globalC;
        }

        public double getMaxSpeed() {
            return maxSpeed;
        }

        public void setMaxSpeed(double maxSpeed) {
            this.maxSpeed = maxSpeed;
        }

        public double getRepulsiveForce() {
            return repulsiveC;
        }

        public void setRepulsiveForce(double repulsiveC) {
            this.repulsiveC = repulsiveC;
        }

        public double getSpringForce() {
            return springC;
        }

        public void setSpringForce(double springC) {
            this.springC = springC;
        }

        public double getSpringLength() {
            return springL;
        }

        public void setSpringLength(double springL) {
            this.springL = springL;
        }

        public double getStepTime() {
            return stepT;
        }

        public void setStepTime(double stepT) {
            this.stepT = stepT;
        }
        
        
    }
    //</editor-fold>

    protected Parameters parameters = new Parameters();
    
    
    // <editor-fold defaultstate="collapsed" desc="Constructors">

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
    // </editor-fold>


    public Parameters getParameters() { return parameters; }
    public void setParameters(Parameters p) { this.parameters = p; }
    
    // <editor-fold defaultstate="collapsed" desc="IterativeGraphLayout interface methods (excluding main iteration)">

    public double getCoolingParameter() { return parameters.dampingC; }
    public double getEnergyStatus() { return energy; }
    public int getIteration() { return iteration; }
    public Map<Object,Point2D.Double> getPositions() { return loc; }

    public final <V> void reset(Map<V, Point2D.Double> positions) {
        loc.clear();
        vel.clear();
        for (V v : positions.keySet()) {
            loc.put(v, positions.get(v) == null ? new Point2D.Double() : positions.get(v));
            vel.put(v, new Point2D.Double());
        }
        iteration = 0;
        tempLoc = null;
    }

    public synchronized void requestPositions(Map<Object, Point2D.Double> positions, boolean resetNodes) {
        if (tempLoc == null)
            tempLoc = positions;
        else
            tempLoc.putAll(positions);
        this.resetNodes = resetNodes;
    }
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="IterativeGraphLayout interface methods (main iteration)">

    private void checkForNodeUpdate(List nodes) throws ConcurrentModificationException {
        if (tempLoc != null) {
            for (Entry<?, Point2D.Double> en : tempLoc.entrySet()) {
                Object n = en.getKey();
                if (nodes.contains(n)) {
                    loc.put(n, en.getValue());
                    if (vel.containsKey(n))
                        vel.get(n).setLocation(0, 0);
                    else
                        vel.put(en, new Point2D.Double());
                }
            }
            if (resetNodes) {
                Set keep = new HashSet(nodes);
                keep.addAll(tempLoc.keySet());
                loc.keySet().retainAll(keep);
                vel.keySet().retainAll(keep);
            }
            resetNodes = false;
            tempLoc = null;
        }
        
        for (Object v : nodes) {
            if (!loc.containsKey(v)) {
                loc.put(v, new Point2D.Double());
                vel.put(v, new Point2D.Double());
            }
        }
    }

    public final void iterate(Graph g) throws ConcurrentModificationException {
        long t0 = System.currentTimeMillis();
        List nodes = g.nodes();

        if (g.isDirected())
            g = GraphUtils.undirectedCopy(g);

        // check for temporary location updates
        checkForNodeUpdate(nodes);
        updateRegions();

        energy = 0;

        // helper variables
        Point2D.Double iLoc, iVel;

        // compute energies
        for (Object io : nodes) {
            iLoc = loc.get(io);
            if (iLoc == null) {
                iLoc = new Point2D.Double();
                loc.put(io, iLoc);
            }
            iVel = vel.get(io);
            if (iVel == null) {
                iVel = new Point2D.Double();
                vel.put(io, iVel);
            }
            Point2D.Double netForce = new Point2D.Double();

            addGlobalForce(netForce, io, iLoc);
            addRepulsiveForces(nodes, netForce, io, iLoc);
            addSpringForces(g, netForce, io, iLoc);
            addAdditionalForces(g, netForce, io, iLoc);

            boolean test = !Double.isNaN(netForce.x) && !Double.isNaN(netForce.y) && !Double.isInfinite(netForce.x) && !Double.isInfinite(netForce.y);
            assert test;
            if (!test)
                System.err.println("Computed infinite force: " + netForce + " for " + io);
            
            // adjusts velocity with damping;
            adjustVelocity(iVel, netForce);
        }

        // move nodes
        for (Object o : nodes)
            adjustPosition(loc.get(o), vel.get(o));
        
        iteration ++;
//        if (iteration % 100 == 0)
//            System.err.println("Iteration " + iteration + ", " + (System.currentTimeMillis()-t0) + "ms");
    }
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Methods to add on various forces">

    //
    // UTILITIES
    //

    /**
     * Adds a global attractive force pushing vertex at specified location toward the origin
     * @param sum vector representing the sum of forces (will be adjusted)
     * @param io the node of interest
     * @param iLoc location of first vertex
     */
    protected void addGlobalForce(Point2D.Double sum, Object io, Point2D.Double iLoc) {
        double dist = iLoc.distance(0,0);
        if (dist > MINIMUM_GLOBAL_FORCE_DISTANCE) {
            sum.x += -parameters.globalC * iLoc.x / dist;
            sum.y += -parameters.globalC * iLoc.y / dist;
        }
    }

    /**
     * Adds all repulsive forces for a particular vertex.
     * @param g the graph
     * @param sum vector representing the sum of forces (will be adjusted)
     * @param io the node of interest
     * @param iLoc location of first vertex
     */
    protected void addRepulsiveForces(List nodes, Point2D.Double sum, Object io, Point2D.Double iLoc) {
        Point2D.Double jLoc;
        double dist;
        Region ireg = getRegion(iLoc);
        if (ireg == null) {
            for (Object jo : nodes) {
                if (io != jo) {
                    jLoc = loc.get(jo);
                    dist = iLoc.distance(jLoc);
                    // repulsive force from other nodes
                    if (dist < MAX_REPEL_DIST)
                        addRepulsiveForce(sum, io, iLoc, jo, jLoc, dist);
                }
            }
        } else {
            for (Region r : getRegion(iLoc).adj) {
                for (Entry<Object, Point2D.Double> jEntry : r.pts.entrySet()) {
                    Object jo = jEntry.getKey();
                    if (io != jo) {
                        jLoc = jEntry.getValue();
                        dist = iLoc.distance(jLoc);
                        // repulsive force from other nodes
                        if (dist < MAX_REPEL_DIST)
                            addRepulsiveForce(sum, io, iLoc, jo, jLoc, dist);
                    }
                }
            }
        }
    }

    /**
     * Adds repulsive force at vertex i1 pointing away from vertex i2.
     * @param g the graph
     * @param sum vector representing the sum of forces (will be adjusted)
     * @param io the node of interest
     * @param iLoc location of first vertex
     * @param jo the second node of interest
     * @param jLoc location of second vertex
     * @param dist distance between vertices
     */
    protected void addRepulsiveForce(Point2D.Double sum, Object io, Point2D.Double iLoc, Object jo, Point2D.Double jLoc, double dist) {
        if (iLoc == jLoc)
            return;
        if (dist == 0) {
            double angle = Math.random()*2*Math.PI;
            sum.x += parameters.repulsiveC * Math.cos(angle);
            sum.y += parameters.repulsiveC * Math.sin(angle);
        } else {
            double multiplier = Math.min(parameters.repulsiveC / (dist*dist), MAX_FORCE);
            sum.x += multiplier * (iLoc.x - jLoc.x) / dist;
            sum.y += multiplier * (iLoc.y - jLoc.y) / dist;
        }
    }
    
    /** 
     * Adds symmetric attractive force from adjacencies
     * @param g the graph
     * @param sum the total force for the current object
     * @param io the node of interest
     * @param iLoc position of node of interest
     */
    protected void addSpringForces(Graph g, Point2D.Double sum, Object io, Point2D.Double iLoc) {
        Point2D.Double jLoc;
        double dist;
        for (Object o : g.neighbors(io)) {
            if (!(o.equals(io))) {
                jLoc = loc.get(o);
                if (jLoc == null) {
                    jLoc = new Point2D.Double();
                    loc.put(o, jLoc);
                }
                dist = iLoc.distance(jLoc);
                addSpringForce(g, sum, io, iLoc, o, jLoc, dist);
            }
        }
    }

    /** Adds spring force at vertex i1 pointing to vertex i2.
     * @param g the graph
     * @param sum vector representing the sum of forces (will be adjusted)
     * @param io the node of interest
     * @param iLoc location of first vertex
     * @param jo the second node of interest
     * @param jLoc location of second vertex
     * @param dist distance between vertices
     */
    protected void addSpringForce(Graph g, Point2D.Double sum, Object io, Point2D.Double iLoc, Object jo, Point2D.Double jLoc, double dist) {
        double wt = 1;
        if (g instanceof WeightedGraph) {
            Object w = ((WeightedGraph)g).getWeight(io, jo);
            if (w instanceof Number) {
                wt = Math.abs(((Number)w).doubleValue());
                maxWeight = Math.max(wt, maxWeight);
                wt /= maxWeight;
            }
        }
        if (wt == 0)
            return;
        if (dist == 0) {
            System.err.println("Distance 0 between " + io + " and " + jo + ": " + iLoc + ", " + jLoc);
            sum.x += parameters.springC / (MIN_DIST * MIN_DIST);
            sum.y += 0;
        } else {
            double displacement = dist - parameters.springL;
            sum.x += wt*wt * parameters.springC * displacement * (jLoc.x - iLoc.x) / dist;
            sum.y += wt*wt * parameters.springC * displacement * (jLoc.y - iLoc.y) / dist;
//            double displacement = dist - (springL / wt);
//            sum.x += springC * Math.min(displacement, springL*2) * (jLoc.x - iLoc.x) / dist;
//            sum.y += springC * Math.min(displacement, springL*2) * (jLoc.y - iLoc.y) / dist;
        }
    }

    /**
     * Provides hook for subclasses to add on any additional forces they desire. Does nothing here.
     * @param g the graph
     * @param sum the total force for the current object
     * @param io the node of interest
     * @param iLoc position of node of interest
     */
    protected void addAdditionalForces(Graph g, Point2D.Double sum, Object io, Point2D.Double iLoc) {
    }

    /**
     * Adjusts the velocity vector with the specified net force, possibly by applying damping.
     * SpringLayout uses iVel = dampingC*(iVel + stepT*netForce),
     *  and caps maximum speed.
     * @param iVel velocity to adjust
     * @param netForce force vector to use
     */
    protected void adjustVelocity(Point2D.Double iVel, Point2D.Double netForce) {
        iVel.x = parameters.dampingC * (iVel.x + parameters.stepT * netForce.x);
        iVel.y = parameters.dampingC * (iVel.y + parameters.stepT * netForce.y);
        double speed = iVel.x*iVel.x+iVel.y*iVel.y;

        if (speed > parameters.maxSpeed) {
            iVel.x *= parameters.maxSpeed/speed;
            iVel.y *= parameters.maxSpeed/speed;
            speed = parameters.maxSpeed;
        }

        energy += .5 * speed * speed;
    }

    /**
     * Adjusts a node's position using specified initial position and velocity.
     * SpringLayout uses iLoc += stepT*iVel
     * @param iVel velocity to adjust
     * @param netForce force vector to use
     */
    protected void adjustPosition(Point2D.Double iLoc, Point2D.Double iVel) {
        iLoc.x += parameters.stepT * iVel.x;
        iLoc.y += parameters.stepT * iVel.y;
    }

    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Code to Handle Regions (reduces iteration time for repulsive forces from n^2 to n log n)">

    transient List<List<Region>> regions;

    /** Return region for specified point */
    private Region getRegion(Point2D.Double p) {
        int ix = (int) ((p.x + 10 * MAX_REPEL_DIST) / MAX_REPEL_DIST);
        int iy = (int) ((p.y + 10 * MAX_REPEL_DIST) / MAX_REPEL_DIST);
        if (ix < 0 || ix > 20 || iy < 0 || iy > 20)
            return null;
        return regions.get(ix).get(iy);
    }

    /** Generates the regions */
    private void updateRegions() {
        if (regions == null) {
            regions = new ArrayList<List<Region>>();
            List<Region> r;
            for (int ix = -10; ix <= 10; ix++) {
                regions.add(r = new ArrayList<Region>());
                for (int iy = -10; iy <= 10; iy++)
                    r.add(new Region(new Rectangle2D.Double(ix*MAX_REPEL_DIST,iy*MAX_REPEL_DIST,MAX_REPEL_DIST,MAX_REPEL_DIST)));
            }
            for (int ix = -10; ix <= 10; ix++) {
                r = new ArrayList<Region>();
                for (int iy = -10; iy <= 10; iy++) {
                    for (int ix2 = Math.max(ix-1,-10); ix2 <= Math.min(ix+1, 10); ix2++)
                        for (int iy2 = Math.max(iy-1,-10); iy2 <= Math.min(iy+1, 10); iy2++)
                            regions.get(ix+10).get(iy+10).adj.add(regions.get(ix2+10).get(iy2+10));
                }
            }
        }
        for (List<Region> l : regions) for (Region r : l) r.pts.clear();
        for (Entry<Object,Point2D.Double> en : loc.entrySet()) {
            Region r = getRegion(en.getValue());
            if (r != null)
                r.pts.put(en.getKey(), en.getValue());
        }
    }

    private class Region {
        Rectangle2D.Double bounds;
        HashMap<Object,Point2D.Double> pts = new HashMap<Object,Point2D.Double>();
        List<Region> adj = new ArrayList<Region>();
        public Region(Rectangle2D.Double bounds) { this.bounds = bounds; }
    }
    // </editor-fold>

}
