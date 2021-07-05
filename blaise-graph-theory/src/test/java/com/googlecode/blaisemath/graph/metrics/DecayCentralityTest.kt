package com.googlecode.blaisemath.graph.metrics

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
*/   class DecayCentralityTest {
    @Test
    fun testGetParameter_setParameter() {
        val instance = DecayCentrality(0.1)
        Assert.assertEquals(0.1, instance.getParameter(), 0.0)
        Assert.assertEquals(instance.parameter, instance.getParameter(), 0.0)
        instance.setParameter(0.2)
        Assert.assertEquals(0.2, instance.parameter, 0.0)
        try {
            instance.setParameter(1.2)
            Assert.fail("Illegal Parameter")
        } catch (ex: IllegalArgumentException) {
            // expected
        }
        try {
            instance.setParameter(-.2)
            Assert.fail("Illegal Parameter")
        } catch (ex: IllegalArgumentException) {
            // expected
        }
    }
}