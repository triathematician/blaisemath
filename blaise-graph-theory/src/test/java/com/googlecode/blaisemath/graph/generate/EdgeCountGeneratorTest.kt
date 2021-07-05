package com.googlecode.blaisemath.graph.generate

import com.googlecode.blaisemath.graph.GraphUtils
import org.junit.Assert
import org.junit.Test

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
*/   class EdgeCountGeneratorTest {
    @Test
    fun testEdgeCountBuilder() {
        var result1 = EdgeCountGenerator().apply(ExtendedGeneratorParameters(false, 10, 0))
        Assert.assertEquals(10, result1.nodes().size.toLong())
        Assert.assertEquals(0, result1.edges().size.toLong())
        result1 = EdgeCountGenerator().apply(ExtendedGeneratorParameters(false, 10, 30))
        Assert.assertEquals(10, result1.nodes().size.toLong())
        Assert.assertEquals(30, result1.edges().size.toLong())
        println("  UNDIRECTED: " + GraphUtils.printGraph(result1))
        result1 = EdgeCountGenerator().apply(ExtendedGeneratorParameters(true, 10, 30))
        Assert.assertEquals(10, result1.nodes().size.toLong())
        Assert.assertEquals(30, result1.edges().size.toLong())
        println("  DIRECTED: " + GraphUtils.printGraph(result1))
    }
}