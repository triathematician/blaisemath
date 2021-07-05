package com.googlecode.blaisemath.graph.layout

import com.google.common.collect.Maps
import com.google.common.collect.Sets
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
 * Utility class for decomposing a graph background into separate regions.
 *
 * @param <N> type of element in region
 * @author Elisha Peterson
</N> */
internal class LayoutRegion<N> {
    private val points: MutableMap<N?, Point2D.Double?>? = Maps.newHashMap()
    private val adjacentRegions: MutableSet<LayoutRegion<N?>?>? = Sets.newLinkedHashSet()
    fun points(): MutableSet<N?>? {
        return points.keys
    }

    fun entries(): Iterable<MutableMap.MutableEntry<N?, Point2D.Double?>?>? {
        return points.entries
    }

    fun clear() {
        points.clear()
    }

    operator fun get(io: N?): Point2D.Double? {
        return points.get(io)
    }

    fun put(io: N?, iLoc: Point2D.Double?) {
        points[io] = iLoc
    }

    fun adjacentRegions(): MutableSet<LayoutRegion<N?>?>? {
        return adjacentRegions
    }

    fun addAdjacentRegion(reg: LayoutRegion<N?>?) {
        adjacentRegions.add(reg)
    }
}