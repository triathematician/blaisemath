/*
 * SpringLayout.java
 * Created May 13, 2010
 */

package com.googlecode.blaisemath.graph.modules.layout;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.IterativeGraphLayout;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple energy-layout engine
 *
 * @author Elisha Peterson
 */
public class SpringLayout implements IterativeGraphLayout {
    
    /** Distance scale. This is the approximate length of an edge in the graph. */
    public static final double DIST_SCALE = 50;

    /** Distance outside which global force acts */
    private static final double MINIMUM_GLOBAL_FORCE_DISTANCE = DIST_SCALE;
    /** Maximum force that can be applied between nodes */
    private static final double MAX_FORCE = 100*DIST_SCALE*DIST_SCALE;
    /** Min distance to assume between nodes */
    private static final double MIN_DIST = DIST_SCALE/100;
    /** Max distance to apply repulsive force */
    private static final double MAX_REPEL_DIST = 2*DIST_SCALE;
    /** # of regions away from origin in x and y directions. Region size is determined by the maximum repel distance. */
    private static final int REGION_N = 5;

    // STATE VARIABLES

    /** Current locations */
    private final Map<Object, Point2D.Double> loc = Maps.newHashMap();
    /** Current velocities */
    private final Map<Object, Point2D.Double> vel = Maps.newHashMap();
    /** Regions used for localizing computation */
    private Region[][] regions;

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

    /** Algorithm parameters object */
    protected Parameters parameters = new Parameters();

    /**
     * Construct for specified graph, using specified default layout
     * @param g the graph to layout
     * @param initialLayout the initial layout mechanism
     * @param initialParameters the parameters for the initial layout
     */
    public SpringLayout(Graph g, StaticGraphLayout initialLayout, double... initialParameters) {
        try {
            reset(initialLayout.layout(g, initialParameters));
        } catch (InterruptedException ex) {
            Logger.getLogger(SpringLayout.class.getName()).log(Level.SEVERE, "Initial layout interrupted", ex);
        }
    }

    /** Construct using specified starting locations */
    public SpringLayout(Map<Object, Point2D.Double> positions) {
        reset(positions);
    }

    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    

    public Parameters getParameters() { 
        return parameters; 
    }
    
    public void setParameters(Parameters p) { 
        this.parameters = p; 
    }
    
    public double getCoolingParameter() { 
        return parameters.dampingC;
    }
    
    public double getEnergyStatus() { 
        return energy; 
    }
    
    public int getIteration() { 
        return iteration;
    }

    public synchronized Map<Object,Point2D.Double> getPositions() {
        return new HashMap<Object,Point2D.Double>(loc);
    }
    
    //</editor-fold>
    

    // <editor-fold defaultstate="collapsed" desc="IterativeGraphLayout interface methods (excluding main iteration)">

    public void reset(Map<?, Point2D.Double> positions) {
        loc.clear();
        vel.clear();
        for (Object v : positions.keySet()) {
            loc.put(v, positions.get(v) == null ? new Point2D.Double() : positions.get(v));
            vel.put(v, new Point2D.Double());
        }
        iteration = 0;
        tempLoc = null;
    }

    public synchronized void requestPositions(Map<?, Point2D.Double> positions, boolean resetNodes) {
        if (tempLoc == null) {
            tempLoc = new HashMap<Object,Point2D.Double>(positions);
        } else {
            tempLoc.putAll(positions);
        }
        this.resetNodes = resetNodes;
    }
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="IterativeGraphLayout interface methods (main iteration)">

    private void checkForNodeUpdate(Set nodes) throws ConcurrentModificationException {
        if (tempLoc != null) {
            for (Entry<?, Point2D.Double> en : tempLoc.entrySet()) {
                Object n = en.getKey();
                if (nodes.contains(n)) {
                    loc.put(n, en.getValue());
                    if (vel.containsKey(n)) {
                        vel.get(n).setLocation(0, 0);
                    } else {
                        vel.put(en, new Point2D.Double());
                    }
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

    public final <V> Map<V,Point2D.Double> iterate(Graph<V> g) throws ConcurrentModificationException {
        Set<V> nodes = g.nodes();

        if (g.isDirected()) {
            g = GraphUtils.copyAsUndirectedSparseGraph(g);
        }

        // check for temporary location updates
        checkForNodeUpdate(nodes);
        updateRegions();

        energy = 0;

        // helper variables
        Point2D.Double iLoc, iVel;

        // compute basic forces
        Map<V, Point2D.Double> forces = new HashMap<V,Point2D.Double>();
        for (V io : nodes) {
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
            addSpringForces(g, netForce, io, iLoc);
            addAdditionalForces(g, netForce, io, iLoc);
            forces.put(io, netForce);
        }
        
        // compute repulsive forces
        for (Region[] rr : regions) {
            for (Region r : rr) {
                for (Object io : r.pts.keySet()) {
                    addRepulsiveForces(g, r, forces.get(io), io, r.pts.get(io));
                }
            }
        }
            
        // check for infinite forces
        for (V io : nodes) {
            Point2D.Double netForce = forces.get(io);
            boolean test = !Double.isNaN(netForce.x) && !Double.isNaN(netForce.y) && !Double.isInfinite(netForce.x) && !Double.isInfinite(netForce.y);
            if (!test) {
                Logger.getLogger(SpringLayout.class.getName()).log(Level.SEVERE, 
                        "Computed infinite force: {0} for {1}", new Object[]{netForce, io});
            }
        }

        // adjusts velocity with damping;
        for (V io : nodes) {
            adjustVelocity(vel.get(io), forces.get(io));
        }

        // move nodes
        for (V o : nodes) {
            adjustPosition(loc.get(o), vel.get(o));
        }

        iteration ++;

        Map<V,Point2D.Double> res = new HashMap<V,Point2D.Double>();
        for (V v : nodes) {
            res.put(v, loc.get(v));
        }

//        if ((iteration <= 500 && iteration % 50 == 0) || (iteration % 500 == 0)) {
//            for (Region[] rr : regions) {
//                System.out.println(Joiner.on(" - ").join(Iterables.transform(Arrays.asList(rr), new Function<Region,String>(){
//                    public String apply(Region input) {
//                        return input.pts.size()+"";
//                    }
//                })));
//            }
//        }

        return res;
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
     * @param ireg the region for the node
     * @param sum vector representing the sum of forces (will be adjusted)
     * @param io the node of interest
     * @param iLoc location of first vertex
     */
    protected void addRepulsiveForces(Graph g, Region ireg, Point2D.Double sum, Object io, Point2D.Double iLoc) {
        Point2D.Double jLoc;
        double dist;
        if (ireg == null) {
            for (Object jo : g.nodes()) {
                if (!g.adjacent(io, jo)) {
                    jLoc = loc.get(jo);
                    dist = iLoc.distance(jLoc);
                    // repulsive force from other nodes
                    if (dist < MAX_REPEL_DIST) {
                        addRepulsiveForce(sum, io, iLoc, jo, jLoc, dist);
                    }
                }
            }
        } else {
            for (Region r : ireg.adj) {
                for (Entry<Object, Point2D.Double> jEntry : r.pts.entrySet()) {
                    Object jo = jEntry.getKey();
                    if (!g.adjacent(io, jo)) {
                        jLoc = jEntry.getValue();
                        dist = iLoc.distance(jLoc);
                        // repulsive force from other nodes
                        if (dist < MAX_REPEL_DIST) {
                            addRepulsiveForce(sum, io, iLoc, jo, jLoc, dist);
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds repulsive force at vertex i1 pointing away from vertex i2.
     * @param sum vector representing the sum of forces (will be adjusted)
     * @param io the node of interest
     * @param iLoc location of first vertex
     * @param jo the second node of interest
     * @param jLoc location of second vertex
     * @param dist distance between vertices
     */
    protected void addRepulsiveForce(Point2D.Double sum, Object io, Point2D.Double iLoc, Object jo, Point2D.Double jLoc, double dist) {
        if (iLoc == jLoc) {
            return;
        }
        if (dist == 0) {
            double angle = Math.random()*2*Math.PI;
            sum.x += parameters.repulsiveC * Math.cos(angle);
            sum.y += parameters.repulsiveC * Math.sin(angle);
        } else {
            double multiplier = Math.min(parameters.repulsiveC/(dist*dist), MAX_FORCE) / dist;
            sum.x += multiplier * (iLoc.x - jLoc.x);
            sum.y += multiplier * (iLoc.y - jLoc.y);
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
            if (!Objects.equal(o, io)) {
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
        if (dist == 0) {
            Logger.getLogger(SpringLayout.class.getName()).log(Level.WARNING,
                    "Distance 0 between {0} and {1}: {2}, {3}", new Object[]{io, jo, iLoc, jLoc});
            sum.x += parameters.springC / (MIN_DIST * MIN_DIST);
            sum.y += 0;
        } else {
            double displacement = dist - parameters.springL;
            sum.x += parameters.springC * displacement * (jLoc.x - iLoc.x) / dist;
            sum.y += parameters.springC * displacement * (jLoc.y - iLoc.y) / dist;
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
        // do nothing, provide hook for overriding classes
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
     * @param iLoc position to change
     * @param iVel velocity to adjust
     */
    protected void adjustPosition(Point2D.Double iLoc, Point2D.Double iVel) {
        iLoc.x += parameters.stepT * iVel.x;
        iLoc.y += parameters.stepT * iVel.y;
    }

    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Code to Handle Regions (reduces iteration time for repulsive forces from n^2 to n log n)">

    /** Return region for specified point */
    private Region getRegion(Point2D.Double p) {
        int ix = (int) ((p.x + REGION_N * MAX_REPEL_DIST) / MAX_REPEL_DIST);
        int iy = (int) ((p.y + REGION_N * MAX_REPEL_DIST) / MAX_REPEL_DIST);
        if (ix < 0 || ix > 2*REGION_N || iy < 0 || iy > 2*REGION_N) {
            return null;
        }
        return regions[ix][iy];
    }

    /** Generates the regions */
    private void updateRegions() {
        if (regions == null) {
            regions = new Region[2*REGION_N+1][2*REGION_N+1];
            for (int ix = -REGION_N; ix <= REGION_N; ix++) {
                for (int iy = -REGION_N; iy <= REGION_N; iy++) {
                    regions[ix+REGION_N][iy+REGION_N] = new Region(new Rectangle2D.Double(
                            ix*MAX_REPEL_DIST,iy*MAX_REPEL_DIST,MAX_REPEL_DIST,MAX_REPEL_DIST));
                }
            }
            for (int ix = -REGION_N; ix <= REGION_N; ix++) {
                for (int iy = -REGION_N; iy <= REGION_N; iy++) {
                    for (int ix2 = Math.max(ix-1,-REGION_N); ix2 <= Math.min(ix+1, REGION_N); ix2++) {
                        for (int iy2 = Math.max(iy-1,-REGION_N); iy2 <= Math.min(iy+1, REGION_N); iy2++) {
                            regions[ix+REGION_N][iy+REGION_N].adj.add(regions[ix2+REGION_N][iy2+REGION_N]);
                        }
                    }
                }
            }
        }
        for (int ix = -REGION_N; ix <= REGION_N; ix++) {
            for (int iy = -REGION_N; iy <= REGION_N; iy++) {
                regions[ix+REGION_N][iy+REGION_N].pts.clear();
            }
        }
        for (Entry<Object,Point2D.Double> en : loc.entrySet()) {
            Region r = getRegion(en.getValue());
            if (r != null) {
                r.pts.put(en.getKey(), en.getValue());
            }
        }
    }
    
    // </editor-fold>

    
    //<editor-fold defaultstate="collapsed" desc="Parameters INNER CLASS">
    
    /** Parameters of the SpringLayout algorithm */
    public static class Parameters {
        /** Global attractive constant (keeps vertices closer to origin) */
        double globalC = 1;
        /** Attractive constant */
        double springC = .1;
        /** Natural spring length */
        double springL = .5*DIST_SCALE;
        /** Repelling constant */
        double repulsiveC = DIST_SCALE*DIST_SCALE;

        /** 
         * Damping constant (the "cooling" parameter. Smaller values will make
         * movements less "jumpy".
         */
        double dampingC = 0.7;
        /** Time step per iteration */
        double stepT = 1;
        /** The maximum speed (movement per unit time) */
        double maxSpeed = 10*DIST_SCALE;

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
    
    /** Describes a region with a subset of the graph's nodes */
    private static class Region {
        Rectangle2D.Double bounds;
        HashMap<Object,Point2D.Double> pts = Maps.newHashMap();
        List<Region> adj = Lists.newArrayList();
        public Region(Rectangle2D.Double bounds) { 
            this.bounds = bounds; 
        }
    }
    
    //</editor-fold>
    
}
