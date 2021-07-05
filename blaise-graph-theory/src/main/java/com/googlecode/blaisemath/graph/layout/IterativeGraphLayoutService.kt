package com.googlecode.blaisemath.graph.layout

import com.google.common.util.concurrent.AbstractScheduledService
import com.google.common.util.concurrent.MoreExecutors
import com.google.common.util.concurrent.Service
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
import java.util.concurrent.TimeUnit
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
 * Service used for executing iterative graph layouts on a background thread.
 * The "runOneIteration" method is assumed to be run from a background or alternate thread.
 */
internal class IterativeGraphLayoutService @JvmOverloads constructor(
        /** Manages the layout  */
        private val manager: IterativeGraphLayoutManager?,
        /** Delay between loops  */
        private val loopDelay: Int = DEFAULT_DELAY
) : AbstractScheduledService() {
    //region PROPERTIES
    fun isLayoutActive(): Boolean {
        return isRunning
    }

    //endregion
    @Synchronized
    public override fun runOneIteration() {
        try {
            manager.runOneLoop()
        } catch (x: InterruptedException) {
            LOG.log(Level.FINE, "Background layout interrupted", x)
            // restore interrupt after bypassing update
            Thread.currentThread().interrupt()
        }
    }

    override fun scheduler(): Scheduler? {
        return Scheduler.newFixedDelaySchedule(0, loopDelay.toLong(), TimeUnit.MILLISECONDS)
    }

    companion object {
        private val LOG = Logger.getLogger(IterativeGraphLayoutService::class.java.name)

        /** Default time between layout iterations.  */
        private const val DEFAULT_DELAY = 10
    }

    init {
        addListener(object : Service.Listener() {
            override fun failed(from: Service.State?, failure: Throwable?) {
                LOG.log(Level.SEVERE, "Layout service failed", failure)
            }
        }, MoreExecutors.newDirectExecutorService())
    }
}