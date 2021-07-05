package com.googlecode.blaisemath.graph.generate

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
*/   class WattsStrogatzGeneratorTest {
    @Test
    fun testWattsStrogatzGenerator() {
        println("getInstance: MANUALLY CHECK FOR DESIRED OUTPUT")
        val result1 = WattsStrogatzGenerator().apply(WattsStrogatzGenerator.WattsStrogatzParameters(false, 10, 2, 0f))
        Assert.assertEquals(10, result1.nodes().size.toLong())
        Assert.assertEquals(10, result1.edges().size.toLong())
        for (i in 0..9) {
            Assert.assertTrue(result1.hasEdgeConnecting(i, (i + 1) % 10))
        }
        val result2 = WattsStrogatzGenerator().apply(WattsStrogatzGenerator.WattsStrogatzParameters(false, 50, 4, .5f))
        println(result2)
        Assert.assertEquals(50, result2.nodes().size.toLong())
        Assert.assertEquals(100, result2.edges().size.toLong())
    }
}