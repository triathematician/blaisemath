package com.googlecode.blaisemath.json

import com.google.common.collect.BoundType
import com.google.common.collect.Range
import org.junit.Assert
import org.junit.Test
import java.io.IOException

/*-
* #%L
* blaise-json
* --
* Copyright (C) 2019 - 2021 Elisha Peterson
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
*/   class RangeProxyTest {
    @Test
    fun testToRange() {
        val rp = RangeProxy()
        rp.min = 1
        rp.minType = BoundType.OPEN
        rp.max = 2
        rp.maxType = BoundType.CLOSED
        Assert.assertEquals(Range.openClosed(1, 2), rp.toRange())
    }

    @Test
    fun testCycle() {
        testRecycle(Range.greaterThan(1))
        testRecycle(Range.lessThan(1))
        testRecycle(Range.openClosed(1, 2))
        testRecycle(Range.singleton(1))
        testRecycle(Range.closed(1, 2))
        assertException { RangeProxy(Range.all<Int?>()).toRange() }
    }

    private fun testRecycle(r: Range<*>?) {
        Assert.assertEquals(r, RangeProxy(r).toRange())
    }

    private fun assertException(r: Runnable?) {
        try {
            r.run()
            Assert.fail("Should throw exception")
        } catch (x: Exception) {
            // expected
        }
    }

    @Test
    fun testCreate() {
        val rp = RangeProxy(Range.openClosed(1, 2))
        Assert.assertEquals(1, rp.min)
        Assert.assertEquals(2, rp.max)
        Assert.assertEquals(BoundType.OPEN, rp.minType)
        Assert.assertEquals(BoundType.CLOSED, rp.maxType)
    }

    @Test
    @Throws(IOException::class)
    fun testSerialize() {
        val r: Range<*>? = Range.openClosed(1, 2)
        BlaiseJson.writerWithDefaultPrettyPrinter().writeValue(System.out, r)
    }
}