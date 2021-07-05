package com.googlecode.blaisemath.graph.layout

import com.google.common.collect.Lists
import com.googlecode.blaisemath.graph.ContractedGraphTest
import com.googlecode.blaisemath.graph.GraphUtilsTest
import com.googlecode.blaisemath.graph.IterativeGraphLayoutState
import com.googlecode.blaisemath.graph.SubgraphTest
import com.googlecode.blaisemath.graph.generate.GraphGrowthRule
import com.googlecode.blaisemath.graph.generate.GraphSeedRule
import com.googlecode.blaisemath.graph.generate.HopGrowthRule
import com.googlecode.blaisemath.graph.layout.SpringLayoutPerformanceTest
import com.googlecode.blaisemath.graph.metrics.AdditiveSubsetMetricTest
import com.googlecode.blaisemath.graph.metrics.BetweenCentralityTest
import com.googlecode.blaisemath.graph.metrics.ClosenessCentralityTest
import com.googlecode.blaisemath.graph.metrics.CooperationMetric
import com.googlecode.blaisemath.graph.metrics.EigenCentralityTest
import com.googlecode.blaisemath.graph.metrics.GraphCentralityTest
import com.googlecode.blaisemath.graph.metrics.SubsetMetricsTest
import com.googlecode.blaisemath.test.AssertUtils
import org.junit.BeforeClass
import java.awt.geom.Point2D
import java.util.logging.Level
import java.util.logging.Logger

/*
* #%L
* BlaiseGraphTheory (v3)
* --
* Copyright (C) 2009 - 2021 Elisha Peterson
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
*/ /**
 * State object for spring layout. This tracks node locations and velocities, and divides node space up into regions to allow for more efficient
 * layout calculations. This class may be safely modified by multiple threads simultaneously.
 * @param <N> graph node type
 * @author Elisha Peterson
</N> */
class SpringLayoutState<N> : IterativeGraphLayoutState<N?>() {
    /** Regions used for localizing computation  */
    var regions: Array<Array<LayoutRegion<N?>?>?>?

    /** Points that are not in a region  */
    var oRegion: LayoutRegion<N?>? = null

    /** List of all regions  */
    var allRegions: MutableList<LayoutRegion<N?>?>? = null

    //region UPDATERS
    fun getLoc(io: N?): Point2D.Double? {
        return loc[io]
    }

    fun putLoc(io: N?, pt: Point2D.Double?) {
        loc[io] = pt
    }

    fun getVel(io: N?): Point2D.Double? {
        return vel[io]
    }

    fun putVel(io: N?, pt: Point2D.Double?) {
        vel[io] = pt
    }
    //endregion
    //region MANAGING REGIONS
    /** Updates the alignment of points to region  */
    fun updateRegions(regionSz: Double) {
        if (regions == null) {
            initRegions()
        }
        for (r in allRegions) {
            r.clear()
        }
        for (en in loc.entries) {
            val r = regionByLoc(en.value, regionSz)
            if (r != null) {
                r.put(en.key, en.value)
            } else {
                LOG.log(Level.WARNING, "Point not in any region: {0}", en)
            }
        }
    }

    /** Return region for specified point  */
    private fun regionByLoc(p: Point2D.Double?, regionSz: Double): LayoutRegion<*>? {
        val ix = ((p.x + REGION_N * regionSz) / regionSz) as Int
        val iy = ((p.y + REGION_N * regionSz) / regionSz) as Int
        return if (ix < 0 || ix > 2 * REGION_N || iy < 0 || iy > 2 * REGION_N) {
            oRegion
        } else regions.get(ix).get(iy)
    }

    /** Initializes regions  */
    private fun initRegions() {
        regions = Array<Array<LayoutRegion<*>?>?>(2 * REGION_N + 1) { arrayOfNulls<LayoutRegion<*>?>(2 * REGION_N + 1) }
        allRegions = Lists.newArrayList()
        for (ix in -REGION_N..REGION_N) {
            for (iy in -REGION_N..REGION_N) {
                val region: LayoutRegion<*> = LayoutRegion<Any?>()
                regions.get(ix + REGION_N).get(iy + REGION_N) = region
                allRegions.add(region)
            }
        }
        // set up adjacencies
        for (ix in -REGION_N..REGION_N) {
            for (iy in -REGION_N..REGION_N) {
                for (ix2 in Math.max(ix - 1, -REGION_N)..Math.min(ix + 1, REGION_N)) {
                    for (iy2 in Math.max(iy - 1, -REGION_N)..Math.min(iy + 1, REGION_N)) {
                        regions.get(ix + REGION_N).get(iy + REGION_N).addAdjacentRegion(regions.get(ix2 + REGION_N).get(iy2 + REGION_N))
                    }
                }
            }
        }
        // set up adjacencies with outer region
        oRegion = LayoutRegion()
        allRegions.add(oRegion)
        oRegion.addAdjacentRegion(oRegion)
        for (ix in -REGION_N..REGION_N) {
            val min = regions.get(ix + REGION_N).get(0)
            val max = regions.get(ix + REGION_N).get(2 * REGION_N)
            min.addAdjacentRegion(oRegion)
            max.addAdjacentRegion(oRegion)
            oRegion.addAdjacentRegion(min)
            oRegion.addAdjacentRegion(max)
        }
        for (iy in -REGION_N + 1..REGION_N - 1) {
            val min = regions.get(0).get(iy + REGION_N)
            val max = regions.get(2 * REGION_N).get(iy + REGION_N)
            min.addAdjacentRegion(oRegion)
            max.addAdjacentRegion(oRegion)
            oRegion.addAdjacentRegion(min)
            oRegion.addAdjacentRegion(max)
        }
    } //endregion

    companion object {
        private val LOG = Logger.getLogger(SpringLayoutState::class.java.name)

        /** # of regions away from origin in x and y directions. Region size is determined by the maximum repel distance.  */
        private const val REGION_N = 5
    }
}