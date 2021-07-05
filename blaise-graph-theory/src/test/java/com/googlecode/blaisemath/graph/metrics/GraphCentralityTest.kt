package com.googlecode.blaisemath.graph.metrics

import com.google.common.graph.Graph
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
*/   class GraphCentralityTest {
    @Test
    fun testApply() {
        Assert.assertEquals(1.0 / 2, INST1.apply(TEST2, 4), 1e-10)
    }

    @Test
    fun testApply_All() {
        val values = INST1.apply(TEST2)
        Assert.assertEquals(7, values.size.toLong())
        for (i in 0..6) {
            Assert.assertEquals(INST1.apply(TEST2, i + 1), values[i + 1], 1e-10)
        }
    }

    companion object {
        private var TEST2: Graph<Int?>? = null
        private var INST1: GraphCentrality? = null
        @BeforeClass
        fun setUpClass() {
            TEST2 = GraphUtils.createFromArrayEdges(false, Arrays.asList(1, 2, 3, 4, 5, 6, 7),
                    Arrays.asList(arrayOf<Int?>(1, 2), arrayOf<Int?>(1, 3), arrayOf<Int?>(2, 4), arrayOf<Int?>(3, 4), arrayOf<Int?>(3, 7), arrayOf<Int?>(4, 5), arrayOf<Int?>(5, 6)))
            // 1--2
            // |  |
            // 3--4--5--6
            // |
            // 7
            INST1 = GraphCentrality()
        }
    }
}