package com.googlecode.blaisemath.graph.mod.layout;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.graph.Graph;
import com.googlecode.blaisemath.annotation.InvokedFromThread;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.IterativeGraphLayout;

import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Graph layout modeled after repulsive charges between nodes, and spring forces between nodes.
 *
 * @author Elisha Peterson
 */
public class SpringLayout implements IterativeGraphLayout<SpringLayoutParameters, SpringLayoutState> {
    
    private static final Logger LOG = Logger.getLogger(SpringLayout.class.getName());

    @Override
    public String toString() {
        return "Spring layout algorithm";
    }

    @Override
    public SpringLayoutState createState() {
        return new SpringLayoutState();
    }

    @Override
    public SpringLayoutParameters createParameters() {
        return new SpringLayoutParameters();
    }

    @InvokedFromThread("unknown")
    @Override
    public final synchronized <C> double iterate(Graph<C> og, SpringLayoutState state, SpringLayoutParameters params) {
        Graph<C> g = og.isDirected() ? GraphUtils.copyUndirected(og) : og;
        Set<C> nodes = g.nodes();
        Set<C> pinned = params.getConstraints().getPinnedNodes();
        Set<C> unpinned = Sets.difference(nodes, pinned).immutableCopy();
        double energy;

        state.nodeLocationSync(nodes);
        state.updateRegions(params.maxRepelDist);

        Map<C, Point2D.Double> forces = Maps.newHashMap();
        computeNonRepulsiveForces(g, nodes, pinned, forces, state, params);
        computeRepulsiveForces(pinned, forces, state, params);
        checkForces(unpinned, forces);
        energy = move(g, unpinned, forces, state, params);
        
        return energy;
    }
    
    protected <C> void computeNonRepulsiveForces(Graph<C> g, Set<C> nodes, 
            Set<C> pinned, Map<C, Point2D.Double> forces, 
            SpringLayoutState<C> state, SpringLayoutParameters params) {
        for (C io : nodes) {
            Point2D.Double iLoc = state.getLoc(io);
            if (iLoc == null) {
                iLoc = newNodeLocation(g, io, state, params);
                state.putLoc(io, iLoc);
            }
            Point2D.Double iVel = state.getVel(io);
            if (iVel == null) {
                iVel = new Point2D.Double();
                state.putVel(io, iVel);
            }

            if (!pinned.contains(io)) {
                Point2D.Double netForce = new Point2D.Double();
                addGlobalForce(netForce, iLoc, params);
                addSpringForces(g, netForce, io, iLoc, state, params);
                addAdditionalForces(g, netForce, io, iLoc, state, params);
                forces.put(io, netForce);
            }
        }
    }
    
    protected <C> void addAdditionalForces(Graph<C> g, 
            Point2D.Double sum, C io, Point2D.Double iLoc, 
            SpringLayoutState<C> state, SpringLayoutParameters params) {
        // hook for adding additional forces per the needs of child layouts
    }
    
    protected <C> void computeRepulsiveForces(Set<C> pinned, Map<C, Point2D.Double> forces, 
            SpringLayoutState<C> state, SpringLayoutParameters params) {
        for (LayoutRegion<C>[] rr : state.regions) {
            for (LayoutRegion<C> r : rr) {
                for (C io : r.points()) {
                    if (!pinned.contains(io)) {
                        addRepulsiveForces(r, forces.get(io), io, r.get(io), params);
                    }
                }
            }
        }
        for (C io : state.oRegion.points()) {
            if (!pinned.contains(io)) {
                addRepulsiveForces(state.oRegion, forces.get(io), io, state.oRegion.get(io), params);
            }
        }
    }

    //region STATIC METHODS

    /**
     * This method returns a position for a node that doesn't currently have a position.
     * @param node the node to get new location of
     */
    private static <C> Point2D.Double newNodeLocation(Graph<C> g, C node, 
            SpringLayoutState<C> state, SpringLayoutParameters params) {
        double len = params.springL;
        double sx = 0;
        double sy = 0;
        int n = 0;
        for (C o : g.adjacentNodes(node)) {
            Point2D.Double p = state.getLoc(o);
            if (p != null) {
                sx += p.x;
                sy += p.y;
                n++;
            }
        }
        if (n == 0) {
            return new Point2D.Double(sx+2*len*Math.random(), sy+2*len*Math.random());
        } else if (n == 1) {
            return new Point2D.Double(sx+len*Math.random(), sy+len*Math.random());
        } else {
            return new Point2D.Double(sx/n, sy/n);
        }
    }

    /**
     * Adds a global attractive force pushing vertex at specified location toward the origin
     * @param sum vector representing the sum of forces (will be adjusted)
     * @param iLoc location of first vertex
     * @param params algorithm parameters
     */
    private static <C> void addGlobalForce(Point2D.Double sum, Point2D.Double iLoc, SpringLayoutParameters params) {
        double dist = iLoc.distance(0,0);
        if (dist > params.minGlobalForceDist) {
            sum.x += -params.globalC * iLoc.x / dist;
            sum.y += -params.globalC * iLoc.y / dist;
        }
    }

    /**
     * Adds all repulsive forces for a particular vertex.
     * @param region the region for the node
     * @param sum vector representing the sum of forces (will be adjusted)
     * @param io the node of interest
     * @param iLoc location of first vertex
     * @param params algorithm parameters
     */
    private static <C> void addRepulsiveForces(LayoutRegion<C> region, Point2D.Double sum, C io, Point2D.Double iLoc,
            SpringLayoutParameters params) {
        Point2D.Double jLoc;
        double dist;
        for (LayoutRegion<C> r : region.adjacentRegions()) {
            for (Entry<C, Point2D.Double> jEntry : r.entries()) {
                C jo = jEntry.getKey();
                if (io != jo) {
                    jLoc = jEntry.getValue();
                    dist = iLoc.distance(jLoc);
                    // repulsive force from other nodes
                    if (dist < params.maxRepelDist) {
                        addRepulsiveForce(sum, iLoc, jLoc, dist, params);
                    }
                }
            }
        }
    }

    /**
     * Adds repulsive force at vertex i1 pointing away from vertex i2.
     * @param sum vector representing the sum of forces (will be adjusted)
     * @param iLoc location of first vertex
     * @param jLoc location of second vertex
     * @param dist distance between vertices
     * @param params algorithm parameters
     */
    private static <C> void addRepulsiveForce(Point2D.Double sum, Point2D.Double iLoc, Point2D.Double jLoc, double dist,
                                              SpringLayoutParameters params) {
        if (iLoc == jLoc) {
            return;
        }
        if (dist == 0) {
            double angle = Math.random()*2*Math.PI;
            sum.x += params.repulsiveC * Math.cos(angle);
            sum.y += params.repulsiveC * Math.sin(angle);
        } else {
            double multiplier = Math.min(params.repulsiveC/(dist*dist), params.maxForce) / dist;
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
     * @param params algorithm parameters
     */
    private static <C> void addSpringForces(Graph<C> g,
            Point2D.Double sum, C io, Point2D.Double iLoc, 
            SpringLayoutState<C> state, SpringLayoutParameters params) {
        Point2D.Double jLoc;
        double dist;
        for (C o : g.adjacentNodes(io)) {
            if (!Objects.equal(o, io)) {
                jLoc = state.getLoc(o);
                dist = iLoc.distance(jLoc);
                addSpringForce(sum, io, iLoc, o, jLoc, dist, params);
            }
        }
    }

    /** Adds spring force at vertex i1 pointing to vertex i2.
     * @param sum vector representing the sum of forces (will be adjusted)
     * @param io the node of interest
     * @param iLoc location of first vertex
     * @param jo the second node of interest
     * @param jLoc location of second vertex
     * @param dist distance between vertices
     */
    private static <C> void addSpringForce(Point2D.Double sum, C io, Point2D.Double iLoc, C jo, Point2D.Double jLoc,
                                           double dist, SpringLayoutParameters params) {
        if (dist == 0) {
            LOG.log(Level.WARNING, "Distance 0 between {0} and {1}: {2}, {3}", 
                    new Object[]{io, jo, iLoc, jLoc});
            sum.x += params.springC / (params.minDist * params.minDist);
            sum.y += 0;
        } else {
            double displacement = dist - params.springL;
            sum.x += params.springC * displacement * (jLoc.x - iLoc.x) / dist;
            sum.y += params.springC * displacement * (jLoc.y - iLoc.y) / dist;
        }
    }
    
    private static <C> void checkForces(Set<C> unpinned, Map<C, Point2D.Double> forces) {
        for (C io : unpinned) {
            Point2D.Double netForce = forces.get(io);
            boolean test = !Double.isNaN(netForce.x) && !Double.isNaN(netForce.y) 
                    && !Double.isInfinite(netForce.x) && !Double.isInfinite(netForce.y);
            if (!test) {
                LOG.log(Level.SEVERE, "Computed infinite force: {0} for {1}", new Object[]{netForce, io});
            }
        }
    }
    
    private static <C> double move(Graph<C> g, Set<C> unpinned, Map<C, Point2D.Double> forces, 
            SpringLayoutState<C> state, SpringLayoutParameters params) {
        double energy = 0;
        for (C io : unpinned) {
            energy += adjustVelocity(state.getVel(io), forces.get(io), g.degree(io), params);
        }
        for (C io : unpinned) {
            adjustPosition(state.getLoc(io), state.getVel(io), params.stepT);
        }
        return energy;
    }

    /**
     * Adjusts the velocity vector with the specified net force, possibly by applying damping.
     * SpringLayout uses iVel = dampingC*(iVel + stepT*netForce),
     *  and caps maximum speed.
     * @param iVel velocity to adjust
     * @param netForce force vector to use
     * @param iDeg node's degree, used to increase damping for high degree nodes
     * @param params layout parameters
     * @return node's energy
     */
    private static double adjustVelocity(Point2D.Double iVel, Point2D.Double netForce, double iDeg, SpringLayoutParameters params) {
        double maxForce = iDeg <= 15 ? params.maxForce
                : params.maxForce * (.2 + .8/(iDeg-15));
        
        double fm = netForce.distance(0, 0);
        if (fm > maxForce) {
            netForce.x *= maxForce/fm;
            netForce.y *= maxForce/fm;
        }
        iVel.x = params.dampingC * (iVel.x + params.stepT * netForce.x);
        iVel.y = params.dampingC * (iVel.y + params.stepT * netForce.y);
        double speed = iVel.x*iVel.x+iVel.y*iVel.y;

        if (speed > params.maxSpeed) {
            iVel.x *= params.maxSpeed/speed;
            iVel.y *= params.maxSpeed/speed;
            speed = params.maxSpeed;
        }

        return .5 * speed * speed;
    }

    /**
     * Adjusts a node's position using specified initial position and velocity.
     * SpringLayout uses iLoc += stepT*iVel
     * @param iLoc position to change
     * @param iVel velocity to adjust
     * @param stepT step time
     */
    private static void adjustPosition(Point2D.Double iLoc, Point2D.Double iVel, double stepT) {
        iLoc.x += stepT * iVel.x;
        iLoc.y += stepT * iVel.y;
    }

    //endregion

}
