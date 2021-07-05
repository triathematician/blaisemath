package com.googlecode.blaisemath.graph.generate

import com.google.common.graph.Graph
import com.googlecode.blaisemath.graph.GraphUtils
import org.junit.Assert
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
*/   class DegreeDistributionGeneratorTest {
    @Test
    fun testGetDirectedInstance() {
        val sum = 1 + 7 + 3 + 2 + 1
        val expected = intArrayOf(1, 7, 3, 2, 1)
        val result: Graph<Int?> = DegreeDistributionGenerator.Companion.generateDirected(expected)
        println("graph: " + GraphUtils.printGraph(result))
        Assert.assertEquals(sum.toLong(), result.nodes().size.toLong())
        val foundDegrees = IntArray(5)
        Arrays.fill(foundDegrees, 0)
        for (i in result.nodes()) {
            foundDegrees[result.outDegree(i)]++
        }
        for (i in foundDegrees.indices) {
            Assert.assertEquals(expected[i], foundDegrees[i])
        }
    }

    @Test
    fun testGetUndirectedInstance() {
        try {
            DegreeDistributionGenerator.Companion.generateUndirected(intArrayOf(1, 7, 3, 2, 1))
            Assert.fail("Shouldn't be able to use odd degree sum.")
        } catch (ex: IllegalArgumentException) {
            // expected
        }
        val expected = intArrayOf(1, 7, 3, 3, 1)
        val sum = 1 + 7 + 3 + 3 + 1
        val result: Graph<Int?> = DegreeDistributionGenerator.Companion.generateUndirected(expected)
        Assert.assertEquals(sum.toLong(), result.nodes().size.toLong())
        val left = IntArray(5)
        System.arraycopy(expected, 0, left, 0, 5)
        for (i in result.nodes()) {
            left[result.degree(i)]--
        }
        println("  Unable to test this directly, but this array should be about 0: " + Arrays.toString(left))
    }
}