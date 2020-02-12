package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory (v3)
 * --
 * Copyright (C) 2009 - 2020 Elisha Peterson
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

import com.google.common.collect.Maps;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Core state properties required for iterative graph layouts. Allows for inserting
 * location updates from alternate threads.
 *
 * @param <N> graph node type
 *
 * @author Elisha Peterson
 */
public abstract class IterativeGraphLayoutState<N> {
    
    /** Current locations of nodes in the graph. */
    protected final Map<N, Point2D.Double> loc = Maps.newHashMap();
    /** Current velocities of nodes in the graph. */
    protected final Map<N, Point2D.Double> vel = Maps.newHashMap();
    
    /** Interim update locations to be applied at next opportunity. */
    private final Map<N, Point2D.Double> updateLoc = Maps.newHashMap();
    /** If true, the in-memory state will be updated to include only nodes in the update. */
    private boolean resetNodes = false;
    
    /** Cooling parameter, used to gradually reduce the impact of the layout */
    private double coolingParameter;
    
    
    //region ThreadSafe GETTERS/MUTATORS

    public double getCoolingParameter() {
        return coolingParameter;
    }
    
    public void setCoolingParameter(double val) {
        coolingParameter = val;
    }
    
    public synchronized Map<N,Point2D.Double> getPositionsCopy() {
        return Maps.newHashMap(loc);
    }

    /**
     * Request the specified locations to be applied at the next opportunity in the layout algorithm.
     * @param loc new locations
     * @param resetNodes if true, the keys in the provided map will be used to alter the set of nodes
     */
    public synchronized void requestPositions(Map<N, Point2D.Double> loc, boolean resetNodes) {
        // in some race conditions, the request positions is called with empty loc, because the coordinate manager's
        // active location copy is empty (no current active locations). we don't want to clear the update locations in this
        // case, because it erases the entire state
        if (!loc.isEmpty()) {
            updateLoc.clear();
            updateLoc.putAll(loc);
            this.resetNodes = resetNodes;
        }
    }
    
    //endregion
    
    //region LOCATION UPDATES
    
    /** 
     * Synchronizes pending updates to node locations, executed prior to each layout step.
     * This method locks the entire layout state to prevent clashing updates.
     * @param nodes the set of nodes to be retained
     */
    public synchronized void nodeLocationSync(Set<N> nodes) {
        loc.keySet().retainAll(nodes);

        for (Map.Entry<N, Point2D.Double> en : updateLoc.entrySet()) {
            N n = en.getKey();
            if (nodes.contains(n)) {
                loc.put(n, en.getValue());
                if (vel.containsKey(n)) {
                    vel.get(n).setLocation(0, 0);
                } else {
                    vel.put(en.getKey(), new Point2D.Double());
                }
            }
        }
        if (resetNodes) {
            Set<N> keep = new HashSet<>(nodes);
            keep.addAll(updateLoc.keySet());
            loc.keySet().retainAll(keep);
            vel.keySet().retainAll(keep);
        }
        resetNodes = false;
        updateLoc.clear();

        for (N v : nodes) {
            if (!loc.containsKey(v)) {
                loc.put(v, new Point2D.Double());
                vel.put(v, new Point2D.Double());
            }
        }
    }
    
    //endregion
    
}
