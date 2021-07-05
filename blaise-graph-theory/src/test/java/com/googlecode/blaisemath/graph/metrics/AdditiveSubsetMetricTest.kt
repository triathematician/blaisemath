package com.googlecode.blaisemath.graph.metrics

import com.google.common.graph.Graph
import com.googlecode.blaisemath.graph.GraphSubsetMetric
import com.googlecode.blaisemath.graph.GraphUtils
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.util.*

/*
* #%L
* BlaiseGraphTheory
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
*/   class AdditiveSubsetMetricTest {
    @Test
    fun testGetValue() {
        Assert.assertEquals(10, INST.getValue(TEST2, HashSet(Arrays.asList(1, 2, 3, 4))))
        Assert.assertEquals(6, INST.getValue(TEST2, HashSet(Arrays.asList(4, 5, 6))))
    }

    companion object {
        private var TEST2: Graph<Int?>? = null
        private var INST: GraphSubsetMetric<*>? = null
        @BeforeClass
        fun setUpClass() {
            INST = SubsetMetrics.additiveSubsetMetric(Degree())
            TEST2 = GraphUtils.createFromArrayEdges(false, Arrays.asList(1, 2, 3, 4, 5, 6, 7),
                    Arrays.asList(arrayOf<Int?>(1, 2), arrayOf<Int?>(1, 3), arrayOf<Int?>(2, 4), arrayOf<Int?>(3, 4), arrayOf<Int?>(3, 7), arrayOf<Int?>(4, 5), arrayOf<Int?>(5, 6)))
            // 1--2
            // |  |
            // 3--4--5--6
            // |
            // 7
        }
    }
}