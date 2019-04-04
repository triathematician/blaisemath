/**
 * SpringLayoutState.java
 * Created Jan 16, 2016
 */
package com.googlecode.blaisemath.graph.mod.layout;

/*
 * #%L
 * BlaiseGraphTheory (v3)
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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
import com.googlecode.blaisemath.graph.IterativeGraphLayoutState;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

/**
 * <p>
 *   State object for spring layout. This tracks node locations and velocities,
 *   and divides node space up into regions to allow for more efficient
 *   layout calculations.
 * </p>
 * <p>
 *   This class may be safely modified by multiple threads simultaneously.
 * </p>
 * @param <C> graph node type
 * @author elisha
 */
@ThreadSafe
public final class SpringLayoutState<C> extends IterativeGraphLayoutState<C> {
    
    //<editor-fold defaultstate="collapsed" desc="STATIC CONSTANTS">
    
    private static final Logger LOG = Logger.getLogger(SpringLayoutState.class.getName());
    
    //</editor-fold>
    
    /** Regions used for localizing computation */
    @GuardedBy("this")
    LayoutRegion<C>[][] regions;
    /** Points that are not in a region */
    @GuardedBy("this")
    LayoutRegion<C> oRegion;
    /** List of all regions */
    @GuardedBy("this")
    List<LayoutRegion<C>> allRegions;
    
    //<editor-fold defaultstate="collapsed" desc="LOCATION UPDATES">
    
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
    
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="REGION MANAGEMENT">

    /** 
     * Updates the alignment of points to region.
     * @param nRegios number of regions in each dimension
     * @param regionSz ideal region size
     */
    void updateRegions(int nRegions, double regionSz) {
        initRegions(nRegions);
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
        int n = (regions.length - 1) / 2;
        int ix = (int) ((p.x + n * regionSz) / regionSz);
        int iy = (int) ((p.y + n * regionSz) / regionSz);
        if (ix < 0 || ix > 2 * n || iy < 0 || iy > 2 * n) {
            return oRegion;
        }
        return regions[ix][iy];
    }
    
    /** Initializes regions, with n in each dimension. */
    private void initRegions(int nRegions) {
        int n = nRegions/2;
        regions = new LayoutRegion[2 * n + 1][2 * n + 1];
        allRegions = Lists.newArrayList();
        for (int ix = -n; ix <= n; ix++) {
            for (int iy = -n; iy <= n; iy++) {
                LayoutRegion region = new LayoutRegion();
                regions[ix + n][iy + n] = region;
                allRegions.add(region);
            }
        }
        // set up adjacencies
        for (int ix = -n; ix <= n; ix++) {
            for (int iy = -n; iy <= n; iy++) {
                for (int ix2 = Math.max(ix - 1, -n); ix2 <= Math.min(ix + 1, n); ix2++) {
                    for (int iy2 = Math.max(iy - 1, -n); iy2 <= Math.min(iy + 1, n); iy2++) {
                        regions[ix + n][iy + n].addAdjacentRegion(regions[ix2 + n][iy2 + n]);
                    }
                }
            }
        }
        // set up adjacencies with outer region
        oRegion = new LayoutRegion<C>();
        allRegions.add(oRegion);
        oRegion.addAdjacentRegion(oRegion);
        for (int ix = -n; ix <= n; ix++) {
            LayoutRegion<C> min = regions[ix + n][0];
            LayoutRegion<C> max = regions[ix + n][2 * n];
            min.addAdjacentRegion(oRegion);
            max.addAdjacentRegion(oRegion);
            oRegion.addAdjacentRegion(min);
            oRegion.addAdjacentRegion(max);
        }
        for (int iy = -n + 1; iy <= n - 1; iy++) {
            LayoutRegion<C> min = regions[0][iy + n];
            LayoutRegion<C> max = regions[2 * n][iy + n];
            min.addAdjacentRegion(oRegion);
            max.addAdjacentRegion(oRegion);
            oRegion.addAdjacentRegion(min);
            oRegion.addAdjacentRegion(max);
        }
    }
    
    // </editor-fold>
    
}
