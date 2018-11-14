package com.googlecode.blaisemath.graph.mod.layout;

/*
 * #%L
 * BlaiseGraphTheory (v3)
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

import com.google.common.collect.Lists;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import com.googlecode.blaisemath.graph.IterativeGraphLayoutState;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * State object for spring layout. This tracks node locations and velocities,
 * and divides node space up into regions to allow for more efficient
 * layout calculations. This class may be safely modified by multiple threads simultaneously.
 * @param <C> graph node type
 * @author elisha
 */
final class SpringLayoutState<C> extends IterativeGraphLayoutState<C> {
    
    private static final Logger LOG = Logger.getLogger(SpringLayoutState.class.getName());
    
    /** # of regions away from origin in x and y directions. Region size is determined by the maximum repel distance. */
    private static final int REGION_N = 5;

    /** Regions used for localizing computation */
    @GuardedBy("this")
    LayoutRegion<C>[][] regions;
    /** Points that are not in a region */
    @GuardedBy("this")
    LayoutRegion<C> oRegion;
    /** List of all regions */
    @GuardedBy("this")
    List<LayoutRegion<C>> allRegions;
    
    //region UPDATERS
    
    Point2D.Double getLoc(C io) {
        return loc.get(io);
    }
    
    void putLoc(C io, Point2D.Double pt) {
        loc.put(io, pt);
    }
    
    Point2D.Double getVel(C io) {
        return vel.get(io);
    }
    
    void putVel(C io, Point2D.Double pt) {
        vel.put(io, pt);
    }
    
    //endregion

    //region MANAGING REGIONS

    /** Updates the alignment of points to region */
    void updateRegions(double regionSz) {
        if (regions == null) {
            initRegions();
        }
        for (LayoutRegion<C> r : allRegions) {
            r.clear();
        }
        for (Map.Entry<C, Point2D.Double> en : loc.entrySet()) {
            LayoutRegion r = regionByLoc(en.getValue(), regionSz);
            if (r != null) {
                r.put(en.getKey(), en.getValue());
            } else {
                LOG.log(Level.WARNING, "Point not in any region: {0}", en);
            }
        }
    }
    
    /** Return region for specified point */
    private LayoutRegion regionByLoc(Point2D.Double p, double regionSz) {
        int ix = (int) ((p.x + REGION_N * regionSz) / regionSz);
        int iy = (int) ((p.y + REGION_N * regionSz) / regionSz);
        if (ix < 0 || ix > 2 * REGION_N || iy < 0 || iy > 2 * REGION_N) {
            return oRegion;
        }
        return regions[ix][iy];
    }
    
    /** Initializes regions */
    private void initRegions() {
        regions = new LayoutRegion[2 * REGION_N + 1][2 * REGION_N + 1];
        allRegions = Lists.newArrayList();
        for (int ix = -REGION_N; ix <= REGION_N; ix++) {
            for (int iy = -REGION_N; iy <= REGION_N; iy++) {
                LayoutRegion region = new LayoutRegion();
                regions[ix + REGION_N][iy + REGION_N] = region;
                allRegions.add(region);
            }
        }
        // set up adjacencies
        for (int ix = -REGION_N; ix <= REGION_N; ix++) {
            for (int iy = -REGION_N; iy <= REGION_N; iy++) {
                for (int ix2 = Math.max(ix - 1, -REGION_N); ix2 <= Math.min(ix + 1, REGION_N); ix2++) {
                    for (int iy2 = Math.max(iy - 1, -REGION_N); iy2 <= Math.min(iy + 1, REGION_N); iy2++) {
                        regions[ix + REGION_N][iy + REGION_N].addAdjacentRegion(regions[ix2 + REGION_N][iy2 + REGION_N]);
                    }
                }
            }
        }
        // set up adjacencies with outer region
        oRegion = new LayoutRegion<C>();
        allRegions.add(oRegion);
        oRegion.addAdjacentRegion(oRegion);
        for (int ix = -REGION_N; ix <= REGION_N; ix++) {
            LayoutRegion<C> min = regions[ix + REGION_N][0];
            LayoutRegion<C> max = regions[ix + REGION_N][2 * REGION_N];
            min.addAdjacentRegion(oRegion);
            max.addAdjacentRegion(oRegion);
            oRegion.addAdjacentRegion(min);
            oRegion.addAdjacentRegion(max);
        }
        for (int iy = -REGION_N + 1; iy <= REGION_N - 1; iy++) {
            LayoutRegion<C> min = regions[0][iy + REGION_N];
            LayoutRegion<C> max = regions[2 * REGION_N][iy + REGION_N];
            min.addAdjacentRegion(oRegion);
            max.addAdjacentRegion(oRegion);
            oRegion.addAdjacentRegion(min);
            oRegion.addAdjacentRegion(max);
        }
    }
    
    //endregion
    
}
