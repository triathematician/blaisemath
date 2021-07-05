package com.googlecode.blaisemath.graph

import com.google.common.collect.Maps
import com.googlecode.blaisemath.graph.ContractedGraphTest
import com.googlecode.blaisemath.graph.GraphUtilsTest
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
import java.util.*

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
 * Core state properties required for iterative graph layouts. Allows for inserting
 * location updates from alternate threads.
 *
 * @param <N> graph node type
 *
 * @author Elisha Peterson
</N> */
abstract class IterativeGraphLayoutState<N> {
    /** Current locations of nodes in the graph.  */
    protected val loc: MutableMap<N?, Point2D.Double?>? = Maps.newHashMap()

    /** Current velocities of nodes in the graph.  */
    protected val vel: MutableMap<N?, Point2D.Double?>? = Maps.newHashMap()

    /** Interim update locations to be applied at next opportunity.  */
    private val updateLoc: MutableMap<N?, Point2D.Double?>? = Maps.newHashMap()

    /** If true, the in-memory state will be updated to include only nodes in the update.  */
    private var resetNodes = false

    /** Cooling parameter, used to gradually reduce the impact of the layout  */
    private var coolingParameter = 0.0

    //region ThreadSafe GETTERS/MUTATORS
    fun getCoolingParameter(): Double {
        return coolingParameter
    }

    fun setCoolingParameter(`val`: Double) {
        coolingParameter = `val`
    }

    @Synchronized
    fun getPositionsCopy(): MutableMap<N?, Point2D.Double?>? {
        return Maps.newHashMap(loc)
    }

    /**
     * Request the specified locations to be applied at the next opportunity in the layout algorithm.
     * @param loc new locations
     * @param resetNodes if true, the keys in the provided map will be used to alter the set of nodes
     */
    @Synchronized
    fun requestPositions(loc: MutableMap<N?, Point2D.Double?>?, resetNodes: Boolean) {
        // in some race conditions, the request positions is called with empty loc, because the coordinate manager's
        // active location copy is empty (no current active locations). we don't want to clear the update locations in this
        // case, because it erases the entire state
        if (!loc.isEmpty()) {
            updateLoc.clear()
            updateLoc.putAll(loc)
            this.resetNodes = resetNodes
        }
    }
    //endregion
    //region LOCATION UPDATES
    /**
     * Synchronizes pending updates to node locations, executed prior to each layout step.
     * This method locks the entire layout state to prevent clashing updates.
     * @param nodes the set of nodes to be retained
     */
    @Synchronized
    fun nodeLocationSync(nodes: MutableSet<N?>?) {
        loc.keys.retainAll(nodes)
        for ((n, value) in updateLoc) {
            if (nodes.contains(n)) {
                loc[n] = value
                if (vel.containsKey(n)) {
                    vel.get(n).setLocation(0.0, 0.0)
                } else {
                    vel[n] = Point2D.Double()
                }
            }
        }
        if (resetNodes) {
            val keep: MutableSet<N?> = HashSet(nodes)
            keep.addAll(updateLoc.keys)
            loc.keys.retainAll(keep)
            vel.keys.retainAll(keep)
        }
        resetNodes = false
        updateLoc.clear()
        for (v in nodes) {
            if (!loc.containsKey(v)) {
                loc[v] = Point2D.Double()
                vel[v] = Point2D.Double()
            }
        }
    } //endregion
}