package com.googlecode.blaisemath.test

import org.junit.Assert
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
*/   object AssertUtils {
    /** Tests to see if all elements of one set are contained in the other, and vice versa.  */
    fun <X> assertSets(sets: MutableCollection<MutableSet<X?>?>?, vararg test: MutableSet<*>?) {
        assertCollectionContentsSame(Arrays.asList(*test), sets)
    }

    /** Tests to see if all elements of one collection are contained in the other, and vice versa.  */
    fun <X> assertCollectionContentsSame(expected: MutableCollection<X?>?, found: MutableCollection<X?>?) {
        Assert.assertEquals("Collection size mismatch. Expected $expected but was $found", expected.size.toLong(), found.size.toLong())
        Assert.assertTrue("Collection contents mismatch. Expected $expected but was $found", expected.containsAll(found))
        Assert.assertTrue("Collection contents mismatch. Expected $expected but was $found", found.containsAll(expected))
    }

    /** Tests to see if runnable throws an exception.  */
    fun assertIllegalArgumentException(r: Runnable?) {
        try {
            r.run()
            Assert.fail("Expected IllegalArgumentException")
        } catch (x: IllegalArgumentException) {
            // passes
        }
    }
}